package servlets.dashboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import servlets.ParentServlet;

public class DashboardServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		HttpSession session =  request.getSession(false);
		String email = (String) session.getAttribute("user");
		String operation = request.getParameter("operation");

		if( operation.equals("getActiveGames") ) {
			
			String currentGameweek = request.getParameter("currentGameweek");
			String games = retrieveGames(email,true,false,Integer.parseInt(currentGameweek));
					
			if(games.equals("")) {
				out.write("No Games");
			} else {
				String [] gamesArray = games.split(",");
				String html = createGamesHTML(gamesArray,true,false);
				out.write(html);
			}
			
		} else if( operation.equals("getActiveButEliminatedGames") ) {
			
			String currentGameweek = request.getParameter("currentGameweek");
			String games = retrieveGames(email,true,true,Integer.parseInt(currentGameweek));
						
			if(games.equals("")) {
				out.write("No Games");
			} else {
				String [] gamesArray = games.split(",");
				String html = createGamesHTML(gamesArray,true,true);
				out.write(html);
			}
			
		} else if( operation.equals("getEndedGames") ) {
			
			String currentGameweek = request.getParameter("currentGameweek");
			String games = retrieveGames(email,false,false,Integer.parseInt(currentGameweek));
			
			if(games.equals("")) {
				out.write("No Games");
			} else {
				String [] gamesArray = games.split(",");
				String html = createGamesHTML(gamesArray,false,false);
				out.write(html);
			}
			
		} else if( operation.equals("getGameweek") ) {

			int currentGameWeek = getCurrentGameWeek();
			if( currentGameWeek > 0 && currentGameWeek < 39 ) {
				out.write(String.valueOf(currentGameWeek));
			} else {
				out.write("Failed to get current gameweek");
			}
		}
		


	}
	
	private String retrieveGames(String email, boolean activeGames, boolean eliminated, int currentGameweek) throws ServletException {
		
		String games = "";
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "";
			if(activeGames) {
				if( !eliminated ) { //active games and not eliminated
					selectStatement = "SELECT * FROM `usersplaying` WHERE email = ? and endweek >= ? and eliminated = 0 ORDER BY gameName DESC ";
				} else {// active games but eliminated
					selectStatement = "SELECT * FROM `usersplaying` WHERE email = ? and endweek >= ? and eliminated = 1 ORDER BY gameName DESC ";
				}
			} else { //inactive games
				selectStatement = "SELECT * FROM `usersplaying` WHERE email = ? and endweek < ? ORDER BY gameName DESC ";
			}			
						
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, email.toLowerCase());
			prepStmt.setInt(2, currentGameweek);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				games += rs.getObject(2);
				if(!rs.isLast()) {
					games +=",";
				}
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
		
		return games;
	}
	
	private String createGamesHTML(String[] games, boolean activeGames, boolean eliminated) throws ServletException {
		//String HTML = "<p>";
		String HTML = "";
	
		for (String game : games) {
			
			if (eliminated) {
				HTML +=  "<button gamename=\""+game+"\" id=\""+game+"GameBtn\"  class=\"btn btn-info\">"+game+"</button>\n";	
			} else if(activeGames) {
				HTML +=  "<button gamename=\""+game+"\" id=\""+game+"GameBtn\"  class=\"btn btn-success\">"+game+"</button>\n";	
			} else {
				HTML +=  "<button gamename=\""+game+"\" id=\""+game+"GameBtn\"  class=\"btn btn-danger\">"+game+"</button>\n";	
			}
		}
		
		//HTML += "</p>";
		return HTML;
	}




}
