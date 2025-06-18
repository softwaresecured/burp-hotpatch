/*
    Utility script ( JavaScript ):
    A script that runs inside BurpSuite. You are provided with a montoyaApi object which gives you access to the
    BurpSuite Montoya API.

    Environment:
    - This script runs inside a GraalVM JavaScript environment.
    - You can import Java libraries using the following syntax:
        var Matcher = Packages.java.util.regex.Matcher;

    Memory:
    - Variables can be shared across scripts using the memory feature
    - The setString(name,value), setInt(name,value) and setObject(name,value) functions can be used to set values
    - The getString(name), getInt(name) and getObject(name) functions can be used to retrieve values
    - The above functions throw the burp_hotpatch.script.ScriptSharedMemoryException exception

    Example:
    - This script prints the current BurpSuite version

    For more information on Montoya API consult the API docs here:
    https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html
*/
function main( montoyaApi ) {
	print("Hello from " + montoyaApi.burpSuite().version().toString())
}