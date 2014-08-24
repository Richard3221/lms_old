package lms;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import servlets.game.GameServlet;

public class Gameweek {
	
	private final static String htmlBtnClassSelect = "btn btn-success";
	private final static String htmlBtnClassUsed = "btn btn-danger";
	private final static String htmlBtnClassSelected = "btn btn-warning";
	private final static String htmlBtnStyleNoClick = "style=\"width: 65px; pointer-events: none;\"";
		
	private static Logger logger = Logger.getLogger(Gameweek.class.getName());

	private int gameweek;
	private int round;
	private int startWeek;
	private int endWeek;
	private int currentGameweek;
	private int numberOfGameweeksInThePast = 0;
	private String availableTeamsString;
	private String usedTeamsString;
	private List availableTeamsList;
	private List usedTeamsList;	
	private String lastTeamSelectedString;
	private boolean isEliminated;
	private boolean teamHasBeenPickedForCurrentGameweek;
	private int numberOfTeamsPicked;

	List <Fixture> fixturesArrayList;
	
	public Gameweek (List <Fixture> fixturesArrayList){
		this.fixturesArrayList = fixturesArrayList;
	}
	
	public List <Fixture> getFixturesArrayList() {
		return fixturesArrayList;
	}

	public int getGameweek() {
		return gameweek;
	}

	public void setGameweek(int gameweek) {
		this.gameweek = gameweek;
	}

