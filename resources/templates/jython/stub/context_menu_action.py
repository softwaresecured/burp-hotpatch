"""
    Context menu action ( Jython )
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
		method = requestResponse.request().method().replace("/","_").lower()
		path = requestResponse.request().pathWithoutQuery().replace("/","_").lower()
		scan_id = montoyaApi.utilities().randomUtils().randomString(8)
		project_directory = "/tmp"
		filename = project_directory + "/sqlmapreq_" + method + "_" + path + "_" + scan_id + ".txt"
		logfile = project_directory + "/sqlmap_" + scan_id + ".log"

		# write the file
		f = open(filename, "w")
  		f.write(requestResponse.request().toString())
  		f.close()

  		# run sqlmap in the background
  		command = [
  		"/bin/sh",
  		"-c",
  		"nohup sqlmap --ignore-stdin --level 5  --risk 3 --random-agent --batch -r " + filename + " > " + logfile + " &"
  		]
  		builder = ProcessBuilder(command)
		builder.start()
		Logger.log("INFO","Started SQLMap, check " + logfile + " for details")
