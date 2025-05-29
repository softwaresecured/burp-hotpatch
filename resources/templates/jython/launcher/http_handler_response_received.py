__SCRIPT__
_script_result = None
try:
    global _script_result
    _script_result = handleHttpResponseReceived(montoyaApi, httpResponseReceived)
except NameError:
    pass