__SCRIPT__
_script_result = null
if (typeof interceptedRequest !== 'undefined') {
    _script_result = handleRequestReceived(montoyaApi, interceptedRequest)
}