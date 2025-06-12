__SCRIPT__
_script_result = null;
if (typeof payloadData !== 'undefined') {
    _script_result = processPayload(montoyaApi, payloadData);
}