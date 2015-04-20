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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.ArrayList;
import org.mongodb.morphia.annotations.Embedded;

@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ResourcePropertyOptions implements Serializable {

    private Integer numericStep;

    private Integer maxNumber;

    private Integer minNumber;

    private Double floatStep;

    private String displayUnits;

    private String dateFormat;

    private Integer minuteStep;
    
    private Integer numRows;

    @Embedded
    private ArrayList<String> listItems = new ArrayList<String>();

    
    public ResourcePropertyOptions() {
    }

    public Integer getNumericStep() {
        return numericStep;
    }

    public void setNumericStep(Integer numericStep) {
        this.numericStep = numericStep;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(Integer minNumber) {
        this.minNumber = minNumber;
    }

    public Double getFloatStep() {
        return floatStep;
    }

    public void setFloatStep(Double floatStep) {
        this.floatStep = floatStep;
    }

    public String getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(String displayUnits) {
        this.displayUnits = displayUnits;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public ArrayList<String> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<String> listItems) {
        this.listItems = listItems;
    }

    public Integer getMinuteStep() {
        return minuteStep;
    }

    public void setMinuteStep(Integer minuteStep) {
        this.minuteStep = minuteStep;
    }

    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer numRows) {
        this.numRows = numRows;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.numericStep != null ? this.numericStep.hashCode() : 0);
        hash = 59 * hash + (this.maxNumber != null ? this.maxNumber.hashCode() : 0);
        hash = 59 * hash + (this.minNumber != null ? this.minNumber.hashCode() : 0);
        hash = 59 * hash + (this.floatStep != null ? this.floatStep.hashCode() : 0);
        hash = 59 * hash + (this.displayUnits != null ? this.displayUnits.hashCode() : 0);
        hash = 59 * hash + (this.dateFormat != null ? this.dateFormat.hashCode() : 0);
        hash = 59 * hash + (this.minuteStep != null ? this.minuteStep.hashCode() : 0);
        hash = 59 * hash + (this.numRows != null ? this.numRows.hashCode() : 0);
        hash = 59 * hash + (this.listItems != null ? this.listItems.hashCode() : 0);
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
        final ResourcePropertyOptions other = (ResourcePropertyOptions) obj;
        if (this.numericStep != other.numericStep && (this.numericStep == null 
                || !this.numericStep.equals(other.numericStep))) {
            return false;
        }
        if (this.maxNumber != other.maxNumber && (this.maxNumber == null 
                || !this.maxNumber.equals(other.maxNumber))) {
            return false;
        }
        if (this.minNumber != other.minNumber && (this.minNumber == null 
                || !this.minNumber.equals(other.minNumber))) {
            return false;
        }
        if (this.floatStep != other.floatStep && (this.floatStep == null 
                || !this.floatStep.equals(other.floatStep))) {
            return false;
        }
        if ((this.displayUnits == null) ? (other.displayUnits != null) 
                : !this.displayUnits.equals(other.displayUnits)) {
            return false;
        }
        if ((this.dateFormat == null) ? (other.dateFormat != null) 
                : !this.dateFormat.equals(other.dateFormat)) {
            return false;
        }
        if (this.minuteStep != other.minuteStep && (this.minuteStep == null 
                || !this.minuteStep.equals(other.minuteStep))) {
            return false;
        }
        if (this.numRows != other.numRows && (this.numRows == null 
                || !this.numRows.equals(other.numRows))) {
            return false;
        }
        if (this.listItems != other.listItems && (this.listItems == null 
                || !this.listItems.equals(other.listItems))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourcePropertyOptions{" + "numericStep=" + numericStep 
                + ", maxNumber=" + maxNumber + ", minNumber=" + minNumber 
                + ", floatStep=" + floatStep + ", displayUnits=" + displayUnits 
                + ", dateFormat=" + dateFormat + ", minuteStep=" + minuteStep 
                + ", numRows=" + numRows + ", listItems=" + listItems + '}';
    }
}
