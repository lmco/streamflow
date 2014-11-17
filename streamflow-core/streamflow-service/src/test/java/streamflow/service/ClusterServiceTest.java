package streamflow.service;

import java.util.Collection;
import streamflow.engine.StormEngine;
import streamflow.model.Cluster;
import streamflow.model.config.StreamflowConfig;
import streamflow.model.storm.ClusterSummary;
import streamflow.service.exception.EntityNotFoundException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClusterServiceTest {

    @Mock
    public StormEngine stormEngine;
    
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    private StreamflowConfig config;
    
    private ClusterService clusterService;
    
    @Before
    public void setUp() {
        config = new StreamflowConfig();
        config.getClusters().add(new Cluster("test", "Test Cluster"));
        
        clusterService = new ClusterService(stormEngine, config);
        
        when(stormEngine.getClusterSummary(any(Cluster.class)))
                .thenReturn(new ClusterSummary());
    }
    
    @Test
    public void listClusters() {
        Collection<Cluster> clusters = clusterService.listClusters();
        
        assertEquals("Default config should produce two clusters", 2, clusters.size());
    }
    
    @Test
    public void addClusterWithUniqueId() {
        Cluster newCluster = new Cluster("third", "Third Cluster");
        
        clusterService.addCluster(newCluster);
        
        assertEquals("New cluster should result in three clusters",
                3, clusterService.listClusters().size());
    }
    
    @Test
    public void addClusterWithDuplicateId() {
        Cluster newCluster = new Cluster("test", "Third Cluster");
        
        clusterService.addCluster(newCluster);
        
        assertEquals("New cluster should overwrite existing cluster and have count of 2",
                2, clusterService.listClusters().size());
        
        assertEquals("New cluster should override existing cluster with same id",
                newCluster.getDisplayName(), clusterService.getCluster("test").getDisplayName());
    }
    
    @Test
    public void getClusterWithValidId() {
        Cluster cluster = clusterService.getCluster("test");
        
        assertNotNull("Retrieved cluster should not be null with a valid id", cluster);
        
        assertEquals("Retrieved cluster name should match the requested cluster id",
                "Test Cluster", cluster.getDisplayName());
    }
    
    @Test
    public void getClusterWithInvalidId() {
        String clusterId = "invalid-id";
        
        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Cluster with the specified ID not found: ID = " + clusterId);
        
        clusterService.getCluster(clusterId);
    }
    
    @Test
    public void deleteClusterWithValidId() {
        String clusterId = "test";
        
        clusterService.deleteCluster(clusterId);
        
        assertEquals("Deleting a cluster should reduce the cluster list by 1",
                1, clusterService.listClusters().size());
        
        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Cluster with the specified ID not found: ID = " + clusterId);
        
        // Get the cluster after deleting to make sure it is actually gone
        clusterService.getCluster(clusterId);
    }
    
    @Test
    public void deleteClusterWithInvalidId() {
        String clusterId = "invalid-id";
        
        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Cluster with the specified ID not found: ID = " + clusterId);
        
        // Get the cluster after deleting to make sure it is actually gone
        clusterService.deleteCluster(clusterId);
    }
    
    @Test
    public void updateClusterWithValidId() {
        Cluster updatedCluster = new Cluster("test", "Updated Cluster");
        
        clusterService.updateCluster(updatedCluster.getId(), updatedCluster);
        
        assertEquals("Update cluster should overwrite existing cluster and still have count of 2",
                2, clusterService.listClusters().size());
        
        assertEquals("New cluster should override existing cluster with same id",
                updatedCluster.getDisplayName(), clusterService.getCluster(
                        updatedCluster.getId()).getDisplayName());
    }
    
    @Test
    public void updateClusterWithInvalidId() {
        Cluster updatedCluster = new Cluster("invalid", "Updated Cluster");
        
        expectedEx.expect(EntityNotFoundException.class);
        expectedEx.expectMessage("Cluster with the specified ID not found: ID = " 
                + updatedCluster.getId());
        
        // Get the cluster after deleting to make sure it is actually gone
        clusterService.updateCluster(updatedCluster.getId(), updatedCluster);
    }
}
