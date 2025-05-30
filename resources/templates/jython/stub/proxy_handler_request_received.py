"""
    Proxy handler - request received ( Jython )
    - Used by the proxy to determine what to do with a request that is received
    - This request is called every time a request is received by the proxy
    - The interceptedRequest is an HttpRequest object

    Example:
    - This script drops a request if the url contains the text "dropthisrequest"

    Returns:
    A ProxyRequestReceivedAction object with an action to take ( drop, intercept, continue, etc )
"""
from burp.api.montoya.proxy.http import ProxyRequestReceivedAction
from burp.api.montoya.proxy.http import InterceptedRequest

def handleRequestReceived(montoyaApi, interceptedRequest):
	if interceptedRequest.url().contains("dropthisrequest"):
		return ProxyRequestReceivedAction.drop()
	return ProxyRequestReceivedAction.continueWith(interceptedRequest)