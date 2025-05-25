package burp_jyconsole.util;

import burp.api.montoya.MontoyaApi;

/**
 * A helper class to gain access to the MontoyaAPI when required
 */
public final class MontoyaUtil {

    private MontoyaApi montoyaApi;

    private static MontoyaUtil INSTANCE;
    public MontoyaUtil() {
    }

    public static MontoyaUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MontoyaUtil();
        }

        return INSTANCE;
    }

    public void setMontoyaApi( MontoyaApi montoyaApi ) {
        this.montoyaApi = montoyaApi;
    }

    public MontoyaApi getApi() {
        return montoyaApi;
    }

    /**
     * Logs a debug message to the Burp event log
     * @param message
     */
    public void logDebugMessage(String message ) {
        montoyaApi.logging().raiseDebugEvent(message);
    }

    /**
     * Logs a message to the stderr
     * @param message
     */
    public void logStderr( String message ) {
        montoyaApi.logging().logToError(message);
    }
}
