package servlets.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import servlets.ParentServlet;

import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

public class AdminServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		String operation = request.getParameter("operation");
		HttpSession session =  request.getSession(false);
		String adminEmail = (String) session.getAttribute("user");

		if( adminEmail.equals("lmsadmin@gmail.com")) {
			String html = "";
			boolean success = false;
			
			if( operation.equals("updateFixtures") ) {
				updateFixtures();
				out.write(Boolean.toString(success));

			} else if ( operation.equals("addUser") ) {
				String email = request.getParameter("email");
				String password = request.getParameter("password");
				boolean userExists = lookForUser(email);
				if(!userExists) {
					success = addUser(email,password);
					out.write(String.valueOf(success));
				} else {
					out.write("User exists");
				}
			} else if ( operation.equals("deleteUser") ) {
				String email = request.getParameter("email");
				boolean deleteAll = Boolean.parseBoolean(request.getParameter("deleteAll"));
				boolean userExists = lookForUser(email);
				if(userExists) {
					success = deleteUser(email);
					if(deleteAll) {
						deleteGameRecords(email);
						out.write(String.valueOf(success));
					} else {
						out.write(String.valueOf(success));
					}
				} else {
					out.write("User does not exist");
				}
			}
		}		

	}
	
	private boolean deleteUser(String email) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "DELETE FROM `users` WHERE email = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, email.toLowerCase());

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

	private boolean deleteGameRecords(String email) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "DELETE FROM `usersplaying` WHERE email = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, email.toLowerCase());

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
	private boolean lookForUser(String email) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		String result = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "Select * From `users` WHERE email = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, email.toLowerCase());

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

	private boolean addUser(String email, String password) throws ServletException {

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "INSERT INTO `users`(`email`, `password`) VALUES (?,?)";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setString(1, email.toLowerCase());
			prepStmt.setString(2, password.toLowerCase());
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

