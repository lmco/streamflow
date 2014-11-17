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
package streamflow.model.generator;

import streamflow.model.TopologyProperties;
import streamflow.model.Topology;
import streamflow.model.TopologyLogPage;
import streamflow.model.TopologyResourceEntry;
import streamflow.model.TopologyComponent;
import streamflow.model.TopologySerialization;
import streamflow.model.Serialization;
import streamflow.model.ComponentPropertyOptions;
import streamflow.model.Cluster;
import streamflow.model.FileInfo;
import streamflow.model.ResourceEntry;
import streamflow.model.TopologyLogCriteria;
import streamflow.model.PasswordChange;
import streamflow.model.ResourceProperty;
import streamflow.model.ComponentConfig;
import streamflow.model.ComponentInterface;
import streamflow.model.ResourceEntryConfig;
import streamflow.model.FrameworkConfig;
import streamflow.model.TopologyConfig;
import streamflow.model.SerializationConfig;
import streamflow.model.TopologyLog;
import streamflow.model.TopologyFields;
import streamflow.model.TopologyConnector;
import streamflow.model.TopologyLogEntry;
import streamflow.model.User;
import streamflow.model.Role;
import streamflow.model.ResourceConfig;
import streamflow.model.Framework;
import streamflow.model.Component;
import streamflow.model.ResourcePropertyOptions;
import streamflow.model.ComponentProperty;
import streamflow.model.Resource;
import streamflow.model.test.TestEntity;
import static org.junit.Assert.*;
import org.junit.Test;

public class RandomGeneratorTest {

    @Test
    public void generateEntityObjects() {
        assertNotNull(RandomGenerator.randomObject(Cluster.class));
        assertNotNull(RandomGenerator.randomObject(Component.class));
        assertNotNull(RandomGenerator.randomObject(ComponentConfig.class));
        assertNotNull(RandomGenerator.randomObject(ComponentInterface.class));
        assertNotNull(RandomGenerator.randomObject(ComponentProperty.class));
        assertNotNull(RandomGenerator.randomObject(ComponentPropertyOptions.class));
        assertNotNull(RandomGenerator.randomObject(FileInfo.class));
        assertNotNull(RandomGenerator.randomObject(Framework.class));
        assertNotNull(RandomGenerator.randomObject(FrameworkConfig.class));
        assertNotNull(RandomGenerator.randomObject(PasswordChange.class));
        assertNotNull(RandomGenerator.randomObject(Resource.class));
        assertNotNull(RandomGenerator.randomObject(ResourceConfig.class));
        assertNotNull(RandomGenerator.randomObject(ResourceEntry.class));
        assertNotNull(RandomGenerator.randomObject(ResourceEntryConfig.class));
        assertNotNull(RandomGenerator.randomObject(ResourceProperty.class));
        assertNotNull(RandomGenerator.randomObject(ResourcePropertyOptions.class));
        assertNotNull(RandomGenerator.randomObject(Role.class));
        assertNotNull(RandomGenerator.randomObject(Serialization.class));
        assertNotNull(RandomGenerator.randomObject(SerializationConfig.class));
        assertNotNull(RandomGenerator.randomObject(TestEntity.class));
        assertNotNull(RandomGenerator.randomObject(Topology.class));
        assertNotNull(RandomGenerator.randomObject(TopologyComponent.class));
        assertNotNull(RandomGenerator.randomObject(TopologyConfig.class));
        assertNotNull(RandomGenerator.randomObject(TopologyConnector.class));
        assertNotNull(RandomGenerator.randomObject(TopologyFields.class));
        assertNotNull(RandomGenerator.randomObject(TopologyLog.class));
        assertNotNull(RandomGenerator.randomObject(TopologyLogCriteria.class));
        assertNotNull(RandomGenerator.randomObject(TopologyLogEntry.class));
        assertNotNull(RandomGenerator.randomObject(TopologyLogPage.class));
        assertNotNull(RandomGenerator.randomObject(TopologyProperties.class));
        assertNotNull(RandomGenerator.randomObject(TopologyResourceEntry.class));
        assertNotNull(RandomGenerator.randomObject(TopologySerialization.class));
        assertNotNull(RandomGenerator.randomObject(User.class));
    }
}
