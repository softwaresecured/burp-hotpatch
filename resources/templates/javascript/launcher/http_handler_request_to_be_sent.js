__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof httpRequestToBeSent !== 'undefined') {
    _script_result = handleHttpRequestToBeSent(montoyaApi, httpRequestToBeSent);
}