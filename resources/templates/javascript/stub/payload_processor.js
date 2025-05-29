var PayloadProcessingResult = Packages.burp.api.montoya.intruder.PayloadProcessingResult;
var PayloadData = Packages.burp.api.montoya.intruder.PayloadData;
var ByteArray = Packages.burp.api.montoya.core.ByteArray;

function processPayload(montoyaApi, payloadData) {
    var origPayload = java.lang.String(payloadData.currentPayload())
    var processedPayload = "";
    for ( var i =origPayload.length-1 ; i >= 0 ; i-- ) {
            processedPayload += origPayload[i];
    }
    return PayloadProcessingResult.usePayload(ByteArray.byteArray(processedPayload));
}