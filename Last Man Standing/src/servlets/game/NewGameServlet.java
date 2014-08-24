package servlets.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import servlets.ParentServlet;

/**
 * Servlet implementation class NewGameServlet
 */
public class NewGameServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		
		HttpSession session =  request.getSession(false);
		String hostEmail = (String) session.getAttribute("user");
		String operation = request.getParameter("operation");
		String gameName = request.getParameter("gameName");
		
		
		if(operation.equals("checkGame")) {
			out.write(String.valueOf(lookForGame(hostEmail,gameName)));
		} else if ( operation.equals("newGame") ) {
			String playerEmails = request.getParameter("playerEmails");
			playerEmails = playerEmails.trim().replaceAll(",+", ",");

			int currentGameWeek = getCurrentGameWeek();

			boolean createGameSuccess = createGame(hostEmail,gameName,currentGameWeek);
			String allTeams = getListOfTeams();
			boolean addPlayersSuccess = addPlayersToGame(hostEmail,gameName,playerEmails,allTeams,currentGameWeek);

			out.write(String.valueOf(createGameSuccess)+"|"+String.valueOf(addPlayersSuccess));
		}
		
	
		//out.write(playerEmails);
	}
  
    private String getListOfTeams() throws ServletException {
    	
    	Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "SELECT `homeTeam`, `awayTeam`  FROM `fixtures` WHERE gameweek = 38";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);

			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				result += rs.getObject(1)+","+rs.getObject(2)+",";
			}	
			
			if (result.endsWith(",")) {
				result = result.substring(0, result.length() - 1);
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
		
		
		return result;

	}

	private boolean lookForGame(String hostEmail, String gameName) throws ServletException {
		
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "Select * From `usersplaying` WHERE email = ? AND gameName = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, hostEmail.toLowerCase());
			prepStmt.setString(2, gameName.toLowerCase());;

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
	
	private boolean createGame(String hostEmail, String gameName, int currentGameweek) throws ServletException {
				
		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "INSERT INTO `games`(`gameName`, `hostEmail`, `startweek`, `endweek`) VALUES (?,?,?,?)";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, gameName.toLowerCase());
			prepStmt.setString(2, hostEmail.toLowerCase());
			prepStmt.setInt(3, currentGameweek);
			prepStmt.setInt(4, 100);

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
	
	private boolean addPlayersToGame(String hostEmail, String gameName, String playerEmails, String teamsList, int currentGameweek) throws ServletException {
		
		String [] playerEmailsArray = null;
		if(playerEmails.indexOf(",") == -1) { 
			if(playerEmails != "") {
				playerEmailsArray = new String[1];
				playerEmailsArray[0] = playerEmails;
			}
		} else {
			playerEmailsArray = playerEmails.split(",");
		}

		
		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "INSERT INTO `usersplaying`(`email`, `gameName`, `availableteams`, `usedteams`, `startweek`, `endweek`, `choicemade`) VALUES (?,?,?,?,?,?,?)";
			
			if(playerEmailsArray != null) {
				for (String email : playerEmailsArray) {
					PreparedStatement prepStmt = con.prepareStatement(statement);
					prepStmt.setString(1, email.toLowerCase());
					prepStmt.setString(2, gameName.toLowerCase());
					prepStmt.setString(3, teamsList);
					prepStmt.setString(4, "");
					prepStmt.setInt(5, currentGameweek);
					prepStmt.setInt(6, 39);
					prepStmt.setInt(7, 0);

					rs = prepStmt.executeUpdate();		
				}
			}			

		} catch (SQLException e) {
			if (e.getMessage().toLowerCase().contains("duplicate entry")) {
				   // handle duplicate table name problem
		    } else {
				throw new ServletException("Servlet Could not display records.", e);
		    }
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
