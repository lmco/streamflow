/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import streamflow.model.Cluster;
import streamflow.model.Topology;
import streamflow.model.TopologyLog;
import streamflow.model.TopologyLogCriteria;
import streamflow.model.TopologyLogEntry;
import streamflow.model.TopologyLogPage;
import streamflow.model.config.StreamflowConfig;
import streamflow.service.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogService {

    protected static final Logger LOG = LoggerFactory.getLogger(LogService.class);
    
    private final StreamflowConfig streamflowConfig;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Inject
    public LogService(StreamflowConfig streamflowConfig) {
        this.streamflowConfig = streamflowConfig;
    }
    
    public TopologyLog getTopologyLogLocal(Topology topology, long offset, long limit) {
        TopologyLog topologyLog = new TopologyLog();
        topologyLog.setOffset(offset);
        topologyLog.setCount(0);
        
        BufferedReader inputReader = null;
        
        try {
            inputReader = new BufferedReader(new FileReader(new File(
                    streamflowConfig.getLogger().getBaseDir(), 
                    "topology-" + topology.getId() + ".log")));

            String currentLine;

            // Skip over the specified number of lines until the offset is reached or max line
            long currentOffset = 0;
            while ((currentOffset < offset) && ((currentLine = inputReader.readLine()) != null)) {
                currentOffset++;
            }

            // Process the specified number of lines from the log file
            long lineCount = 0;
            while ((lineCount < limit) && ((currentLine = inputReader.readLine()) != null)) {
                topologyLog.getLines().add(currentLine);
                lineCount++;
            }

            // Update the log metadata with the final line count
            topologyLog.setCount(lineCount);
        } catch (IOException ex) {
            LOG.error("Error reading log data for topology: {}", ex.getMessage());
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException ex) {
                }
            }
        }

        return topologyLog;
    }
    
    public TopologyLogPage getTopologyLogCluster(Topology topology, Cluster cluster,
            TopologyLogCriteria criteria) {
        TopologyLogPage logPage = new TopologyLogPage();
        logPage.setTopology(topology.getId());
        logPage.setCriteria(criteria);
        
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("topology.raw", topology.getId()));

        if (criteria.getQuery() != null && !criteria.getQuery().trim().isEmpty()) {
            query.must(QueryBuilders.queryString(criteria.getQuery()));
        }
        if (criteria.getLevel() != null) {
            query.must(QueryBuilders.termQuery("level.raw", criteria.getLevel()));
        }
        if (criteria.getComponent() != null) {
            query.must(QueryBuilders.termQuery("component.raw", criteria.getComponent()));
        }
        if (criteria.getCategory() != null) {
            query.must(QueryBuilders.termQuery("category.raw", criteria.getCategory()));
        }
        if (!criteria.getShowHistoric()) {
            query.must(QueryBuilders.termQuery("project.raw", topology.getProjectId()));
        }

        // TODO: HANDLE THE AGE CRITERIA

        SortOrder sortOrder = SortOrder.DESC;
        if (criteria.getSortOrder() != null) {
            if (criteria.getSortOrder() == TopologyLogCriteria.SortOrder.ASC) {
                sortOrder = SortOrder.ASC;
            }
        }

        SearchSourceBuilder searchBuilder = SearchSourceBuilder.searchSource()
                .query(query)
                .from((criteria.getPageNum() - 1) * criteria.getPageSize())
                .size(criteria.getPageSize())
                .sort("@timestamp", sortOrder)
                .facet(FacetBuilders.termsFacet("levels").field("level.raw"))
                .facet(FacetBuilders.termsFacet("components").field("component.raw"))
                .facet(FacetBuilders.termsFacet("categories").field("category.raw"));

        try {
            Response searchResponse = Request.Post(String.format("http://%s:%d/_all/topology/_search", 
                    cluster.getLogServerHost(), cluster.getLogServerPort()))
                    .bodyString(searchBuilder.toString(), ContentType.APPLICATION_JSON)
                    .execute();
            
            logPage = parseSearchResponse(
                    logPage, criteria, searchResponse.returnContent().asString());
            
        } catch (IOException ex) {
            //LOG.error("Unable to parse log search response: ", ex);
            
            throw new ServiceException("Unable to parse log search response: " + ex.getMessage());
        }
        
        return logPage;
    }
    
    public void clearTopologyLog(Topology topology, Cluster cluster) {
        File logFile = new File(streamflowConfig.getLogger().getBaseDir(), 
                "topology-" + topology.getId() + ".log");
        
        try {
            // Delete the local log file from the server
            if (logFile.exists()) {
                FileUtils.forceDelete(logFile);
            }
        } catch (IOException ex) {
            LOG.error("Error deleting local topology log file: " + logFile.getAbsolutePath());
        }
        
        if (cluster != null) {
            // If the topology was deployed to the cluster also clear the log entries from the server
            if (!cluster.getId().equals(Cluster.LOCAL)) {
                // Disable delete of log data from elasticsearch for historic purposes
                /*
                try {
                    Response searchResponse = Request.Delete(
                            String.format("http://%s:%d/_all/topology/_query?q=topology:%s", 
                            cluster.getLogServerHost(), cluster.getLogServerPort(), topology.getId()))
                            .execute();

                    if (searchResponse.returnResponse().getStatusLine().getStatusCode() != 200) {
                        LOG.error("Error response from server when submitting the delete by query request");
                    }
                } catch (IOException ex) {
                    LOG.error("Error deleting cluster topology log data from server: " + ex.getMessage());
                }
                */
            }
        }
    }
    
    private TopologyLogPage parseSearchResponse(TopologyLogPage logPage, 
            TopologyLogCriteria criteria, String searchResponseJson) {
        try {
            Map<String, Object> searchResponseObject = 
                    objectMapper.readValue(searchResponseJson, Map.class);
        
            Map<String, Object> hitsObject = (Map<String, Object>) searchResponseObject.get("hits");
            
            // Iterate over each of the search results
            List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsObject.get("hits");
            for (Map<String, Object> searchHit : hitsList) {
                Map<String, Object> sourceObject = (Map<String, Object>) searchHit.get("_source");
                
                TopologyLogEntry logEntry = new TopologyLogEntry();
                logEntry.setTimestamp((String) sourceObject.get("timestamp"));
                logEntry.setLevel((String) sourceObject.get("level"));
                logEntry.setHost((String) sourceObject.get("host"));
                logEntry.setTask((String) sourceObject.get("task"));
                logEntry.setComponent((String) sourceObject.get("component"));
                logEntry.setCategory((String) sourceObject.get("category"));
                logEntry.setText((String) sourceObject.get("text"));

                logPage.getResults().add(logEntry);
            }
            
            logPage.setPageResults((long) logPage.getResults().size());
            logPage.setPageStart(((criteria.getPageNum() - 1) * criteria.getPageSize()) + 1);
            logPage.setPageEnd(logPage.getPageStart() + logPage.getPageResults() - 1);
            logPage.setResultsPerPage((long) criteria.getPageSize());
            logPage.setTotalResults((Integer) hitsObject.get("total"));
            logPage.setTotalPages((logPage.getTotalResults() / logPage.getResultsPerPage()) + 1l);
            
            logPage.setFirstPage(1l);
            logPage.setLastPage(logPage.getTotalPages());
            logPage.setCurrPage((long) criteria.getPageNum());

            if (logPage.getCurrPage() > logPage.getLastPage()) {
                logPage.setCurrPage(logPage.getLastPage());
            }

            if (logPage.getCurrPage() > 1) {
                logPage.setPrevPage(logPage.getCurrPage() - 1);
            }

            if (logPage.getCurrPage() < logPage.getLastPage()) {
                logPage.setNextPage(logPage.getCurrPage() + 1);
            }
        } catch (IOException ex) {
            LOG.error("Exception occurred while parsing search response: " + ex.getMessage());
            
            throw new ServiceException("Exception occurred while parsing search response: " 
                    + ex.getMessage());
        }
        
        return logPage;
    }
}
