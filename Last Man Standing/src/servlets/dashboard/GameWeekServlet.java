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

public class GameWeekServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		HttpSession session =  request.getSession(false);		
		String operation = request.getParameter("operation");

		if( operation.equals("getGameweek") ) {

			int currentGameWeek = getCurrentGameWeek();
			if( currentGameWeek > 0 && currentGameWeek < 39 ) {
				out.write(String.valueOf(currentGameWeek));
			} else {
				out.write("Failed to get current gameweek");
			}
		}
	}

	/*
	private int getCurrentGameWeek() throws ServletException {
		
		int gameWeek = 0;
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String selectStatement = "SELECT * FROM `fixtures` WHERE `dateTime` >= ? ORDER BY `dateTime` ASC LIMIT 0 , 1";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
		    Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    format.format(date);
		    
			prepStmt.setString(1, format.format(date));
			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				gameWeek = rs.getInt(3);
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
		
		return gameWeek;
	}*/
	
}
