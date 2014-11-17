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
var resourceModule = angular.module('streamflow.resource',
        ['ngResource', 'ui.bootstrap', 'streamflow.notify']);

// Route configuration for the various views
resourceModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
            when('/resources', {
                templateUrl: 'app/resource/resource.tpl.list.html',
                controller: 'ResourceListController'
            }).
            when('/resources/:id', {
                templateUrl: 'app/resource/resource.tpl.view.html',
                controller: 'ResourceViewController'
            }).
            when('/resources/:id/entries/:entryId', {
                templateUrl: 'app/resource/resource.tpl.entry.view.html',
                controller: 'ResourceEntryViewController'
            });
}]);


////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

resourceModule.factory('Resource', ['$resource', function($resource) {
    return $resource('api/resources/:id', {id: '@id'}, {
        getConfig: {
            method: 'GET',
            url: 'api/resources/:id/config'
        }
    });
}]);

resourceModule.factory('ResourceEntry', ['$resource', function($resource) {
    return $resource('api/resources/:id/entries/:entryId', {id: '@id', entryId: '@entryId'}, {
        update: {
            method: 'PUT'
        },
        remove: {
            method: 'DELETE'
        },
        getConfig: {
            method: 'GET',
            url: 'api/resources/:id/entries/:entryId/config'
        },
        updateConfig: {
            method: 'PUT',
            url: 'api/resources/:id/entries/:entryId/config'
        }
    });
}]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

resourceModule.controller('ResourceListController', [
    '$scope', '$modal', 'Resource',
    function($scope, $modal, Resource) {

        $scope.listResources = function() {
            $scope.resources = Resource.query(
                function() {
                    $scope.resourcesMessage = 'No resources were found';
                },
                function() {
                    $scope.resourcesMessage = 'Unable to load the resource list';
                }
            );
        };

        $scope.newResourceEntry = function(resource) {
            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/resource/resource.tpl.entry.create.html',
                controller: 'ResourceEntryCreateController',
                resolve: {
                    resource: function() {
                        return angular.copy(resource);
                    }
                }
            });
        };

        $scope.listResources();
    }
]);

resourceModule.controller('ResourceViewController', [
    '$scope', '$routeParams', '$modal', 'streamflowNotify', 'Resource', 'ResourceEntry',
    function($scope, $routeParams, $modal, streamflowNotify, Resource, ResourceEntry) {
        $scope.resource = Resource.get({id: $routeParams.id});

        $scope.listResourceEntries = function() {
            $scope.resourceEntries = ResourceEntry.query({id: $routeParams.id},
                function() {
                    $scope.resourceEntriesMessage = 'No resource entries were found';
                },
                function() {
                    $scope.resourceEntriesMessage = 'Unable to load the resource entry list';
                }
            );
        };

        $scope.newResourceEntry = function() {
            $modal.open({
                templateUrl: 'app/resource/resource.tpl.entry.create.html',
                controller: 'ResourceEntryCreateController',
                resolve: {
                    resource: function() {
                        return $scope.resource;
                    }
                }
            }).result.then(function() {
                $scope.listResourceEntries();
            });
        };

        $scope.deleteResourceEntry = function(resourceEntry) {
            $modal.open({
                templateUrl: 'app/resource/resource.tpl.entry.delete.html',
                controller: function($scope) {
                    $scope.resourceEntry = resourceEntry;
                }
            }).result.then(function() {
                // Delete the resource entry on the server
                ResourceEntry.remove({id: resourceEntry.resource, entryId: resourceEntry.id},
                    function() {
                        streamflowNotify.success('The resource entry was deleted successfully');
                    },
                    function() {
                        streamflowNotify.error('The resurce entry was not deleted due to a server error.');
                    }
                );

                // After deleting the resource entry, refresh the entries list 
                $scope.listResourceEntries();
            });
        };

        $scope.listResourceEntries();
    }
]);

resourceModule.controller('ResourceEntryViewController',  [
    '$scope', '$location', '$routeParams', '$modal', 'streamflowNotify', 'Resource', 'ResourceEntry',
    function($scope, $location, $routeParams, $modal, streamflowNotify, Resource, ResourceEntry) {
        $scope.resource = Resource.get({id: $routeParams.id});
        $scope.resourceEntry = ResourceEntry.get({id: $routeParams.id, entryId: $routeParams.entryId});

        // Load the user saved resource entry config object
        $scope.resourceEntryConfig = ResourceEntry.getConfig({
            id: $routeParams.id, entryId: $routeParams.entryId},
        function() {
            // Make sure the resource entry config was initialized properly
            if (!$scope.resourceEntryConfig) {
                $scope.resourceEntryConfig = {};
            }
            if (!$scope.resourceEntryConfig.properties) {
                $scope.resourceEntryConfig.properties = {};
            }

            // Retrieve the resource config after the user defined config is loaded
            $scope.resourceConfig = Resource.getConfig({id: $routeParams.id},
            function(resourceConfig) {
                // Iterate over all of the properties to ensure the resource entry config is synced
                angular.forEach(resourceConfig.properties, function(resourceProperty) {
                    // Check if the user config already has a property from the resource config
                    if (!$scope.resourceEntryConfig.properties[resourceProperty.name]) {
                        if (resourceProperty.defaultValue) {
                            $scope.resourceEntryConfig.properties[resourceProperty.name] =
                                    resourceProperty.defaultValue;
                        } else {
                            $scope.resourceEntryConfig.properties[resourceProperty.name] = '';
                        }
                    }
                });
            }
            );
        }
        );

        $scope.saveResourceEntry = function() {
            // Post the update topology config to the server
            ResourceEntry.updateConfig({id: $routeParams.id, entryId: $routeParams.entryId}, $scope.resourceEntryConfig,
                function() {
                    streamflowNotify.success('The resource entry was saved successfully.');

                    // Redirect back to the topology view if saved successfully
                    $location.path('/resources/' + $routeParams.id);
                },
                function() {
                    streamflowNotify.error('The resource entry was not saved due to a server error..');
                }
            );
        };

        $scope.cancelResourceEntry = function() {
            // Redirect back to the resources view without saving if cancelled
            $location.path('/resources/' + $routeParams.id);
        };
    }
]);

/**
 * Topology controller used in the topology-create dialog to create a new topology
 */
resourceModule.controller('ResourceEntryCreateController', [
    '$scope', '$location', '$modalInstance', 'streamflowNotify', 'resource', 'ResourceEntry',
    function($scope, $location, $modalInstance, streamflowNotify, resource, ResourceEntry) {
        
        $scope.create = function() {
            console.log("Resource ID = " + resource.id);
            console.log('Name = ' + $scope.resourceEntryName);
            
            // Create the new topology using the specified paramters from the dialog
            var resourceEntry = new ResourceEntry();
            resourceEntry.name = $scope.resourceEntryName;
            resourceEntry.description = $scope.resourceEntryDescription;
            resourceEntry.resource = resource.id;
            resourceEntry.config = {};

            resourceEntry.$save({id: resource.id},
                function(resourceEntry) {
                    // After the topology is created, redirect the user to the new empty topology view
                    $location.path('/resources/' + resource.id + '/entries/' + resourceEntry.id);

                    // Close the dialog after successful creation
                    $modalInstance.close();
                },
                function() {
                    streamflowNotify.error('Unable to create resource entry due to server error: ', 'error');
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
