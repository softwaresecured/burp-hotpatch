__SCRIPT__
_script_result = null;
if (typeof httpRequestToBeSent !== 'undefined') {
    _script_result = handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent);
}