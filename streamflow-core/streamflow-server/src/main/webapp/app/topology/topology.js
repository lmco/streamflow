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
var topologyModule = angular.module('streamflow.topology', [
    'ngResource', 'ui.bootstrap', 'streamflow.framework', 'streamflow.notify'
]);

// Route configuration for the various views
topologyModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
        when('/topologies', {
            templateUrl: 'app/topology/topology.tpl.list.html',
            controller: 'TopologyListController'
        }).
        when('/topologies/:id', {
            templateUrl: 'app/topology/topology.tpl.view.html',
            controller: 'TopologyViewController'
        }).
        when('/topologies/:id/log/local', {
            templateUrl: 'app/topology/topology.tpl.log.local.html',
            controller: 'TopologyLocalLogController'
        }).
        when('/topologies/:id/log/cluster', {
            templateUrl: 'app/topology/topology.tpl.log.cluster.html',
            controller: 'TopologyClusterLogController'
        }).
        when('/topologies/:id/metrics', {
            templateUrl: 'app/topology/topology.tpl.metrics.html',
            controller: 'TopologyMetricsController'
        });
}]);


////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

/**
 * Resource used to handle all actions relevant to a topology entity
 */
topologyModule.factory('Topology', ['$resource', function($resource) {
    return $resource('api/topologies/:id', {id: '@id'}, {
        update: {
            method: 'PUT'
        },
        remove: {
            method: 'DELETE'
        },
        status: {
            method: 'GET', 
            url: 'api/topologies/:id/status'
        },
        clear: {
            method: 'GET', 
            url: 'api/topologies/:id/clear'
        },
        submit: {
            method: 'GET', 
            url: 'api/topologies/:id/submit', 
            params: {
                clusterId: 'LOCAL'
            }
        },
        kill: {
            method: 'GET', 
            url: 'api/topologies/:id/kill',
            params: {
                waitTimeSecs: 0
            }
        },
        log: {
            method: 'GET', 
            url: 'api/topologies/:id/log'
        },
        logSearch: {
            method: 'POST', 
            url: 'api/topologies/:id/log'
        },
        info: {
            method: 'GET', 
            url: 'api/topologies/:id/info'
        },
        updateCurrentConfig: {
            method: 'PUT', 
            url: 'api/topologies/:id/config'
        },
        getCurrentConfig: {
            method: 'GET', 
            url: 'api/topologies/:id/config'
        },
        getDeployedConfig: {
            method: 'GET', 
            url: 'api/topologies/:id/config/deployed'
        }
    });
}]);

/**
 * Resource used retrieve a list of available clusters
 */
topologyModule.factory('Cluster', ['$resource', function($resource) {
    return $resource('api/clusters/:id', {id: '@id'}, {
        summary: {method: 'GET', url: 'api/clusters/:id/summary'}
    });
}]);

/**
 * Service to work with reusable Topology related resource functionality
 */
topologyModule.service('TopologyService', ['$modal', 'Topology',
    function($modal, Topology) {

        this.createTopology = function(successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.create.html',
                controller: 'TopologyCreateController'
            }).result.then(
                function(topology) {
                    if (topology) {
                        if (angular.isFunction(successCallback)) {
                            successCallback(topology);
                        }
                    } else {
                        if (angular.isFunction(errorCallback)) {
                            errorCallback();
                        }
                    }
                }
            );
        };

        this.importTopology = function(successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.import.html',
                controller: 'TopologyImportController'
            }).result.then(
                function() {
                    if (angular.isFunction(successCallback)) {
                        successCallback();
                    }
                }
            );
        };

        this.deleteTopology = function(topology, successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.delete.html',
                controller: function($scope) {
                    $scope.topology = topology;
                }
            }).result.then(
                function() {
                    Topology.remove({id: topology.id},
                        function() {
                            if (angular.isFunction(successCallback)) {
                                successCallback();
                            }
                        },
                        function() {
                            if (angular.isFunction(errorCallback)) {
                                errorCallback();
                            }
                        }
                    );
                }
            );
        };

        this.killTopology = function(topology, successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.kill.html',
                controller: 'TopologyKillController',
                resolve: {
                    topology: function() {
                        return angular.copy(topology);
                    },
                    successCallback: function() {
                        return successCallback;
                    },
                    errorCallback: function() {
                        return errorCallback;
                    }
                }
            });
        };

        this.clearTopology = function(topology, successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.clear.html',
                controller: function($scope) {
                    $scope.topology = topology;
                }
            }).result.then(
                function() {
                    Topology.clear({id: topology.id},
                        function() {
                            if (angular.isFunction(successCallback)) {
                                successCallback();
                            }
                        },
                        function() {
                            if (angular.isFunction(errorCallback)) {
                                errorCallback();
                            }
                        }
                    );
                }
            );
        };

        this.submitTopology = function(topology, successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.submit.html',
                controller: 'TopologyDeployController',
                resolve: {
                    topology: function() {
                        return angular.copy(topology);
                    },
                    successCallback: function() {
                        return successCallback;
                    },
                    errorCallback: function() {
                        return errorCallback;
                    }
                }
            });
        };
    }]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

/**
 * Topology controller used to manage the list of all topologies
 */
