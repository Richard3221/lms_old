package lms;

import java.sql.Time;
import java.sql.Timestamp;

public class Fixture {
	
	private String homeTeam;
	private String awayTeam;
	private int gameWeek;
	private int date;
	private String month;
	private Time time;
	private long dateTime;
	private boolean homeTeamCanBePicked;
	private boolean awayTeamCanBePicked;
	private String selectHomeTeamBtnHTML;
	private String selectAwayTeamBtnHTML;
	private String resultStr;


	public String getHomeTeam() {
		return homeTeam;
	}
	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}
	public String getAwayTeam() {
		return awayTeam;
	}
	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}
	public int getGameWeek() {
		return gameWeek;
	}
	public void setGameWeek(int gameWeek) {
		this.gameWeek = gameWeek;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public long getDateTime() {
		return dateTime;
	}
	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
	public boolean getHomeTeamCanBePicked() {
		return homeTeamCanBePicked;
	}
	public void setHomeTeamCanBePicked(boolean homeTeamCanBePicked) {
		this.homeTeamCanBePicked = homeTeamCanBePicked;
	}
	public boolean getAwayTeamCanBePicked() {
		return awayTeamCanBePicked;
	}
	public void setAwayTeamCanBePicked(boolean awayTeamCanBePicked) {
		this.awayTeamCanBePicked = awayTeamCanBePicked;
	}
	public String getSelectHomeTeamBtnHTML() {
		return selectHomeTeamBtnHTML;
	}
	public void setSelectHomeTeamBtnHTML(String selectHomeTeamBtnHTML) {
		this.selectHomeTeamBtnHTML = selectHomeTeamBtnHTML;
	}
	public String getSelectAwayTeamBtnHTML() {
		return selectAwayTeamBtnHTML;
	}
	public void setSelectAwayTeamBtnHTML(String selectAwayTeamBtnHTML) {
		this.selectAwayTeamBtnHTML = selectAwayTeamBtnHTML;
	}
	public String getResultStr() {
		return resultStr;
	}
	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}

	
	
}
