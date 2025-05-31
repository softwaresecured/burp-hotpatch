/*
    Audit issue handler ( JavaScript )
    - Called when a new Audit Issue is created

    Example:
    - This script sends the request to the SQLMap REST API

    Note:
    - Waiting on https://github.com/PortSwigger/burp-extensions-montoya-api/issues/9
*/

function handleNewAuditIssue(montoyaApi, auditIssue) {
	var httpRequest = HttpRequest.httpRequestFromUrl("http://localhost:5555/somewebservice").withMethod("POST").withBody(auditIssue.detail());
	montoyaApi.http().sendRequest(httpRequest);
}