package servlets.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import servlets.ParentServlet;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class AccountServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		String operation = request.getParameter("operation");
		HttpSession session =  request.getSession(false);
		String userCurrentEmail = (String) session.getAttribute("user");

		boolean success = false;

		if( operation.equals("updateEmail") ) {
			String newEmail = request.getParameter("email");
			success = updateEmail(userCurrentEmail,newEmail);
			if(success) {
				session.setAttribute("user", newEmail);
			}
			out.write(String.valueOf(success));
		} else if ( operation.equals("updatePassword") ) {
			String password = request.getParameter("password");
			success = updatePassword(userCurrentEmail,password);
			out.write(String.valueOf(success));
		} 
			

	}
	
	private boolean updateEmail(String oldEmail, String newEmail) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "UPDATE `users` SET `email`= ? WHERE `email` = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, newEmail.toLowerCase());
			prepStmt.setString(2, oldEmail.toLowerCase());

			rs = prepStmt.executeUpdate();	
			
			String statement2 = "UPDATE `usersplaying` SET `email`= ? WHERE `email` = ?";
			
			PreparedStatement prepStmt2 = con.prepareStatement(statement2);
			prepStmt2.setString(1, newEmail.toLowerCase());
			prepStmt2.setString(2, oldEmail.toLowerCase());

			rs = prepStmt2.executeUpdate();	
			
			String statement3 = "UPDATE `games` SET `hostEmail`= ? WHERE `hostEmail` = ?";
			
			PreparedStatement prepStmt3 = con.prepareStatement(statement3);
			prepStmt3.setString(1, newEmail.toLowerCase());
			prepStmt3.setString(2, oldEmail.toLowerCase());

			rs = prepStmt3.executeUpdate();	
		
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
	
	private boolean updatePassword(String email, String password) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "UPDATE `users` SET `password`= ? WHERE `email` = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, password);
			prepStmt.setString(2, email.toLowerCase());

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

