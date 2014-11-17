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
var userModule = angular.module('streamflow.user',
        ['ngResource', 'ui.bootstrap', 'streamflow.notify']);

// Route configuration for the various views
userModule.config(['$routeProvider', function($routeProvider) {
    // Set up routes for the specific paths to the different views
    $routeProvider.
        when('/users', {
            templateUrl: 'app/user/user.tpl.list.html',
            controller: 'UserListController'
        });
}]);


////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

userModule.factory('User', ['$resource', function($resource) {
    return $resource('api/users/:id', {id: '@id'}, {
        update: {method: 'PUT'},
        remove: {method: 'DELETE'},
        updatePassword: {method: 'PUT', url: 'api/users/:id/password'}
    });
}]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

userModule.controller('UserListController', [
    '$scope', '$modal', 'streamflowNotify', 'User',
    function($scope, $modal, streamflowNotify, User) {

        $scope.listUsers = function() {
            $scope.users = User.query(
                function() {
                    $scope.usersMessage = 'No users were found';
                },
                function() {
                    $scope.usersMessage = 'Unable to load the user list';
                }
            );
        };

        $scope.createUser = function() {
            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/user/user.tpl.create.html',
                controller: 'UserCreateController'
            }).result.then(function(user) {
                $scope.listUsers();
            });
        };

        $scope.enableUser = function(user) {
            user.enabled = true;

            // Persist the updated enabled status
            User.update({id: user.id}, user,
                function() {
                    $scope.listUsers();

                    streamflowNotify.success('The user account was enabled.');
                },
                function() {
                    streamflowNotify.error('The user account was not enabled due to a server error.');
                }
            );
        };

        $scope.disableUser = function(user) {
            user.enabled = false;

            // Persist the updated enabled status
            User.update({id: user.id}, user,
                function() {
                    $scope.listUsers();

                    streamflowNotify.success('The user account was disabled.');
                },
                function() {
                    streamflowNotify.error('The user account was not disabled due to a server error.');
                }
            );
        };

        $scope.deleteUser = function(user) {
            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/user/user.tpl.delete.html',
                controller: function($scope) {
                    $scope.user = user;
                }
            }).result.then(function() {
                // Delete the resource entry on the server
                User.remove({id: user.id},
                    function() {
                        streamflowNotify.success('The user was deleted successfully');

                        // After deleting the resource entry, refresh the entries list 
                        $scope.listUsers();
                    },
                    function() {
                        streamflowNotify.error('The user was not deleted due to a server error.');
                    }
                );
            });
        };

        $scope.listUsers();
    }
]);

userModule.controller('UserCreateController', [
    '$scope', '$location', '$modalInstance', 'streamflowNotify', 'User',
    function($scope, $location, $modalInstance, streamflowNotify, User) {
        $scope.create = function() {
            // Create the new user using the specified paramters from the dialog
            var user = new User();
            user.username = $scope.username;
            user.password = $scope.password;
            user.email = $scope.email;
            user.firstName = $scope.firstName;
            user.lastName = $scope.lastName;
            user.enabled = true;

            // Persist the new user
            user.$save(
                function(user) {
                    streamflowNotify.success('The user account was created successfully');

                    // Close the dialog after successful creation
                    $modalInstance.close(user);
                },
                function(response) {
                    streamflowNotify.error('Unable to create new user. ' + response.data);
                }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);
