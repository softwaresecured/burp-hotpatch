package burp_hotpatch.scripts;
public class ScriptSharedMemoryException extends Exception {
    public ScriptSharedMemoryException(String errorMessage) {
        super(errorMessage);
    }
}