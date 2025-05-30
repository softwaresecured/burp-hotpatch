"""
    HTTP handler - request to be sent ( Jython )
    - Used by the Burp HTTP client when sending requests ( scanner / intruder / extensions / repeater etc )
    - The httpRequestToBeSent is an HttpRequest object

    Example:
    - This script adds a request header

    Returns:
    A RequestToBeSentAction object with a modified request
"""
from burp.api.montoya.http.handler import RequestToBeSentAction
from burp.api.montoya.http.handler import ResponseReceivedAction
from burp.api.montoya.http.handler import HttpRequestToBeSent
from burp.api.montoya.http.handler import HttpResponseReceived

def handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent):
	return RequestToBeSentAction.continueWith(httpRequestToBeSent.withAddedHeader("DemoHttpHandler","Added by user defined script (py)"))