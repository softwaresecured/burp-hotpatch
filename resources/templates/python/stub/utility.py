"""
    Utility script ( Python ):
    A script that runs inside BurpSuite. You are provided with a montoyaApi object which gives you access to the
    BurpSuite Montoya API.

    Environment:
    - This script runs inside a GraalVM Python environment.
    - You can import Java libraries using the following syntax:
        from java.util.regex import Matcher

    Example:
    - This script prints the current BurpSuite version

    For more information on Montoya API consult the API docs here:
    https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html
"""
def main( montoyaApi ):
	print("Hello from " + montoyaApi.burpSuite().version().toString())