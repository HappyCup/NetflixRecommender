package org.neighborhood.improved;

public class MovieBind {

	public int movie1;
	public int movie2;
	
	public MovieBind(){}
	public MovieBind(int movie1, int movie2){
		this.movie1 = movie1;
		this.movie2 = movie2;
	}
	
	@Override
	public boolean equals(Object movieBind){
		if(this == movieBind){
			return true;
		}
		if(!(movieBind instanceof MovieBind)){
			return false;
		}
		MovieBind bind = (MovieBind) movieBind;
		return this.movie1 == bind.movie1 && this.movie2 == bind.movie2;
	}
	
	@Override
	public int hashCode(){
		return Integer.toString(this.movie1).hashCode() * this.movie2;
	}
}
