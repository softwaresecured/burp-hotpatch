/*
    Proxy handler - request received ( JavaScript )
    - Used by the proxy to determine what to do with a request that is received
    - This request is called every time a request is received by the proxy
    - The interceptedRequest is an HttpRequest object

    Example:
    - This script drops a request if the url contains the text "dropthisrequest"

    Returns:
    A ProxyRequestReceivedAction object with an action to take ( drop, intercept, continue, etc )
*/
var ProxyRequestReceivedAction = Packages.burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
var InterceptedRequest = Packages.burp.api.montoya.proxy.http.InterceptedRequest;

function handleRequestReceived(montoyaApi, interceptedRequest) {
    if (interceptedRequest.url().contains("dropthisrequest")) {
        return ProxyRequestReceivedAction.drop()
    }
    return ProxyRequestReceivedAction.continueWith(interceptedRequest)
}