/*
    Utility script ( JavaScript ):
    A script that runs inside BurpSuite. You are provided with a montoyaApi object which gives you access to the
    BurpSuite Montoya API.

    Environment:
    - This script runs inside a Rhino 1.7.15 JavaScript environment.
    - You can import Java libraries using the following syntax:
        var Matcher = Packages.java.util.regex.Matcher;

    Example:
    - This script prints the current BurpSuite version

    For more information on Montoya API consult the API docs here:
    https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html
*/
function main( montoyaApi ) {
    print("Hello from " + montoyaApi.burpSuite().version().toString())
}