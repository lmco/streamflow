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
var app = angular.module('streamflow', [
    'ngRoute',
    'ui.bootstrap',
    'streamflow.security',
    'streamflow.navigation',
    'streamflow.dashboard',
    'streamflow.topology',
    'streamflow.framework',
    'streamflow.resource',
    'streamflow.service',
    'streamflow.property',
    'streamflow.user',
    'streamflow.editable',
    'streamflow.fileupload',
    'streamflow.notify',
    'streamflow.resize',
    'streamflow.same',
    'streamflow.moment'
]);

// Route configuration for the various views
app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
    'use strict';
    // Set up routes for the specific paths to the different views
    $routeProvider.
        otherwise({
            redirectTo: '/dashboard'
        });
}]);

