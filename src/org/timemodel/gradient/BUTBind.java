package org.timemodel.gradient;

public class BUTBind {

	public int userID;
	public long time;
	
	public BUTBind(){}
	public BUTBind(int userID , long time){
		this.userID = userID;
		this.time = time;
	}
	
	@Override
	public boolean equals(Object Bind){
		if(this == Bind){
			return true;
		}
		if(!(Bind instanceof BUTBind)){
			return false;
		}
		BUTBind bind = (BUTBind) Bind;
		return this.userID == bind.userID && this.time == bind.time;
	}
	
	@Override
	public int hashCode(){
		return Integer.toString(this.userID).hashCode() + Long.toString(time).hashCode();
	}
}
