package burp_jyconsole.constants;

import burp_jyconsole.enums.ScriptTypes;

public class ScriptConstants {
    // Utility
    public static final String UTILITY_SCRIPT =
    """
    def main( montoyaApi ):
        print("Hello from " + montoyaApi.burpSuite().version().toString())
    """;
    public static final String UTILITY_TEMPLATE =
    """
    __SCRIPT__
    main( montoyaApi )
    """;

    // Http handler
    public static final String HTTP_HANDLER_SCRIPT =
    """
    from burp.api.montoya.http.handler import RequestToBeSentAction
    from burp.api.montoya.http.handler import ResponseReceivedAction
    from burp.api.montoya.http.handler import HttpRequestToBeSent
    from burp.api.montoya.http.handler import HttpResponseReceived
    def handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent):
        return httpRequestToBeSent.withAddedHeader("DemoHttpHandler","Added by user defined script")
    
    def handleHttpResponseReceived(montoyaApi, httpResponseReceived):
        return None
    """;
    public static final String HTTP_HANDLER_TEMPLATE =
    """
    __SCRIPT__
    _script_result = None
    try:
         global _script_result
         _script_result = handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent)
    except NameError:
        pass
    try:
        global _script_result
        _script_result = handleHttpResponseReceived(montoyaApi, httpResponseReceived)
    except NameError:
        pass
    """;
    // Proxy handler ( received )
    public static final String PROXY_HANDLER_RECEIVED_SCRIPT =
    """
    from burp.api.montoya.proxy.http import ProxyRequestReceivedAction
    from burp.api.montoya.proxy.http import InterceptedRequest
    def handleRequestReceived(montoyaApi, interceptedRequest):
        return interceptedRequest.withAddedHeader("DemoProxyReceivedHandler","Added by user defined script")
    """;
    public static final String PROXY_HANDLER_RECEIVED_TEMPLATE =
    """
    __SCRIPT__
    _script_result = None
    if interceptedRequest is not None:
        _script_result = handleRequestReceived(montoyaApi, interceptedRequest)
    """;

    // Proxy handler ( To be Sent )
    public static final String PROXY_HANDLER_TO_BE_SENT_SCRIPT =
    """
    from burp.api.montoya.proxy.http import ProxyRequestToBeSentAction
    from burp.api.montoya.proxy.http import InterceptedRequest
    def handleRequestToBeSent(montoyaApi, interceptedRequest):
        return interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script")
    """;
    public static final String PROXY_HANDLER_TO_BE_SENT_TEMPLATE =
    """
    __SCRIPT__
    _script_result = None
    if interceptedRequest is not None:
        _script_result = handleRequestToBeSent(montoyaApi, interceptedRequest)
    """;

    // Session handling action
    public static final String SESSION_HANDLING_ACTION_SCRIPT =
    """
    from burp.api.montoya.http.sessions import ActionResult
    from burp.api.montoya.http.sessions import SessionHandlingActionData
    from java.util.regex import Matcher
    from java.util.regex import Pattern
    def performAction(montoyaApi, sessionHandlingActionData):
        p = Pattern.compile("\\"token\\"\\\\:\\"(.*?)\\"\\\\,\\"refreshToken\\"")
        m = p.matcher(sessionHandlingActionData.macroRequestResponses().getLast().response().toString())
        if m.find():
            return sessionHandlingActionData.request().withUpdatedHeader("X-Authorization","Bearer " + m.group(1))
        return sessionHandlingActionData.request()
    """;
    public static final String SESSION_HANDLING_ACTION_TEMPLATE =
    """
    __SCRIPT__
    _script_result = None
    if sessionHandlingActionData is not None:
        _script_result = performAction(montoyaApi, sessionHandlingActionData)
    """;
    // Payload processor
    public static final String PAYLOAD_PROCESSOR_SCRIPT =
    """
    burp.api.montoya.intruder PayloadProcessingResult
    burp.api.montoya.intruder PayloadData
    def processPayload(montoyaApi, payloadData):
        return None
    """;
    public static final String PAYLOAD_PROCESSOR_TEMPLATE =
    """
    __SCRIPT__
    _script_result = None
    if payloadData is not None:
        _script_result = processPayload(montoyaApi, payloadData)
    """;

    public static String getEditorTemplate(ScriptTypes scriptType ) {
        String template = null;
        switch ( scriptType ) {
            case UTILITY:
                template = UTILITY_SCRIPT;
                break;
            case HTTP_HANDLER:
                template = HTTP_HANDLER_SCRIPT;
                break;
            case PROXY_HANDLER_REQUEST_RECEIVED:
                template = PROXY_HANDLER_RECEIVED_SCRIPT;
                break;
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                template = PROXY_HANDLER_TO_BE_SENT_SCRIPT;
                break;
            case SESSION_HANDLING_ACTION:
                template = SESSION_HANDLING_ACTION_SCRIPT;
                break;
            case PAYLOAD_PROCESSOR:
                template = PAYLOAD_PROCESSOR_SCRIPT;
                break;
        }
        return template;
    }

    public static String getExecutionScript(ScriptTypes scriptType, String scriptContent) {
        String script = null;
        switch ( scriptType ) {
            case UTILITY:
                script = UTILITY_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
            case PROXY_HANDLER_REQUEST_RECEIVED:
                script = PROXY_HANDLER_RECEIVED_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                script = PROXY_HANDLER_TO_BE_SENT_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
            case HTTP_HANDLER:
                script = HTTP_HANDLER_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
            case SESSION_HANDLING_ACTION:
                script = SESSION_HANDLING_ACTION_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
            case PAYLOAD_PROCESSOR:
                script = PAYLOAD_PROCESSOR_TEMPLATE.replace("__SCRIPT__",scriptContent);
                break;
        }
        return script;
    }
}
