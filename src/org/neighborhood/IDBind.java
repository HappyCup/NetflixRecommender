package org.neighborhood;

public class IDBind {

	public int userID;
	public int movieID;
	
	public IDBind(){}
	public IDBind(String line){
		String[] tmp = line.split("::");
		if(tmp.length > 1){
			userID = Integer.parseInt(tmp[0]);
			movieID = Integer.parseInt(tmp[1]);
		}
	}
	public IDBind(int userID,int movieID){
		this.userID = userID;
		this.movieID = movieID;
	}
	
	@Override
	public boolean equals(Object idBind){
		if(this == idBind){
			return true;
		}
		if(!(idBind instanceof IDBind)){
			return false;
		}
		IDBind bind = (IDBind) idBind;
		return this.userID == bind.userID && this.movieID == bind.movieID;
	}
	
	@Override
	public int hashCode(){
		return Integer.toString(this.userID).hashCode() * this.movieID;
	}
}
