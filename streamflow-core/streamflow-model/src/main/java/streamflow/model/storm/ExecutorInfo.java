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
package streamflow.model.storm;

import java.io.Serializable;

public class ExecutorInfo implements Serializable {

    private int taskStart;

    private int taskEnd;

    
    public ExecutorInfo() {
    }

    public int getTaskStart() {
        return taskStart;
    }

    public void setTaskStart(int taskStart) {
        this.taskStart = taskStart;
    }

    public int getTaskEnd() {
        return taskEnd;
    }

    public void setTaskEnd(int taskEnd) {
        this.taskEnd = taskEnd;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.taskStart;
        hash = 97 * hash + this.taskEnd;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutorInfo other = (ExecutorInfo) obj;
        if (this.taskStart != other.taskStart) {
            return false;
        }
        if (this.taskEnd != other.taskEnd) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExecutorInfo{" + "taskStart=" + taskStart + ", taskEnd=" + taskEnd + '}';
    }
}
