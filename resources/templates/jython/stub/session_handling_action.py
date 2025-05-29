"""
    Session handling script ( Jython )
    - Handles requests after the session macro has been run
    - This script is called on every request when session management via macro is enabled
    - sessionHandlingActionData.request() contains the request
    - sessionHandlingActionData.macroRequestResponses() contains all the request / responses sent via the session
    handling macro
    - In this example, the last request contains the JWT token that we need to add to the headers of authenticated
    requests passed to this handler.

    Example:
    - This script reads a token from the last response where the body contains a token that can be matched with the
    regex defined in p

    Returns:
    An ActionResult object
"""
from burp.api.montoya.http.sessions import ActionResult
from burp.api.montoya.http.sessions import SessionHandlingActionData
from java.util.regex import Matcher
from java.util.regex import Pattern

def performAction(montoyaApi, sessionHandlingActionData):
    p = Pattern.compile("\"token\"\\:\"(.*?)\"\\,\"refreshToken\"")
    m = p.matcher(sessionHandlingActionData.macroRequestResponses().getLast().response().toString())
    if m.find():
        return sessionHandlingActionData.request().withUpdatedHeader("X-Authorization","Bearer " + m.group(1))
    return ActionResult.actionResult(sessionHandlingActionData.request()))