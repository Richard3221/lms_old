package servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lms.Fixture;
import lms.Gameweek;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Servlet implementation class ParentServlet
 */
@WebServlet("/ParentServlet")
public class ParentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(ParentServlet.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//local
	//private static String dbUsername = "LMSDBUser";
	//private static String dbPassword = "X9fCbx2D65samhjx";
	//private static String hostname = "jdbc:mysql://localhost:3306/lms";

	//live
	private static String dbUsername = "adminnI66IsZ";
	private static String dbPassword = "44rzcq_9tYVy";
	private static String hostname = "jdbc:mysql://hub-lastmanstanding.rhcloud.com:3306/hub";

	@Override
	public void init(ServletConfig config) throws ServletException {
	   System.out.println("Initialising log4j");
	   String log4jLocation = config.getInitParameter("log4j-properties-location");
	 
	   ServletContext sc = config.getServletContext();
	 
	   if (log4jLocation == null) {
	      System.out.println("No log4j properites...");
	      BasicConfigurator.configure();
	   } else {
	      String webAppPath = sc.getRealPath("/");
	      String log4jProp = webAppPath + log4jLocation;
	      File output = new File(log4jProp);
	 
	      if (output.exists()) {
	         System.out.println("Initialising log4j with: " + log4jProp);
	         PropertyConfigurator.configure(log4jProp);
	         logger.info("Deployed");
	      } else {
	         System.out.println("Find not found (" + log4jProp + ").");
	         BasicConfigurator.configure();
	      }
	   }
	   
	 
	   super.init(config);
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ParentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected Date getDebugDate() {
    	Date debugDate = null;
		try {
			debugDate = sdf.parse("2014-08-25 23:00:00");
			//logger.info("debugDate.toString(): "+debugDate.toString());					   
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return debugDate;
    }
    
    protected boolean getIsDebug() {
    	return false;
    }

	protected int getCurrentGameWeek() throws ServletException {
        logger.info("getCurrentGameWeek()");

		int gameWeek = 0;
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String selectStatement = "SELECT * FROM `fixtures` WHERE `dateTime` >= ? ORDER BY `dateTime` ASC LIMIT 0 , 1";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
		    Date currentDate = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(getIsDebug()) {
				currentDate = getDebugDate();
			}
		    format.format(currentDate); //needed??
	        //logger.info("getCurrentGameWeek(): format.format(date): "+format.format(date));

			prepStmt.setString(1, format.format(currentDate));
			rs = prepStmt.executeQuery();			
			
			while(rs.next()){
				gameWeek = rs.getInt(3);
			}
	        logger.info("getCurrentGameWeek(): gameWeek: "+gameWeek);

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
	}
	
	protected int lockGame(Date unlockDate) throws ServletException {
		
        logger.info("lockGame() unlockDate: "+unlockDate.toString());

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String statement = "UPDATE `lock` SET islocked = ?, unlockTime = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setInt(1,1);
			Timestamp timestamp = new java.sql.Timestamp(unlockDate.getTime());
			prepStmt.setTimestamp(2, timestamp);

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
		
		return rs;
	}
	
	protected int unLockGame(Date unlockDate) throws ServletException {
        logger.info("unLockGame()");

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			
			String statement = "UPDATE `lock` SET islocked = ?, unlockTime = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.setInt(1,0);
			Timestamp timestamp = new java.sql.Timestamp(unlockDate.getTime());
			prepStmt.setTimestamp(2, timestamp);

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
		
		return rs;
	}
	
	protected Date getStartTimeOfFinalGameInCurrentGameWeek() throws ServletException {
		logger.trace("GameweekEndedChecker.getStartTimeOfFinalGameInCurrentGameWeek()");
		Timestamp timestamp = null;

		Date date = null;
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String selectStatement = "SELECT dateTime FROM `fixtures` WHERE `gameweek` = ? ORDER BY `dateTime` desc LIMIT 0 , 1";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setInt(1, getCurrentGameWeek());
			rs = prepStmt.executeQuery();			
			logger.trace("Query: "+prepStmt.toString());
			while(rs.next()){
				timestamp = rs.getTimestamp(1);
			}
			date = new Date(timestamp.getTime());
			logger.trace("date: "+date);

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
		

		return date;
  
	}
	
	protected boolean updateFixtures() throws ServletException {
		boolean success = false;
		dropFixtureTable();
		createFixtureTable();
		int gameweek = 1;
		int retryCount = 5;
		String html = "";
		while(gameweek < 39) {
			boolean wasGetFixturesASuccess = true;
			try {
				html = getFixturesFromFF(gameweek);
			} catch (IOException e) {
		        logger.info("getFixtures failed, exception caught");
		        e.printStackTrace();
		        wasGetFixturesASuccess = false;
		        if(retryCount > 0) {
			        logger.info("retrying getFixtures for gameweek: "+gameweek);
			        gameweek--;
					retryCount--;
		        } else {
		        	retryCount = 5;
			        logger.info("moving onto gameweek: "+gameweek);
		        }
			}
			if(wasGetFixturesASuccess) {
				success = updateDBFixtures(html,gameweek);
			}
			gameweek++;
		} 
		
		return success;
	}
	
	protected String getFixturesFromFF(int gameweek) throws IOException {
		
		String html = "";
        URL oracle = new URL("http://fantasy.premierleague.com/fixtures/"+gameweek);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
        	html += inputLine;
        in.close();
		
		return html;
	}
	

	private void dropFixtureTable()  throws ServletException  {
		Connection con = null;  
		Statement stmt = null;

        logger.info("dropFixtureTable()");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String statement = "DROP table IF EXISTS fixtures";	
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.executeUpdate();			

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
		
	}
	
	private void createFixtureTable() throws ServletException {
		Connection con = null;  
		Statement stmt = null;
		
        logger.info("createFixtureTable()");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());	
			String statement = "CREATE TABLE IF NOT EXISTS fixtures (homeTeam VARCHAR(20), awayTeam VARCHAR(20), gameweek INT, date INT, month VARCHAR(4), time TIME,  dateTime DATETIME, result VARCHAR(5) ) ";	
			PreparedStatement prepStmt = con.prepareStatement(statement);
			prepStmt.executeUpdate();			
		
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
		
	}

	private boolean updateDBFixtures(String html, int gameweek) throws ServletException {
        logger.info("updateDBFixtures() GW: "+gameweek);

		Pattern homeTeamPat = Pattern.compile( "<td class=\"ismHomeTeam\">(.*?)</td>", Pattern.MULTILINE );
		Pattern awayTeamPat = Pattern.compile( "<td class=\"ismAwayTeam\">(.*?)</td>", Pattern.MULTILINE );
		Pattern resultPat = Pattern.compile( "<td class=\"ismScore\">(.*?)</td>", Pattern.MULTILINE );
		Pattern timePat = Pattern.compile( "<td>(.*?)</td>", Pattern.MULTILINE | Pattern.DOTALL );

		Matcher matcher = homeTeamPat.matcher(html);
		String homeTeams = "";
		while (matcher.find() ){
			homeTeams += matcher.group(1)+",";
		}
		
		matcher = awayTeamPat.matcher(html);	
		String awayTeams = "";
		while (matcher.find() ){
			awayTeams += matcher.group(1)+",";
		}
		
		matcher = resultPat.matcher(html);
		String resultStr = "";
		while (matcher.find() ){
			resultStr += matcher.group(1)+",";
		}
		resultStr = resultStr.replaceAll(" ","");

		matcher = timePat.matcher(html);
		String times = "";
		while (matcher.find() ){
			times += matcher.group(1)+",";
		}
				
		
		homeTeams = homeTeams.substring(0, homeTeams.length()-1);
		awayTeams = awayTeams.substring(0, awayTeams.length()-1);
		resultStr = resultStr.substring(0, resultStr.length()-1);
		times = times.replaceAll("(,<)(.*?)(>)", "");
		times = times.substring(0, times.length()-1);

		String homeTeamsArray[] = homeTeams.split(",");
		String awayTeamsArray[] = awayTeams.split(",");
		String resultsArray[] = resultStr.split(",");
		String timesArray[] = times.split(",");

		int numberOfGames = homeTeamsArray.length;
		String finalTimesArray[] = new String [numberOfGames];
		
		int count = 0;
		while (count < numberOfGames) {
			finalTimesArray[count] = timesArray[count];
			count++;
		}
		
		Connection con = null;  
		Statement stmt = null;
		int[] rs;
		String result = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());	
		
			con.setAutoCommit(false);  
			String statement = "INSERT INTO `fixtures`(`homeTeam`, `awayTeam`, `gameweek`, `date`, `month`, `time`, `dateTime`, `result`) VALUES (?,?,?,?,?,?,?,?)";

			PreparedStatement prepStmt = con.prepareStatement(statement);
			
			int index = 0;
			while(index < homeTeamsArray.length) {
	
				prepStmt.setString(1, homeTeamsArray[index]);
				prepStmt.setString(2, awayTeamsArray[index]);
				prepStmt.setInt(3, gameweek);
				
				String dateMonthTime[] = finalTimesArray[index].split(" ");
				int date = Integer.parseInt(dateMonthTime[0]);
				String month = dateMonthTime[1];
				Time time = Time.valueOf(dateMonthTime[2]+":00");
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date formattedDate = null;
				try {
					formattedDate = format.parse(makeDate(date, month, time));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				prepStmt.setInt(4, date);
				prepStmt.setString(5, month);
				prepStmt.setTime(6, time);	
				java.sql.Timestamp timestamp = new java.sql.Timestamp(formattedDate.getTime());
				prepStmt.setTimestamp(7, timestamp);		
				prepStmt.setString(8, resultsArray[index]);

				prepStmt.addBatch();		
				index++;
			}
			
			rs = prepStmt.executeBatch();
			con.commit(); 
				
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
		
		
		if(rs[0] > 0) {
			return true;
		} else {
			return false;
		}
				
	}
	
	private String makeDate(int dateOfGame, String month, Time time ) {
		String finalDate = "";
		//2013-12-31 00:00:00

	    DateTimeFormatter formatForMonth = DateTimeFormat.forPattern("MMM");	
	    DateTime instanceToGetMonth = formatForMonth.withLocale(Locale.ENGLISH).parseDateTime(month);  
	    
	    int monthIntForGame = instanceToGetMonth.getMonthOfYear();
	    
		Date todaysDate = new Date();
		DateTime todaysDatetime = new DateTime(todaysDate);
		int monthRightNow = todaysDatetime.getMonthOfYear(); 
		int yearRightNow = todaysDatetime.getYear(); 

		int year = 0;
		if ( monthRightNow <= 12 && monthRightNow > 6) {//if we're in the first half of the season	
			if ( monthIntForGame <= 12 && monthIntForGame > 6) {//if the game is in the first half of the season
				year = yearRightNow;
			} else {//if the game is in the second half of the season
				year = yearRightNow+1;
			}			
		} else {//if we're in the second half of the season
			if ( monthIntForGame <= 12 && monthIntForGame > 6) {//if the game is in the first half of the season
				year = yearRightNow-1;
			} else {//if the game is in the second half of the season
				year = yearRightNow;
			}
		}
	    
	    finalDate = year+"-"+monthIntForGame+"-"+dateOfGame+" "+time.toString();
	    
		return finalDate;
	}
	
	protected List<Fixture> getFixturesFromDB(int gameWeek) throws ServletException {

		List <Fixture> fixturesArrayList = new ArrayList<Fixture>();
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "";
			selectStatement = "SELECT * FROM `fixtures` WHERE gameweek = ?  ORDER BY dateTime  ";
	
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setInt(1, gameWeek);

			rs = prepStmt.executeQuery();			
				
			while(rs.next()){
			
				Fixture fixture = new Fixture(); 
				fixture.setHomeTeam( String.valueOf(rs.getObject(1)) );
				fixture.setAwayTeam( String.valueOf(rs.getObject(2)) );
				fixture.setGameWeek( Integer.parseInt( String.valueOf(rs.getObject(3)) ) );
				fixture.setDate( Integer.parseInt( String.valueOf(rs.getObject(4)) ) );
				fixture.setMonth( String.valueOf(rs.getObject(5)) );
				
				String timeStr = String.valueOf(rs.getObject(6));
				Time time = Time.valueOf(timeStr);
				fixture.setTime(time);
				
				String dateStr = String.valueOf(rs.getObject(7));
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date formattedDate = null;
				try {
					formattedDate = format.parse(dateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				fixture.setDateTime( formattedDate.getTime() );
				fixture.setResultStr( String.valueOf(rs.getObject(8)) );

				fixturesArrayList.add(fixture);
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
		
		return fixturesArrayList;
	
	}

	public static String getDBUsername() {
		return dbUsername;
	}
	public static String getDBPassword() {
		return dbPassword;
	}
	public static String getHostname() {
		return hostname;
	}


}
