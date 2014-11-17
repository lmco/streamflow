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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.exception.ServiceException;

import static org.junit.Assert.*;
import org.junit.Test;

public class ServiceExceptionMapperTest {
    
    private final ServiceExceptionMapper mapper = new ServiceExceptionMapper();
    
    @Test
    public void mapEntityConflictExceptionToResponse() {
        Response entityConflictResponse = mapper.toResponse(
                new EntityConflictException("Entity Conflict Message"));
        
        assertEquals(entityConflictResponse.getStatus(), Status.CONFLICT.getStatusCode());
        assertEquals(entityConflictResponse.getEntity(), "Entity Conflict Message");
    }
    
    @Test
    public void mapEntityInvalidExceptionToResponse() {
        Response entityInvalidResponse = mapper.toResponse(
                new EntityInvalidException("Entity Invalid Message"));
        
        assertEquals(entityInvalidResponse.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertEquals(entityInvalidResponse.getEntity(), "Entity Invalid Message");
    }
    
    @Test
    public void mapEntityNotFoundExceptionToResponse() {
        Response entityNotFoundResponse = mapper.toResponse(
                new EntityNotFoundException("Entity Not Found Message"));
        
        assertEquals(entityNotFoundResponse.getStatus(), Status.NOT_FOUND.getStatusCode());
        assertEquals(entityNotFoundResponse.getEntity(), "Entity Not Found Message");
    }
    
    @Test
    public void mapGenericServiceExceptionToResponse() {
        Response serviceExceptionResponse = mapper.toResponse(
                new ServiceException("Generic Service Message"));
        
        assertEquals(serviceExceptionResponse.getStatus(), Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertEquals(serviceExceptionResponse.getEntity(), "Generic Service Message");
    }
}
