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
package streamflow.model;

import java.util.ArrayList;
import java.util.List;

public class TopologyLogPage {
    
    private String topology;
    
    private long pageStart;
    
    private long pageEnd;
    
    private long pageResults;
    
    private long totalResults;
    
    private long totalPages;
    
    private long resultsPerPage;
    
    private long firstPage;
    
    private long prevPage;
    
    private long currPage;
    
    private long nextPage;
    
    private long lastPage;
    
    private List<TopologyLogEntry> results = new ArrayList<TopologyLogEntry>();
    
    private TopologyLogCriteria criteria;
    
    
    public TopologyLogPage() {
    }

    public String getTopology() {
        return topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

    public long getPageStart() {
        return pageStart;
    }

    public void setPageStart(long pageStart) {
        this.pageStart = pageStart;
    }

    public long getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(long pageEnd) {
        this.pageEnd = pageEnd;
    }

    public long getPageResults() {
        return pageResults;
    }

    public void setPageResults(long pageResults) {
        this.pageResults = pageResults;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(long resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public long getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(long firstPage) {
        this.firstPage = firstPage;
    }

    public long getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(long prevPage) {
        this.prevPage = prevPage;
    }

    public long getCurrPage() {
        return currPage;
    }

    public void setCurrPage(long currPage) {
        this.currPage = currPage;
    }

    public long getNextPage() {
        return nextPage;
    }

    public void setNextPage(long nextPage) {
        this.nextPage = nextPage;
    }

    public long getLastPage() {
        return lastPage;
    }

    public void setLastPage(long lastPage) {
        this.lastPage = lastPage;
    }

    public List<TopologyLogEntry> getResults() {
        return results;
    }

    public void setResults(List<TopologyLogEntry> results) {
        this.results = results;
    }

    public TopologyLogCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(TopologyLogCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.topology != null ? this.topology.hashCode() : 0);
        hash = 59 * hash + (int) (this.pageStart ^ (this.pageStart >>> 32));
        hash = 59 * hash + (int) (this.pageEnd ^ (this.pageEnd >>> 32));
        hash = 59 * hash + (int) (this.pageResults ^ (this.pageResults >>> 32));
        hash = 59 * hash + (int) (this.totalResults ^ (this.totalResults >>> 32));
        hash = 59 * hash + (int) (this.totalPages ^ (this.totalPages >>> 32));
        hash = 59 * hash + (int) (this.resultsPerPage ^ (this.resultsPerPage >>> 32));
        hash = 59 * hash + (int) (this.firstPage ^ (this.firstPage >>> 32));
        hash = 59 * hash + (int) (this.prevPage ^ (this.prevPage >>> 32));
        hash = 59 * hash + (int) (this.currPage ^ (this.currPage >>> 32));
        hash = 59 * hash + (int) (this.nextPage ^ (this.nextPage >>> 32));
        hash = 59 * hash + (int) (this.lastPage ^ (this.lastPage >>> 32));
        hash = 59 * hash + (this.results != null ? this.results.hashCode() : 0);
        hash = 59 * hash + (this.criteria != null ? this.criteria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TopologyLogPage other = (TopologyLogPage) obj;
        if ((this.topology == null) ? (other.topology != null) : !this.topology.equals(other.topology)) {
            return false;
        }
        if (this.pageStart != other.pageStart) {
            return false;
        }
        if (this.pageEnd != other.pageEnd) {
            return false;
        }
        if (this.pageResults != other.pageResults) {
            return false;
        }
        if (this.totalResults != other.totalResults) {
            return false;
        }
        if (this.totalPages != other.totalPages) {
            return false;
        }
        if (this.resultsPerPage != other.resultsPerPage) {
            return false;
        }
        if (this.firstPage != other.firstPage) {
            return false;
        }
        if (this.prevPage != other.prevPage) {
            return false;
        }
        if (this.currPage != other.currPage) {
            return false;
        }
        if (this.nextPage != other.nextPage) {
            return false;
        }
        if (this.lastPage != other.lastPage) {
            return false;
        }
        if (this.results != other.results && (this.results == null || !this.results.equals(other.results))) {
            return false;
        }
        if (this.criteria != other.criteria && (this.criteria == null || !this.criteria.equals(other.criteria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyLogPage{" + "topology=" + topology + ", pageStart=" + pageStart 
                + ", pageEnd=" + pageEnd + ", pageResults=" + pageResults 
                + ", totalResults=" + totalResults + ", totalPages=" + totalPages 
                + ", resultsPerPage=" + resultsPerPage + ", firstPage=" + firstPage 
                + ", prevPage=" + prevPage + ", currPage=" + currPage + ", nextPage=" + nextPage 
                + ", lastPage=" + lastPage + ", results=" + results + ", criteria=" + criteria + '}';
    }
}
