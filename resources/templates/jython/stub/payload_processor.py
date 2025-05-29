from burp.api.montoya.intruder import PayloadProcessingResult
from burp.api.montoya.intruder import PayloadData

def processPayload(montoyaApi, payloadData):
    return PayloadProcessingResult.usePayload(payloadData.currentPayload().toString()[::-1].encode("utf-8"))