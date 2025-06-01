__SCRIPT__
_script_result = null;
function print( message ) {
    logger.logMessage(message);
}
if (typeof auditIssue !== 'requestResponses') {
    contextMenuAction(montoyaApi, requestResponses);
}