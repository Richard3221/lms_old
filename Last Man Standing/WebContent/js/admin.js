function updateFixtures() {
	admin_AJAX.requestFixtures();
}

function addUser() {
	var email = $('#emailForm').val();
	var password = $('#passwordForm').val();
	admin_AJAX.addUser(email,password);
}

function deleteUser() {
	var email = $('#deleteEmailForm').val();
	var deleteAll = $('#deteteGamesCheckBox').prop('checked');
	admin_AJAX.deleteUser(email,deleteAll);
}


var admin_AJAX = {
	
	requestFixtures: function() {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "AdminServlet?operation=updateFixtures", true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if( htmlResponse == "true") {
						alert("Fixtures updated");
					} else {
						alert("Fixtures failed to update");
					}
					$("#updateFixturesBtn").attr("disabled", false);
					$("#updateFixturesBtn").html('Update Fixtures');

				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		$("#updateFixturesBtn").attr("disabled", true);
		$("#updateFixturesBtn").html('Updating...');

		createRequest();
	
	},	
	
	addUser: function(email,password) {
		
		function createRequest(email,password) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "AdminServlet?operation=addUser&email="+email+"&password="+password, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if( htmlResponse == "User exists") {
						alert("User was not added as they already exist");
					} else if ( htmlResponse == "true") {
						alert("User was added");
					} else {
						alert("Adding user failed");
					}
					$("#addUserBtn").attr("disabled", false);
					$("#addUserBtn").html('Add User');
					
					$('#emailForm').val("");
					$('#passwordForm').val("");

				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		$("#addUserBtn").attr("disabled", true);
		$("#addUserBtn").html('Adding...');

		createRequest(email,password);
	
	},	
	
	deleteUser: function(email,deleteAll) {
		
		function createRequest(email,deleteAll) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "AdminServlet?operation=deleteUser&email="+email+"&deleteAll="+deleteAll, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if( htmlResponse == "User does not exist") {
						alert("User was not deleted as they dont exist");
					} else if ( htmlResponse == "true") {
						alert("User was deleted");
						$('#deleteEmailForm').val("");
					} else {
						alert("Deleting user failed");
					}
					$("#deleteUserBtn").attr("disabled", false);
					$("#deleteUserBtn").html('Delete User');
										
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		$("#deleteUserBtn").attr("disabled", true);
		$("#deleteUserBtn").html('Deleting...');

		createRequest(email,deleteAll);
	
	},	

};