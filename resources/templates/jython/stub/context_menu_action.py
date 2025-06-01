"""
    Context menu action ( Jython )
    - Called when a user right clicks on one or more requests

    Example:
    - This script runs SQLMap in a detached shell session ( nohup )
"""
from burp.api.montoya.http.message import HttpRequestResponse
from burp.api.montoya.http.message.requests import HttpRequest
from java.lang import ProcessBuilder

def contextMenuAction(montoyaApi, requestResponses):
	# for each request
	for requestResponse in requestResponses:

		# prepare the filename to write the request
		method = requestResponse.request().method().replace("/","_").lower()
		path = requestResponse.request().pathWithoutQuery().replace("/","_").lower()
		scan_id = montoyaApi.utilities().randomUtils().randomString(8)
		project_directory = "/tmp"
		filename = project_directory + "/sqlmapreq_" + method + "_" + path + "_" + scan_id + ".txt"

		# write the file
		f = open(filename, "w")
  		f.write(requestResponse.request().toString())
  		f.close()

  		# run sqlmap in the background
  		command = [
  		"/bin/sh",
  		"-c",
  		"nohup sqlmap --ignore-stdin --level 5  --risk 3 --random-agent --batch -r " + filename + " > " + project_directory + "/sqlmap_" + scan_id + ".log &"
  		]
  		builder = ProcessBuilder(command)
		builder.start()
