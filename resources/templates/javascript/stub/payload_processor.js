/*
    Payload processor ( JavaScript )
    - Used by intruder when a payload is to be processed
    - The original payload can be obtained with payloadData.insertionPoint().baseValue()

    Example:
    - This script reverses the string

    Returns:
    A PayloadProcessingResult object with an updated payload
*/
var PayloadProcessingResult = Packages.burp.api.montoya.intruder.PayloadProcessingResult;
var PayloadData = Packages.burp.api.montoya.intruder.PayloadData;
var ByteArray = Packages.burp.api.montoya.core.ByteArray;

function processPayload(montoyaApi, payloadData) {
    var processedPayload = java.lang.String(payloadData.insertionPoint().baseValue()).split('').reverse().join('');
    return PayloadProcessingResult.usePayload(ByteArray.byteArray(processedPayload));
}