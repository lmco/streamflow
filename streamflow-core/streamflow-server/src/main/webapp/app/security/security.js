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
var securityModule = angular.module('streamflow.security',
        ['ngResource', 'ui.bootstrap']);

// Authentication event constants
securityModule.constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    loginFailed: 'auth-login-failed',
    logoutSuccess: 'auth-logout-success',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized'
});

// Parent controller for the secured area of the application
securityModule.controller('SecuredController', 
        function($scope, $modal, AUTH_EVENTS, AuthService) {
    $scope.activeUser = null;
    $scope.loginVisible = false;
    
    $scope.setActiveUser = function(user) {
        $scope.activeUser = user;
    };
    
    $scope.logout = function() {
        AuthService.logout();
    };
    
    $scope.showLogin = function() {
        // Only show the login dialog once at any time
        if (!$scope.loginVisible) {
            $scope.loginVisible = true;

            // Open the submit topology dialog to deploy the selected topology
            $modal.open({
                templateUrl: 'app/security/security.tpl.login.html',
                controller: 'LoginController',
                backdrop: 'static',
                backdropClass: 'login-backdrop',
                windowClass: 'login-window',
                keyboard: false
            }).result.then(function(user) {
                $scope.activeUser = user;
                $scope.loginVisible = false;
            });
        }
    };
    
    //$scope.setActiveUser(AuthService.whoami());
    
    // Show the login dialog when unauthenticated events are received
    $scope.$on(AUTH_EVENTS.notAuthenticated, $scope.showLogin);
    $scope.$on(AUTH_EVENTS.sessionTimeout, $scope.showLogin);
});

securityModule.controller('LoginController', 
        function($scope, $modalInstance, AuthService) {
    $scope.credentials = {
        username: '',
        password: '',
        rememberMe: false
    };
    
    $scope.login = function(credentials) {
        $scope.errorMsg = null;
        
        AuthService.login(credentials).then(
            function(response) {
                // Close this modal providing the logged in user in the response
                $modalInstance.close(response.data);
            }, 
            function(response) {
                // Display the error message in the login dialog and clear out the password
                $scope.errorMsg = response.data;
                $scope.credentials.password = '';
            }
        );
    };
});

securityModule.factory('AuthService', function($http, $rootScope, AUTH_EVENTS, Session) {
    return {
        login : function(credentials) {
            return $http.post('api/security/login', credentials).
                success(function(response) {
                    // Save the user state in the Session service
                    Session.create(response.data);

                    // Broadcast the login success
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);

                    return response;
                }).
                error(function(response) {
                    // Broadcast the login failure
                    $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
                    
                    return response;
                });
        },

        logout : function() {
            return $http.get('api/security/logout').
                success(function() {
                    // Clear the session of the current logged in user
                    Session.destroy();

                    // Broadcast the login success
                    $rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
                });
        },

        whoami : function() {
            return $http.get('api/security/whoami').
                success(function(response) {
                    var user = response.data;
                    Session.create(user);
                    return user;
                }).
                error(function() {
                    return null;
                });
        },

        isAuthenticated : function() {
            // Check if there is a user in this session
            return !!Session.user;
        }
    };
});

securityModule.factory('RequestQueue', function($injector) {
    var requestQueue = [];
    
    return {
        add : function(requestConfig, deferred) {
            requestQueue.push({
                config: requestConfig, 
                deferred: deferred
            });
        },
        
        drain: function() {
            angular.forEach(requestQueue, function(requestEntry) {
                var $http = $injector.get('$http');
                
                $http(requestEntry.config).then(
                    function(response) {
                        requestEntry.deferred.resolve(response);
                    },
                    function(response) {
                        requestEntry.deferred.reject(response);
                    });
            });
            
            // Clear out the queue after all data has been processed
            requestQueue = [];
        }
    };
});

securityModule.service('Session', function() {
    this.create = function(user) {
        this.user = user;
    };
    this.destroy = function() {
        this.user = null;
    };
    return this;
});

securityModule.run(function($rootScope, AUTH_EVENTS, AuthService, RequestQueue) {
    $rootScope.$on('$stateChangeStart', function(event, next) {
        if (!AuthService.isAuthenticated()) {
            event.preventDefault();
            
            // Broadcast the event to trigger display of the login dialog
            $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
        }
    });
    
    // Once login is successful, replay any queued requests so they cna be resolved
    $rootScope.$on(AUTH_EVENTS.loginSuccess, function(event, next) {
        RequestQueue.drain();
    });
});

securityModule.config(function($httpProvider) {
    $httpProvider.interceptors.push(['$injector',
        function($injector) {
            return $injector.get('SecurityInterceptor');
        }
    ]);
});

securityModule.factory('SecurityInterceptor', function($rootScope, $q, AUTH_EVENTS, RequestQueue) {
    return {
        responseError: function(response) {
            // Broadcast the proper event based on the error authentication error type
            $rootScope.$broadcast({
                401: AUTH_EVENTS.notAuthenticated,
                403: AUTH_EVENTS.notAuthorized,
                419: AUTH_EVENTS.sessionTimeout,
                440: AUTH_EVENTS.sessionTimeout
            }[response.status], response);
            
            // Save any request that failed due to an authentication error so it can be retried
            if (response.status === 401) {
                var deferred = $q.defer();
                RequestQueue.add(response.config, deferred);
                return deferred.promise;
            } else {
                // For any other request just reject it like normal
                return $q.reject(response);
            }
        }
    };
});
