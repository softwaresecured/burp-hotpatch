/*
    Proxy handler - request to be sent ( JavaScript )
    - Used by the proxy to process requests before they're sent to the target
    - This request is called every time a request is received by the proxy
    - The interceptedRequest is an HttpRequest object

    Example:
    - This script adds a header to all requests sent through the proxy

    Returns:
    A ProxyRequestReceivedAction object with a modified request
*/
var ProxyRequestToBeSentAction = Packages.burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
var InterceptedRequest = Packages.burp.api.montoya.proxy.http.InterceptedRequest;

function handleRequestToBeSent(montoyaApi, interceptedRequest) {
	return ProxyRequestToBeSentAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script (js)"));
}