package burp_hotpatch.threads;

import burp_hotpatch.constants.Constants;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.util.Logger;

import java.util.ArrayList;

public class ScriptExecutionMonitorThread implements Runnable {
    private BurpHotpatchModel burpHotpatchModel = null;
    private boolean shutdownRequested = false;
    public ScriptExecutionMonitorThread( BurpHotpatchModel burpHotpatchModel ) {
        this.burpHotpatchModel = burpHotpatchModel;
    }

    public void shutdown() {
        shutdownRequested = true;
    }


    public void killLongRunning() {
        for ( ScriptExecutionThread scriptExecutionThread : burpHotpatchModel.getThreadPool() ) {
            if ( scriptExecutionThread.getExecutionTime() > Constants.THREAD_MAX_RUNTIME_MSEC ) {
                scriptExecutionThread.setTerminationReason("Max execution time exceeded");
                scriptExecutionThread.terminate();
                Logger.log("INFO","Terminating long running thread");
            }
        }
    }

    public void joinCompletedThreads() {
        for ( ScriptExecutionThread scriptExecutionThread : burpHotpatchModel.getThreadPool() ) {
            try {
                if ( scriptExecutionThread.getState().equals(Thread.State.TERMINATED)) {
                    scriptExecutionThread.join(100);
                    Logger.log("DEBUG", String.format("JOINED THREAD %s", scriptExecutionThread.getScriptExecutionContainer().getId()));
                    burpHotpatchModel.removeThread(scriptExecutionThread.getScriptExecutionContainer().getId());
                    burpHotpatchModel.removeRunningTask(scriptExecutionThread.getScriptExecutionContainer().getId());
                    if ( scriptExecutionThread.getTerminationReason() != null ) {
                        Logger.log("INFO", String.format("Terminated script %s for reason: %s", scriptExecutionThread.getScriptName(), scriptExecutionThread.getTerminationReason()));
                    }
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        while ( !shutdownRequested ) {
            if (Constants.LONG_RUNNING_THREAD_TERMINATION ) {
                killLongRunning();
            }
            joinCompletedThreads();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Logger.log("INFO","Script execution monitor thread exiting");
    }
}