	public int getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(int startWeek) {
		this.startWeek = startWeek;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(int endWeek) {
		this.endWeek = endWeek;
	}

	public int getCurrentGameweek() {
		return currentGameweek;
	}

	public void setCurrentGameweek(int currentGameweek) {
		this.currentGameweek = currentGameweek;
	}

	public int getNumberOfGameweeksInThePast() {
		return numberOfGameweeksInThePast;
	}

	public void setNumberOfGameweeksInThePast(int numberOfGameweeksInThePast) {
		this.numberOfGameweeksInThePast = numberOfGameweeksInThePast;
	}

	public String getAvailableTeamsString() {
		return availableTeamsString;
	}

	public void setAvailableTeamsString(String availableTeamsString) {
		this.availableTeamsString = availableTeamsString;
		if(this.availableTeamsString.contains(",")) {
			String [] tempArray = this.availableTeamsString.split(",");		
			setAvailableTeamsList(new ArrayList<String>(Arrays.asList(tempArray)));
		} else {
			ArrayList<String> tempList  = new ArrayList<String>();
			tempList.add(availableTeamsString);
			setAvailableTeamsList(tempList);
		}
	}

	public String getUsedTeamsString() {
		return usedTeamsString;
	}

	public void setUsedTeamsString(String usedTeamsString) {
		this.usedTeamsString = usedTeamsString;
		if(this.usedTeamsString.contains(",")) {			
			String [] tempArray = this.usedTeamsString.split(",");			
			setUsedTeamsList(new ArrayList<String>(Arrays.asList(tempArray)));
			lastTeamSelectedString = tempArray[tempArray.length - 1];
		} else {
			ArrayList<String> tempList  = new ArrayList<String>();
			tempList.add(usedTeamsString);
			setUsedTeamsList(tempList);
			lastTeamSelectedString = usedTeamsString;
		}
	}

	public int getAvailableTeamsListLength() {
		return availableTeamsList.size();
	}
	public void setAvailableTeamsList(List<String> availableTeamsList) {
		this.availableTeamsList = availableTeamsList;
	}

	public int getUsedTeamsListLength() {
		return usedTeamsList.size();
	}
	public void setUsedTeamsList(List<String> usedTeamsList) {
		this.usedTeamsList = usedTeamsList;
		numberOfTeamsPicked = this.usedTeamsList.size();
	}


	public boolean isEliminated() {
		return isEliminated;
	}

	public void setEliminated(boolean isEliminated) {
		this.isEliminated = isEliminated;
	}
	
	public void setBtnBHTMLProperties() {
		logger.trace("");			
		logger.trace("setBtnBHTMLProperties");			
		logger.trace("");			
				
		int currentRound = currentGameweek - startWeek;
		if( currentRound == numberOfTeamsPicked ) {
			teamHasBeenPickedForCurrentGameweek = true;
		} else {
			teamHasBeenPickedForCurrentGameweek = false;
		}
		
		//logger.trace("round: "+round+" numberOfTeamsPicked: "+numberOfTeamsPicked);			
		if(isEliminated) {
			int difference = currentRound - numberOfTeamsPicked + 1;
			String tempUsedTeamsString = usedTeamsString;
			while(difference > 0) {
				logger.trace("isEliminated difference: "+ difference);
				tempUsedTeamsString += ",eliminated";
				difference--;
			}
			logger.trace("tempUsedTeamsString: "+ tempUsedTeamsString);

			setUsedTeamsString(tempUsedTeamsString);
		}
		
		
		if(gameweek < currentGameweek && round != numberOfTeamsPicked) {
			
			int difference = currentGameweek - gameweek;
			if(teamHasBeenPickedForCurrentGameweek) {
				difference--;
			}
			String usedTeams = "";

			while(difference > 0) {
				logger.trace("difference: "+ difference);
				int currentSize = usedTeamsList.size() - 1 ;
				this.availableTeamsString += usedTeamsList.get(currentSize);
				usedTeamsList.remove(currentSize);
				difference--;
			}
			
			for(Object usedTeam : usedTeamsList) {
				usedTeams += usedTeam.toString() + ",";
			}
			usedTeams = usedTeams.substring(0,usedTeams.length()-1);
			logger.trace("usedTeams: "+ usedTeams);			
			setUsedTeamsString(usedTeams);	
			
		}
		
		if(this.isEliminated) {	//eliminated
			logger.trace("Eliminated");			

			if(gameweek <= currentGameweek ) { //gameweek is not in the future
				for (Fixture fixture : fixturesArrayList) {		
					setBtnsEliminated(fixture);
				}
			} else {
				for (Fixture fixture : fixturesArrayList) {		
					setBtnsEliminated(fixture);
				}
			}

		} else  {//not eliminated and gameweek is not in the future
			
			if(gameweek <= currentGameweek ) { //gameweek is not in the future
				logger.trace("NOT Eliminated, gameweek NOT in future");			
				logger.trace("round: "+round+" numberOfTeamsPicked: "+numberOfTeamsPicked);			

				if( gameweek < currentGameweek ) {

					logger.trace("Gameweek is in the past");			
					for (Fixture fixture : fixturesArrayList) {		
						setBtnsPastGameweek(fixture);
					}
				} else if( round  == numberOfTeamsPicked  ) { //team has been picked this week

					logger.trace("Team picked but not eliminated");			
					for (Fixture fixture : fixturesArrayList) {		
						setBtnsTeamAlreadyPickedThisWeek(fixture);
					}
				} else {//team has not been picked this week
					logger.trace("Team not picked and not eliminated");			
					for (Fixture fixture : fixturesArrayList) {		
						setBtnsTeamNotPickedThisWeek(fixture);
					}
				}
			} else { //gameweek is in the future
				logger.trace("NOT Eliminated, gameweek in future");			
				for (Fixture fixture : fixturesArrayList) {		
					setBtnsFutureWeek(fixture);
				}
			}

		} 

	}
	
	private void setBtnsEliminated(Fixture fixture) {
		
		
		if( availableTeamsString.contains(fixture.getHomeTeam()) ) {
			//fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\">Out</button>\n");
			fixture.setSelectHomeTeamBtnHTML("");
		} else {
			if(fixture.getHomeTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\">Selected</button>\n");
			} else {
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}
		}
		
		if( availableTeamsString.contains(fixture.getAwayTeam()) ) {
			fixture.setSelectAwayTeamBtnHTML("");
		} else {
			if(fixture.getAwayTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\">Selected</button>\n");
			} else {
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}	
		}
	}
	
	
	private void setBtnsTeamAlreadyPickedThisWeek(Fixture fixture) {

		//logger.trace("fixture.getHomeTeam(): "+fixture.getHomeTeam()+ "   fixture.getAwayTeam(): "+fixture.getAwayTeam());			

		logger.trace("lastTeamSelectedString: "+lastTeamSelectedString);			

		if( availableTeamsString.contains(fixture.getHomeTeam()) ) {//if the team is available
			if(fixture.getHomeTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {//if it's available and not the team selected for this week make it green disabled and unclickable
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-success active\" style=\"width: 65px; pointer-events: none;\" disabled >Select</button>\n");
			}
		} else {//the team has be used
			if(fixture.getHomeTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}
		}
		
		if( availableTeamsString.contains(fixture.getAwayTeam()) ) {//if the team is available 
			if(fixture.getAwayTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {//if it's available and not the team selected for this week make it green disabled and unclickable
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-success active\" style=\"width: 65px; pointer-events: none;\" disabled >Select</button>\n");
			}
		} else {//the team has be used
			if(fixture.getAwayTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}
		}
	}
	
	private void setBtnsPastGameweek(Fixture fixture) {

		//logger.trace("fixture.getHomeTeam(): "+fixture.getHomeTeam()+ "   fixture.getAwayTeam(): "+fixture.getAwayTeam());			

		logger.trace("lastTeamSelectedString: "+lastTeamSelectedString);			

		if( availableTeamsString.contains(fixture.getHomeTeam()) ) {//if the team is available
			if(fixture.getHomeTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {//if it's available and not the team selected for this week make it green but unclickable
				fixture.setSelectHomeTeamBtnHTML("");
			}
		} else {//the team has been used
			if(fixture.getHomeTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}
		}
		
		if( availableTeamsString.contains(fixture.getAwayTeam()) ) {//if the team is available 
			if(fixture.getAwayTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {//if it's available and not the team selected for this week make it green but unclickable
				fixture.setSelectAwayTeamBtnHTML("");
			}
		} else {//the team has be used
			if(fixture.getAwayTeam().equals(lastTeamSelectedString) ) {//if it's the team selected for this week make it orange
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\" >Selected</button>\n");
			} else {
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
			}
		}
	}
	
	private void setBtnsTeamNotPickedThisWeek(Fixture fixture) {
		
		if( availableTeamsString.contains(fixture.getHomeTeam()) ) {//if the team is available 
			fixture.setSelectHomeTeamBtnHTML("<button teamName=\""+fixture.getHomeTeam()+"\" id=\""+fixture.getHomeTeam()+"PickBtn\" class=\"btn btn-success\">Select</button>\n");
		} else {//the team has be used
			fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\" >Used</button>\n");
		}
		
		if( availableTeamsString.contains(fixture.getAwayTeam()) ) {//if the team is available
			fixture.setSelectAwayTeamBtnHTML("<button teamName=\""+fixture.getAwayTeam()+"\" id=\""+fixture.getAwayTeam()+"PickBtn\"  class=\"btn btn-success\">Select</button>\n");
		} else {//the team has be used
			fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\" >Used</button>\n");
		}
		
	}
	
	private void setBtnsFutureWeek(Fixture fixture) {
		
		if( availableTeamsString.contains(fixture.getHomeTeam()) ) {
			if(!isEliminated) {
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-success\" style=\"width: 65px; pointer-events: none;\" disabled>Select</button>\n");
			} else {
				fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\">Out</button>\n");
			}
		} else {
			fixture.setSelectHomeTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
		}
	
		if( availableTeamsString.contains(fixture.getAwayTeam()) ) {
			if(!isEliminated) {
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-success\" style=\"width: 65px; pointer-events: none;\" disabled>Select</button>\n");
			} else {
				fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-warning active\" style=\"width: 65px; pointer-events: none;\">Out</button>\n");
			}

		} else {
			fixture.setSelectAwayTeamBtnHTML("<button class=\"btn btn-danger active\" style=\"width: 65px; pointer-events: none;\">Used</button>\n");
		}	
		
	}
	
	public List<String> getListOfDrawingLosingTeams() {
		logger.trace("Gameweek.getListOfDrawingLosingTeams()");			

		List <String> drawingLosingTeamsList = new ArrayList <String>();
		
		for(Fixture fixture :  fixturesArrayList) {
			
			String resultStr = fixture.getResultStr();
			logger.trace("getListOfDrawingLosingTeams() resultStr: "+resultStr);			

			String resultStrSplitArray[] = null;
			
			if(resultStr.contains("-")) {
				resultStrSplitArray = resultStr.split("-");
				int homeScore = Integer.parseInt(resultStrSplitArray[0]);
				int awayScore = Integer.parseInt(resultStrSplitArray[1]);
				
				if( homeScore == awayScore) {
					drawingLosingTeamsList.add(fixture.getHomeTeam());
					drawingLosingTeamsList.add(fixture.getAwayTeam());
				} else if(homeScore < awayScore) {
					drawingLosingTeamsList.add(fixture.getHomeTeam());
				} else if (homeScore > awayScore) {
					drawingLosingTeamsList.add(fixture.getAwayTeam());
				}
			} else {
				logger.trace("resultStr not parsable");
			} 
			
		
		}
		return drawingLosingTeamsList;
	}
	

}
