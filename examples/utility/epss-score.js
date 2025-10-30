/*
    Author: Software Secured
    Script type: Utility
    Language: JavaScript
    Description: Scores CVEs from audit issues using the EPSS scoring API from First.org.
*/
var HttpRequestResponse = Packages.burp.api.montoya.http.message.HttpRequestResponse;
var HttpRequest = Packages.burp.api.montoya.http.message.requests.HttpRequest;
var AuditIssue = Packages.burp.api.montoya.scanner.audit.issues.AuditIssue;

function getCves( details ) {
	var cves = [];
	if ( details != null ) {
		const matches = Array.from(details.matchAll(/(CVE-\d{4}-\d+)/g));
		matches.forEach(match => {
			cves.push(match[0]);
		});
	}
	return cves;
}

function getDate() {
	var curDate = new Date();
	curDate.setDate(curDate.getDate()-1);
	return curDate.toISOString().split('T')[0];
}

function getEPSSScore ( montoyaApi, cve ) {
	var curDate = getDate();
	var reqRes = montoyaApi.http().sendRequest(HttpRequest.httpRequestFromUrl(`https://api.first.org/data/v1/epss?cve=${cve}&date=${curDate}`));
	if ( reqRes.response().statusCode() == 200 ) {
		var responseData = JSON.parse(reqRes.response().bodyToString());
		if ( responseData.data.length > 0 ) {
			return Math.round(responseData.data[0].epss * 100);
		}
	}
	return 0;
}

function main( montoyaApi ) {
	var cves = [];

	const auditIssues = Array.from(montoyaApi.siteMap().issues());
	auditIssues.forEach( auditIssue => {
		cves = cves.concat(getCves(auditIssue.detail()));
	});
	cves = [...new Set(cves)];

	cves.forEach( cve => {
		var epssScore = getEPSSScore(montoyaApi,cve);
		console.log(`CVE: ${cve}, EPSS Score: ${epssScore}`);
	});
}