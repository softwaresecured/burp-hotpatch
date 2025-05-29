from burp.api.montoya.http.handler import RequestToBeSentAction
from burp.api.montoya.http.handler import ResponseReceivedAction
from burp.api.montoya.http.handler import HttpRequestToBeSent
from burp.api.montoya.http.handler import HttpResponseReceived

def handleHttpResponseReceived(montoyaApi, httpResponseReceived):
    return ResponseReceivedAction.continueWith(httpResponseReceived.withAddedHeader("DemoHttpHandler","Added by user defined script (py)"))