function updateEmail() {
	var email = $('#newEmailForm').val();
	accountUpdate_AJAX.updateEmail(email);
}

function updatePassword() {
	var password = $('#passwordForm1').val();
	accountUpdate_AJAX.updatePassword(password);
}


var accountUpdate_AJAX = {
	
	updateEmail: function(email) {
		
		function createRequest(email) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "AccountServlet?operation=updateEmail&email="+email, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "true") {
						alert("Email updated");
					} else {
						alert("Email update failed");
					}				} else {
				    //alert("Error loading page\n");  
				}
			}				
		}		
		createRequest(email);	
	},	
	
	updatePassword: function(password) {
		
		function createRequest(password) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "AccountServlet?operation=updatePassword&password="+password, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "true") {
						alert("Password updated");
					} else {
						alert("Password update failed");
					}
				} else {
				    //alert("Error loading page\n");  
				}
			}		
		}		
		createRequest(password);
	},	

};