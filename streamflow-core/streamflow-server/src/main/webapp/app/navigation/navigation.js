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
var navigationModule = angular.module('streamflow.navigation', [
    'ui.bootstrap', 'streamflow.notify', 'streamflow.user', 'streamflow.security'
]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

navigationModule.controller('NavigationController', [
    '$scope', '$window', '$location', '$http', '$modal', 'AuthService',
    function($scope, $window, $location, $http, $modal, AuthService) {
        // Navigation menu items (possibly load these from config file so new pages can be added)
        $scope.items = [
            {label: 'Dashboard', path: '#/dashboard', icon: 'fa fa-dashboard'},
            {label: 'Topologies', path: '#/topologies', icon: 'fa fa-sitemap fa-rotate-270'},
            {label: 'Resources', path: '#/resources', icon: 'fa fa-cloud'},
            //{label: 'Services', path: '#/services', icon: 'fa fa-wrench'},
            {label: 'Frameworks', path: '#/frameworks', icon: 'fa fa-gears'},
            {label: 'Accounts', path: '#/users', icon: 'fa fa-group'}
        ];

        // Check if the current path is selected to highlight the proper navigation item
        $scope.isActive = function(itemPath) {
            // Boolean test to see if the nav type is selected
            if (itemPath.substring(1) === $location.path()) {
                // Handle exact matches
                return true;
            } else if ($location.path().length > 1 && itemPath.length > 2 &&
                    $location.path().indexOf(itemPath.substring(1)) >= 0) {
                // Handle prefix matches
                return true;
            } else {
                return false;
            }
        };

        $scope.logout = function() {
            AuthService.logout();

            AuthService.logout().then(
                    function() {
                        // Redirect to the main page after login so new logins can start fresh
                        $window.location.href = "/";
                    });
        };

        $scope.editAccount = function() {
            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/navigation/navigation.tpl.account.html',
                controller: 'AccountEditController',
                resolve: {
                    user: function() {
                        return $scope.user;
                    }
                }
            });
        };

        $scope.loadActiveUser = function() {
            // Retrieve the User that is currently logged in
            $http.get('api/security/whoami').success(function(response) {
                $scope.user = response;
            });
        };

        // When loading the navbar, always attempt to load the user
        $scope.loadActiveUser();
    }
]);

navigationModule.controller('AccountEditController', [
    '$scope', '$modal', '$modalInstance', 'streamflowNotify', 'User', 'user',
    function($scope, $modal, $modalInstance, streamflowNotify, User, user) {
        $scope.user = user;

        $scope.update = function() {
            User.update({id: $scope.user.id}, $scope.user,
                    function() {
                        streamflowNotify.success('User account updated successfully');

                        $modalInstance.close();
                    },
                    function(response) {
                        streamflowNotify.error('Unable to update user account. ' + response.data);
                    }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

        $scope.editPassword = function() {

            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/navigation/navigation.tpl.password.html',
                controller: 'AccountPasswordController',
                resolve: {
                    user: function() {
                        return $scope.user;
                    }
                }
            });
        };
    }
]);

navigationModule.controller('AccountPasswordController', [
    '$scope', '$modalInstance', 'streamflowNotify', 'User', 'user',
    function($scope, $modalInstance, streamflowNotify, User, user) {
        $scope.user = user;

        $scope.update = function() {
            User.updatePassword({id: $scope.user.id}, $scope.passwordChange,
                    function() {
                        streamflowNotify.success('User password updated successfully');

                        $modalInstance.close();
                    },
                    function(response) {
                        streamflowNotify.error('Unable to update user password. ' + response.data);
                    }
            );
        };

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);