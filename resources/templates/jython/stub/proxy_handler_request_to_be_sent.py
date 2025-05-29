"""
    Proxy handler - request to be sent ( Jython )
    - Used by the proxy to process requests before they're sent to the target
    - This request is called every time a request is received by the proxy
    - The interceptedRequest is an HttpRequest object

    Example:
    - This script adds a header to all requests sent through the proxy

    Returns:
    A ProxyRequestReceivedAction object with a modified request
"""
from burp.api.montoya.proxy.http import ProxyRequestToBeSentAction
from burp.api.montoya.proxy.http import InterceptedRequest

def handleRequestToBeSent(montoyaApi, interceptedRequest):
    return ProxyRequestToBeSentAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script (py)"))