function loginClicked() {

	var email = $('#emailForm').val();
	var password = $('#passwordForm').val();
	
	login_AJAX.attemptLogin(email,password);

}

function logoutClicked() {	
	login_AJAX.attemptLogout();
}


var login_AJAX = {
	
	attemptLogin: function(email,password) {
		
		function createRequest(email,password) {
			
			var xmlhttp = new XMLHttpRequest();
			//email = "richard3221@gmail.com";
			//password = "rich"
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "LoginServlet?operation=login&email="+email+"&password="+password, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var response = xmlhttp.responseText;
					$("#loginBtn").attr("disabled", false);
					
					if (response == "Login failed") {
						alert("Incorrect email or password"); 
					} else {
						window.location.href = response;
					}
					
					
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		$("#loginBtn").attr("disabled", true);
		createRequest(email,password);
	
	},
	
	attemptLogout: function() {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();

			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "LoginServlet?operation=logout", true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var response = xmlhttp.responseText;					
					window.location.href = response;
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest();
	
	},
	

};