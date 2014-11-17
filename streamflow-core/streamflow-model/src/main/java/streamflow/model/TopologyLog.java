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
package streamflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TopologyLog implements Serializable {

    private long offset;

    private long count;

    private List<String> lines = new ArrayList<String>();

    
    public TopologyLog() {
    }

    public TopologyLog(long offset, long count, ArrayList<String> lines) {
        this.offset = offset;
        this.count = count;
        this.lines = lines;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.offset ^ (this.offset >>> 32));
        hash = 97 * hash + (int) (this.count ^ (this.count >>> 32));
        hash = 97 * hash + (this.lines != null ? this.lines.hashCode() : 0);
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
        final TopologyLog other = (TopologyLog) obj;
        if (this.offset != other.offset) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        if (this.lines != other.lines && (this.lines == null || !this.lines.equals(other.lines))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyLog{" + "offset=" + offset + ", count=" + count + ", lines=" + lines + '}';
    }
}
