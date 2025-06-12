__SCRIPT__
_script_result = null;
if (typeof httpResponseReceived !== 'undefined') {
    _script_result = handleHttpResponseReceived(montoyaApi, httpResponseReceived);
}