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
var fileuploadModule = angular.module('streamflow.fileupload', []);

fileuploadModule.directive('streamflowFileupload', function() {
    return {
        restrict: 'A',
        scope: {
            add: '&', done: '&', fail: '&', always: '&', progress: '&',
            progressall: '&', start: '&', stop: '&', change: '&'
        },
        link: function(scope, element, attrs) {
            var optionsObj = {
                dataType: 'json'
            };

            if (scope.add) {
                optionsObj.add = function(e, data) {
                    scope.$apply(function() {
                        scope.add({e: e, data: data});
                    });
                };
            }

            if (scope.done) {
                optionsObj.done = function(e, data) {
                    scope.$apply(function() {
                        scope.done({e: e, data: data});
                    });
                };
            }

            if (scope.fail) {
                optionsObj.fail = function(e, data) {
                    scope.$apply(function() {
                        scope.fail({e: e, data: data});
                    });
                };
            }

            if (scope.always) {
                optionsObj.always = function(e, data) {
                    scope.$apply(function() {
                        scope.always({e: e, data: data});
                    });
                };
            }

            if (scope.progress) {
                optionsObj.progress = function(e, data) {
                    scope.$apply(function() {
                        scope.progress({e: e, data: data});
                    });
                };
            }

            if (scope.progressall) {
                optionsObj.progressall = function(e, data) {
                    scope.$apply(function() {
                        scope.progressall({e: e, data: data});
                    });
                };
            }

            if (scope.start) {
                optionsObj.start = function(e) {
                    //scope.$apply(function() {scope.start({e: e});});
                    scope.start({e: e});
                };
            }

            if (scope.stop) {
                optionsObj.stop = function(e) {
                    scope.$apply(function() {
                        scope.stop({e: e});
                    });
                };
            }

            if (scope.change) {
                optionsObj.change = function(e, data) {
                    scope.$apply(function() {
                        scope.change({e: e, data: data});
                    });
                };
            }

            element.fileupload(optionsObj);
        }
    };
});