package streamflow.service.util;

import streamflow.service.util.IDUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public class IDUtilsTest {
    
    @Test
    public void formatPlainTextToId() {
        // Note: IDUtils.formatId should:
        //      (1) Remove leading/trailing whitespace
        //      (2) Remove any non alphanumeric values
        //      (3) Convert all uppercase values to lowercase
        //      (4) Combine all interal consecutive whitespace to a single dash
        assertEquals(IDUtils.formatId("   Hello World   "), "hello-world");
        assertEquals(IDUtils.formatId("He!l@ W#Rld"), "hel-wrld");
        assertEquals(IDUtils.formatId("HELLO-__-WORLD"), "hello-world");
        assertEquals(IDUtils.formatId("  HELLO    WORLD  "), "hello-world");
    }

    @Test
    public void generateRandomId() {
        assertNotNull("Random UUID should be generated", IDUtils.randomUUID());
    }
}
