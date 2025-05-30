__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof interceptedRequest !== 'undefined') {
    _script_result = handleRequestToBeSent(montoyaApi, interceptedRequest);
}