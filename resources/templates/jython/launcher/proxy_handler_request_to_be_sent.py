__SCRIPT__
_script_result = None
if interceptedRequest is not None:
    _script_result = handleRequestToBeSent(montoyaApi, interceptedRequest)