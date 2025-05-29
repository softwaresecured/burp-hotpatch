"""
    HTTP handler - response received ( Jython )
    - Used by the Burp HTTP client when a response is received ( scanner / intruder / extensions / repeater etc )
    - The httpResponseReceived is an HttpRequest object

    Example:
    - This script adds a response header

    Returns:
    A ResponseReceivedAction object with a modified request
"""

from burp.api.montoya.http.handler import RequestToBeSentAction
from burp.api.montoya.http.handler import ResponseReceivedAction
from burp.api.montoya.http.handler import HttpRequestToBeSent
from burp.api.montoya.http.handler import HttpResponseReceived

def handleHttpResponseReceived(montoyaApi, httpResponseReceived):
    return ResponseReceivedAction.continueWith(httpResponseReceived.withAddedHeader("DemoHttpHandler","Added by user defined script (py)"))