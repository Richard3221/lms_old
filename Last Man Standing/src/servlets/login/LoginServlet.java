package servlets.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.startup.FailedContext;

import servlets.ParentServlet;

public class LoginServlet extends ParentServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter(); 
		String operation = request.getParameter("operation");

		if( operation.equals("login") ) {
	
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			
			boolean validCredentials = checkCredentials(email,password);
			
			if(validCredentials == true) {
		        HttpSession session = request.getSession();
		        session.setAttribute("user", email);
		        session.setMaxInactiveInterval(30*60);
				if( email.equals("lmsadmin@gmail.com")) {
					out.write("/LMS/admin.html");
				} else {
					out.write("/LMS/dashboard.html");
				}
			} else {
				out.write("Login failed");
			}
		} else if(  operation.equals("logout") ) {
			HttpSession session =  request.getSession(false);
			session.invalidate();
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setDateHeader("Expires", 0); // Proxies.
			out.write("index.html");
		}
		
		//out.write(String.valueOf(validCredentials) );

	}
	
	private boolean checkCredentials(String email, String password) throws ServletException {
		
		boolean validCredentials = false;
		String passwordFromDB = "";
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			//stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String selectStatement = "SELECT * FROM `users` WHERE email = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setString(1, email);
			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				passwordFromDB += rs.getObject(2);
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
		
		if(passwordFromDB.equals(password)) {
			validCredentials = true;
		} 
		
		return validCredentials;
	}

}
