__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof payloadData !== 'undefined') {
    _script_result = processPayload(montoyaApi, payloadData);
}