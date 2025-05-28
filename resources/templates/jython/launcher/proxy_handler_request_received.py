__SCRIPT__
_script_result = None
if interceptedRequest is not None:
    _script_result = handleRequestReceived(montoyaApi, interceptedRequest)