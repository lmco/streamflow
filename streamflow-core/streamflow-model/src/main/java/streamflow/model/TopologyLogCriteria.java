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

public class TopologyLogCriteria implements Serializable {
    
    private String query;
    
    private String component;
    
    private String level;
    
    private String category;
    
    private String age;
    
    private int pageNum = 1;
    
    private int pageSize = 100;
    
    private boolean showHistoric = false;
    
    private SortOrder sortOrder = SortOrder.DESC;
    
    public enum SortOrder {
        ASC,
        DESC,
    }
    
    
    public TopologyLogCriteria() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean getShowHistoric() {
        return showHistoric;
    }

    public void setShowHistoric(boolean showHistoric) {
        this.showHistoric = showHistoric;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.query != null ? this.query.hashCode() : 0);
        hash = 59 * hash + (this.component != null ? this.component.hashCode() : 0);
        hash = 59 * hash + (this.level != null ? this.level.hashCode() : 0);
        hash = 59 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 59 * hash + (this.age != null ? this.age.hashCode() : 0);
        hash = 59 * hash + this.pageNum;
        hash = 59 * hash + this.pageSize;
        hash = 59 * hash + (this.showHistoric ? 1 : 0);
        hash = 59 * hash + (this.sortOrder != null ? this.sortOrder.hashCode() : 0);
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
        final TopologyLogCriteria other = (TopologyLogCriteria) obj;
        if ((this.query == null) ? (other.query != null) : !this.query.equals(other.query)) {
            return false;
        }
        if ((this.component == null) ? (other.component != null) : !this.component.equals(other.component)) {
            return false;
        }
        if ((this.level == null) ? (other.level != null) : !this.level.equals(other.level)) {
            return false;
        }
        if ((this.category == null) ? (other.category != null) : !this.category.equals(other.category)) {
            return false;
        }
        if ((this.age == null) ? (other.age != null) : !this.age.equals(other.age)) {
            return false;
        }
        if (this.pageNum != other.pageNum) {
            return false;
        }
        if (this.pageSize != other.pageSize) {
            return false;
        }
        if (this.showHistoric != other.showHistoric) {
            return false;
        }
        if (this.sortOrder != other.sortOrder) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TopologyLogCriteria{" + "query=" + query + ", component=" + component 
                + ", level=" + level + ", category=" + category + ", age=" + age 
                + ", pageNum=" + pageNum + ", pageSize=" + pageSize 
                + ", showHistoric=" + showHistoric + ", sortOrder=" + sortOrder + '}';
    }
}
