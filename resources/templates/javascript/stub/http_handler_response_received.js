/*
    HTTP handler - response received ( JavaScript )
    - Used by the Burp HTTP client when a response is received ( scanner / intruder / extensions / repeater etc )
    - The httpResponseReceived is an HttpRequest object

    Example:
    - This script adds a response header

    Returns:
    A ResponseReceivedAction object with a modified request
*/
var RequestToBeSentAction = Packages.burp.api.montoya.http.handler.RequestToBeSentAction;
var ResponseReceivedAction = Packages.burp.api.montoya.http.handler.ResponseReceivedAction;
var HttpRequestToBeSent = Packages.burp.api.montoya.http.handler.HttpRequestToBeSent;
var HttpResponseReceived = Packages.burp.api.montoya.http.handler.HttpResponseReceived;

function handleHttpResponseReceived(montoyaApi, httpResponseReceived) {
	return ResponseReceivedAction.continueWith(httpResponseReceived.withAddedHeader("DemoHttpHandler","Added by user defined script (js)"));
}