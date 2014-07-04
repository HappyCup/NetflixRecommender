package org.timemodel.gradient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.baseline.Rating;
import org.tools.RMSE;

public class TBLGradientDescent {

	private double OverAverage = 0;
	
	private boolean isDescented = false;
	private double StepSize = 0.005;
	private final double nbuda = 0.01;
	private int UserNum = 6040;
	private int MovieNum = 3952;
	
	protected double[] BU = new double[UserNum + 1];
	protected Map<Integer,UserRatings> userRatings = new HashMap<Integer,UserRatings>();
	protected Map<BUTBind,Double> BU_T = new HashMap<BUTBind,Double>();
	protected double[] BI = new double[MovieNum + 1];
	
	protected double[][] BI_BIN = new double[MovieNum + 1][31];
	protected long[] movieMinTime = new long[MovieNum + 1];
	
	public TBLGradientDescent(String trainingfile) throws IOException{
		FileReader reader = new FileReader(trainingfile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		double sumScore = 0;
		double sumNumber = 0;
		while((str = br.readLine())!=null){
			Rating rating = new Rating(str);
			sumScore += rating.getScore();
			sumNumber ++;
			
			if(!this.userRatings.containsKey(rating.getUserID())){
				UserRatings userRatings = new UserRatings();
				this.userRatings.put(rating.getUserID(), userRatings);
			}
			UserRatings userRatings = this.userRatings.get(rating.getUserID());
			userRatings.addRating(rating);
			
			if(movieMinTime[rating.getMovieID()] == 0 || movieMinTime[rating.getMovieID()] > rating.getTimeStamp()){
				movieMinTime[rating.getMovieID()] = rating.getTimeStamp();
			}
		}
		
		this.OverAverage = sumScore / sumNumber;
		br.close();
	}
	
	protected double getBU_T(int u , long T){
		BUTBind bind = new BUTBind(u,T);
		if(BU_T.containsKey(bind)){
			return BU_T.get(bind);
		}
		else{
			BU_T.put(bind, 0.0d);
			return 0;
		}
	}
	
	//@param T seconds / 6*24
	protected void setBU_T(int u , long T , double value){
		BUTBind bind = new BUTBind(u,T);
		BU_T.put(bind, value);
	}
	
	protected void updateBU(int u , double deviation){
		BU[u] = BU[u] + 2*StepSize*(deviation - nbuda*BU[u]); 
	}
	
	//factor haven't been solved
	protected void updateBU_TL(int u , long time, double deviation){
		UserRatings ratings = this.userRatings.get(u);
		for(int tl = 0 ; tl < ratings.getPointNum();tl++){
			double factor = ratings.computeFactor(time, tl);
			double orgin = ratings.getBU_TL(tl);
			double newBUTL = orgin + 2*StepSize*(factor*deviation - nbuda*orgin);
			ratings.setBU_TL(tl, newBUTL);
		}
	}
	
	protected void updateBU_T(int u , long T , double deviation){
		double orgin = getBU_T(u, T);
		double changed = orgin + 2*StepSize*(deviation - nbuda*orgin);
		setBU_T(u, T ,changed);
	}
	
	protected void updateBI(int i , double deviation){
		BI[i] = BI[i] + 2*StepSize*(deviation - nbuda*BI[i]);
	}
	
	protected void updateBI_BIN(int i , long time , double deviation){
		int bin = getItemBin(i , time);
		BI_BIN[i][bin] = BI_BIN[i][bin] +  2*StepSize*(deviation - nbuda*BI_BIN[i][bin]);
	}
	
	protected double computeBU_TL(int u , long time){
		return this.userRatings.get(u).getOverDeviation(time);
	}
	
	public int getItemBin(int i , long time){
		return TimeSplite.itemBin(movieMinTime[i], time);
	}
	
	protected double predict(int u , int i , long time){
		return OverAverage + BU[u] + computeBU_TL(u, time) + getBU_T(u, time) + BI[i] + BI_BIN[i][getItemBin(i,time)];
	}
	
	protected double sumLoss(){
		double sumloss = 0;
//		double sumBU_TL = 0;
//		double sumBU = 0;
//		double sumBU_T = 0;
//		double sumBI =0;
//		double sumBI_T = 0;
//		for(int i = 1 ; i<this.UserNum; i++){
//			sumBU += BU[i]*BU[i];
//		}
//		for(Entry entry : this.BU_T.entrySet()){
//			sumBU_T += (Double)entry.getValue();
//		}
//		for(int i = 1 ; i<this.MovieNum; i++){
//			sumBI += BI[i]*BI[i];
//		}
//		for(int i = 1 ; i<this.MovieNum; i++){
//			for(int j = 1;j<31;j++){
//				sumBI_T += BI_BIN[i][j];
//			}
//		}
		for(int j =1 ;j < this.UserNum + 1 ;j++){
			UserRatings user_ratings = this.userRatings.get(j);
//			sumBU_TL += user_ratings.getSumBU_TL();
			for(Rating rating : user_ratings.userRatings){
				double predictScore = predict(rating.getUserID(),rating.getMovieID(),rating.getTimeStamp());
				sumloss += (rating.getScore() - predictScore)*(rating.getScore() - predictScore);
			}
		}
		
		return sumloss;
	}
	
	protected void StochasticGradientDescent(){
		for(int i = 0 ; i < 30 ;i++){
			System.out.println("iteration : " + (i) + " time");
			for(int j =1 ;j < this.UserNum + 1 ;j++){
				double deviation = 0;
				UserRatings user_ratings = this.userRatings.get(j);
				System.out.println("iteration : " + (i) + " time userID : " + j);
				for(Rating rating : user_ratings.userRatings){
					double predictScore = predict(rating.getUserID(),rating.getMovieID(),rating.getTimeStamp());
					deviation = rating.getScore() - predictScore;
					
					updateBU(rating.getUserID(),deviation);
					updateBU_TL(rating.getUserID() , rating.getTimeStamp(), deviation);
					updateBU_T(rating.getUserID() , rating.getTimeStamp(), deviation);
					updateBI(rating.getMovieID() ,  deviation);
					updateBI_BIN(rating.getMovieID() , rating.getTimeStamp() ,  deviation);
				}
				
			}
		}
		double loss = sumLoss();
		System.out.println("loss_ " + loss);
		isDescented = true;
	}
	
	public void ProcessToFile(String targetFile , String resultFile) throws IOException{
		if(!isDescented){
			StochasticGradientDescent();
		}
		FileReader reader = new FileReader(targetFile);
		BufferedReader br = new BufferedReader(reader);
		FileWriter writer = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(writer);
		String str = null;
		
		while((str = br.readLine()) != null){
			String[] tmp = str.split("::");
			if(tmp !=null && tmp.length>3){
				Integer userID = Integer.parseInt(tmp[0]);
				Integer movieID = Integer.parseInt(tmp[1]);
				Long time = Long.parseLong(tmp[3]);
				try{
					double predictScore = predict(userID , movieID, time);
					bw.write(userID + "::" + movieID + "::" + predictScore + "\n");
				}
				catch(Exception e){
					System.out.println(userID + "::" + movieID + "::" + time);
					e.printStackTrace();
					bw.write("\n");
				}
			}
		}
		
		br.close();
		bw.close();
	}
	
	public static void main(String[] args){
		File file = new File("data/timeModel/predict.dat");
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		try {
			TBLGradientDescent tnlp = new TBLGradientDescent("data/training.dat");
			
			tnlp.ProcessToFile("data/test.dat", "data/timeModel/predict.dat");
			RMSE.storeToFile("data/test.dat", "data/timeModel/predict.dat", "data/timeModel/rmse.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
