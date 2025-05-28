var RequestToBeSentAction = Packages.burp.api.montoya.http.handler.RequestToBeSentAction;
var ResponseReceivedAction = Packages.burp.api.montoya.http.handler.ResponseReceivedAction;
var HttpRequestToBeSent = Packages.burp.api.montoya.http.handler.HttpRequestToBeSent;
var HttpResponseReceived = Packages.burp.api.montoya.http.handler.HttpResponseReceived;
function handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent) {
    return httpRequestToBeSent.withAddedHeader("DemoHttpHandler","Added by user defined script");
}

function handleHttpResponseReceived(montoyaApi, httpResponseReceived) {
    return null;
}