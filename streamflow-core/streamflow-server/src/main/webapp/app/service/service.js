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
var serviceModule = angular.module('streamflow.service',
        ['ngResource', 'ui.bootstrap', 'streamflow.notify']);

// Route configuration for the various views
serviceModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
            when('/services', {
                templateUrl: 'app/service/service.tpl.list.html',
                controller: 'ServiceListController'
            }).
            when('/services/kafka/:id', {
                templateUrl: 'app/service/kafka.tpl.view.html',
                controller: 'KafkaViewController'
            });
}]);


////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

serviceModule.factory('KafkaCluster', ['$resource', function($resource) {
    return $resource('api/kafka/clusters/:id', {id: '@id'}, {
        remove: {
            method: 'DELETE'
        }
    });
}]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

serviceModule.controller('ServiceListController', [
    '$scope', '$modal', 'streamflowNotify', 'KafkaCluster',
    function($scope, $modal, streamflowNotify, KafkaCluster) {

        $scope.listKafkaClusters = function() {
            $scope.kafkaClusters = KafkaCluster.query(
                function() {
                    $scope.kafkaMessage = 'No Kafka clusters were found';
                },
                function() {
                    $scope.kafkaMessage = 'Unable to load the Kafka cluster list';
                }
            );
        };

        $scope.createKafkaCluster = function() {
            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/service/kafka.tpl.create.html',
                controller: 'KafkaCreateController'
            });
        };
        
        $scope.deleteKafkaCluster = function(kafkaCluster) {
            $modal.open({
                templateUrl: 'app/service/kafka.tpl.delete.html',
                controller: function($scope) {
                    $scope.kafkaCluster = kafkaCluster;
                }
            }).result.then(function() {
                // Delete the resource entry on the server
                KafkaCluster.remove({id: kafkaCluster.id},
                    function() {
                        streamflowNotify.success('The Kafka cluster was deleted successfully');
                    },
                    function() {
                        streamflowNotify.error('The Kafka cluster was not deleted due to a server error.');
                    }
                );
        
                $scope.listKafkaClusters();
            });
        };

        $scope.listKafkaClusters();
    }
]);

serviceModule.controller('KafkaViewController', [
    '$scope', '$routeParams', '$modal', 'streamflowNotify', 'KafkaCluster',
    function($scope, $routeParams, $modal, streamflowNotify, KafkaCluster) {
        $scope.kafkaCluster = KafkaCluster.get({id: $routeParams.id});
    }
]);

/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
serviceModule.controller('KafkaCreateController', [
    '$scope', '$location', '$modalInstance', 'streamflowNotify', 'KafkaCluster',
    function($scope, $location, $modalInstance, streamflowNotify, KafkaCluster) {
        
        $scope.register = function() {
            // Create the new topology using the specified paramters from the dialog
            var kafkaCluster = new KafkaCluster();
            kafkaCluster.name = $scope.name;
            kafkaCluster.zookeeperUri = $scope.zookeeperUri;

            kafkaCluster.$save({},
                function(kafkaCluster) {
                    // After the topology is created, redirect the user to the new empty topology view
                    $location.path('/services/kafka/' + kafkaCluster.id);

                    // Close the dialog after successful creation
                    $modalInstance.close();
                },
                function() {
                    streamflowNotify.error('Unable to register the Kafka cluster due to server error: ', 'error');
                }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);


/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
propertyModule.controller('KafkaAddTopicController', [
    '$scope', '$modalInstance', 'KafkaCluster',
    function($scope, $modalInstance, KafkaCluster) {
        $scope.clusters = KafkaCluster.query();
        
        $scope.create = function() {
            // Create the new topology using the specified paramters from the dialog
            var kafkaTopic = new KafkaTopic();
            kafkaTopic.clusterId = $scope.clusterId;
            kafkaTopic.name = $scope.name;
            kafkaTopic.numPartitions = $scope.numPartitions;
            kafkaTopic.replicationFactor = $scope.replicationFactor;

            kafkaTopic.$save(
                function(kafkaTopic) {
                    // Close the dialog after successful creation
                    $modalInstance.close(kafkaTopic);
                }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);


////////////////////////////////////////////////////////////////////////////////
// DIRECTIVES
////////////////////////////////////////////////////////////////////////////////
