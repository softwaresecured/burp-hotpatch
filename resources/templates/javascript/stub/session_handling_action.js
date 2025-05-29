/*
    Session handling script ( JavaScript )
    - This script is called on every request when session management via macro is enabled
    - sessionHandlingActionData.request() contains the request
    - sessionHandlingActionData.macroRequestResponses() contains all the request / responses sent via the session
    handling macro
    - In this example, the last request contains the JWT token that we need to add to the headers of authenticated
    requests passed to this handler.

    Returns:
    An ActionResult object
*/
var ActionResult = Packages.burp.api.montoya.http.sessions.ActionResult;
var SessionHandlingActionData = Packages.burp.api.montoya.http.sessions.SessionHandlingActionData;
var Matcher = Packages.java.util.regex.Matcher;
var Pattern = Packages.java.util.regex.Pattern;

function performAction(montoyaApi, sessionHandlingActionData) {
    p = Pattern.compile("\"token\"\\:\"(.*?)\"\\,\"refreshToken\"");
    m = p.matcher(sessionHandlingActionData.macroRequestResponses().getLast().response().toString());
    if (m.find()) {
        return ActionResult.actionResult(sessionHandlingActionData.request().withUpdatedHeader("X-Authorization","Bearer " + m.group(1)));
    }
    return ActionResult.actionResult(sessionHandlingActionData.request());
}