var ProxyRequestToBeSentAction = Packages.burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
var InterceptedRequest = Packages.burp.api.montoya.proxy.http.InterceptedRequest;

function handleRequestToBeSent(montoyaApi, interceptedRequest) {
    return ProxyRequestToBeSentAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script (js)"));
}