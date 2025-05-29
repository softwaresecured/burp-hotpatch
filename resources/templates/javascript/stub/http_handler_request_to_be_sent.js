/*
    HTTP handler - request to be sent ( JavaScript )
    - Used by the Burp HTTP client when sending requests ( scanner / intruder / extensions / repeater etc )
    - The httpRequestToBeSent is an HttpRequest object

    Example:
    - This script adds a request header

    Returns:
    A RequestToBeSentAction object with a modified request
*/
var RequestToBeSentAction = Packages.burp.api.montoya.http.handler.RequestToBeSentAction;
var ResponseReceivedAction = Packages.burp.api.montoya.http.handler.ResponseReceivedAction;
var HttpRequestToBeSent = Packages.burp.api.montoya.http.handler.HttpRequestToBeSent;
var HttpResponseReceived = Packages.burp.api.montoya.http.handler.HttpResponseReceived;

function handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent) {
    return RequestToBeSentAction.continueWith(httpRequestToBeSent.withAddedHeader("DemoHttpHandler","Added by user defined script (js)"));
}