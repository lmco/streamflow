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
var resizeModule = angular.module('streamflow.resize', []);

resizeModule.directive('streamflowResize', function($window, $timeout) {
    return function(scope, element) {
        var window = angular.element($window);

        scope.getWindowDimensions = function() {
            return {
                height: window.height(),
                width: window.width()
            };
        };

        scope.$watch(scope.getWindowDimensions, function(windowDimensions) {
            // Get the heights of the relevant elements to calculate proper resized height
            var windowHeight = windowDimensions.height,
                    elementHeight = element.height(),
                    headerHeight = angular.element('.header').outerHeight(),
                    bodyHeight = angular.element('.body').outerHeight(),
                    footerHeight = angular.element('.footer').outerHeight(),
                    paddingHeight = 25;

            // Update the element height to fill the remaining space
            element.height(windowHeight + elementHeight - headerHeight
                    - bodyHeight - footerHeight - paddingHeight);
        }, true);

        // Bind to the resize event so resizing of the window readjusts the height
        window.bind('resize', function() {
            scope.$apply();
        });
    };
});