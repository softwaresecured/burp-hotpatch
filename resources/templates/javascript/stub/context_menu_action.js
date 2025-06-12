/*
    Context menu action ( JavaScript )
    - Called when a user right clicks on one or more requests

    Example:
    - This script runs SQLMap in a detached shell session ( nohup )
    - A log message will be emitted at INFO log level explaining where to view the SQLMap output
*/

var HttpRequestResponse = Packages.burp.api.montoya.http.message.HttpRequestResponse;
var HttpRequest = Packages.burp.api.montoya.http.message.requests.HttpRequest;
var ProcessBuilder = Packages.java.lang.ProcessBuilder;
var FileWriter = Packages.java.io.FileWriter;
var ArrayList = Packages.java.util.ArrayList;
var Logger = Packages.burp_hotpatch.util.Logger;

function contextMenuAction(montoyaApi, requestResponses) {
	for ( var i = 0; i < requestResponses.size(); i++ ) {
		var requestResponse = requestResponses.get(i);
		// prepare the filename to write the request
		var method = requestResponse.request().method().toLowerCase();
		var path = requestResponse.request().pathWithoutQuery().replaceAll("/","_").toLowerCase();
		var scanId = montoyaApi.utilities().randomUtils().randomString(6);
		var project_directory = "/tmp";
		var requestFile = project_directory + "/sqlmapreq_" + method + "_" + path + "_" + scanId + ".txt";
		var logFile = project_directory + "/sqlmap_" + scanId + ".log";

		// write the file
  		var writer = new FileWriter(requestFile);
  		writer.write(requestResponse.request().toString());
  		writer.close();

  		// run sqlmap in the background
		command = new ArrayList();
		command.add("/bin/sh");
		command.add("-c");
		command.add("sqlmap --ignore-stdin --level 5  --risk 3 --random-agent --batch -r " + requestFile + " > " + logFile);

  		var builder = new ProcessBuilder(command);
		var message = "Started SQLMap, check " + logFile + " for details"
		print(message)
		Logger.log("INFO",message);
		var p = builder.start();
		p.waitFor();
	}
}