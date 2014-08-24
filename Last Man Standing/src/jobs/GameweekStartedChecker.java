package jobs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import lms.Gameweek;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import servlets.ParentServlet;

public class GameweekStartedChecker extends ParentServlet implements Job {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GameweekStartedChecker.class.getName());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
 
		logger.trace("");			
		logger.trace("GameweekStartedChecker.execute");		
		logger.trace("");			
		
		Date unlockDate;

		try {
			unlockDate = getUnLockGameDate();
			logger.trace("GameweekStartedChecker unlockDate: "+unlockDate);			
		
			Date currentDate = new Date();
			if(getIsDebug()) {
				currentDate = getDebugDate();
			}
			
			logger.trace("GameweekStartedChecker currentDate: "+currentDate);			

			if( unlockDate.before(currentDate) ) {
				logger.info("GameweekStartedChecker games all ended, new gameweek starting. UNLOCK");	
				
				eliminatePlayers();
				
				resetChoiceMadeForUsers();
				Date startTimeOfFinalGame = getStartTimeOfFinalGameInCurrentGameWeek();
				Calendar cal = Calendar.getInstance();
				cal.setTime(startTimeOfFinalGame);
				cal.add(Calendar.HOUR, 2);
				Date startTimeOfFinalGamePlus2Hours = cal.getTime();
				//unLockGame(startTimeOfFinalGamePlus2Hours);
			}

			
		} catch (ServletException e) {
			e.printStackTrace();
		}
					
	}

	private void eliminatePlayers() throws ServletException  {
		logger.trace("GameweekStartedChecker.eliminatePlayers()");			

		//updateFixtures();
		int gameweek = getCurrentGameWeek();
		gameweek--;
		Gameweek GW = new Gameweek(getFixturesFromDB(gameweek));
		List <String> drawingLosingTeamsList = GW.getListOfDrawingLosingTeams();
		if(drawingLosingTeamsList.size() > 0) {
			updateDBWithEliminatedPlayers(drawingLosingTeamsList,gameweek);
		}

		//updateGames
		
	}


	private void updateDBWithEliminatedPlayers(List<String> drawingLosingTeamsList, int gameweek) throws ServletException {
		logger.trace("GameweekStartedChecker.updateDBWithEliminatedPlayers()");			

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String statement = "UPDATE `usersplaying` SET `eliminated` = 1, `endweek` = "+gameweek+" where ";
			for (String teamName : drawingLosingTeamsList) {
				if(statement.contains("LIKE")) {
					statement += " OR ";
				}
				statement += "`usedteams` LIKE '%"+teamName+"'";
			}
			logger.trace("updateDBWithEliminatedPlayers() statement: "+statement);			

			PreparedStatement prepStmt = con.prepareStatement(statement);

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
			} catch (SQLException e) {
				
			}
		}
				
	}

	private boolean resetChoiceMadeForUsers() throws ServletException {
		
		logger.trace("GameweekStartedChecker.resetChoiceMadeForUsers()");			

		Connection con = null;  
		Statement stmt = null;
		int rs = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			String statement = "UPDATE `usersplaying` SET `choicemade`= 0 where eliminated = 0 ";
			
			PreparedStatement prepStmt = con.prepareStatement(statement);

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
			} catch (SQLException e) {
				
			}
		}
		
		if(rs > 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	protected Date getUnLockGameDate() throws ServletException {
		logger.trace("GameweekStartedChecker.getUnLockGameDate()");
		Timestamp timestamp = null;

		Date date = null;
		Connection con = null;  
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(getHostname(),getDBUsername(),getDBPassword());
			
			String selectStatement = "SELECT unlockTime FROM `lock` ";
			
			PreparedStatement prepStmt = con.prepareStatement(selectStatement);
			rs = prepStmt.executeQuery();			
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
