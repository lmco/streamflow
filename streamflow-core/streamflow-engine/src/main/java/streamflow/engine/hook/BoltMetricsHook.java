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
package streamflow.engine.hook;

import backtype.storm.hooks.BaseTaskHook;
import backtype.storm.hooks.info.BoltAckInfo;
import backtype.storm.hooks.info.BoltFailInfo;
import backtype.storm.hooks.info.EmitInfo;
import backtype.storm.task.TopologyContext;
import java.util.Map;

public class BoltMetricsHook extends BaseTaskHook {
    
    @Override
    public void prepare(Map config, TopologyContext topologyContext) {
    }
    
    @Override
    public void boltAck(BoltAckInfo info) {
    }
    
    @Override
    public void boltFail(BoltFailInfo info) {
    }
    
    @Override
    public void emit(EmitInfo info) {
    }
    
    @Override
    public void cleanup() {
    }
}
