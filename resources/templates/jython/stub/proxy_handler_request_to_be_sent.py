from burp.api.montoya.proxy.http import ProxyRequestToBeSentAction
from burp.api.montoya.proxy.http import InterceptedRequest

def handleRequestToBeSent(montoyaApi, interceptedRequest):
    return ProxyRequestToBeSentAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script (py)"))