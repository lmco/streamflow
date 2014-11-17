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
var notifyModule = angular.module('streamflow.notify', ['cgNotify']);

notifyModule.service('streamflowNotify', function(notify) {
        
    notify.config({
        duration: 3000
    });
    
    // Indicate that the notify service should use the streamflow notify template
    this.notify = function(type, title, message) {
        // Use the default type of info if an incorrect type is specified
        if (type !== 'info' && type !== 'success' && type !== 'danger' && type !== 'warning') {
            type = 'info';
        }

        // Display the notification message using the cgNotify 3rd party service
        notify({
            message: message,
            classes: 'alert-' + type,
            messageTemplate: 
                '<span>' +
                    '<h4 class="text-left" style="text-transform: capitalize;">' + title + '</h4>' +
                    '{{ $message }}' + 
                '</span>'
        });
    };

    this.info = function(message) {
        this.notify('info', 'info', message);
    };

    this.success = function(message) {
        this.notify('success', 'success', message);
    };

    this.warning = function(message) {
        this.notify('warning', 'warning', message);
    };

    this.error = function(message) {
        this.notify('danger', 'error', message);
    };
});