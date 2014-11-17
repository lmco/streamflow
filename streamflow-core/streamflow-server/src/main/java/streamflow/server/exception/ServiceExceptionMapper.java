/**
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
package streamflow.server.exception;

import com.google.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;

@Provider
@Singleton
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException exception) {
        ResponseBuilder response;

        // Use the proper status type based on the type of exception
        if (exception instanceof EntityConflictException) {
            response = Response.status(Status.CONFLICT);
        } else if (exception instanceof EntityInvalidException) {
            response = Response.status(Status.BAD_REQUEST);
        } else if (exception instanceof EntityNotFoundException) {
            response = Response.status(Status.NOT_FOUND);
        } else {
            response = Response.status(Status.INTERNAL_SERVER_ERROR);
        }

        // Add the optional message content to the entity
        return response.type(MediaType.TEXT_PLAIN).entity(exception.getMessage()).build();
    }
}
