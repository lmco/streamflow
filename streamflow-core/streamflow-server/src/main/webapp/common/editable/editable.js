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
var editableModule = angular.module('streamflow.editable', []);

editableModule.directive('contenteditable', function() {
    return {
        require: 'ngModel',
        link: function(scope, element, attrs, ctrl) {
            // view -> model
            element.bind('blur', function() {
                scope.$apply(function() {
                    ctrl.$setViewValue(element.text());
                });
            });

            // model -> view
            ctrl.$render = function() {
                element.html(ctrl.$viewValue);
            };

            // load init value from DOM
            ctrl.$render();
        }
    };
});