package burp_hotpatch.scripts;
public class ScriptExecutionException extends Exception {
    public ScriptExecutionException(String errorMessage) {
        super(errorMessage);
    }
}