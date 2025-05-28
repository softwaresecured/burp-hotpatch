from burp.api.montoya.proxy.http import ProxyRequestToBeSentAction
from burp.api.montoya.proxy.http import InterceptedRequest
def handleRequestToBeSent(montoyaApi, interceptedRequest):
    return interceptedRequest.withAddedHeader("DemoProxyToBeSentHandler","Added by user defined script")