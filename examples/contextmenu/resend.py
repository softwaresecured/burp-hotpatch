from burp.api.montoya.http.message import HttpRequestResponse
from burp.api.montoya.http.message.requests import HttpRequest
def contextMenuAction(montoyaApi, requestResponses):
	for requestResponse in requestResponses:
		montoyaApi.http().sendRequest(requestResponse.request())
