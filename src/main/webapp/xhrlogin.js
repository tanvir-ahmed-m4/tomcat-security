var xhr = new XMLHttpRequest();
var uri1 = "http://empowerbd.metafour.lan:8080/tomcat-security/LOGIN";
xhr.open("GET", uri1, false);
xhr.overrideMimeType("text/xml");
xhr.send(null);
if (xhr.readyState == 4 && xhr.status == 200) {
	try {
		console.log("Response 1...");
		console.log(xhr.responseText);
		var responseXML = xhr.responseXML;
		console.log(responseXML.documentElement);
		var action = responseXML.documentElement.getAttribute("action");
		console.log("Form action: " + action);
		if (action == "j_security_check") {
			var uri2 = "http://empowerbd.metafour.lan:8080/tomcat-security/j_security_check";
			xhr.open("POST", uri2, false);
			xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			xhr.send("j_username=admin&j_password=META4");
			if (xhr.readyState == 4 && xhr.status == 200) {
				console.log("Response 2...");
				console.log(xhr.responseText);
				var responseXML = xhr.responseXML;
				console.log(responseXML.documentElement);
			}
		}
	} catch (e) {
		console.log(e);
	}
}
