import burp_hotpatch.scripts.ScriptSharedMemory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SharedMemoryTests {
    @Test
    @DisplayName("Test shared memory read and write string")
    public void sharedMemoryReadWriteString() {
        String testVal = "1234";
        ScriptSharedMemory sharedMemory = ScriptSharedMemory.getInstance();
        sharedMemory.setString("test",testVal);
        assertEquals(testVal, sharedMemory.getString("test"));
    }

    @Test
    @DisplayName("Test shared memory read and write integer")
    public void sharedMemoryReadInteger() {
        int testVal = 1234;
        ScriptSharedMemory sharedMemory = ScriptSharedMemory.getInstance();
        sharedMemory.setInt("test",testVal);
        assertEquals(testVal, sharedMemory.getInt("test"));
    }


    @Test
    @DisplayName("Test shared memory read a missing key becomes null")
    public void sharedMemoryReadIntegerNull() {
        ScriptSharedMemory sharedMemory = ScriptSharedMemory.getInstance();
        assertNull(sharedMemory.getInt("asdf"));
    }
}
