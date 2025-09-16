import burp_hotpatch.scripts.ScriptSharedMemory;
import burp_hotpatch.scripts.ScriptSharedMemoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedMemoryTests {
    @Test
    @DisplayName("Test shared memory read and write string")
    public void sharedMemoryReadWriteString() throws ScriptSharedMemoryException {
        String testVal = "1234";
        ScriptSharedMemory sharedMemory = ScriptSharedMemory.getInstance();
        sharedMemory.setString("test",testVal);
        assertEquals(testVal, sharedMemory.getString("test"));
    }

    @Test
    @DisplayName("Test shared memory read and write integer")
    public void sharedMemoryReadInteger() throws ScriptSharedMemoryException {
        int testVal = 1234;
        ScriptSharedMemory sharedMemory = ScriptSharedMemory.getInstance();
        sharedMemory.setInt("test",testVal);
        assertEquals(testVal, sharedMemory.getInt("test"));
    }
}
