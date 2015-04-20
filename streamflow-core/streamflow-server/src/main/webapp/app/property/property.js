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
var propertyModule = angular.module('streamflow.property', 
    ['ngResource', 'streamflow.resource', 'streamflow.service']);

////////////////////////////////////////////////////////////////////////////////
// SERVICES
////////////////////////////////////////////////////////////////////////////////

propertyModule.factory('File', ['$resource', function($resource) {
    return $resource('api/files/:id', {id: '@id'});
}]);

propertyModule.factory('Serialization', ['$resource', function($resource) {
    return $resource('api/serializations');
}]);


////////////////////////////////////////////////////////////////////////////////
// CONTROLLERS
////////////////////////////////////////////////////////////////////////////////

propertyModule.controller('ComponentPropertiesController', [
    '$scope', '$modalInstance', 'componentObject', 'componentConfig',
    function($scope, $modalInstance, componentObject, componentConfig) {
        $scope.componentObject = componentObject;
        $scope.componentConfig = componentConfig;

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
 * Directive that dynamically generates a form input using the speciied property config
 */
propertyModule.directive('streamflowProperty', 
    function($compile, $filter, $modal, ResourceEntry, File, Serialization, KafkaCluster) {
        return {
            restrict: 'A',
            template:
                '<div ng-form="propertyForm" class="streamflow-property-form" novalidate>' +
                    '<span class="streamflow-property-input"></span>' +
                    '<span class="streamflow-property-error">' +
                        '<div ng-show="propertyForm.{{ propertyIndex }}.$invalid">' +
                            '<div ng-show="propertyForm.{{ propertyIndex }}.$error.required" class="text-danger">' +
                                'This value is required' +
                            '</div>' +
                            '<div ng-show="propertyForm.{{ propertyIndex }}.$error.email" class="text-danger">' +
                                'This value is not a valid email' +
                            '</div>' +
                            '<div ng-show="propertyForm.{{ propertyIndex }}.$error.url" class="text-danger">' +
                                'This value is not a valid url' +
                            '</div>' +
                            '<div ng-show="propertyForm.{{ propertyIndex }}.$error.number" class="text-danger">' +
                                'This value is not a valid number' +
                            '</div>' +
                        '</div>' +
                    '</span>' +
                '</div>',
            scope: {
                propertyConfig: '=',
                propertyObject: '=',
                propertyIndex: '@',
                propertyErrorPosition: '@'
            },
            link: function(scope, element, attrs) {
                var propertyInput;
                var propertyName = scope.propertyIndex;
                var propertyType = scope.propertyConfig.type;
                var propertyOptions = scope.propertyConfig.options;

                // Convert the property type to lower case to make comparison easier
                if (propertyType) {
                    propertyType = propertyType.toLowerCase();
                }
                else {
                    propertyType = 'text';
                }

                // Build the correct property input based on the property type
                switch (propertyType) {
                    // Input type which is defined as an integer number
                    case 'float': {
                        propertyInput = $('<input type="number"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');

                        if (propertyOptions && !isNaN(propertyOptions.maxNumber)) {
                            propertyInput.attr('max', propertyOptions.maxNumber);
                        }
                        if (propertyOptions && !isNaN(propertyOptions.minNumber)) {
                            propertyInput.attr('min', propertyOptions.minNumber);
                        }
                        if (propertyOptions && !isNaN(propertyOptions.floatStep)) {
                            propertyInput.attr('step', propertyOptions.floatStep);
                        }
                        
                        // Watch the property object so the string can be converted to a number
                        scope.$watch('propertyObject', function(numberObject) {
                            if (angular.isString(numberObject)) {
                                scope.propertyObject = parseFloat(numberObject);
                            }
                        });
                        
                        break;
                    }
                    case 'number': {
                        propertyInput = $('<input type="number"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');

                        if (propertyOptions && !isNaN(propertyOptions.maxNumber)) {
                            propertyInput.attr('max', propertyOptions.maxNumber);
                        }
                        if (propertyOptions && !isNaN(propertyOptions.minNumber)) {
                            propertyInput.attr('min', propertyOptions.minNumber);
                        }
                        if (propertyOptions && !isNaN(propertyOptions.numericStep)) {
                            propertyInput.attr('step', propertyOptions.numericStep);
                        }
                        
                        // Watch the property object so the string can be converted to an integer
                        scope.$watch('propertyObject', function(numberObject) {
                            if (angular.isString(numberObject)) {
                                scope.propertyObject = parseInt(numberObject);
                            }
                        });
                        
                        break;
                    }
                    case 'url': {
                        // use html5 - url input
                        propertyInput = $('<input type="url"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');
                        break;
                    }
                    case 'email':
                    case 'e-mail': {
                        // use html5 e-mail input
                        propertyInput = $('<input type="email"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');
                        break;
                    }
                    case 'password': {
                        propertyInput = $('<input type="password"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');
                        break;
                    }
                    case 'textarea':
                    case 'text-area': {
                        var numRows = 5;
                        if (propertyOptions.numRows) {
                            numRows = propertyOptions.numRows;
                        }
                        propertyInput = $('<textarea></textarea>')
                                .attr('ng-model', 'propertyObject')
                                .attr('rows', numRows)
                                .attr('name', propertyName)
                                .addClass('form-control');
                        break;
                    }
                    case 'boolean': {
                        scope.booleanValue = false;
                        if (scope.propertyObject) {
                            if (scope.propertyObject.toLowerCase() === 'true') {
                                scope.booleanValue = true;
                            }
                        }

                        // Create the input field for the property and append it to the template
                        propertyInput = $('<input type="checkbox"></input>')
                                .attr('ng-model', 'booleanValue')
                                .attr('name', propertyName);

                        // Update the model value as the boolean value is changed
                        scope.$watch('booleanValue', function(booleanValue) {
                            if (angular.isDefined(booleanValue)) {
                                scope.propertyObject = booleanValue.toString();
                            }
                        });

                        break;
                    }
                    case 'date': {
                        // Set the default date to today if no date is specified
                        scope.dateObject = new Date();

                        // Initialize the date object with the existing value if it exists
                        if (scope.propertyObject) {
                            // Attempt to make sure at least the time is the right number of chars
                            if (scope.propertyObject.length === 10) {
                                // Parse the hours and minutes from the property object value
                                scope.dateObject.setMonth(
                                        parseInt(scope.propertyObject.substr(0, 2)) - 1);
                                scope.dateObject.setDate(
                                        parseInt(scope.propertyObject.substr(3, 2)));
                                scope.dateObject.setFullYear(
                                        parseInt(scope.propertyObject.substr(6, 4)));
                            }
                        }

                        scope.datePickerOptions = {
                            'month-format': "'MMMM'",
                            'day-format': "'dd'",
                            'year-format': "'yyyy'",
                            'show-weeks': "'false'"
                        };

                        scope.isPickerOpen = false;

                        propertyInput = $('<input type="text"></input')
                                .attr('ng-model', 'dateObject')
                                .attr('datepicker-popup', 'MM-dd-yyyy')
                                .attr('is-open', 'isPickerOpen')
                                .attr('show-weeks', 'false')
                                .attr('datepicker-options', 'datePickerOptions')
                                .addClass('form-control');

                        // Update the model value as the date object is changed
                        scope.$watch('dateObject', function(dateObject) {
                            if (dateObject && angular.isDate(dateObject)) {
                                scope.propertyObject = $filter('date')(dateObject, 'MM-dd-yyyy');
                            }
                        });

                        break;
                    }
                    case 'time': {
                        // Set the default time to 00:00 if no time is specified
                        scope.dateObject = new Date();
                        scope.dateObject.setHours(0);
                        scope.dateObject.setMinutes(0);

                        // Initialize the date object with the existing value if it exists
                        if (scope.propertyObject) {
                            // Attempt to make sure at least the time is the right number of chars
                            if (scope.propertyObject.length === 5) {
                                // Parse the hours and minutes from the property object value
                                scope.dateObject.setHours(
                                        parseInt(scope.propertyObject.substr(0, 2)));
                                scope.dateObject.setMinutes(
                                        parseInt(scope.propertyObject.substr(3, 2)));
                            }
                        }

                        // Build the time picker directive using the temporary date object as a placeholder
                        propertyInput = $('<timepicker></timepicker>')
                                .attr('ng-model', 'dateObject')
                                .attr('name', propertyName)
                                .attr('hour-step', "1")
                                .attr('minute-step', "1")
                                .attr('show-meridian', "false")
                                .addClass('form-control');

                        // Update the model value as the date object is changed
                        scope.$watch('dateObject', function(dateObject) {
                            if (dateObject && angular.isDate(dateObject)) {
                                scope.propertyObject = $filter('date')(dateObject, 'HH:mm');
                            }
                        });

                        break;
                    }
                    case 'select': {
                        propertyInput = $('<select></select>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');

                        // Append the default select option to the list
                        $('<option></option>').text('Select An Option...').val('').appendTo(
                                propertyInput);

                        // Parse out the text which contains just the select options
                        if (propertyOptions && propertyOptions.listItems) {
                            if (propertyOptions.listItems.length > 0) {
                                // Split up the options and append them as new options to the select
                                angular.forEach(propertyOptions.listItems, function(listItem) {
                                    $('<option></option>').text($.trim(listItem)).val(
                                            $.trim(listItem)).appendTo(propertyInput);
                                });
                            }
                        }
                        break;
                    }
                    case 'resource': {
                        propertyInput = $('<select></select>')
                                .attr('name', propertyName)
                                .addClass('form-control');

                        // Append the default select option to the list
                        $('<option></option>').text('Select A Resource...').val('').appendTo(
                                propertyInput);

                        // Parse out the text which contains just the select options
                        if (propertyOptions && propertyOptions.resourceFramework
                                && propertyOptions.resourceName) {
                            // Retrieve all of the resource entries for the specified resource
                            ResourceEntry.query({id: propertyOptions.resourceFramework
                                        + '_' + propertyOptions.resourceName},
                                function(resourceEntries) {
                                    // Iterate over each of the resource entires for the specified resource
                                    angular.forEach(resourceEntries, function(resourceEntry) {
                                        $('<option></option>').text($.trim(resourceEntry.name)).val(
                                                $.trim(resourceEntry.id)).appendTo(propertyInput);
                                    });

                                    // Delay binding of the ng-model to prevent empty selection
                                    propertyInput.attr('ng-model', 'propertyObject');
                                    $compile(propertyInput)(scope);
                                }
                            );
                        }
                        break;
                    }
                    case 'serialization': {
                        propertyInput = $('<select></select>')
                                .attr('name', propertyName)
                                .addClass('form-control');

                        // Append the default select option to the list
                        $('<option></option>').text('Select A Class...').val('').appendTo(
                                propertyInput);

                        Serialization.query(
                            function(serializations) {
                                // Iterate over each of the registered serializations 
                                angular.forEach(serializations, function(serialization) {
                                    $('<option></option>').text($.trim(serialization.typeClass)).val(
                                            $.trim(serialization.typeClass)).appendTo(propertyInput);
                                });

                                // Delay binding of the ng-model to prevent empty selection
                                propertyInput.attr('ng-model', 'propertyObject');
                                $compile(propertyInput)(scope);
                            }
                        );

                        break;
                    }
                    case 'kafka-topic': {
                        console.log('Loading Kafka Select...');
                            
                        var kafkaSelect = $('<select></select>')
                                .attr('name', propertyName)
                                .addClass('form-control');
                        
                        var kafkaTopicButton = $('<button><i class="fa fa-plus"></i> Add Kafka Topic</button>')
                                .css('margin-top', '5px').addClass('btn')
                                .addClass('btn-primary').addClass('pull-right');
                        
                        scope.loadTopicModal = function() {
                            // Open the submit topology dialog to deploy the selected topology
                            $modal.open({
                                templateUrl: 'app/property/topic.create.tpl.html',
                                controller: 'KafkaAddTopicController'
                            }).result.then(function(topic) {
                                console.log('Topic Created');
                            });
                        };
                        
                        propertyInput = $('<div></div>');
                        propertyInput.append(kafkaSelect);
                        propertyInput.append(kafkaTopicButton);

                        // Append the default select option to the list
                        $('<option></option>').text('Select A Kafka Topic...').val('').appendTo(
                                kafkaSelect);
                        
                        KafkaCluster.query(function(clusters) {
                            // Iterate over each of the registered serializations 
                            angular.forEach(clusters, function(cluster) {
                                var clusterGroup = $('<optgroup></optgroup>')
                                        .attr('label', $.trim('Kafka Cluster: ' + cluster.name));

                                angular.forEach(cluster.topics, function(topic) {
                                    $('<option></option>').text($.trim('Kafka Topic: ' + topic.name)).val(
                                            $.trim(cluster.id + ':' + topic.name)).appendTo(clusterGroup);
                                });

                                clusterGroup.appendTo(kafkaSelect);
                            });

                            // Delay binding of the angualr attributes to prevent empty selection
                            kafkaSelect.attr('ng-model', 'propertyObject');
                            kafkaTopicButton.attr('ng-click', 'loadTopicModal()');

                            $compile(propertyInput)(scope);
                        });

                        break;    
                    }
                    case 'file': {
                        // HTML template of the file upload interface (Ugly, replace with template)
                        var fileUploadTemplate =
                            '<div>' +
                                '<input type="hidden" name="' + propertyName + '" ' +
                                    'ng-model="propertyObject" style="width: 100%;"/>' +
                                '<input streamflow-fileupload class="file-upload-input hide" type="file" name="file" ' +
                                    'data-url="api/files" add="add(e, data)" done="done(e, data)" ' +
                                    'fail="fail(e, data)" progressall="progressall(e, data)"/>' +
                                '<div class="file-upload-container clearfix well" ' +
                                    'style="min-width: 300px; padding: 10px; margin-bottom: 10px;">' +
                                    '<div ng-show="!upload">No File Selected...</div>' +
                                    '<div ng-show="upload">' +
                                        '<div class="row-fluid">' +
                                            '<div class="col-sm-3 text-right"><strong>Name:</strong></div>' +
                                            '<div class="col-sm-9">{{ upload.fileName }}</div>' +
                                        '</div>' +
                                        '<div class="row-fluid">' +
                                            '<div class="col-sm-3 text-right"><strong>Size:</strong></div>' +
                                            '<div class="col-sm-9">{{ upload.fileSize | number }} bytes</div>' +
                                        '</div>' +
                                        '<div class="row-fluid">' +
                                            '<div class="col-sm-3 text-right"><strong>Type:</strong></div>' +
                                            '<div class="col-sm-9">{{ upload.fileType }}</div>' +
                                        '</div>' +
                                        '<div class="row-fluid">' +
                                            '<div class="col-sm-3 text-right"><strong>Date:</strong></div>' +
                                            '<div class="col-sm-9">{{ upload.created | date:"medium" }}</div>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="row-fluid">' +
                                    '<button class="btn btn-small btn-primary" style="margin-right: 5px;" ' +
                                        'ng-show="!isUploading" ng-click="reset()">Clear File</button>' +
                                    '<button class="btn btn-small btn-primary" style="margin-right: 5px;" ' +
                                        'ng-show="!isUploading" ng-click="choose()">Choose File</button>' +
                                    '<button class="btn btn-small btn-primary" style="margin-right: 5px;" ' +
                                        'ng-show="isUploading" ng-click="cancel()">Cancel</button>' +
                                    '<span>{{ uploadMessage }}</span>' +
                                '</div>' +
                            '</div>';

                        propertyInput = $(fileUploadTemplate);

                        scope.isUploading = false;
                        scope.uploadHandle = null;

                        // Preload the file details if there was a file already saved
                        if (scope.propertyObject) {
                            scope.upload = File.get({id: scope.propertyObject});
                        }

                        scope.choose = function() {
                            // Trigger a click of the hidden file input box
                            propertyInput.find('.file-upload-input').trigger('click');
                        };

                        scope.cancel = function() {
                            scope.uploadHandle.abort();
                            scope.uploadMessage = 'Cancelled...';
                            scope.isUploading = false;
                        };

                        scope.reset = function() {
                            scope.upload = null;
                            scope.uploadMessage = null;
                            scope.propertyObject = '';
                        };

                        scope.add = function(e, data) {
                            scope.uploadHandle = data.submit();
                            scope.isUploading = true;
                        };

                        scope.done = function(e, data) {
                            scope.uploadMessage = 'Uploaded... 100%';
                            scope.upload = data.result;
                            scope.propertyObject = scope.upload.id;
                            scope.isUploading = false;
                        };

                        scope.fail = function(e, data) {
                            scope.uploadMessage = 'Upload Failed... ' + data.errorThrown;
                            scope.isUploading = false;
                        };

                        scope.progressall = function(e, data) {
                            var uploadProgress = parseInt(data.loaded / data.total * 100, 10);
                            scope.uploadMessage = 'Uploading... ' + uploadProgress + '%';
                        };

                        break;
                    }
                    default: {
                        // worst case fall back to text input
                        propertyInput = $('<input type="text"></input>')
                                .attr('ng-model', 'propertyObject')
                                .attr('name', propertyName)
                                .addClass('form-control');
                    }
                }

                // Adjust the size of the property is a property class was given
                if (scope.propertySize && propertyType !== 'time' && propertyType !== 'file') {
                    propertyInput.addClass(scope.propertySize);
                }

                // Flag the property as required
                if (scope.propertyConfig.required) {
                    // Some property types do not utilize the required attribute properly
                    if (propertyType !== 'time' && propertyType !== 'date' &&
                            propertyType !== 'boolean') {
                        propertyInput.attr('required', '');
                    }
                }

                // Append the generated property input the form element
                element.find('.streamflow-property-input').append(propertyInput);

                // Compile the new directive elements using the given settings
                $compile(propertyInput)(scope);
            }
        };
    }
);
