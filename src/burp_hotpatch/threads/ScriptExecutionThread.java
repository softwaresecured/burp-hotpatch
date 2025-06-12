package burp_hotpatch.threads;
import burp_hotpatch.scripts.ScriptExecutionContainer;
import burp_hotpatch.scripts.ScriptExecutionException;
import burp_hotpatch.util.Logger;

public class ScriptExecutionThread extends Thread {
    private ScriptExecutionContainer scriptExecutionContainer;
    private String terminationReason = null;
    private long startTime = 0;
    public ScriptExecutionThread ( ScriptExecutionContainer scriptExecutionContainer )  {
        this.scriptExecutionContainer = scriptExecutionContainer;
    }


    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        try {
            scriptExecutionContainer.execute();
        } catch (ScriptExecutionException e) {
            Logger.log("ERROR", String.format("Failed to execute script: %s", e.getMessage()));
        }
    }

    public long getExecutionTime() {
        return System.currentTimeMillis()-startTime;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public String getScriptName() {
        return scriptExecutionContainer.getScript().getName();
    }

    public void terminate() {
        Logger.log("INFO", String.format("ATTEMPTING TO TERMINATE %s", getScriptName()));
        scriptExecutionContainer.terminate();
    }

    public ScriptExecutionContainer getScriptExecutionContainer() {
        return scriptExecutionContainer;
    }
}
