__SCRIPT__
_script_result = null;
if (typeof sessionHandlingActionData !== 'undefined') {
    _script_result = performAction(montoyaApi, sessionHandlingActionData)
}