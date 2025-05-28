from burp.api.montoya.http.sessions import ActionResult
from burp.api.montoya.http.sessions import SessionHandlingActionData
from java.util.regex import Matcher
from java.util.regex import Pattern
def performAction(montoyaApi, sessionHandlingActionData):
    p = Pattern.compile("\"token\"\\:\"(.*?)\"\\,\"refreshToken\"")
    m = p.matcher(sessionHandlingActionData.macroRequestResponses().getLast().response().toString())
    if m.find():
        return sessionHandlingActionData.request().withUpdatedHeader("X-Authorization","Bearer " + m.group(1))
    return sessionHandlingActionData.request()