topologyModule.controller('TopologyListController', [
    '$scope', '$location', 'streamflowNotify', 'TopologyService', 'Topology',
    function($scope, $location, streamflowNotify, TopologyService, Topology) {
        $scope.listTopologies = function() {
            // Retrieve the list of topologies from the server
            $scope.topologies = Topology.query(
                function() {
                    if ($scope.topologies.length === 0) {
                        $scope.topologiesMessage = 'No topologies were found';
                    }
                },
                function() {
                    $scope.topologiesMessage = 'Unable to load the topology list';
                }
            );
        };

        $scope.createTopology = function() {
            TopologyService.createTopology(
                function(topology) {
                    // After the topology is created, redirect the user to the new empty topology view
                    $location.path('/topologies/' + topology.id);
                },
                function(response) {
                    streamflowNotify.error('Unable to create new topology. ' + response.data);
                }
            );
        };

        $scope.importTopology = function() {
            TopologyService.importTopology(
                function(topology) {
                    $scope.listTopologies();
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

        $scope.submitTopology = function(topology) {
            TopologyService.submitTopology(topology,
                function() {
                    $scope.listTopologies();
                }
            );
        };

        $scope.listTopologies();
    }
]);

/**
 * Topology controller used to handle editing of a single topology in the canvas
 */
topologyModule.controller('TopologyViewController', [
    '$scope', '$routeParams', '$window', '$location', '$modal',
        'streamflowNotify', 'TopologyService', 'Topology',
    function($scope, $routeParams, $window, $location, $modal,
        streamflowNotify, TopologyService, Topology) {
            
        $scope.topologyLoaded = false;
        $scope.topologyFailed = false;
        $scope.topologyState = {
            isModified: false
        };

        $scope.topology = Topology.get({id: $routeParams.id}, 
            function(topology) {
                $scope.topologyLoaded = true;
                $scope.topologyState.type = topology.type;
            },
            function() {
                $scope.topologyLoaded = true;
                $scope.topologyFailed = true;
            }
        );

        $scope.topologyConfig = Topology.getCurrentConfig({id: $routeParams.id});

        $scope.saveTopology = function() {
            // Post the update topology config to the server
            Topology.updateCurrentConfig({id: $routeParams.id}, $scope.topologyConfig,
                function() {
                    streamflowNotify.success('The topology was saved successfully.');

                    // Reset the modified state since the save was successful
                    $scope.topologyState.isModified = false;
                },
                function() {
                    streamflowNotify.error('The topology was not saved due to a server error..');
                }
            );
        };

        $scope.submitTopology = function() {
            // Check if the topology has been modified
            if ($scope.topologyState.isModified) {
                $modal.open({
                    templateUrl: 'app/topology/topology.tpl.modified.html',
                    controller: function($scope) {
                        $scope.topology = Topology.get({id: $routeParams.id});
                    }
                }).result.then(
                    function(result) {
                        // Save the topology before submitted if necessary
                        if (result === 'SAVE') {
                            $scope.saveTopology();
                        }

                        // As long as the selection was not CANCEL, submit the topology
                        if (result === 'SAVE' || result === 'IGNORE') {
                            // Once saved, submit the topology
                            TopologyService.submitTopology($scope.topology,
                                function() {
                                    $scope.topology = Topology.get({id: $routeParams.id});
                                }
                            );
                        }
                    }
                );
            } else {
                // There were no unsaved changes to the topology so load the submit dialog immediately
                TopologyService.submitTopology($scope.topology,
                    function() {
                        $scope.topology = Topology.get({id: $routeParams.id});
                    }
                );
            }
        };

        $scope.killTopology = function() {
            TopologyService.killTopology($scope.topology,
                function() {
                    $scope.topology = Topology.get({id: $routeParams.id});
                },
                function() {
                    $scope.topology = Topology.get({id: $routeParams.id});
                }
            );
        };

        $scope.clearTopology = function() {
            TopologyService.clearTopology($scope.topology,
                function() {
                    streamflowNotify.success('The topology was cleared successfully.');

                    $scope.topology = Topology.get({id: $routeParams.id});
                },
                function() {
                    streamflowNotify.error('The topology was not cleared due to a server error.');
                }
            );
        };
        
        $scope.loadTopologyProperties = function() {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.config.html',
                controller: 'TopologyPropertiesController',
                resolve: {
                    topologyProperties: function() {
                        return angular.copy($scope.topologyConfig.properties);
                    }
                }
            }).result.then(
                function(topologyProperties) {
                    // A new properties object was generated by editing properties
                    if (topologyProperties) {
                        // Update the component config properties with the new values
                        $scope.topologyConfig.properties = topologyProperties;

                        $scope.topologyState.isModified = true;
                    }
                }
            );
        };

        // Add watch event to capture attempts to browse away from the topology
        var changeEvent = $scope.$on('$locationChangeStart', function(event, next, current) {
            // Check if the topology has been modified
            if ($scope.topologyState.isModified) {
                // Prevent default action which is to change pages
                event.preventDefault();
                
                $modal.open({
                    templateUrl: 'app/topology/topology.tpl.modified.html'
                }).result.then(
                    function(result) {
                        if (result === 'SAVE') {
                            // Cancel the change event so this message box will not reappear inifinitely
                            changeEvent();

                            // Post the update topology config to the server
                            Topology.updateCurrentConfig({id: $routeParams.id}, $scope.topologyConfig,
                                function() {
                                    // Change to the specified target page 
                                    // (needed because nextUrl is provided as absolute URL)
                                    $window.location.href = next;
                                },
                                function() {
                                    streamflowNotify.error('The topology was not saved due to a server error.');
                                }
                            );
                        } else if (result === 'IGNORE') {
                            // Cancel the change event so this message box will not reappear inifinitely
                            changeEvent();

                            // Change to the specified target page (needed because nextUrl is provided as absolute URL)
                            $window.location.href = next;
                        }
                    }
                );
            }
        });
    }
]);

/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
topologyModule.controller('TopologyPropertiesController', [
    '$scope', '$modal', '$modalInstance', 'topologyProperties', 
    function($scope, $modal, $modalInstance, topologyProperties) {
        $scope.topologyProperties = topologyProperties;
        if (!$scope.topologyProperties) {
            $scope.topologyProperties = [];
        }
        
        $scope.newProperty = function() {
            $modal.open({
                templateUrl: 'app/topology/topology.tpl.config.new.html',
                controller: 'TopologyPropertiesNewController'
            }).result.then(
                function(topologyProperty) {
                    if (topologyProperty) {
                        $scope.topologyProperties.push(topologyProperty);
                    }
                }
            );
        };
        
        $scope.removeProperty = function(topologyProperty) {
            var index = $scope.topologyProperties.indexOf(topologyProperty);
            $scope.topologyProperties.splice(index, 1);   
        };

        $scope.apply = function() {
            // Close the dialog with the updated connector settings
            $modalInstance.close($scope.topologyProperties);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);

/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
topologyModule.controller('TopologyPropertiesNewController', [
    '$scope', '$modalInstance', function($scope, $modalInstance) {
        $scope.topologyProperty = null;
        
        $scope.propertyKeys = [
            'storm.messaging.netty.server_worker_threads',
            'storm.messaging.netty.client_worker_threads',
            'storm.messaging.netty.buffer_size',
            'storm.messaging.netty.max_retries',
            'storm.messaging.netty.max_wait_ms',
            'storm.messaging.netty.min_wait_ms',
            'storm.messaging.netty.transfer.batch.size',
            'storm.messaging.netty.socket.backlog',
            'storm.messaging.netty.flush.check.interval.ms',
            'storm.messaging.netty.authentication',
            'topology.enable.message.timeouts',
            'topology.debug',
            'topology.workers',
            'topology.acker.executors',
            'topology.tasks',
            'topology.message.timeout.secs',
            'topology.multilang.serializer',
            'topology.skip.missing.kryo.registrations',
            'topology.max.task.parallelism',
            'topology.max.spout.pending',
            'topology.state.synchronization.timeout.secs',
            'topology.stats.sample.rate',
            'topology.builtin.metrics.bucket.size.secs',
            'topology.fall.back.on.java.serialization',
            'topology.worker.childopts',
            'topology.executor.receive.buffer.size',
            'topology.executor.send.buffer.size',
            'topology.receiver.buffer.size',
            'topology.transfer.buffer.size',
            'topology.tick.tuple.freq.secs',
            'topology.worker.shared.thread.pool.size',
            'topology.disruptor.wait.strategy',
            'topology.spout.wait.strategy',
            'topology.sleep.spout.wait.strategy.time.ms',
            'topology.error.throttle.interval.secs',
            'topology.max.error.report.per.interval',
            'topology.kryo.factory',
            'topology.tuple.serializer',
            'topology.trident.batch.emit.interval.millis',
            'storm.group.mapping.service.cache.duration.secs',
            'topology.classpath',
            'topology.environment',
            'topology.bolts.outgoing.overflow.buffer.enable'
        ];
        
        $scope.add = function() {
            $modalInstance.close($scope.topologyProperty);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);

/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
topologyModule.controller('TopologyCreateController', [
    '$scope', '$modalInstance', 'Topology',
    function($scope, $modalInstance, Topology) {
        
        $scope.create = function() {
            // Create the new topology using the specified paramters from the dialog
            var topology = new Topology();
            topology.name = $scope.topologyName;
            topology.description = $scope.topologyDescription;
            topology.type = $scope.topologyType;

            topology.$save(
                function(topology) {
                    // Close the dialog after successful creation
                    $modalInstance.close(topology);
                }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);

topologyModule.controller('TopologyKillController', [
    '$scope', '$modalInstance', 'topology', 'successCallback', 'errorCallback', 'Topology',
    function($scope, $modalInstance, topology, successCallback, errorCallback, Topology) {
        $scope.topology = topology;
        $scope.waitTimeSecs = 0;
        $scope.state = 'INITIALIZED';
        $scope.message = null;
        $scope.cancelHandle;

        $scope.isStarted = function() {
            return ($scope.state !== 'INITIALIZED');
        };

        $scope.isActive = function() {
            return ($scope.state === 'ACTIVE');
        };

        $scope.isFinished = function() {
            return ($scope.state === 'COMPLETED'
                    || $scope.state === 'CANCELED'
                    || $scope.state === 'FAILED');
        };
        
        $scope.isValid = function() {
            return angular.isNumber($scope.waitTimeSecs) && $scope.waitTimeSecs >= 0;
        };

        $scope.kill = function() {
            $scope.state = 'ACTIVE';

            // Execute the ajax request to submit the topology to Storm
            Topology.kill({id: $scope.topology.id, waitTimeSecs: $scope.waitTimeSecs},
                function() {
                    $scope.state = 'COMPLETED';
                    $scope.message = 'The topology was killed successfully!';

                    if (angular.isFunction(successCallback)) {
                        successCallback();
                    }
                },
                function() {
                    $scope.state = 'FAILED';
                    $scope.message = 'The topology kill failed!';

                    if (angular.isFunction(errorCallback)) {
                        errorCallback();
                    }
                }
            );
        };

        $scope.close = function() {
            $modalInstance.dismiss();
        };

        $scope.cancel = function() {
            ////////////////////////////////////////////////////////////////////////
            // TODO: NEED WAY TO CANCEL ACTIVE TOPOLOGY SUBMISSION
            ////////////////////////////////////////////////////////////////////////

            $scope.state = 'CANCELED';
            $scope.message = 'The topology kill was cancelled!';
        };
    }
]);

/**
 * Topology controller used int he topology-deploy dialog to submit a topology to Storm
 */
topologyModule.controller('TopologyDeployController', [
    '$scope', '$modalInstance', 'topology', 'successCallback', 'errorCallback', 'Topology', 'Cluster',
    function($scope, $modalInstance, topology, successCallback, errorCallback, Topology, Cluster) {
        $scope.clusters = Cluster.query();
        $scope.cluster = null;
        $scope.logLevel = 'INFO';
        $scope.classLoaderPolicy = 'FRAMEWORK_FIRST';
        $scope.topology = topology;
        $scope.state = 'INITIALIZED';
        $scope.message = null;
        $scope.cancelHandle;

        $scope.isStarted = function() {
            return ($scope.state !== 'INITIALIZED');
        };

        $scope.isActive = function() {
            return ($scope.state === 'ACTIVE');
        };

        $scope.isFinished = function() {
            return ($scope.state === 'COMPLETED'
                    || $scope.state === 'CANCELED'
                    || $scope.state === 'FAILED');
        };

        $scope.submit = function() {
            $scope.state = 'ACTIVE';

            // Execute the ajax request to submit the topology to Storm
            Topology.submit({
                    id: $scope.topology.id, 
                    clusterId: $scope.cluster.id,
                    logLevel: $scope.logLevel,
                    classLoaderPolicy: $scope.classLoaderPolicy
                },
                function() {
                    $scope.state = 'COMPLETED';
                    $scope.message = 'The Topology was submitted successfully!';

                    if (angular.isFunction(successCallback)) {
                        successCallback();
                    }
                },
                function() {
                    $scope.state = 'FAILED';
                    $scope.message = 'The Topology submission failed!';

                    if (angular.isFunction(errorCallback)) {
                        errorCallback();
                    }
                }
            );
        };

        $scope.close = function() {
            $modalInstance.dismiss();
        };

        $scope.cancel = function() {
            ////////////////////////////////////////////////////////////////////////
            // TODO: NEED WAY TO CANCEL ACTIVE TOPOLOGY SUBMISSION
            ////////////////////////////////////////////////////////////////////////

            $scope.state = 'CANCELED';
            $scope.message = 'The Topology deploy was cancelled!';
        };
    }
]);

topologyModule.controller('TopologyImportController', [
    '$scope', '$modalInstance', 'successCallback', 'errorCallback',
    function($scope, $modalInstance, successCallback, errorCallback) {
        // Set the default state of the import dialog
        $scope.mainClass = 'streamflow.topology.TopologyDriver';
        $scope.importState = 'INITIALIZED';
        $scope.importProgress = 0;
        $scope.importMessage = null;
        $scope.importFile = 'No topology selected...';
        $scope.submitHandle = null;
        $scope.cancelHandle = null;

        $scope.isStarted = function() {
            return ($scope.importState !== 'INITIALIZED');
        };

        $scope.isActive = function() {
            return ($scope.importState === 'ACTIVE');
        };

        $scope.isFinished = function() {
            return ($scope.importState === 'COMPLETED'
                    || $scope.importState === 'CANCELED'
                    || $scope.importState === 'FAILED');
        };

        $scope.choose = function() {
            // Trigger a click of the file input box
            $('#topology_file_import').trigger('click');
        };

        $scope.upload = function() {
            $scope.importState = 'ACTIVE';

            // Add the form data for the topology name and main class to the request
            $scope.submitHandle.formData = {
                name: $scope.name,
                mainClass: $scope.mainClass
            };

            // Call submit to start the data handle
            $scope.cancelHandle = $scope.submitHandle.submit();
        };

        $scope.close = function() {
            $modalInstance.dismiss();
        };

        $scope.cancel = function() {
            $scope.cancelHandle.abort();

            $scope.importState = 'CANCELED';
            $scope.importMessage = 'Topology import cancelled. ';
        };

        $scope.reset = function() {
            $scope.importState = 'INITIALIZED';
            $scope.importProgress = 0;
            $scope.importFile = 'No topology selected...';
        };

        $scope.add = function(e, data) {
            // Save a handle to the data object so submission can occur later
            $scope.submitHandle = data;

            if (data.files.length > 0) {
                $scope.importFile = data.files[0].name;
            }
        };

        $scope.done = function(e, data) {
            $scope.importState = 'COMPLETED';
            $scope.importMessage = 'Topology import completed successfully.';

            if (angular.isFunction(successCallback)) {
                successCallback();
            }
        };

        $scope.fail = function(e, data) {
            $scope.importState = 'FAILED';
            $scope.importMessage = 'Topology import failed. ' + data.errorThrown;

            if (angular.isFunction(errorCallback)) {
                errorCallback();
            }
        };

        $scope.progressall = function(e, data) {
            $scope.importProgress = parseInt(data.loaded / data.total * 100, 10);
        };
    }
]);

/**
 * Topology controller used in the topology-create modal to create a new topology
 */
topologyModule.controller('TopologyGroupingController', [
    '$scope', '$modalInstance', 'connector', 'topologyType',
    function($scope, $modalInstance, connector, topologyType) {
        $scope.connector = connector;
        $scope.topologyType = topologyType;

        $scope.apply = function() {
            // Close the modal with the updated connector settings
            $modalInstance.close(connector);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);

/**
 * Topology controller used to manage the draggable component palette 
 */
topologyModule.controller('TopologyPaletteController', [
    '$scope', 'Component', function($scope, Component) {
        $scope.groupBy = 'FRAMEWORK';
        $scope.visibility = 'ALL';
        $scope.topologyType;

        $scope.refreshComponents = function() {
            $scope.components = Component.query(
                    {visibility: $scope.visibility}, function() {
                $scope.processComponents();
            });
        };

        $scope.processComponents = function() {
            $scope.componentsProcessed = {};

            if ($scope.groupBy === 'FRAMEWORK') {
                // Update the filtered component list based on the new filter mode
                angular.forEach($scope.components, function(component) {
                    var showComponent = false;
                    var componentType = component.type;
                    
                    if ($scope.topology.type === 'STANDARD') {
                        if (componentType === 'storm-spout' || componentType === 'storm-bolt') {
                            showComponent = true;
                        }
                    } else if ($scope.topology.type === 'TRIDENT') {
                        if (componentType !== 'storm-bolt') {
                            showComponent = true;
                        }
                    }
                    
                    if (showComponent) {
                        if (!(component.frameworkLabel in $scope.componentsProcessed)) {
                            $scope.componentsProcessed[component.frameworkLabel] = [];
                        }

                        $scope.componentsProcessed[component.frameworkLabel].push(component);
                    }
                });
                
            } else if ($scope.groupBy === 'TYPE') {
                // Update the filtered component list based on the new filter mode
                angular.forEach($scope.components, function(component) {
                    // Clean up the component type label to something more readable
                    var showComponent = false;
                    var componentType = component.type;
                    
                    if ($scope.topology.type === 'STANDARD') {
                        if (componentType === 'storm-spout' || componentType === 'storm-bolt') {
                            showComponent = true;
                            
                            if (componentType === 'storm-spout') {
                                componentType = 'Spouts';
                            } else if (componentType === 'storm-bolt') {
                                componentType = 'Bolts';
                            }
                        }
                    } else if ($scope.topology.type === 'TRIDENT') {
                        if (componentType !== 'storm-bolt') {
                            showComponent = true;
                            
                            if (componentType === 'trident-drpc' ||
                                componentType === 'trident-project' ||
                                componentType === 'trident-merge' ||
                                componentType === 'trident-join' ||
                                componentType === 'trident-chained-agg' ||
                                componentType === 'trident-chain-end') {
                                componentType = 'Core';
                            } else if (componentType === 'storm-spout' ||
                                componentType === 'trident-spout' ||
                                componentType === 'trident-batch-spout' ||
                                componentType === 'trident-partitioned-spout' ||
                                componentType === 'trident-opaque-partitioned-spout') {
                                componentType = 'Spouts';
                            } else if (componentType === 'trident-aggregator' ||
                                componentType === 'trident-combiner-aggregator' ||
                                componentType === 'trident-reducer-aggregator') {
                                componentType = 'Aggregators';
                            } else if (componentType === 'trident-function') {
                                componentType = 'Functions';
                            } else if (componentType === 'trident-filter') {
                                componentType = 'Filters';
                            }
                        }
                    }
                    
                    if (showComponent) {
                        // Add the array for the component type if it does not exist
                        if (!(componentType in $scope.componentsProcessed)) {
                            $scope.componentsProcessed[componentType] = [];
                        }

                        $scope.componentsProcessed[componentType].push(component);
                    }
                });
            }
        };

        $scope.selectGroupBy = function(groupBy) {
            $scope.groupBy = groupBy;

            $scope.processComponents();
        };

        $scope.hasGroupBy = function(groupBy) {
            return $scope.groupBy === groupBy;
        };

        $scope.selectVisibility = function(visibility) {
            $scope.visibility = visibility;

            $scope.refreshComponents();
        };

        $scope.hasVisibility = function(visibility) {
            return $scope.visibility === visibility;
        };
        
        // Wait until the topology is fully loaded to load the components so topology type is satisfied
        $scope.$watch('topologyLoaded', function() {
            if ($scope.topology.type) {
                $scope.refreshComponents();
            }
        });
    }
]);

/**
 * Topology controller used to view log data for a topology 
 */
topologyModule.controller('TopologyLocalLogController', [
    '$scope', '$routeParams', 'Topology', 
    function($scope, $routeParams, Topology) {
        $scope.offset = 0;
        $scope.limit = 100;
        $scope.lines = [];

        $scope.updateLog = function() {
            // Retrieve the specified log data
            Topology.log({id: $routeParams.id, offset: $scope.offset, limit: $scope.limit},
                function(topologyLog) {
                    // Update the offset for the next pass based on the returned line count
                    $scope.offset += topologyLog.count;

                    if (topologyLog.count > 0) {
                        // Iterate over all of the lines and append them to the data array
                        angular.forEach(topologyLog.lines, function(line) {
                            $scope.lines.push(line);
                        });

                        // Progressively load more log data until there is no more
                        $scope.updateLog();
                    }
                },
                function() {
                    $scope.lines.push('Error retrieving the log updates for this topology');
                }
            );
        };

        // Retrieve the topology properties and update the log once retrieved
        $scope.topology = Topology.get({id: $routeParams.id}, function() {
            $scope.updateLog();
        });
    }
]);

/**
 * Topology controller used to view log data for a topology 
 */
topologyModule.controller('TopologyClusterLogController', [
    '$scope', '$routeParams', 'Topology', 
    function($scope, $routeParams, Topology) {
        $scope.topology = Topology.get({id: $routeParams.id});
        
        $scope.criteria = {
            query: null,
            component: null,
            level: null,
            category: null,
            pageNum: 1,
            pageSize: 25,
            sortOrder: 'DESC'
        };
        
        $scope.updateLog = function(pageNum) {
            $scope.logPage = null;
            $scope.criteria.pageNum = pageNum;
            
            Topology.logSearch({id: $routeParams.id}, $scope.criteria,
                function(logPage) {
                    $scope.logPage = logPage;
                }, 
                function() {
                    $scope.logMessage = "Unable to retrieve topology log data";
                }
            );
        };
        
        // Pull the initial page of log data
        $scope.updateLog(1);
    }
]);

/**
 * Topology controller used to view metrics data for a topology
 */
topologyModule.controller('TopologyMetricsController', [
    '$scope', '$routeParams', 'Topology', function($scope, $routeParams, Topology) {
        $scope.topologyLoaded = false;
        $scope.topologyFailed = false;
        $scope.topologyState = {
            isModified: false
        };

        $scope.topology = Topology.get({id: $routeParams.id}, 
            function(topology) {
                $scope.topologyLoaded = true;
                $scope.topologyState.type = topology.type;
            },
            function() {
                $scope.topologyLoaded = true;
                $scope.topologyFailed = true;
            }
        );

        $scope.topologyConfig = Topology.getDeployedConfig({id: $routeParams.id});
        $scope.topologyInfo = Topology.info({id: $routeParams.id});
    }
]);

topologyModule.controller('ComponentFieldsController', [
    '$scope', '$modalInstance', 'componentObject',
    function($scope, $modalInstance, componentObject) {
        $scope.componentObject = componentObject;

        $scope.apply = function() {
            // Include the component object in the response so it is updated
            $modalInstance.close(componentObject);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);


////////////////////////////////////////////////////////////////////////////////
// DIRECTIVES
////////////////////////////////////////////////////////////////////////////////

/**
 * Directive for draggable components found in the component palette
 */
topologyModule.directive('streamflowPaletteComponent', function(FrameworkService) {
    return {
        restrict: 'A',
        template:
                '<img class="topology-palette-icon pull-left" width="20" height="20"' +
                        'ng-src="api/components/{{ component.id }}/icon"/>' +
                '<span class="topology-palette-label">{{ component.label }}</span>',
        link: function(scope, element, attrs) {
            // Add the necessary attributes to the palette component for drag add
            element.attr('component-id', scope.component.id);
            element.attr('component-name', scope.component.name);
            element.attr('component-framework', scope.component.framework);
            element.attr('component-label', scope.component.label);
            element.attr('component-type', scope.component.type);

            // Load the framework info for the framework associated with the Component
            element.find('.topology-palette-info').click(function() {
                FrameworkService.loadFrameworkInfo(scope.component.framework);

                // Trigger display of the info modal
                scope.$apply();
            });

            // Allow the palette components to be dragged
            element.draggable({
                cursor: 'move',
                cursorAt: {
                    top: 0,
                    left: 0
                },
                distance: 20,
                revert: 'invalid',
                helper: function() {
                    return $('<img></img>').width(40).height(40)
                        .attr('src', 'api/components/' + scope.component.id + '/icon')
                        .appendTo('body').css('z-index', '20000');
                }
            });
            
            element.disableSelection();
        }
    };
});

/**
 * Directive for handling all major events for the streamflow canvas
 */
topologyModule.directive('streamflowCanvas', function($compile, $timeout, $modal, Component) {
    return {
        restrict: 'A',
        scope: {
            topologyConfig: '=', // Topology config updates dual binded (always up to date)
            topologyDisabled: '@', // "true" if editing is disabled, "false" otherwise
            topologyState: '='
        },
        link: function(scope, element, attrs) {
            // Reusable maps to keep track of active endpoint objects and component configs
            scope.topologyEndpoints = {};
            scope.componentCache = {};
            scope.renderedObjects = {};

            // If the topology config was null initialize the default object
            if (!scope.topologyConfig) {
                scope.topologyConfig = {};
            }

            // If the components object was null then initialize it
            if (!scope.topologyConfig.components) {
                scope.topologyConfig.components = {};
            }

            // If the components object was null then initialize it
            if (!scope.topologyConfig.connectors) {
                scope.topologyConfig.connectors = {};
            }

            scope.isTopologyDisabled = function() {
                return scope.topologyDisabled.toLowerCase() === 'true';
            };
            
            element.click(function() {
                $('.topology-component-selected').removeClass('topology-component-selected');
                $('.topology-component-control').fadeOut(0);
            });

            // Initialize the jsPlumb connectors
            jsPlumb.ready(function() {
                // As this jsPlumb instance can be reused for topologies reset on each pass
                jsPlumb.reset();

                var deleteHtml =
                        '<span class="fa-stack">' +
                            '<i class="fa fa-circle fa-inverse fa-stack-2x"></i>' +
                            '<i class="fa fa-circle-o fa-stack-2x"></i>' +
                            '<i class="fa fa-times fa-stack-1x"></i>' +
                        '</span>';

                // Set the jsPlumb defaults for the canvas
                jsPlumb.importDefaults({
                    Connector: ['Flowchart', {
                        stub: [40, 60], gap: 10, cornerRadius: 5,
                        alwaysRespectStubs: true
                    }],
                    ConnectionOverlays: [
                        ['Arrow', {length: 16, width: 13, location: 1, foldback: 1.0}],
                        ['Label', {id: 'topology-connector-label', label: 'Shuffle',
                            location: 0.5, cssClass: 'topology-connector-label',
                            events: {
                                click: function(label, event) {
                                    var connectorKey = label.component.getParameter('key');
                                    var connector = scope.topologyConfig.connectors[connectorKey];

                                    $modal.open({
                                        templateUrl: 'app/topology/topology.tpl.grouping.html',
                                        controller: 'TopologyGroupingController',
                                        resolve: {
                                            connector: function() {
                                                return connector;
                                            },
                                            topologyType: function() {
                                                return scope.topologyState.type;
                                            }
                                        }
                                    }).result.then(
                                        function(connectorModified) {
                                            if (connectorModified) {
                                                // Update the connector grouping properties with the new values
                                                connector.grouping = connectorModified.grouping;
                                                connector.groupingRef = connectorModified.groupingRef;

                                                // Update the connection grouping label with the active value
                                                label.component.getOverlay('topology-connector-label')
                                                        .setLabel(connectorModified.grouping);

                                                scope.topologyState.isModified = true;
                                            }
                                        }
                                    );

                                    scope.$apply();
                                }
                            }
                        }],
                        ['Label', {label: deleteHtml, id: 'topology-connector-delete-source',
                            location: 0.1, cssClass: 'topology-connector-delete',
                            events: {
                                click: function(label, event) {
                                    // Hide the endpoint labels before deleting the connection
                                    angular.forEach(label.component.endpoints, function(endpoint) {
                                        endpoint.hideOverlay('topology-component-endpoint-label');
                                    });

                                    // Detach the connection if the delete icon is clicked
                                    jsPlumb.detach(label.component);

                                    scope.topologyState.isModified = true;
                                }
                            }

                        }],
                        ['Label', {label: deleteHtml, id: 'topology-connector-delete-target',
                            location: 0.9, cssClass: 'topology-connector-delete',
                            events: {
                                click: function(label, event) {
                                    // Hide the endpoint labels before deleting the connection
                                    angular.forEach(label.component.endpoints, function(endpoint) {
                                        endpoint.hideOverlay('topology-component-endpoint-label');
                                    });

                                    // Detach the connection if the delete icon is clicked
                                    jsPlumb.detach(label.component);

                                    scope.topologyState.isModified = true;
                                }
                            }
                        }]
                    ],
                    PaintStyle: {
                        lineWidth: 4, strokeStyle: '#33485D',
                        outlineWidth: 3, outlineColor: 'transparent'
                    },
                    HoverPaintStyle: {
                        lineWidth: 4, strokeStyle: '#1ABC9C',
                        outlineWidth: 3, outlineColor: '#33485D'
                    },
                    Endpoint: 'Dot',
                    EndpointStyle: {
                        fillStyle: '#33485D', radius: 9, lineWidth: 0
                    },
                    EndpointHoverStyle: {
                        fillStyle: '#1ABC9C', radius: 9, strokeStyle: '#33485D',
                        lineWidth: 3
                    }
                });

                // Do not monitor connection events if the topology is disabled
                if (!scope.isTopologyDisabled()) {
                    // Bind to the connection event to keep the model up to date
                    jsPlumb.bind('connection', function(info) {
                        var sourceKey = info.sourceEndpoint.getParameter('sourceKey'),
                                sourceInterface = info.sourceEndpoint.getParameter('sourceInterface'),
                                targetKey = info.targetEndpoint.getParameter('targetKey'),
                                targetInterface = info.targetEndpoint.getParameter('targetInterface'),
                                grouping = info.connection.getParameter('grouping'),
                                groupingRef = info.connection.getParameter('groupingRef');

                        // Generate a connector key using the input/output keys
                        var connectorKey = sourceKey + sourceInterface + targetKey + targetInterface;

                        // If the connector does not have a grouping, use a default value of shuffle
                        if (!grouping) {
                            if (scope.topologyState.type === 'TRIDENT') {
                                grouping = 'Default';
                                groupingRef = '';
                            } else {
                                grouping = 'Shuffle';
                                groupingRef = '';
                                //info.connection.setParameter('grouping', grouping);
                                //info.connection.setParameter('groupingRef', '');
                            }
                        }
                        info.connection.setParameter('key', connectorKey);

                        // Update the connection grouping label with the active value
                        info.connection.getOverlay('topology-connector-label').setLabel(grouping);

                        // Build a connector object to save the connection
                        var connectorObject = new Object();
                        connectorObject.key = connectorKey;
                        connectorObject.sourceComponentKey = sourceKey;
                        connectorObject.sourceComponentInterface = sourceInterface;
                        connectorObject.targetComponentKey = targetKey;
                        connectorObject.targetComponentInterface = targetInterface;
                        connectorObject.grouping = grouping;
                        connectorObject.groupingRef = groupingRef;

                        // Initialize the connectors object if it is not initialized
                        if (!scope.topologyConfig.connectors) {
                            scope.topologyConfig.connectors = {};
                        }

                        // Add the new connector to the topology config
                        scope.topologyConfig.connectors[connectorKey] = connectorObject;

                        // Show the grouping overlay once the connector is connected
                        info.connection.showOverlay('topology-connector-label');

                        // Bind mouse evnets to the connector to hide the arrow for cosmetic reasons
                        info.connection.bind('mouseenter', function() {
                            info.connection.showOverlay('topology-connector-delete-source');
                            info.connection.showOverlay('topology-connector-delete-target');

                            angular.forEach(info.connection.endpoints, function(endpoint) {
                                //endpoint.showOverlay('topology-component-endpoint-label');
                            });
                        });
                        info.connection.bind('mouseexit', function() {
                            info.connection.hideOverlay('topology-connector-delete-source');
                            info.connection.hideOverlay('topology-connector-delete-target');

                            angular.forEach(info.connection.endpoints, function(endpoint) {
                                endpoint.hideOverlay('topology-component-endpoint-label');
                            });
                        });

                        scope.topologyState.isModified = true;
                    });

                    // Bind to the detach event to keep the model up to date
                    jsPlumb.bind('connectionDetached', function(info) {
                        var sourceKey = info.sourceEndpoint.getParameter('sourceKey'),
                                sourceInterface = info.sourceEndpoint.getParameter('sourceInterface'),
                                targetKey = info.targetEndpoint.getParameter('targetKey'),
                                targetInterface = info.targetEndpoint.getParameter('targetInterface');

                        // Generate a connector key using the input/output keys
                        var connectorKey = sourceKey + sourceInterface + targetKey + targetInterface;

                        // Remove the connector from the topology config permanently
                        delete scope.topologyConfig.connectors[connectorKey];

                        scope.topologyState.isModified = true;
                    });

                    // Bind to the beforeDrop event to prevent duplicate connections from being
                    jsPlumb.bind("beforeDrop", function(info) {
                        var sourceKey = info.connection.endpoints[0].getParameter('sourceKey'),
                                sourceInterface = info.connection.endpoints[0].getParameter('sourceInterface'),
                                targetKey = info.dropEndpoint.getParameter('targetKey'),
                                targetInterface = info.dropEndpoint.getParameter('targetInterface');

                        // Generate a connector key using the input/output keys
                        var connectorKey = sourceKey + sourceInterface + targetKey + targetInterface;

                        // Check if the connection already exists
                        if (scope.topologyConfig.connectors) {
                            if (connectorKey in scope.topologyConfig.connectors) {
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    });
                }

                // Only allow dropping of components if the canvas is enabled
                if (!scope.isTopologyDisabled()) {
                    // Make the canvas a drop zone for components from the palette
                    element.droppable({
                        accept: '.topology-palette-component',
                        drop: function(event, ui) {
                            var dragElement = angular.element(ui.draggable),
                                    dropElement = angular.element(this);

                            // Calculate the real position of the component in the dropped
                            var scrollLeft = dropElement.parent().scrollLeft();
                            var scrollTop = dropElement.parent().scrollTop();
                            var offset = dropElement.parent().offset();

                            var posX = ui.position.left - offset.left + scrollLeft - 50;
                            var posY = ui.position.top - offset.top + scrollTop - 40;

                            if (posX < 0) {
                                posX = 0;
                            }
                            if (posY < 0) {
                                posY = 0;
                            }

                            // Build a new starter component to add to the toology config
                            var componentObject = new Object();
                            componentObject.key = jsPlumbUtil.uuid();
                            componentObject.posX = posX;
                            componentObject.posY = posY;
                            componentObject.framework = dragElement.attr('component-framework');
                            componentObject.name = dragElement.attr('component-name');
                            componentObject.label = dragElement.attr('component-label');
                            componentObject.type = dragElement.attr('component-type');
                            componentObject.parallelism = 1;
                            componentObject.properties = {};

                            // Add the component object to the topology config
                            scope.topologyConfig.components[componentObject.key] = componentObject;

                            // Render the new directive element in the canvas
                            scope.addComponentToCanvas(componentObject);

                            scope.topologyState.isModified = true;

                            // Repaint the connectors to fix offset issue
                            $timeout(function() {
                                jsPlumb.repaintEverything();
                            });
                        }
                    });
                }

                scope.addTopologyToCanvas = function(topologyConfig) {
                    // Retrieve all component configs on initial load to prevent endpoint
                    // resolution error on initial load of a saved topology
                    Component.query({}, function(componentList) {
                        // Add each of the component configs to the component cache
                        angular.forEach(componentList, function(component) {
                            scope.componentCache[component.id] = angular.fromJson(component.config);
                        });

                        // Iterate over the topology components and add them to the canvas
                        angular.forEach(topologyConfig.components, function(componentObject) {
                            scope.addComponentToCanvas(componentObject);
                        });

                        // Iterate over the topology components and add them to the canvas
                        angular.forEach(topologyConfig.connectors, function(connectorObject) {
                            scope.addConnectorToCanvas(connectorObject);
                        });

                        scope.topologyState.isModified = false;

                        // Repaint the connectors to fix offset issue
                        $timeout(function() {
                            jsPlumb.repaintEverything();
                        });
                    });
                };

                scope.addComponentToCanvas = function(componentObject) {
                    var componentId = componentObject.framework + '_' + componentObject.name;

                    // Make sure this component has not already been rendered
                    if (!(componentObject.key in scope.renderedObjects)) {
                        scope.renderedObjects[componentObject.key] = true;

                        // Build the template for the new streamflow-component directive
                        var componentTemplate = angular.element('<div></div>');
                        componentTemplate.attr('streamflow-component', '');
                        componentTemplate.attr('id', componentObject.key);
                        componentTemplate.attr('component-id', componentId);
                        componentTemplate.attr('component-key', componentObject.key);
                        componentTemplate.attr('component-disabled', scope.topologyDisabled);
                        componentTemplate.attr('topology-state', 'topologyState');
                        componentTemplate.attr('topology-config', 'topologyConfig');
                        componentTemplate.attr('topology-endpoints', 'topologyEndpoints');
                        componentTemplate.attr('component-cache', 'componentCache');
                        componentTemplate.addClass('topology-component');
                        componentTemplate.css({left: componentObject.posX, top: componentObject.posY});

                        // Check if the component config is already in the cache
                        if (scope.componentCache[componentId]) {
                            // Append the new component to the canvas div in the proper position
                            element.append(componentTemplate);

                            // Compile the new directive using the given settings
                            $compile(componentTemplate)(scope);
                        } else {
                            // Retrieve the component config because it is not yet in the component cache
                            Component.getConfig({id: componentId},
                                function(componentConfig) {
                                    // Add the component config to the cache for future use
                                    scope.componentCache[componentId] = componentConfig;

                                    // Append the new component to the canvas div in the proper position
                                    element.append(componentTemplate);

                                    // Compile the new directive with angular
                                    $compile(componentTemplate)(scope);
                                }
                            );
                        }
                    }
                };

                scope.addConnectorToCanvas = function(connectorObject) {
                    // Make sure this connector has not already been rendered
                    if (!(connectorObject.key in scope.renderedObjects)) {
                        scope.renderedObjects[connectorObject.key] = true;

                        // Determine if the connector is detachable based on the disabled status
                        var connectorDetachable = true;
                        if (scope.isTopologyDisabled()) {
                            connectorDetachable = false;
                        }

                        // Programatically connect the components with jsplumb
                        jsPlumb.connect({
                            source: scope.topologyEndpoints[connectorObject.sourceComponentKey]
                                    ['SOURCE_' + connectorObject.sourceComponentInterface],
                            target: scope.topologyEndpoints[connectorObject.targetComponentKey]
                                    ['TARGET_' + connectorObject.targetComponentInterface],
                            parameters: {
                                key: connectorObject.key,
                                grouping: connectorObject.grouping,
                                groupingRef: connectorObject.groupingRef
                            },
                            detachable: connectorDetachable
                        });
                    }
                };

                // Watch for external changes to the topology config to update the canvas
                var configWatcher = scope.$watch('topologyConfig.components', function(updatedValue) {
                    if (updatedValue) {
                        // Add the existing topology config elements to the map on inital load
                        scope.addTopologyToCanvas(scope.topologyConfig);

                        // Disable the config watcher once it has been updated once
                        configWatcher();
                    }
                });

            });
        }
    };
});

/**
 * Directive for handling connectivity and functionality of components in the streamflow canvas
 */
topologyModule.directive('streamflowComponent', function($modal) {
    return {
        restrict: 'A',
        template:
                '<span class="topology-component-control topology-component-control-delete fa-stack">' +
                    '<i class="fa fa-circle fa-inverse fa-stack-2x"></i>' +
                    '<i class="fa fa-circle-o fa-stack-2x"></i>' +
                    '<i class="fa fa-times fa-stack-1x"></i>' +
                '</span>' +
                '<span class="topology-component-control topology-component-control-edit fa-stack">' +
                    '<i class="fa fa-circle fa-inverse fa-stack-2x"></i>' +
                    '<i class="fa fa-circle-o fa-stack-2x"></i>' +
                    '<i class="fa fa-info fa-stack-1x"></i>' +
                '</span>' +
                '<span class="topology-component-control topology-component-control-fields fa-stack"' + 
                    ' ng-show="topologyState.type == \'TRIDENT\'">' +
                    '<i class="fa fa-circle fa-inverse fa-stack-2x"></i>' +
                    '<i class="fa fa-circle-o fa-stack-2x"></i>' +
                    '<i class="fa fa-ellipsis-h fa-stack-1x"></i>' +
                '</span>' +
                '<input ng-model="component.parallelism" maxlength="3" ' +
                        'class="topology-component-control-parallelism"/>' +
                '<div class="topology-component-label-outer">' +
                    '<div class="topology-component-label-inner" ' +
                            'ng-model="component.label">{{ component.label }}</div>' +
                '</div>' +
                '<div class="topology-component-icon" >' +
                    '<img width="40" height="40" ng-if="component.type" ' +
                            'ng-src="api/components/{{ componentId }}/icon" alt=""/>' +
                '</div>',
        scope: {
            componentId: '@',
            componentKey: '@',
            componentDisabled: '@',
            topologyState: '=',
            topologyConfig: '=',
            topologyEndpoints: '=',
            componentCache: '='
        },
        link: function(scope, element, attrs) {
            // Retrieve the component object from the binded topology config
            scope.component = scope.topologyConfig.components[scope.componentKey];

            // Retrieve the component config from the binded component cache
            scope.componentConfig = scope.componentCache[scope.componentId];

            // Add a new entry to the endpoints map for the new component
            scope.topologyEndpoints[scope.componentKey] = {};

            scope.isComponentDisabled = function() {
                return scope.componentDisabled.toLowerCase() === 'true';
            };

            scope.openPropertiesDialog = function() {
                $modal.open({
                    templateUrl: 'app/topology/topology.tpl.properties.html',
                    controller: 'ComponentPropertiesController',
                    resolve: {
                        componentObject: function() {
                            return angular.copy(scope.component);
                        },
                        componentConfig: function() {
                            return angular.copy(scope.componentConfig);
                        }
                    }
                }).result.then(
                    function(componentObject) {
                        // A new properties object was generated by editing properties
                        if (componentObject) {
                            // Update the component config properties with the new values
                            scope.component.label = componentObject.label;
                            scope.component.properties = componentObject.properties;

                            scope.topologyState.isModified = true;
                        }
                    }
                );

                scope.$apply();
            };

            // Only enable dragging if the component is not disabled
            if (!scope.isComponentDisabled()) {
                // jsPlumb uses the containment from the underlying library, in our case that is jQuery.
                jsPlumb.draggable(element, {
                    containment: '.topology-canvas',
                    stop: function(event, ui) {
                        // Update the stored position of the component once drag stops
                        scope.component.posX = ui.position.left;
                        scope.component.posY = ui.position.top;

                        scope.topologyState.isModified = true;
                    }
                });

                // Bind a click handler to the entire element
                element.click(function(event) {
                    // Prevent triggering click event of the canvas
                    event.stopPropagation();
                    
                    // Reset the state of any other component that was selected
                    $('.topology-component-selected').removeClass('topology-component-selected');
                    $('.topology-component-control').fadeOut(0);

                    // Select the current element to raise it's z-index above others
                    element.addClass('topology-component-selected');
                    element.find('.topology-component-control').fadeIn(0);
                });

                // Bind a double click handler to the entire element
                element.dblclick(function(e) {
                    scope.openPropertiesDialog();
                });

                // Bind a click handler to the delete button on the component
                element.find('.topology-component-control-delete').click(function(event) {
                    // Open the submit topology dialog to deploy the selected topology
                    $modal.open({
                        templateUrl: 'app/topology/topology.tpl.component.html',
                        controller: function($scope) {
                        }
                    }).result.then(function(result) {
                        // Remove this component from the topolology config
                        delete scope.topologyConfig.components[scope.componentKey];

                        // Detach all jsPlumb connections to this element
                        jsPlumb.detachAllConnections(element);

                        // Iterate over all of the endpoints for this component
                        angular.forEach(scope.topologyEndpoints[scope.componentKey], function(endpoint) {
                            // Delete the endpoint permanently
                            jsPlumb.deleteEndpoint(endpoint);
                        });

                        // Remove the endpoints from the map
                        delete scope.topologyEndpoints[scope.componentKey];

                        // Remove this component from the DOM
                        element.remove();

                        scope.topologyState.isModified = true;
                    });
                });

                // Bind a click handler to the delete button on the component
                element.find('.topology-component-control-edit').click(function(event) {
                    scope.openPropertiesDialog();
                });
                
                // Bind a click handler to the delete button on the component
                element.find('.topology-component-control-fields').click(function(event) {
                    console.log('clicked fields button');
                    
                    $modal.open({
                        templateUrl: 'app/topology/topology.tpl.fields.html',
                        controller: 'ComponentFieldsController',
                        resolve: {
                            componentObject: function() {
                                return angular.copy(scope.component);
                            }
                        }
                    }).result.then(
                        function(componentObject) {
                            // A new properties object was generated by editing properties
                            if (componentObject) {
                                // Update the component config properties with the new values
                                scope.component.fields = componentObject.fields;

                                scope.topologyState.isModified = true;
                            }
                        }
                    );

                    scope.$apply();
                });
            }

            // Iterate over each of the inputs to add a new endpoint for each
            angular.forEach(scope.componentConfig.properties, function(propertyObject) {
                var propertyName = propertyObject.name;
                var propertyValue = scope.component.properties[propertyName];

                // If topology config does not have a value or is null for the property
                if (!propertyValue) {
                    if (propertyObject.defaultValue) {
                        // If the property does not have a value, set it to the default
                        scope.component.properties[propertyName] =
                                propertyObject.defaultValue;
                    } else {
                        // If there is no default value for the  property, set it as empty
                        scope.component.properties[propertyName] = '';
                    }
                }
            });

            var minHeight = 80;
            var targetAnchorOffset = 1;
            var sourceAnchorOffset = 1;

            // Iterate over the inputs to calculate the endpoint offsets and min height
            if (scope.componentConfig.inputs) {
                // Calculcate the offset between each anchor position
                targetAnchorOffset = 1 / (scope.componentConfig.inputs.length + 1);
                if (targetAnchorOffset < 1) {
                    var targetMinHeight = 25 * (scope.componentConfig.inputs.length + 2);
                    if (targetMinHeight > minHeight) {
                        minHeight = targetMinHeight;
                    }
                }
            }

            // Iterate over the outputs to calculate the endpoint offsets and min height
            if (scope.componentConfig.outputs) {
                // Calculcate the offset between each anchor position
                sourceAnchorOffset = 1 / (scope.componentConfig.outputs.length + 1);
                if (sourceAnchorOffset < 1) {
                    var sourceMinHeight = 25 * (scope.componentConfig.outputs.length + 2);
                    if (sourceMinHeight > minHeight) {
                        minHeight = sourceMinHeight;
                    }
                }
            }

            // Set the min height to fit the number of inputs/outputs
            element.css('min-height', minHeight + 'px');

            // Iterate over each of the inputs to add a new endpoint for each
            angular.forEach(scope.componentConfig.inputs, function(inputInterface, inputIndex) {
                var anchorPosition = (inputIndex + 1) * targetAnchorOffset;

                // Add a target endpoint to the component
                var targetEndpoint = jsPlumb.addEndpoint(element, {
                    anchor: [0, anchorPosition, -1, 0],
                    isTarget: true,
                    maxConnections: -1,
                    uniqueEndpoint: true,
                    dropOptions: {
                        tolerance: 'touch',
                        hoverClass: 'topology-component-endpoint-drop-hover',
                        activeClass: 'topology-component-endpoint-drop-active'
                    },
                    overlays: [
                        ['Label', {
                            id: 'topology-component-endpoint-label',
                            location: [-1.0, -0.5],
                            label: inputInterface.key,
                            cssClass: 'topology-component-endpoint-label'
                        }]
                    ],
                    parameters: {
                        targetKey: scope.component.key,
                        targetInterface: inputInterface.key
                    }
                });

                // Show and hide the endpoint label overlay on hover
                targetEndpoint.bind("mouseenter", function(endpoint) {
                    endpoint.showOverlay('topology-component-endpoint-label');
                    endpoint.addClass('topology-component-endpoint-hover');
                });
                targetEndpoint.bind("mouseexit", function(endpoint) {
                    endpoint.hideOverlay('topology-component-endpoint-label');
                    endpoint.removeClass('topology-component-endpoint-hover');
                });
                targetEndpoint.bind("mousedown", function(endpoint) {
                    endpoint.hideOverlay('topology-component-endpoint-label');
                    endpoint.removeClass('topology-component-endpoint-hover');
                });

                // Save the input endpoint object for connection use
                scope.topologyEndpoints[scope.component.key]['TARGET_' + inputInterface.key] =
                        targetEndpoint;
            });

            // Iterate over each of the output to add a new endpoint for each
            angular.forEach(scope.componentConfig.outputs, function(outputInterface, outputIndex) {
                var anchorPosition = (outputIndex + 1) * sourceAnchorOffset;

                // Add a source endpoint to the component
                var sourceEndpoint = jsPlumb.addEndpoint(element, {
                    anchor: [1, anchorPosition, 1, 0],
                    isSource: true,
                    maxConnections: -1,
                    uniqueEndpoint: true,
                    dragOptions: {
                        cursor: 'pointer'
                    },
                    overlays: [
                        ['Label', {
                            id: 'topology-component-endpoint-label',
                            location: [2.0, -0.5],
                            label: outputInterface.key,
                            cssClass: 'topology-component-endpoint-label'
                        }]
                    ],
                    parameters: {
                        sourceKey: scope.component.key,
                        sourceInterface: outputInterface.key
                    }
                });

                // Show and hide the endpoint label on hover
                sourceEndpoint.bind("mouseenter", function(endpoint) {
                    endpoint.showOverlay('topology-component-endpoint-label');
                    endpoint.addClass('topology-component-endpoint-hover');
                });
                sourceEndpoint.bind("mouseexit", function(endpoint) {
                    endpoint.hideOverlay('topology-component-endpoint-label');
                    endpoint.removeClass('topology-component-endpoint-hover');
                });
                sourceEndpoint.bind("mousedown", function(endpoint) {
                    endpoint.hideOverlay('topology-component-endpoint-label');
                    endpoint.removeClass('topology-component-endpoint-hover');
                });

                // Save the output endpoint object for connection use
                scope.topologyEndpoints[scope.component.key]['SOURCE_' + outputInterface.key] =
                        sourceEndpoint;
            });

            jsPlumb.recalculateOffsets(element);

            jsPlumb.repaint(scope.componentKey);
        }
    };
});
