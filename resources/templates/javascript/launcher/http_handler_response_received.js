__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof httpResponseReceived !== 'undefined') {
    _script_result = handleHttpResponseReceived(montoyaApi, httpResponseReceived);
}