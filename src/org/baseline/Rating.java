package org.baseline;

public class Rating implements Comparable<Rating>{

	private int userID;
	private int movieID;
	private double score;
	private Long timeStamp;
	private String orgin;
	
	public Rating(){}
	public Rating(String line){
		this.orgin = line;
		String[] tmp = line.split("::");
		if(tmp.length > 3){
			userID = Integer.parseInt(tmp[0]);
			movieID = Integer.parseInt(tmp[1]);
			score = Double.parseDouble(tmp[2]);
			timeStamp = Long.parseLong(tmp[3]);
		}
	}
	
	@Override
	public int compareTo(Rating rating) {
		// TODO Auto-generated method stub
		return this.timeStamp.compareTo(rating.timeStamp);
	}
	@Override
	public String toString(){
		return orgin;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getMovieID() {
		return movieID;
	}
	public void setMovieID(int movieID) {
		this.movieID = movieID;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
