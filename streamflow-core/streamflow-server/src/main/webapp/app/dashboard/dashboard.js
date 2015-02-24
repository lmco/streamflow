/*
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
var dashboardModule = angular.module('streamflow.dashboard', [
    'ngResource', 'ui.bootstrap', 'streamflow.notify', 'streamflow.topology'
]);

// Route configuration for the various views
dashboardModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
        when('/dashboard', {
            templateUrl: 'app/dashboard/dashboard.tpl.html',
            controller: 'DashboardController'
        }).
        when('/dashboard/clusters/:clusterId', {
            templateUrl: 'app/dashboard/dashboard.tpl.cluster.html',
            controller: 'DashboardClusterController'
        });
}]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

dashboardModule.controller('DashboardController', [
    '$scope', 'streamflowNotify', 'TopologyService', 'Topology', 'Cluster',
    function($scope, streamflowNotify, TopologyService, Topology, Cluster) {

        $scope.listTopologies = function() {
            $scope.activeTopologies = [];
            $scope.killedTopologies = [];

            // Reset the messages to prevent a flicker of the empty list message
            $scope.activeMessage = '';
            $scope.killedMessage = '';

            // Retrieve the list of topologies from the server
            $scope.topologies = Topology.query(
                function(topologies) {
                    // Iterate over each of the topologies to retrieve it's updated statuss
                    angular.forEach(topologies, function(topology) {
                        if (topology.status === 'ACTIVE') {
                            $scope.activeTopologies.push(topology);
                        }
                        else if (topology.status === 'KILLED') {
                            $scope.killedTopologies.push(topology);
                        }
                    });

                    if ($scope.activeTopologies.length === 0) {
                        $scope.activeMessage = 'No active topologies were found';
                    }
                    if ($scope.killedTopologies.length === 0) {
                        $scope.killedMessage = 'No killed topologies were found';
                    }
                },
                function() {
                    $scope.activeMessage = 'Unable to load the active topology list';
                    $scope.killedMessage = 'Unable to load the killed topology list';
                }
            );
        };

        $scope.submitTopology = function(topology) {
            TopologyService.submitTopology(topology,
                function() {
                    $scope.listTopologies();
                }
            );
        };

        $scope.killTopology = function(topology) {
            TopologyService.killTopology(topology,
                function() {
                    $scope.listTopologies();
                },
                function() {
                    $scope.listTopologies();
                }
            );
        };

        $scope.clearTopology = function(topology) {
            TopologyService.clearTopology(topology,
                function() {
                    streamflowNotify.success('The topology was cleared successfully.');

                    $scope.listTopologies();
                },
                function() {
                    streamflowNotify.error('The topology was not cleared due to a server error.');
                }
            );
        };

        $scope.deleteTopology = function(topology) {
            TopologyService.deleteTopology(topology,
                function() {
                    streamflowNotify.success('The topology was deleted successfully.');

                    $scope.listTopologies();
                },
                function() {
                    streamflowNotify.error('The topology was not deleted due to a server error.');
                }
            );
        };

        $scope.listClusters = function() {
            $scope.clusters = Cluster.query(function(clusters) {
                angular.forEach(clusters, function(cluster) {
                    cluster.summary = Cluster.summary({id: cluster.id}, 
                        function(clusterSummary) {
                            cluster.status = 'CONNECTED';

                            cluster.summary.numSupervisors = clusterSummary.supervisors.length;
                            cluster.summary.usedSlots = 0;
                            cluster.summary.freeSlots = 0;
                            cluster.summary.totalSlots = 0;

                            // Iterate over each of the supervisors to get global worker count
                            angular.forEach(clusterSummary.supervisors, function(supervisorSummary) {
                                cluster.summary.usedSlots += supervisorSummary.numUsedWorkers;
                                cluster.summary.totalSlots += supervisorSummary.numWorkers;
                                cluster.summary.freeSlots += supervisorSummary.numWorkers - supervisorSummary.numUsedWorkers;
                            });

                            cluster.summary.executors = 0;
                            cluster.summary.tasks = 0;

                            // Iterate over each of the topologies to get global executor/task count
                            angular.forEach(clusterSummary.topologies, function(topologySummary) {
                                cluster.summary.executors += topologySummary.numExecutors;
                                cluster.summary.tasks += topologySummary.numTasks;
                            });
                        },
                        function() {
                            cluster.status = 'DISCONNECTED';
                            cluster.summary.nimbusUptimeSecs = '---';
                            cluster.summary.numSupervisors = '---';
                            cluster.summary.usedSlots = '---';
                            cluster.summary.freeSlots = '---';
                            cluster.summary.executors = '---';
                            cluster.summary.tasks = '---';
                        }
                    );
                });
            });
        };

        $scope.listTopologies();
        $scope.listClusters();
    }
]);

dashboardModule.controller('DashboardClusterController', [
    '$scope', '$routeParams', 'Cluster', 
    function($scope, $routeParams, Cluster) {
        $scope.cluster = Cluster.get({id: $routeParams.clusterId});
        $scope.clusterSummary = Cluster.summary({id: $routeParams.clusterId}, 
            function(clusterSummary) {
                $scope.clusterSummary.numSupervisors = clusterSummary.supervisors.length;
                $scope.clusterSummary.usedSlots = 0;
                $scope.clusterSummary.freeSlots = 0;
                $scope.clusterSummary.totalSlots = 0;

                // Iterate over each of the supervisors to get global worker count
                angular.forEach(clusterSummary.supervisors, function(supervisorSummary) {
                    $scope.clusterSummary.usedSlots += supervisorSummary.numUsedWorkers;
                    $scope.clusterSummary.totalSlots += supervisorSummary.numWorkers;
                    $scope.clusterSummary.freeSlots += supervisorSummary.numWorkers - supervisorSummary.numUsedWorkers;
                });

                $scope.clusterSummary.executors = 0;
                $scope.clusterSummary.tasks = 0;

                // Iterate over each of the topologies to get global executor/task count
                angular.forEach(clusterSummary.topologies, function(topologySummary) {
                    $scope.clusterSummary.executors += topologySummary.numExecutors;
                    $scope.clusterSummary.tasks += topologySummary.numTasks;
                });

                // Parse the nimbus conf which is stored as a single json string
                $scope.clusterSummary.nimbusConf = angular.fromJson(clusterSummary.nimbusConf);
                $scope.cluster.status = 'CONNECTED';
            },
            function() {
                $scope.clusterSummary.numSupervisors = '---';
                $scope.clusterSummary.nimbusUptimeSecs = '---';
                $scope.clusterSummary.usedSlots = '---';
                $scope.clusterSummary.freeSlots = '---';
                $scope.clusterSummary.totalSlots = '---';
                $scope.clusterSummary.executors = '---';
                $scope.clusterSummary.tasks = '---';
                $scope.clusterSummary.supervisors = [];
                $scope.clusterSummary.topologies = [];
                $scope.clusterSummary.nimbusConf = {};
                $scope.cluster.status = 'DISCONNECTED';
            }
        );
    }
]);