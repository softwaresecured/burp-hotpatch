__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof sessionHandlingActionData !== 'undefined') {
    _script_result = performAction(montoyaApi, sessionHandlingActionData)
}