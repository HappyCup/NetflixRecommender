package org.timemodel.gradient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.baseline.Rating;

public class UserRatings {

	public List<Rating> userRatings;
	protected List<Rating> controlPoints = new ArrayList<Rating>();
	protected double[] BU_TL;
	
	public UserRatings() {
		this.userRatings = new ArrayList<Rating>();
	}

	public void addRating(Rating rating) {
		this.userRatings.add(rating);
	}
	
	public double getBU_TL(int tl){
		return BU_TL[tl];
	}
	public void setBU_TL(int tl, double value){
		BU_TL[tl] = value;
	}
	public int getPointNum(){
		return controlPoints.size();
	}
	public double getSumBU_TL(){
		double sum = 0;
		for(double a : BU_TL){
			sum += a*a;
		}
		return sum;
	}
	protected void computeContolPoints() {
		int ratingTimes = this.userRatings.size();
		int pointsNum = (int) Math.pow(ratingTimes, 0.25);
		int span = ratingTimes / (pointsNum + 1);

		BU_TL = new double[pointsNum];
		Collections.sort(userRatings);

		for (int i = 1; i < pointsNum + 1; i++) {
			Rating rating = userRatings.get(i * span);
			this.controlPoints.add(rating);
		}
	}

	//
	public double getOverDeviation(long time) {
		if (this.controlPoints.size() == 0) {
			computeContolPoints();
		}
		double sumDivisor = 0;
		double sumDividend = 0;
		for (int i = 0; i< BU_TL.length;i++) {
			Rating rating = this.controlPoints.get(i);
			double power = (-0.3)
					* TimeSplite.dayContrast(time, rating.getTimeStamp());// not
																			// accurate
			double multiplier = Math.pow(Math.E, power);
			sumDivisor += multiplier;
			sumDividend += multiplier * BU_TL[i];
		}
		if (sumDividend == 0)
			return 0;
		else
			return sumDividend / sumDivisor;
	}

	public double computeFactor(long time , int tl){
		double sumDivisor = 0;
		double sumDividend = 0;
		for (int i = 0; i< BU_TL.length;i++) {
			Rating rating = this.controlPoints.get(i);
			double power = (-0.3)
					* TimeSplite.dayContrast(time, rating.getTimeStamp());// not
																			// accurate
			double multiplier = Math.pow(Math.E, power);
			sumDivisor += multiplier;
			if(i == tl){
				sumDividend = multiplier;
			}
		}
		if (sumDividend == 0)
			return 0;
		else
			return sumDividend / sumDivisor;
	}
}
