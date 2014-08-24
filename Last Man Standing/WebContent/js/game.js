function getFixtures() {
	

	var url = window.location.href;
	var pieces = url.split("?");
	var parameters = pieces[1];
	//gameName=test3&gameWeek=22
	pieces = parameters.split("&");
	//gameName=test3
	var paramNameAndValue = pieces[0].split("=");;
	var gameName = paramNameAndValue[1];
	paramNameAndValue = pieces[1].split("=");;
	var gameWeek = paramNameAndValue[1];		

	GAMENAME = gameName;
	GAMEWEEK = gameWeek;
	
	
	if(gameName == "" || gameWeek == "" || gameWeek > 38 || gameWeek <= 0) {
		window.location.href = "/LMS/dashboard.html";
	} else {
		Games_AJAX.requestFixturesForGames(gameName,gameWeek);
	}
}


var Games_AJAX = {	

	requestFixturesForGames: function(gameName,gameWeek) {
		
		function createRequest(gameName,gameWeek) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "GameServlet?operation=getFixtures&gameName="+gameName+"&gameWeek="+gameWeek, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;

					if( htmlResponse == "Wrong game") {
						window.location.href = "/LMS/dashboard.html";
					} else {						
						$("#gameDiv").append(htmlResponse);
						
						var previousGWDiv = "<div id=\"previousGWDiv\"><p><a href=\"#\" onclick=\"previousGWClicked()\"><img src=\"images/left-arrow-icon.jpg\" alt=\"Previous gameweek\" width=\"50\" height=\"40\"></a></p></div>";
						var nextGWDiv = "<div id=\"nextGWDiv\"><p><a href=\"#\" onclick=\"nextGWClicked()\"><img src=\"images/right-arrow-icon.jpg\" alt=\"Next gameweek\" width=\"50\" height=\"40\"></a></p></div>";				
						
						if(GAMEWEEK > 1 && !htmlResponse.contains("Game Round: 1 ")) {
							$("#previousGWTH").append(previousGWDiv);
						} 
						if(GAMEWEEK < 38) {
							$("#nextGWTH").append(nextGWDiv);
						}
					}
					

					//alert(htmlResponse);
				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest(gameName,gameWeek);
	
	},
	
	makeChoice: function(gameName,gameWeek,teamName) {
		
		function createRequest(gameName,gameWeek,teamName) {
			
			var xmlhttp = new XMLHttpRequest();
			xmlhttp.onreadystatechange = callback;
			xmlhttp.open("GET", "GameServlet?operation=makeChoice&gameName="+gameName+"&gameWeek="+gameWeek+"&teamName="+teamName, true);
			xmlhttp.send(null);

			function callback() {

				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					var htmlResponse = xmlhttp.responseText;
					//alert(htmlResponse);

					if( htmlResponse != "choice ok") {
						alert(htmlResponse);
					} else {
						location.reload();
					}
					

				} else {
				    //alert("Error loading page\n");  
				}
			}
			
			
		}
		
		createRequest(gameName,gameWeek,teamName);
	
	},	


};

function previousGWClicked() {
	var previousGW = GAMEWEEK;
	previousGW--;
	window.location.href = "/LMS/game.html?gameName="+GAMENAME+"&gameWeek="+previousGW;
}
	
function nextGWClicked() {
	var nextGW = ++GAMEWEEK;
	window.location.href = "/LMS/game.html?gameName="+GAMENAME+"&gameWeek="+nextGW;
}

$(document).on("click","button[teamName]",function() {
	var teamName = $(this).attr("teamName");
	//alert(teamName);
	
	var htmlForModal = "" +
	"<div id=\"confirmModal\" class=\"modal fade\"> " +
	"	<div class=\"modal-dialog\"> " +
	"			<div class=\"modal-content\"> " +
	"        	<div class=\"modal-header\"> " +
	"           	<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button> " +
	"            	<h4 class=\"modal-title\">Confirmation</h4> " +
	"           </div> " +
	"		    <div class=\"modal-body\"> " +
	"		    	<p>Are you sure you want to choose "+teamName+" this week?</p> " +
	"		    </div> " +
	"		    <div class=\"modal-footer\"> " +
	"		    	<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\" onclick=\"removeModal()\">Cancel</button> " +
	"		        <button type=\"button\" class=\"btn btn-success\" data-dismiss=\"modal\" onclick=\"makeChoice('"+teamName+"')\">Make Choice</button> " +
	"		    </div> " +
	"		</div> " +
	"	</div> " +
	"</div> ";
	
	
	$("body").append(htmlForModal);
	
	$("#confirmModal").modal('show');

	
	
});

function removeModal() {
	$("#confirmModal").modal('hide');
	$("#confirmModal").remove();
}

function makeChoice(teamName) {
	$("#confirmModal").modal('hide');
	$("#confirmModal").remove();
	Games_AJAX.makeChoice(GAMENAME,GAMEWEEK,teamName);
}



