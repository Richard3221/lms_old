package servlets.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import servlets.ParentServlet;
import lms.Fixture;
import lms.Gameweek;

public class GameServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GameServlet.class.getName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		HttpSession session =  request.getSession(false);
		String email = (String) session.getAttribute("user");
		String operation = request.getParameter("operation");

		if( operation.equals("getFixtures") ) {
			String gameName = request.getParameter("gameName");
			int gameWeek = Integer.valueOf(request.getParameter("gameWeek"));
			boolean gameExists = lookForGame(gameName,email);
			
			if(gameExists) {				
				Gameweek GW = new Gameweek(getFixturesFromDB(gameWeek));
				GW.setCurrentGameweek(getCurrentGameWeek());
				GW.setGameweek(gameWeek);
				GW.setAvailableTeamsString(getAvailableTeams(email, gameName));
				GW.setUsedTeamsString(getUsedTeams(email, gameName));
				GW.setStartWeek(getStartWeek(email, gameName));
				GW.setRound(GW.getGameweek() - GW.getStartWeek() + 1);
				GW.setEliminated(getIsEliminated(email, gameName));
				GW.setBtnBHTMLProperties();
				int currentGameWeek = getCurrentGameWeek();

				String usedTeams = getUsedTeams(email, gameName);
				String [] usedTeamsArry = usedTeams.split(",");
				int startWeek = getStartWeek(email, gameName);
				int round = gameWeek - startWeek + 1;

				if( gameWeek == currentGameWeek && usedTeamsArry.length != round) {
					String html = createFixturesHTML(GW.getFixturesArrayList(),gameName,gameWeek,round,currentGameWeek);
					out.write(html);
				} else if( gameWeek <= currentGameWeek   ) {
					String html = createFixturesHTML(GW.getFixturesArrayList(),gameName,gameWeek,round,currentGameWeek);
					out.write(html);
				} else {
					String html = createFixturesHTML(GW.getFixturesArrayList(),gameName,gameWeek,round,currentGameWeek);
					out.write(html);
				}

			} else {
				out.write("Wrong game");
			}
		} else if( operation.equals("makeChoice") ) {
			String gameName = request.getParameter("gameName");
			int gameWeek = Integer.valueOf(request.getParameter("gameWeek"));
			String teamName = request.getParameter("teamName");
			int startWeek = getStartWeek(email, gameName);
			int round = gameWeek - startWeek + 1;
			String availableTeams = getAvailableTeams(email, gameName);
			String result = checkChoice(availableTeams,startWeek,round,teamName);

			String availableTeamsArray [] = availableTeams.split(",");
			
			List<String> availableTeamslist = new ArrayList<String>(Arrays.asList(availableTeamsArray));
			availableTeamslist.remove(teamName);
			String newAvailableTeams = "";
			for (String tempTeamName : availableTeamslist) {
				newAvailableTeams += tempTeamName + ",";
			}
			newAvailableTeams = newAvailableTeams.substring(0, newAvailableTeams.length()-1);
			String usedTeams = getUsedTeams(email, gameName);
			String newUsedTeams = usedTeams+","+teamName;
			if(result.equals("choice ok")) {
				updateSelectedTeams(email, gameName,newUsedTeams,newAvailableTeams);
				out.write(result);
			} else {
				out.write(result);
			}
			
		}

	}

	private String checkChoice(String availableTeams, int startWeek, int round, String teamName) {
		
		String availableTeamsArray [] = availableTeams.split(",");
		int numberOfTeamsLeftToChooseFrom = availableTeamsArray.length;
		int totalNumberOfTeams = 20;
		if(	!availableTeams.contains(teamName)) {
			return "Team already used";
		} else if( totalNumberOfTeams-numberOfTeamsLeftToChooseFrom != (round-1) ) {
			return "You cannot choose a team for round "+round;
		} else {
			return "choice ok";
		}
		
	}

	
	private String createFixturesHTML(List <Fixture> fixturesArrayList, String gameName, int gameWeek, int round, int currentGameWeek) throws ServletException {
		//String HTML = "<p>";
		
		String fontColour = "";
		String panel = "";
		
		if(currentGameWeek == gameWeek) {
			fontColour = "#419641";
			panel = "\"panel panel-success\"";
		} else if( currentGameWeek > gameWeek ) {
			fontColour = "#c12e2a";
			panel = "\"panel panel-danger\"";
		} else {
			fontColour = "#2aabd2";
			panel = "\"panel panel-info\"";
		}
		
		String HTML = "<div class="+panel+" style=\"align:center;\">";
		HTML += "<div class=\"panel-heading\" style=\"text-align: left;\">"+gameName+"</div>" +
		        "<div class=\"panel-body\" style=\"text-align: left;\" >" +
		        "<div class=\"well well-sm\" align=\"center\" style=\"width:260px;height:40px;\"><font color=\""+fontColour+"\">Gameweek: "+gameWeek+" / Game Round: "+round+" </font></div>" +
		        "</div>";
		
		HTML += "<table class=\"table\" >" +
		        "	<thead>" +
			    "		<tr>" +
			    "	    	<th style=\"text-align: left; width:120px;\">Time</th>" +
			    "	    	<th style=\"text-align: right; width:130px;\"></th>" +
			    "   		<th style=\"text-align: right; width:115px;\">Home Team</th>" +
			    "   		<th style=\"text-align: right; width:40px;\"></th>" +
			    "    		<th style=\"text-align: center; width:40px;\"></th>" +
			    "    		<th style=\"text-align: left; width:40px;\"></th>" +
			    "    		<th style=\"text-align: left; width:115px;\">Away Team</th>" +
			    "    		<th style=\"text-align: left; width:70px;\"></th>" +
			    "  		</tr>" +
			    "	</thead>";
		
		HTML += "<tbody>";
	
		for (Fixture fixture : fixturesArrayList) {
			
			String time = fixture.getTime().toString();
			time = time.substring(0,(time.length()-3));
			String kickOffTime = fixture.getDate() + " " + fixture.getMonth() + " " + time;
			
			HTML += "	    <tr>" +
				    "    		<th style=\"text-align: center; vertical-align: middle;\">"+kickOffTime+"</th>" +
				    "   		<td style=\"text-align: right; font-weight:normal; vertical-align: middle;\">"+fixture.getSelectHomeTeamBtnHTML()+"</td>" +
				    "    		<td style=\"text-align: right; vertical-align: middle;\">"+fixture.getHomeTeam()+"</td>" +
				    "    		<td style=\"text-align: right; vertical-align: middle;\"><img height=\"30\" width=\"30\" src=\"images/crests/"+fixture.getHomeTeam().replace(" ","")+".png\" alt=\""+fixture.getHomeTeam()+"\"></img></td>" +
				    "    		<th style=\"text-align: center; vertical-align: middle;\">"+fixture.getResultStr()+"</th>" +
				    "    		<td style=\"text-align: left; vertical-align: middle;\"><img height=\"30\" width=\"30\" src=\"images/crests/"+fixture.getAwayTeam().replace(" ","")+".png\" alt=\""+fixture.getAwayTeam()+"\"></img></td>" +
				    "    		<td style=\"text-align: left; vertical-align: middle; \">"+fixture.getAwayTeam()+"</td>" +
				    "   		<td style=\"text-align: left; font-weight:normal;\">"+fixture.getSelectAwayTeamBtnHTML()+"</td>" +
				    "  		</tr>";
					//<img src=\"images/crests/"++"\" alt=\"Man Utd\"></img>
			//HTML += addBtn(game,activeGames);
		}
		
		HTML += "	</tbody>" +
		        "</table>"+
				"</div>";
		
		return HTML;
	}

	private String getAvailableTeams(String email, String gameName) throws ServletException {

		String teams = "";
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "";
			selectStatement = "SELECT `availableteams` FROM `usersplaying` WHERE gameName = ? AND email = ?";

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, gameName);
			prepStmt.setString(2, email);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				teams += rs.getObject(1);
			}
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
					rs = null;
				}
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		return teams;
	
	}
	
	private String getUsedTeams(String email, String gameName) throws ServletException {

		String teams = "";
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "";
			selectStatement = "SELECT `usedteams` FROM `usersplaying` WHERE gameName = ? AND email = ?";

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, gameName);
			prepStmt.setString(2, email);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				teams += rs.getObject(1);
			}
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
					rs = null;
				}
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		return teams;
	
	}
	
	private int getStartWeek(String email, String gameName) throws ServletException {

		int startWeek = 0;

		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "";
			selectStatement = "SELECT `startweek` FROM `usersplaying` WHERE gameName = ? AND email = ?";

			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, gameName);
			prepStmt.setString(2, email);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				startWeek = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
					rs = null;
				}
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		
		return startWeek;
	
	}
	
	private boolean lookForGame(String gameName, String email) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "Select * From `usersplaying` WHERE email = ? and gameName = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, email.toLowerCase());
			prepStmt.setString(2, gameName);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				result += "2"+rs.getObject(1);
			}		
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {				
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		
		if(result == "") {
			return false;
		} else {
			return true;
		}
	}

	private boolean getIsEliminated(String email, String gameName) throws ServletException {
		
		String eliminated = "";
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "SELECT eliminated FROM `usersplaying` WHERE email = ? and gameName = ?  ORDER BY gameName DESC ";
		
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, email.toLowerCase());
			prepStmt.setString(2, gameName);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				eliminated = String.valueOf(rs.getObject(1));
			}
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
					rs = null;
				}
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		System.out.println("Eliminated: "+eliminated);
		if(eliminated.equals("true")) {
			return true;
		} else {
			return false;
		}
			
	}
	
	private boolean updateSelectedTeams(String hostEmail, String gameName, String updatedUsedTeams, String updatedAvailableTeams) throws ServletException {
		
		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		logger.trace("UpdatedTeams:");
		logger.trace(updatedUsedTeams);
		logger.trace("updatedAvailableTeams:");
		logger.trace(updatedAvailableTeams);
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "UPDATE `usersplaying` SET `availableteams`= ?, `usedteams` = ?, `choicemade` = 0 where `email` = ? and `gameName` = ?" ;
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, updatedAvailableTeams);
			prepStmt.setString(2, updatedUsedTeams);
			prepStmt.setString(3, hostEmail);
			prepStmt.setString(4, gameName);

			rs = prepStmt.executeUpdate();	
			
		
		} catch (SQLException e) {
			throw new ServletException("Servlet Could not display records.", e);
		} catch (ClassNotFoundException e) {
			throw new ServletException("JDBC Driver not found.", e);
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
					stmt = null;
				}
				if(con != null) {				
					con.close();
					con = null;
				}
			} catch (SQLException e) {}
		}
		
		if(rs > 0) {
			return true;
		} else {
			return false;
		}
		
	}
	

}
