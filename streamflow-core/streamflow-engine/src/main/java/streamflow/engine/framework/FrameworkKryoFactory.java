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
package streamflow.engine.framework;

import backtype.storm.Config;
import backtype.storm.serialization.IKryoFactory;
import backtype.storm.serialization.SerializableSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import streamflow.model.Topology;
import streamflow.model.TopologyConfig;
import streamflow.model.TopologySerialization;
import streamflow.engine.topology.TopologySubmitter;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkKryoFactory implements IKryoFactory {

    protected static final Logger LOG = LoggerFactory.getLogger(FrameworkKryoFactory.class);

    public static final String KRYO_REALM = "kryo-realm";

    public static class KryoSerializableDefault extends Kryo {

        boolean override = false;

        public void overrideDefault(boolean value) {
            override = value;
        }

        @Override
        public Serializer getDefaultSerializer(Class type) {
            if (override) {
                return new SerializableSerializer();
            } else {
                return super.getDefaultSerializer(type);
            }
        }
    }

    @Override
    public Kryo getKryo(Map conf) {
        KryoSerializableDefault kryo = new KryoSerializableDefault();
        kryo.setRegistrationRequired(!((Boolean) conf.get(
                Config.TOPOLOGY_FALL_BACK_ON_JAVA_SERIALIZATION)));
        kryo.setReferences(false);

        // Initialize this topology with the the serialization realms
        try {
            ////////////////////////////////////////////////////////////////////////////////////////
            // TODO: NEED TO FIND A WAY TO LOAD THE LIST OF SERIALZATIONS WITHOUT TOPOLOGY.JSON
            //       AS THIS METHOD WILL ONLY WORK FOR CLUSTER DEPLOYS
            ////////////////////////////////////////////////////////////////////////////////////////
            
            String topologyJson = IOUtils.toString(
                    TopologySubmitter.class.getClassLoader().getResourceAsStream(
                            "STREAMFLOW-INF/topology.json"));

            // Retrieve the topology from the inbuilt topology json file
            ObjectMapper mapper = new ObjectMapper();

            Topology topology = mapper.readValue(topologyJson, Topology.class);
            TopologyConfig topologyConfig = topology.getDeployedConfig();

            // Iterate over all of the registered serializations and add them to the realm
            for (TopologySerialization serialization : topologyConfig.getSerializations()) {

                try {
                    // Retrieve the required serialization class from the Kryo Realm
                    Class typeClass = FrameworkUtils.getInstance().loadFrameworkClass(
                            serialization.getFrameworkHash(), serialization.getTypeClass(),
                            topology.getClassLoaderPolicy());

                    // Retrieve the optional serializer class
                    Class<Serializer> serializerClass = null;
                    if (serialization.getSerializerClass() != null) {
                        serializerClass = FrameworkUtils.getInstance().loadFrameworkClass(
                                serialization.getFrameworkHash(), serialization.getSerializerClass(),
                                topology.getClassLoaderPolicy());
                    }

                    if (serializerClass == null) {
                        // Register the type class using the default Kryo Fields Serializer
                        kryo.register(typeClass);

                        LOG.info("Streamflow Registered serialization: Type Class = "
                                + serialization.getTypeClass());
                    } else {
                        // Register the type class using the custom serializer
                        kryo.register(typeClass, serializerClass.newInstance());

                        LOG.info("Streamflow Registered serialization: Type Class = "
                                + serialization.getTypeClass() + ", Serializer Class = "
                                + serialization.getSerializerClass());
                    }
                } catch (Exception ex) {
                    LOG.error("Exception while registering serialization: "
                            + serialization.getTypeClass() + ": ", ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception while initializing the Streamflow Framework Kryo Factory: ", ex);
        }

        return kryo;
    }

    @Override
    public void preRegister(Kryo kryo, Map map) {
    }

    @Override
    public void postRegister(Kryo kryo, Map map) {
        ((KryoSerializableDefault) kryo).overrideDefault(true);
    }

    @Override
    public void postDecorate(Kryo kryo, Map map) {
    }
}
