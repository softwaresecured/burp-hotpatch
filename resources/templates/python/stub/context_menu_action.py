"""
    Context menu action ( Python )
    - Called when a user right clicks on one or more requests

    Example:
    - This script runs SQLMap in a detached shell session ( nohup )
    - A log message will be emitted at INFO log level explaining where to view the SQLMap output
"""
from burp.api.montoya.http.message import HttpRequestResponse
from burp.api.montoya.http.message.requests import HttpRequest
from java.lang import ProcessBuilder
from burp_hotpatch.util import Logger

def contextMenuAction(montoyaApi, requestResponses):
	# for each request
	for requestResponse in requestResponses:
		# prepare the filename to write the request
		method = requestResponse.request().method().lower()
		path = requestResponse.request().pathWithoutQuery().replace("/","_").lower()
		scan_id = montoyaApi.utilities().randomUtils().randomString(8)
		project_directory = "/tmp"
		request_file = project_directory + "/sqlmapreq_" + method + "_" + path + "_" + scan_id + ".txt"
		log_file = project_directory + "/sqlmap_" + scan_id + ".log"

		# write the file
		f = open(request_file, "w")
		f.write(requestResponse.request().toString())
		f.close()

		# run sqlmap in the background
		command = [
		"/bin/sh",
		"-c",
		"sqlmap --ignore-stdin --level 5  --risk 3 --random-agent --batch -r " + request_file + " > " + log_file
		]
		builder = ProcessBuilder(command)
		message = "Started SQLMap, check " + log_file + " for details"
		print(message)
		Logger.log("INFO",message)
		p = builder.start()
		p.waitFor()
