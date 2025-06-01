package burp_hotpatch.enums;

public enum ScriptTypes {
    UTILITY,
    HTTP_HANDLER_REQUEST_TO_BE_SENT,
    HTTP_HANDLER_RESPONSE_RECEIVED,
    PROXY_HANDLER_REQUEST_RECEIVED,
    PROXY_HANDLER_REQUEST_TO_BE_SENT,
    SESSION_HANDLING_ACTION,
    PAYLOAD_PROCESSOR,
    AUDIT_ISSUE_HANDLER,
    CONTEXT_MENU_ACTION;

    public static String getCategory ( ScriptTypes scriptType ) {
        switch ( scriptType ) {
            case HTTP_HANDLER_REQUEST_TO_BE_SENT:
            case HTTP_HANDLER_RESPONSE_RECEIVED:
                return "HTTP";
            case PROXY_HANDLER_REQUEST_RECEIVED:
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                return "Proxy";
            case SESSION_HANDLING_ACTION:
                return "Session";
            case PAYLOAD_PROCESSOR:
                return "Payload";
            case AUDIT_ISSUE_HANDLER:
                return "Audit";
            case CONTEXT_MENU_ACTION:
                return "Menu";
            default:
                return "Utility";
        }
    }

    public static String toFriendlyName( ScriptTypes scriptType ) {
        switch (scriptType) {
            case UTILITY:
                return "Utility";
            case HTTP_HANDLER_REQUEST_TO_BE_SENT:
                return "Http handler ( request )";
            case HTTP_HANDLER_RESPONSE_RECEIVED:
                return "Http handler ( response )";
            case PROXY_HANDLER_REQUEST_RECEIVED:
                return "Proxy handler received";
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                return "Proxy handler to be sent";
            case SESSION_HANDLING_ACTION:
                return "Session handling action";
            case PAYLOAD_PROCESSOR:
                return "Payload processor";
            case AUDIT_ISSUE_HANDLER:
                return "Audit issue handler";
            case CONTEXT_MENU_ACTION:
                return "Context menu action";
        }
        return null;
    }

    public static ScriptTypes fromFriendlyName( String name ) {
        switch (name) {
            case "Utility":
                return UTILITY;
            case "Http handler ( request )":
                return HTTP_HANDLER_REQUEST_TO_BE_SENT;
            case "Http handler ( response )":
                return HTTP_HANDLER_RESPONSE_RECEIVED;
            case "Proxy handler received":
                return PROXY_HANDLER_REQUEST_RECEIVED;
            case "Proxy handler to be sent":
                return PROXY_HANDLER_REQUEST_TO_BE_SENT;
            case "Session handling action":
                return SESSION_HANDLING_ACTION;
            case "Payload processor":
                return PAYLOAD_PROCESSOR;
            case "Audit issue handler":
                return AUDIT_ISSUE_HANDLER;
            case "Context menu action":
                return CONTEXT_MENU_ACTION;
        }
        return null;
    }
}
