package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import lms.Gameweek;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import servlets.ParentServlet;

public class GameweekEndedChecker extends ParentServlet implements Job {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GameweekEndedChecker.class.getName());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
 
		logger.trace("");			
		logger.trace("GameweekEndedChecker.execute");		
		logger.trace("");			
	
		Date date;
		try {
			date = getEndTimeOfCurrentGameWeek();
			logger.trace("GameweekEndedChecker gameweekEndDate: "+date);			

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MINUTE, -30);
			Date gameeekEndDateMinus30 = cal.getTime();
			
			logger.trace("GameweekEndedChecker gameweekEndDateMinus30: "+gameeekEndDateMinus30);			

			Date currentDate = new Date();

			if(getIsDebug()) {
				currentDate = getDebugDate();
			}
			
			logger.trace("GameweekEndedChecker currentDate: "+currentDate);			

			if( gameeekEndDateMinus30.before(currentDate) ) { //if the current game week minus 30 minutes is in the past, the game week is over
				logger.info("GameweekEndedChecker Gameweek has ended, making choices for players. LOCKING");			

				Date startTimeOfFinalGame = getStartTimeOfFinalGameInCurrentGameWeek();
				cal.setTime(startTimeOfFinalGame);
				cal.add(Calendar.HOUR, 2);
				Date startTimeOfFinalGamePlus2Hours = cal.getTime();
				
				makeChoiceForUsers();
				lockGame(startTimeOfFinalGamePlus2Hours);
			} else {
				//logger.trace("GameweekEndedChecker Gameweek has not ended");			
			}
			
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	private void makeChoiceForUsers() throws ServletException {
		
		logger.trace("makeChoiceForUsers()");			

		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String selectStatement = "SELECT * FROM `usersplaying` WHERE choicemade = 0 and eliminated = 0";						
			PreparedStatement prepStmt = con.prepareStatement(selectStatement,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			rs = prepStmt.executeQuery();			
			while(rs.next()){

				String availableTeamsString = rs.getString("availableteams");
				String usedTeamsString = rs.getString("usedteams");
				
				logger.trace("availableteams: "+availableTeamsString);			
				logger.trace("usedTeamsString: "+usedTeamsString);			

				logger.trace("updating...");			

				String availableTeamsArray [] = availableTeamsString.split(",");
				List<String> availableTeamslist = new ArrayList<String>(Arrays.asList(availableTeamsArray));
				
				String teamChosen = availableTeamslist.get((int)(Math.random() * availableTeamslist.size()));
				if(usedTeamsString.equals("")) {
					usedTeamsString = teamChosen;
				} else {
					usedTeamsString += ","+teamChosen;
				}
				
				String newAvailableTeams = "";
				for (String tempTeamName : availableTeamslist) {
					if(tempTeamName != teamChosen) {
						newAvailableTeams += tempTeamName + ",";
					}
				}
				newAvailableTeams = newAvailableTeams.substring(0, newAvailableTeams.length()-1);

				logger.trace("availableteams: "+newAvailableTeams);			
				logger.trace("usedTeamsString: "+usedTeamsString);			

				rs.updateString("availableteams", newAvailableTeams);
				rs.updateString("usedteams", usedTeamsString);

				boolean choiceMade = true;
				rs.updateBoolean("choicemade", choiceMade);
				rs.updateRow();
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
		
	}
	
	protected Date getEndTimeOfCurrentGameWeek() throws ServletException {
		logger.trace("GameweekEndedChecker.getEndTimeOfCurrentGameWeek()");
		Timestamp timestamp = null;

		Date date = null;
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String selectStatement = "SELECT dateTime FROM `fixtures` WHERE `gameweek` = ? ORDER BY `dateTime` ASC LIMIT 0 , 1";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			prepStmt.setInt(1, getCurrentGameWeek());
			rs = prepStmt.executeQuery();			
			//logger.trace("Query: "+prepStmt.toString());
			while(rs.next()){
				timestamp = rs.getTimestamp(1);
			}
			date = new Date(timestamp.getTime());
			//logger.trace("date: "+date);

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
	
}
