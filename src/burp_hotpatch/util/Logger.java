package burp_hotpatch.util;

import burp.api.montoya.logging.Logging;

public class Logger {
    private static Logging logger = null;

    public static void log(String status, String message) {
        if (logger != null) {
            switch (status) {
                case "ERROR":
                    logger.raiseErrorEvent(message);
                    break;
                case "WARN":
                case "INFO":
                    logger.raiseInfoEvent(message);
                    break;
                case "DEBUG":
                default:
                    logger.raiseDebugEvent(message);
                    break;
            }
            
        } else {
            System.out.println(String.format("[%s] %s", status, message));
        }
    }

    public static void setLogger(Logging logger) {
        Logger.logger = logger;
    }
}
