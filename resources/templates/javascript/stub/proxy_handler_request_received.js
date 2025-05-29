var ProxyRequestReceivedAction = Packages.burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
var InterceptedRequest = Packages.burp.api.montoya.proxy.http.InterceptedRequest;

function handleRequestReceived(montoyaApi, interceptedRequest) {
    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyReceivedHandler","Added by user defined script (js)"));
}