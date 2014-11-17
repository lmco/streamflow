package streamflow.model.test;

import java.io.Serializable;
import java.util.Arrays;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("test")
public class TestEntity implements streamflow.model.util.Entity<String>, Serializable {

    @Id
    private String id;
    
    private byte byteField = Byte.MAX_VALUE;
    
    private int intField = Integer.MAX_VALUE;
    
    private long longField = Long.MAX_VALUE;
    
    private double doubleField = 2.001;
    
    private float floatField = 1.5f;
    
    private boolean booleanField = true;
    
    private String stringField = "Hello, World!";
    
    private byte[] byteArrayField = "Hello, World!".getBytes();
    
    
    public TestEntity() {
    }

    @Override
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public byte getByteField() {
        return byteField;
    }

    public void setByteField(byte byteField) {
        this.byteField = byteField;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public boolean getBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    public byte[] getByteArrayField() {
        return byteArrayField;
    }

    public void setByteArrayField(byte[] byteArrayField) {
        this.byteArrayField = byteArrayField;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 11 * hash + this.byteField;
        hash = 11 * hash + this.intField;
        hash = 11 * hash + (int) (this.longField ^ (this.longField >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.doubleField) 
                ^ (Double.doubleToLongBits(this.doubleField) >>> 32));
        hash = 11 * hash + Float.floatToIntBits(this.floatField);
        hash = 11 * hash + (this.booleanField ? 1 : 0);
        hash = 11 * hash + (this.stringField != null ? this.stringField.hashCode() : 0);
        hash = 11 * hash + Arrays.hashCode(this.byteArrayField);
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
        final TestEntity other = (TestEntity) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.byteField != other.byteField) {
            return false;
        }
        if (this.intField != other.intField) {
            return false;
        }
        if (this.longField != other.longField) {
            return false;
        }
        if (Double.doubleToLongBits(this.doubleField) 
                != Double.doubleToLongBits(other.doubleField)) {
            return false;
        }
        if (Float.floatToIntBits(this.floatField) 
                != Float.floatToIntBits(other.floatField)) {
            return false;
        }
        if (this.booleanField != other.booleanField) {
            return false;
        }
        if ((this.stringField == null) ? (other.stringField != null) 
                : !this.stringField.equals(other.stringField)) {
            return false;
        }
        if (!Arrays.equals(this.byteArrayField, other.byteArrayField)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestEntity{" + "id=" + id + ", byteField=" + byteField + ", intField=" + intField 
                + ", longField=" + longField + ", doubleField=" + doubleField 
                + ", floatField=" + floatField + ", booleanField=" + booleanField 
                + ", stringField=" + stringField + ", byteArrayField=" + byteArrayField + "}";
    }
}
