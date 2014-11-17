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
package streamflow.model.util;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Test;

public class DateFormatTest {

    @Test
    public void serializeDateToLong() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule serializerModule = new SimpleModule("SerializerModule", new Version(1, 0, 0, null));
        serializerModule.addSerializer(Date.class, new DateSerializer());
        mapper.registerModule(serializerModule);
        
        Date testDate = new Date();
        
        assertEquals("Serialized time should match Date.getTime()",
                Long.valueOf(testDate.getTime()), mapper.convertValue(testDate, Long.class));
    }

    @Test
    public void deserializeLongToDate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule deserializerModule = new SimpleModule("DeserializerModule", new Version(1, 0, 0, null));
        deserializerModule.addDeserializer(Date.class, new DateDeserializer());
        mapper.registerModule(deserializerModule);
        
        Date testDate = new Date();
        
        assertEquals("Deserialized time should match Date.getTime()",
                testDate, mapper.convertValue(testDate.getTime(), Date.class));
    }
}
