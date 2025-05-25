package burp_jyconsole.enums;

public enum ScriptTypes {
    UTILITY,
    HTTP_HANDLER,
    PROXY_HANDLER_REQUEST_RECEIVED,
    PROXY_HANDLER_REQUEST_TO_BE_SENT,
    SESSION_HANDLING_ACTION,
    PAYLOAD_PROCESSOR;

    public static String getCategory ( ScriptTypes scriptType ) {
        switch ( scriptType ) {
            case HTTP_HANDLER:
                return "HTTP";
            case PROXY_HANDLER_REQUEST_RECEIVED:
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                return "Proxy";
            case SESSION_HANDLING_ACTION:
                return "Session";
            case PAYLOAD_PROCESSOR:
                return "Payload";
            default:
                return "Utility";
        }
    }

    public static String toFriendlyName( ScriptTypes scriptType ) {
        switch (scriptType) {
            case UTILITY:
                return "Utility";
            case HTTP_HANDLER:
                return "Http handler";
            case PROXY_HANDLER_REQUEST_RECEIVED:
                return "Proxy handler received";
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                return "Proxy handler to be sent";
            case SESSION_HANDLING_ACTION:
                return "Session handling action";
            case PAYLOAD_PROCESSOR:
                return "Payload processor";
        }
        return null;
    }

    public static ScriptTypes fromFriendlyName( String name ) {
        switch (name) {
            case "Utility":
                return UTILITY;
            case "Http handler":
                return HTTP_HANDLER;
            case "Proxy handler received":
                return PROXY_HANDLER_REQUEST_RECEIVED;
            case "Proxy handler to be sent":
                return PROXY_HANDLER_REQUEST_TO_BE_SENT;
            case "Session handling action":
                return SESSION_HANDLING_ACTION;
            case "Payload processor":
                return PAYLOAD_PROCESSOR;
        }
        return null;
    }
}
