function addGameButtons(currentGameweek) {
	Games_AJAX.requestActiveGames(currentGameweek);
	Games_AJAX.requestActiveButEliminatedGames(currentGameweek);
	Games_AJAX.requestEndedGames(currentGameweek);
}

function getGameweek() {
	Games_AJAX.getGameweek();
}

var Games_AJAX = {
	
	requestActiveGames: function(currentGameweek) {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "DashboardServlet?operation=getActiveGames&currentGameweek="+currentGameweek, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "No Games") {
						//alert("nogames");
					} else {
						$("#activeGamesDiv").append(htmlResponse);
					}
					
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest(currentGameweek);
	
	},	
	
	requestActiveButEliminatedGames: function(currentGameweek) {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "DashboardServlet?operation=getActiveButEliminatedGames&currentGameweek="+currentGameweek, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "No Games") {
						//alert("nogames");
					} else {
						$("#eliminatedActiveGamesDiv").append(htmlResponse);
					}
					
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest(currentGameweek);
	
	},	
	
	requestEndedGames: function(currentGameweek) {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "DashboardServlet?operation=getEndedGames&currentGameweek="+currentGameweek, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "No Games") {
						//alert("nogames");
					} else {
						$("#endedGamesDiv").append(htmlResponse);
					}
					
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest(currentGameweek);
	
	},	

	
	getGameweek: function() {
		
		function createRequest() {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "DashboardServlet?operation=getGameweek", true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					if(htmlResponse == "Failed to get current gameweek") {
						alert("Failed to get current gameweek");
					} else {
						
						CURRENTGAMEWEEK = htmlResponse;
						if(CURRENTGAMEWEEK == 38) {
							$("#newGameBtn").attr("disabled", true);
						}
						var html = "<div class=\"alert alert-success\"><strong>Current Gameweek - "+CURRENTGAMEWEEK+" / 38</strong></div>";
						
						$("#gameweekDiv").append(html);
						
						var endGameweek = 38;
						
						var completed = CURRENTGAMEWEEK / endGameweek * 100;
						var uncomplete = 100 - completed - 2.6;
						
						var html2 = "<div class=\"progress\"> " +
						  "<div class=\"progress-bar progress-bar-danger\" style=\"width: "+completed+"%\"> " +
						  "  <span class=\"sr-only\">35% Complete (success)</span> " +
						  "</div> " +
						  "<div class=\"progress-bar progress-bar-success\" style=\"width: 2.6%\"> " +
						  "  <span class=\"sr-only\">20% Complete (warning)</span> " +
						  "</div> " +
						  "<div class=\"progress-bar progress-bar-primary\" style=\"width: "+uncomplete+"%\"> " +
						  "  <span class=\"sr-only\">10% Complete (danger)</span> " +
						  "</div> " +
						"</div> ";
						
						$("#gameweekDiv").append(html2);
						
						addGameButtons(CURRENTGAMEWEEK);
					}
					
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest();
	
	},	

};

$(document).on("click","button[gamename]",function() {
	var gameName = $(this).attr("gamename");
	window.location.href = "/LMS/game.html?gameName="+gameName+"&gameWeek="+CURRENTGAMEWEEK;
});

