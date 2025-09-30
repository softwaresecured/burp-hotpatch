"""
Author: Software Secured
Script type: Utility
Language: Python
Description: Searches all responses in scope for URLs in quoted strings

TIP: Bulk check for dangling domains after using something similar to this:

for domain in $(cat /tmp/domains.txt) ; do whois $domain > /dev/null || echo $domain;  done

"""
import re
def extract_hosts( response_str ):
	hosts = re.findall(r'["\']https?:\/\/([^\?\/"\':]+)', response_str, re.IGNORECASE|re.MULTILINE)
	return list(set(hosts))

def main( montoyaApi ):
	discovered_hosts = []
	for req_res in montoyaApi.proxy().history():
		if not req_res.request().isInScope() or req_res.response() is None:
			continue
		discovered_hosts += extract_hosts(req_res.response().toString())
	print("\n".join(list(set(discovered_hosts))))