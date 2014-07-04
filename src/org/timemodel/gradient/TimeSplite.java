package org.timemodel.gradient;

import java.util.Calendar;

public class TimeSplite {

	protected static long timeSpan = 10*7*24*60;
	protected static long day = 24*60;
	
	public static int itemBin(long timeBasis,long time){
		int result =  (int)((time - timeBasis)/timeSpan);
		if(result > 30){
			result = 30;
		}
		if(result < 0){
			result = 0;
		}
		return result;
	}
	
	/*
	 * @param time1 the new time in UTC seconds from the epoch
	 * @param time2 the new time in UTC seconds from the epoch
	 */
	public static boolean isTheSameDay(long time1, long time2){
		time1 = time1*1000;
		time2 = time2*1000;
		Calendar c1=Calendar.getInstance();
		c1.setTimeInMillis(time1);
		int y1=c1.get(Calendar.YEAR);
		int m1=c1.get(Calendar.MONTH);
		int d1=c1.get(Calendar.DATE);
		Calendar c2=Calendar.getInstance();
		c2.setTimeInMillis(time2);
		int y2=c2.get(Calendar.YEAR);
		int m2=c2.get(Calendar.MONTH);
		int d2=c2.get(Calendar.DATE);
		return y1==y2&&m1==m2&&d1==d2;
	}
	
	/*
	 * @param time1 the new time in UTC seconds from the epoch
	 * @param time2 the new time in UTC seconds from the epoch
	 */
	public static int dayContrast(long time1, long time2){
		Calendar c1=Calendar.getInstance();
		Calendar c2=Calendar.getInstance();
		time1 = time1*1000;
		time2 = time2*1000;
		if(time1 > time2){
			c1.setTimeInMillis(time1);
			c2.setTimeInMillis(time2);
		}
		else{
			c1.setTimeInMillis(time2);
			c2.setTimeInMillis(time1);
		}
		
		int y1=c1.get(Calendar.YEAR);
		int d1=c1.get(Calendar.DAY_OF_YEAR);
		
		int y2=c2.get(Calendar.YEAR);
		int d2=c2.get(Calendar.DAY_OF_YEAR);
		
		if(y1 == y2){
			return d1 -d2;
		}
		else{
			return (int) (Math.abs(time1 - time2)/day);// not accurate
		}
	}
	/*
	 * @return the days from epoch
	 */
	public static long days(long time){
		return time/day;
	}
}
