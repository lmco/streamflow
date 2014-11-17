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
var frameworkModule = angular.module('streamflow.framework', [
    'ngResource', 'ui.bootstrap', 'streamflow.fileupload'
]);

// Route configuration for the various views
frameworkModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
        when('/frameworks', {
            templateUrl: 'app/framework/framework.tpl.list.html',
            controller: 'FrameworkListController'
        }).
        when('/frameworks/:name', {
            templateUrl: 'app/framework/framework.tpl.view.html',
            controller: 'FrameworkViewController'
        });
}]);


////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

frameworkModule.factory('Framework', ['$resource', function($resource) {
    return $resource('api/frameworks/:name', {name: '@name'}, {
        remove: {
            method: 'DELETE'
        },
        getInfo: {
            method: 'GET',
            isArray: true,
            url: 'api/frameworks/:name/info'
        },
        getComponents: {
            method: 'GET',
            isArray: true,
            url: 'api/frameworks/:name/components'
        },
        getResources: {
            method: 'GET',
            isArray: true,
            url: 'api/frameworks/:name/resources'
        },
        getSerializations: {
            method: 'GET',
            isArray: true,
            url: 'api/frameworks/:name/serializations'
        }
    });
}]);

frameworkModule.factory('Component', ['$resource', function($resource) {
    return $resource('api/components/:id', {id: '@id'}, {
        getConfig: {
            method: 'GET',
            url: 'api/components/:id/config'
        }
    });
}]);

frameworkModule.service('FrameworkService', [
    '$modal', 'Framework',  
    function($modal, Framework) {
        
        this.uploadFramework = function(successCallback, errorCallback) {
            // Open the framework upload dialog backed by the FrameworkUploadController
            $modal.open({
                templateUrl: 'app/framework/framework.tpl.upload.html',
                controller: 'FrameworkUploadController'
            }).result.then(
                function() {
                    successCallback();
                }, 
                function() {
                    errorCallback();
                }
            );
        };

        this.deleteFramework = function(framework, successCallback, errorCallback) {
            $modal.open({
                templateUrl: 'app/framework/framework.tpl.delete.html',
                controller: function($scope) {
                    $scope.framework = framework;
                }
            }).result.then(function() {
                Framework.remove({name: framework.name},
                    function() {
                        successCallback();
                    },
                    function() {
                        errorCallback();
                    }
                );
            });
        };

        this.loadFrameworkInfo = function(frameworkName) {
            $modal.open({
                templateUrl: 'app/framework/framework.tpl.info.html',
                controller: function($scope) {
                    $scope.framework = Framework.get({name: frameworkName});
                }
            });
        };
    }
]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

frameworkModule.controller('FrameworkListController', [
    '$scope', 'streamflowNotify', 'FrameworkService', 'Framework',
    function($scope, streamflowNotify, FrameworkService, Framework) {

        $scope.listFrameworks = function() {
            $scope.frameworks = Framework.query(
                function() {
                    $scope.frameworksMessage = 'No frameworks were found';
                },
                function() {
                    $scope.frameworksMessage = 'Unable to load the framework list';
                }
            );
        };

        $scope.uploadFramework = function() {
            FrameworkService.uploadFramework(function() {
                $scope.listFrameworks();
            });
        };

        $scope.deleteFramework = function(framework) {
            FrameworkService.deleteFramework(framework,
                function() {
                    streamflowNotify.success('The framework was deleted successfully.');

                    $scope.listFrameworks();
                },
                function() {
                    streamflowNotify.error('The framework was not deleted due to a server error.');
                }
            );
        };

        $scope.listFrameworks();
    }
]);

frameworkModule.controller('FrameworkViewController', [
    '$scope', '$routeParams', '$location', 'streamflowNotify', 'FrameworkService', 'Framework',
    function($scope, $routeParams, $location, streamflowNotify, FrameworkService, Framework) {
        $scope.framework = Framework.get({name: $routeParams.name});
        $scope.resources = Framework.getResources({name: $routeParams.name});
        $scope.serializations = Framework.getSerializations({name: $routeParams.name});
        $scope.components = Framework.getComponents({name: $routeParams.name}, function(components) {
            $scope.spouts = [];
            $scope.bolts = [];
            
            angular.forEach(components, function(component) {
                if (component.type === 'storm-spout') {
                    $scope.spouts.push(component);
                }
                else if (component.type === 'storm-bolt') {
                    $scope.bolts.push(component);
                }
            });
        });

        $scope.deleteFramework = function() {
            FrameworkService.deleteFramework($scope.framework,
                function() {
                    streamflowNotify.success('The framework was deleted successfully.');

                    // Redirect to the frameworks page after deleting the framework
                    $location.path('/frameworks');
                },
                function() {
                    streamflowNotify.error('The framework was not deleted due to a server error.');
                }
            );
        };
    }
]);

frameworkModule.controller('FrameworkUploadController', [
    '$scope', '$modalInstance', 
    function($scope, $modalInstance) {
        // Set the default state of the upload dialog
        $scope.isPublic = false;
        $scope.uploadState = 'INITIALIZED';
        $scope.uploadProgress = 0;
        $scope.uploadMessage = null;
        $scope.uploadFile = 'No framework selected...';
        $scope.submitHandle = null;
        $scope.cancelHandle = null;

        $scope.isStarted = function() {
            return ($scope.uploadState !== 'INITIALIZED');
        };

        $scope.isActive = function() {
            return ($scope.uploadState === 'ACTIVE');
        };

        $scope.isFinished = function() {
            return ($scope.uploadState === 'COMPLETED'
                    || $scope.uploadState === 'CANCELED'
                    || $scope.uploadState === 'FAILED');
        };

        $scope.choose = function() {
            // Trigger a click of the file input box
            $('#framework_file_upload').trigger('click');
        };

        $scope.upload = function() {
            $scope.uploadState = 'ACTIVE';
            
            $scope.submitHandle.formData = {
                isPublic: $scope.isPublic
            };

            // Call submit to start the data handle
            $scope.cancelHandle = $scope.submitHandle.submit();
        };

        $scope.close = function() {
            $modalInstance.close();
        };

        $scope.cancel = function() {
            $scope.cancelHandle.abort();

            $scope.uploadState = 'CANCELED';
            $scope.uploadMessage = 'Framework upload cancelled. ';
        };

        $scope.reset = function() {
            $scope.uploadState = 'INITIALIZED';
            $scope.uploadProgress = 0;
            $scope.uploadFile = 'No framework selected...';
            
            $('#framework_file_upload').val('');
        };

        $scope.add = function(e, data) {
            // Save a handle to the data object so submission can occur later
            $scope.submitHandle = data;

            if (data.files.length > 0) {
                $scope.uploadFile = data.files[0].name;
            }
        };

        $scope.done = function(e, data) {
            $scope.uploadState = 'COMPLETED';
            $scope.uploadMessage = 'Framework upload completed successfully. ';
            //+ 'Registered '  data.result + ' components.';
        };

        $scope.fail = function(e, data) {
            $scope.uploadState = 'FAILED';
            $scope.uploadMessage = 'Framework upload failed. ' + data.jqXHR.responseText;
            
            testData = data;
        };

        $scope.progressall = function(e, data) {
            $scope.uploadProgress = parseInt(data.loaded / data.total * 100, 10);
        };
    }
]);