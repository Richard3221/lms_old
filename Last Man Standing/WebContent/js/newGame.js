function addPlayerForm() {

	var totalNumberOfPlayer = 0;
	var playerNum;
	
	for(playerNum = 1 ; playerNum < 100 ; playerNum++ ) {
		
		if ( $('#player'+playerNum+'Form').length == 0 ) {
			break;
		}
		totalNumberOfPlayer++;
	}
	
	//alert("Total number of players: "+totalNumberOfPlayer);

	var btnString = "<input id=\"player"+playerNum+"Form\" type=\"text\" class=\"form-control\" placeholder=\"Additional player email address\" required>\n";
	btnString += "<div id=\"emailNotValidAlert"+playerNum+"Form\" hidden=\"true\" class=\"alert alert-danger\">Not a valid email address</div>";
	btnString += "<div id=\"emailDuplicateAlert"+playerNum+"Form\" hidden=\"true\" class=\"alert alert-danger\">Duplicate email address</div>";

	var lastBtn = "emailDuplicateAlert"+(--playerNum)+"Form";
	
	$('#'+lastBtn).after(btnString);

}

function saveNewGame() {
	saveNewGame_AJAX.saveGame();
}

function getEmailAddress() {
	getEmail_AJAX.getEmailAddress();
}

function checkForms() {
	var allIsOK = true;

	$('.alert').attr("hidden", true);
	
	if( $('#newGameNameForm').val().length == 0 ) {
		$("#newGameFormEmptyAlert").attr("hidden", false);
		allIsOK = false;
	} else {
		$("#newGameFormEmptyAlert").attr("hidden", true);
	}
	
	var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	for(var playerNum = 1 ; playerNum < 100 ; playerNum++ ) {

		playerEmail = $('#player'+playerNum+'Form').val();
		if( playerEmail.length != 0 && !regex.test( playerEmail) ) {
			allIsOK = false;
			$("#emailNotValidAlert"+playerNum+"Form").attr("hidden", false);
		} else {
			$("#emailNotValidAlert"+playerNum+"Form").attr("hidden", true);
		}
		
		if ( $('#player'+(playerNum + 1)+'Form').length == 0 ) {
			break;
		}
	}
	
	for(var playerNum = 1 ; playerNum < 100 ; playerNum++ ) {
		
		playerEmail = $('#player'+playerNum+'Form').val();
		var duplicateFound = "false";	
		for(var innerPlayerNum = (playerNum + 1) ; innerPlayerNum < 100 ; innerPlayerNum++ ) {
			innerPlayerEmail = $('#player'+innerPlayerNum+'Form').val();
			//alert("playerEmail: "+playerEmail+" innerPlayerEmail: "+innerPlayerEmail );
			//alert("playerNum: "+playerNum+" innerPlayerNum: "+innerPlayerNum );

			if( playerEmail == innerPlayerEmail && innerPlayerEmail != "" ) {
				allIsOK = false;
				duplicateFound = "true";
				$("#emailDuplicateAlert"+innerPlayerNum+"Form").attr("hidden", false);
			} 				
			
			if ( $('#player'+(innerPlayerNum + 1)+'Form').length == 0 ) {
				break;
			}
		}
		
		if ( duplicateFound == "false" ) {
		//	$("#emailDuplicateAlert"+innerPlayerNum+"Form").attr("hidden", false);
		}
		
		if ( $('#player'+(playerNum + 1)+'Form').length == 0 ) {
			break;
		}
	}
	
	if(allIsOK == true) {
		saveNewGame();
	}
	
}

var getEmail_AJAX = {

		getEmailAddress: function() {
			
			function createRequest() {
				
				var xmlhttp = new XMLHttpRequest();
				xmlhttp.onreadystatechange = callback;
				xmlhttp.open("GET", "EmailAddressServlet?", true);
				xmlhttp.send(null);

				function callback() {

					if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
						var htmlResponse = xmlhttp.responseText;
					    //alert(htmlResponse);  
						$('#player1Form').val(htmlResponse);

					} else {
					    //alert("Error loading page\n");  
					}
				}
				
				
			}
					
			createRequest();
		
		},
		

};

var saveNewGame_AJAX = {

	
	saveGame: function() {
	
	var gameDoesExist = "true";

		function checkGameExistsRequest(gameName,playerEmails) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "NewGameServlet?operation=checkGame&gameName="+gameName, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					//		out.write("createGameSuccess:"+String.valueOf(createGameSuccess)+"|"+"addPlayersSuccess:"+String.valueOf(addPlayersSuccess))				
					
				    //alert("Saved");  		
				    $("#saveBtn").attr("disabled", false);
				    					
					if (htmlResponse == "true" ) {
						//alert("Game already exists");
						$("#newGameFormAlreadyExistsAlert").attr("hidden", false);
						
					} else {
					    $("#saveBtn").attr("disabled", true);
						createNewGameRequest(gameName,playerEmails);
					}
				    
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		function createNewGameRequest(gameName,playerEmails) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "NewGameServlet?operation=newGame&gameName="+gameName+"&playerEmails="+playerEmails, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					//		out.write("createGameSuccess:"+String.valueOf(createGameSuccess)+"|"+"addPlayersSuccess:"+String.valueOf(addPlayersSuccess));

					var returnValues = htmlResponse.split("|");
					
					
				    //alert("Saved");  		
				    $("#saveBtn").attr("disabled", false);
					if (returnValues[0] == "true" && returnValues[1] == "true") {
						window.location.href = "/LMS/dashboard.html";
					} else {
						if(returnValues[0] == "false") {
							alert("Game couldn't be created"); 
						} 
						if(returnValues[1] == "false") {
							alert("Players couldn't be added"); 
						}
					}
				    
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
				
		var gameName = $('#newGameNameForm').val();
		var playerEmails = "";
		for(var playerNum = 1 ; playerNum < 100 ; playerNum++ ) {
	
			playerEmails += $('#player'+playerNum+'Form').val();
			
			if ( $('#player'+(playerNum + 1)+'Form').length == 0 ) {
				break;
			} else {
				playerEmails += ",";
			}
		}
		
		var lastChar = playerEmails.substr(playerEmails.length - 1); // => "1"
		if(lastChar == ',') {
			playerEmails = 	playerEmails.slice(0,-1);
		}
		
		$("#saveBtn").attr("disabled", true);
		
		checkGameExistsRequest(gameName,playerEmails);		
	
	},
	

};