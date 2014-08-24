package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class SessionCheckFilter
 */
public class SessionCheckFilter implements Filter
{	
    private ServletContext context;
    
    public void init(FilterConfig fConfig) throws ServletException {

    }
     
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
         
        String uri = req.getRequestURI();
         
        HttpSession session = req.getSession(false);
        
        if(session == null && !uri.endsWith("index.html") &&  !uri.endsWith("LoginServlet") && uri.indexOf("/css") == -1 && uri.indexOf("/images") == -1 && uri.indexOf("/js") == -1 ){
            res.sendRedirect("index.html");
        } else {
    		if( uri.contains("admin")  ) {
        		String email = (String) session.getAttribute("user");
        		if (email.equals("lmsadmin@gmail.com")) {
                    chain.doFilter(request, response);
        		} else {
                    res.sendRedirect("index.html");
        		}
    			
     		} else {     			
                chain.doFilter(request, response);    			
    		}
          }    
    }     
 
    public void destroy() {
        //close any resources here
    }
}
