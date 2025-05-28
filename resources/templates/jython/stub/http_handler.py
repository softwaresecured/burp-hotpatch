from burp.api.montoya.http.handler import RequestToBeSentAction
from burp.api.montoya.http.handler import ResponseReceivedAction
from burp.api.montoya.http.handler import HttpRequestToBeSent
from burp.api.montoya.http.handler import HttpResponseReceived
def handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent):
    return httpRequestToBeSent.withAddedHeader("DemoHttpHandler","Added by user defined script")

def handleHttpResponseReceived(montoyaApi, httpResponseReceived):
    return None