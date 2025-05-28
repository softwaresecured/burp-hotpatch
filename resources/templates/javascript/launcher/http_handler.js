__SCRIPT__
_script_result = null;
if (typeof httpRequestToBeSent !== 'undefined') {
    _script_result = handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent);
}

if (typeof httpResponseReceived !== 'undefined') {
    _script_result = handleHttpResponseReceived(montoyaApi, httpResponseReceived);
}