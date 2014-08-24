package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lms.LMSScheduler;

/**
 * Servlet implementation class InitializerServlet
 */
@WebServlet("/InitializerServlet")
public class InitializeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitializeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {

    	   try {
    	       System.out.println("InitializerServlet: starting lmsScheduler");
    	       LMSScheduler lmsScheduler = new LMSScheduler();
    	   } catch (Exception ex) {
    	       ex.printStackTrace();
    	   }

     }
	

}
