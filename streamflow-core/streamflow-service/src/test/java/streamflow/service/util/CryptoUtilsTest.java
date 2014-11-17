package streamflow.service.util;

import streamflow.service.util.CryptoUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public class CryptoUtilsTest {

    @Test
    public void generateSaltIsRandom() {
        assertFalse(CryptoUtils.generateSalt().equals(CryptoUtils.generateSalt()));
        assertFalse(CryptoUtils.generateSalt().equals(CryptoUtils.generateSalt()));
    }
    
    @Test
    public void verifyDefaultHashIterations() {
        assertEquals(CryptoUtils.hashPassword("password", "passwordSalt"),
                CryptoUtils.hashPassword("password", "passwordSalt", 2048));
    }
    
    @Test
    public void verifyPasswordAndSaltGenerateConsistentValue() {
        assertEquals(CryptoUtils.hashPassword("password", "WJK2Zv3oy4VC2t0eilUxWQ=="),
                "LumJwyW0UKSgltUzn9g1WMdwef29MOYXtqgJf8PVcZaw96U4oCidS0QZLlTHjKfXkT0/xvxk+LGRyIiniDwGQg==");
        
        assertEquals(CryptoUtils.hashPassword("password", "WJK2Zv3oy4VC2t0eilUxWQ==", 4096),
                "SOvuBHsNrEKz3mKsJ8cJW3xZ6NrgVlIy8gBjXUqo3f/j6gm0VnhT/OYus0QpvxBVyy3EkUs8dj8phqGVXDstGw==");
    }
}
