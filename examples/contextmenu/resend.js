var HttpRequestResponse = Packages.burp.api.montoya.http.message.HttpRequestResponse;
var HttpRequest = Packages.burp.api.montoya.http.message.requests.HttpRequest;

function contextMenuAction(montoyaApi, requestResponses) {
	for ( requestResponse of requestResponses ) {
		montoyaApi.http().sendRequest(requestResponse.request());
	}

}