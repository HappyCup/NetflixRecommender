package org.baseline;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tools.RMSE;


public class BaseLine {

	private class Bind{
		public double sumNumber = 0.0;
		public double sumScore = 0.0;
		public double getAvg(){
			return sumScore/sumNumber;
		}
	}
	private Bind allU = new Bind();
	private Map<Integer,Bind> userface;
	private Map<Integer,Bind> movieface;
	
	public BaseLine(){
		userface = new HashMap<Integer,Bind>();
		movieface = new HashMap<Integer,Bind>();
	}
	
	public void setInputFile(String ratingfile) throws IOException{
		FileReader reader = new FileReader(ratingfile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		
		while((str = br.readLine()) != null){
			Rating rating = new Rating(str);
			Integer userID = rating.getUserID();
			Integer movieID = rating.getMovieID();
			double score = rating.getScore();
			this.allU.sumNumber++;
			this.allU.sumScore += score;
			
			if(!userface.containsKey(userID)){
				this.userface.put(userID, new Bind());
			}
			Bind userBind = this.userface.get(userID);
			userBind.sumNumber++;
			userBind.sumScore += score;
			
			if(!movieface.containsKey(movieID)){
				this.movieface.put(movieID, new Bind());
			}
			Bind movieBind = this.movieface.get(movieID);
			movieBind.sumNumber++;
			movieBind.sumScore += score;
		}
		
		br.close();
	}
	
	public double getOverAverage(){
		return this.allU.getAvg();
	}
	
	public void outputBaseLine(String ufile, String bufile, String bifile) throws IOException{
		FileWriter reader = new FileWriter(ufile);
		BufferedWriter ubr = new BufferedWriter(reader);
		FileWriter reader2 = new FileWriter(bufile);
		BufferedWriter bubr = new BufferedWriter(reader2);
		FileWriter reader3 = new FileWriter(bifile);
		BufferedWriter bibr = new BufferedWriter(reader3);
		
		ubr.write(Double.toString(allU.getAvg()));
		
		Set<Integer> userIDSet = userface.keySet();
        List<Integer> userIDList = new ArrayList<Integer>(userIDSet);
        Collections.sort(userIDList);
        for(Integer userID : userIDList){
        	Bind userBind = this.userface.get(userID);
        	double userDeviation = userBind.getAvg() - allU.getAvg();
        	bubr.write(userID + "::" + userDeviation + "\n");
        }
        
        Set<Integer> movieIDSet = movieface.keySet();
        List<Integer> movieIDList = new ArrayList<Integer>(movieIDSet);
        Collections.sort(movieIDList);
        for(Integer movieID : movieIDList){
        	Bind movieBind = this.movieface.get(movieID);
        	double movieDeviation = movieBind.getAvg() - allU.getAvg();
        	bibr.write(movieID + "::" + movieDeviation + "\n");
        }
        
        ubr.close();
        bubr.close();
        bibr.close();
	}
	
	public double predict(int userID , int movieID){
		double predictScore = 0.0d - allU.getAvg();
		if(userface.containsKey(userID)){
			predictScore += userface.get(userID).getAvg();
		}
		if(movieface.containsKey(movieID)){
			predictScore += movieface.get(movieID).getAvg();
		}
		return predictScore;
	}
	
	public void outputPredict(String predictTargetFile , String outputFile) throws IOException{
		FileReader reader = new FileReader(predictTargetFile);
		BufferedReader br = new BufferedReader(reader);
		FileWriter writer = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(writer);
		String str = null;
		
		while((str = br.readLine()) != null){
			String[] tmp = str.split("::");
			if(tmp !=null && tmp.length>1){
				Integer userID = Integer.parseInt(tmp[0]);
				Integer movieID = Integer.parseInt(tmp[1]);
				
				double predictScore = predict(userID , movieID);
				bw.write(userID + "::" + movieID + "::" + predictScore + "\n");
			}
		}
		
		br.close();
		bw.close();
	}
	
	public static void main(String[] args){
		BaseLine baseline = new BaseLine();
		File file = new File("data/baseline/predict.dat");
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		try {
			baseline.setInputFile("data/training.dat");
			
			baseline.outputPredict("data/test.dat", "data/baseline/predict.dat");
			RMSE.storeToFile("data/test.dat", "data/baseline/predict.dat", "data/baseline/rmse.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
