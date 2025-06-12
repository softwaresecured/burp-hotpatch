"""
    Audit issue handler ( Python )
    - Called when a new Audit Issue is created

    Example:
    - This script sends the request to the SQLMap REST API

    Note:
    - Waiting on https://github.com/PortSwigger/burp-extensions-montoya-api/issues/9
"""
from burp.api.montoya.http.message.requests import HttpRequest
def handleNewAuditIssue(montoyaApi, auditIssue):
	httpRequest = HttpRequest.httpRequestFromUrl("http://localhost:5555/somewebservice").withMethod("POST").withBody(auditIssue.detail())
	montoyaApi.http().sendRequest(httpRequest)