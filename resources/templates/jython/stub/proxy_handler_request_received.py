from burp.api.montoya.proxy.http import ProxyRequestReceivedAction
from burp.api.montoya.proxy.http import InterceptedRequest

def handleRequestReceived(montoyaApi, interceptedRequest):
    return ProxyRequestReceivedAction.continueWith(interceptedRequest.withAddedHeader("DemoProxyReceivedHandler","Added by user defined script (py)"))