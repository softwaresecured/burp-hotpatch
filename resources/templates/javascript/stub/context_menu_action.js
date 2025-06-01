/*
    Context menu action ( JavaScript )
    - Called when a user right clicks on one or more requests

    Example:
    - This script runs SQLMap in a detached shell session ( nohup )
*/

var HttpRequestResponse = Packages.burp.api.montoya.http.message.HttpRequestResponse;
var HttpRequest = Packages.burp.api.montoya.http.message.requests.HttpRequest;
var ProcessBuilder = java.lang.ProcessBuilder;
var Files = Packages.java.nio.file.Files;
var Paths = Packages.java.nio.file.Paths;
var ArrayList = Packages.java.util.ArrayList;

function contextMenuAction(montoyaApi, requestResponses) {
	for ( var i = 0; i < requestResponses.size(); i++ ) {
		var requestResponse = requestResponses.get(i);
		// prepare the filename to write the request
		var method = requestResponse.request().method().replace("/","_").toLowerCase();
		var path = requestResponse.request().pathWithoutQuery().replace("/","_").toLowerCase();
		var scan_id = montoyaApi.utilities().randomUtils().randomString(6);
		var project_directory = "/tmp";
		var filename = project_directory + "/sqlmapreq_" + method + "_" + path + "_" + scan_id + ".txt";

		// write the file
  		Files.write(Paths.get(filename),requestResponse.request().toString().getBytes());

  		// run sqlmap in the background
		command = new ArrayList();
		command.add("/bin/sh");
		command.add("-c");
		command.add("nohup sqlmap --ignore-stdin --level 5  --risk 3 --random-agent --batch -r " + filename + " > " + project_directory + "/sqlmap_" + scan_id + ".log &");

  		var builder = new ProcessBuilder(command);
		builder.start();
	}
}