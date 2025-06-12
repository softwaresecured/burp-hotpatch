"""
    Payload processor ( Python )
    - Used by intruder when a payload is to be processed
    - The original payload can be obtained with payloadData.insertionPoint().baseValue()

    Example:
    - This script reverses the string

    Returns:
    A PayloadProcessingResult object with an updated payload
"""
from burp.api.montoya.intruder import PayloadProcessingResult
from burp.api.montoya.intruder import PayloadData
from burp.api.montoya.core import ByteArray

def processPayload(montoyaApi, payloadData):
	return PayloadProcessingResult.usePayload(ByteArray.byteArray(payloadData.insertionPoint().baseValue().toString()[::-1]))