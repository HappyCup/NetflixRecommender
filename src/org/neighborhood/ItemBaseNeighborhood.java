package org.neighborhood;
import org.baseline.BaseLine;
import org.tools.RMSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ItemBaseNeighborhood {

	protected final int userNumber;
	protected final int movieNumber; 
	protected double[][] MovieMetrix;
	protected List<IDBind> target;
	protected Map<IDBind,Double> baseline;
	protected Map<IDBind,NeighborhoodList> SimSet;
	protected BaseLine BaseLineEstimator;
	
	public ItemBaseNeighborhood(int userNumber, int movieNumber){
		this.userNumber = userNumber;
		this.movieNumber = movieNumber;
		this.MovieMetrix = new double[movieNumber][userNumber];
		this.baseline = new HashMap<IDBind,Double>();
		this.target = new ArrayList<IDBind>();
		this.SimSet = new HashMap<IDBind,NeighborhoodList>();
		this.BaseLineEstimator = new BaseLine();
	}
	
	protected void inputTrainingData(String trainingfile) throws IOException{
		BaseLineEstimator.setInputFile(trainingfile);
		
		FileReader reader = new FileReader(trainingfile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		
		while((str = br.readLine())!=null){
			String[] tmp = str.split("::");
			if(tmp.length<3) continue;
			int userID = Integer.parseInt(tmp[0]);
			int movieID = Integer.parseInt(tmp[1]);
			double score = Double.parseDouble(tmp[2]);
			this.MovieMetrix[movieID-1][userID-1] = score;
		}
		
		br.close();
	}
	
	protected void inputTargetFile(String targetFile) throws IOException{
		FileReader reader = new FileReader(targetFile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		
		while((str = br.readLine())!=null){
			IDBind bind = new IDBind(str);
			this.target.add(bind);
		}
		
		br.close();
	}
	
	protected void inputBaseLineFile(String baselineFile) throws IOException{
		FileReader reader = new FileReader(baselineFile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		
		while((str = br.readLine())!=null){
			IDBind bind = new IDBind(str);
			String[] tmp = str.split("::");
			if(tmp.length>2){
				double baselineScore = Double.parseDouble(tmp[2]);
				this.baseline.put(bind, baselineScore);
			}
		}
		
		br.close();
	}
	
	public void inputFiles(String trainingfile, String targetFile, String baselineFile) throws IOException{
		inputTrainingData(trainingfile);
		inputTargetFile(targetFile);
		inputBaseLineFile(baselineFile);
	}
	
	/*
	 * movieID, userID start from 1
	 */
	protected double getRatingOrgin(int movieID , int userID){
		return this.MovieMetrix[movieID-1][userID-1];
	}
	
	private void takeAvg(double[] movieVector){
		double numRated = 0.0d;
		double sumScore = 0.0d;
		for(int i = 0;i < movieVector.length; i++){
			if(movieVector[i] > 0){
				sumScore += movieVector[i];
				numRated++;
			}
		}
		double avg = sumScore/numRated;
		for(int i = 0;i < movieVector.length; i++){
			if(movieVector[i] > 0){
				movieVector[i] = movieVector[i] - avg;
			}
		}
	}
	private double lengthVector(double[] vector){
		double squareSum = 0.0d;
		for(int i = 0;i < vector.length; i++){
			squareSum += vector[i]*vector[i];
		}
		return Math.sqrt(squareSum);
	}
	private double person(int i ,int j){
		double[] movieI = this.MovieMetrix[i].clone();
		double[] movieJ = this.MovieMetrix[j].clone();
		takeAvg(movieI);
		takeAvg(movieJ);
		double lengthI = lengthVector(movieI);
		double lengthJ = lengthVector(movieJ);
		
		double mutliSum = 0.0d;
		for(int index = 0; index < movieI.length;index++){
			mutliSum += movieI[index] * movieJ[index];
		}
		
		//when the average equals to every ratings.
		if(lengthI == 0 || lengthJ == 0){
			return 0;
		}
		else{
			return mutliSum/(lengthI*lengthJ);
		}
	}
	
	/*
	 * i,j start from 0
	 */
	protected double NumSame(int i ,int j){
		double num = 0.0d;
		for(int index = 0;index < this.MovieMetrix[i].length;index++){
			if(this.MovieMetrix[i][index] > 0 && this.MovieMetrix[j][index] > 0){
				num++;
			}
		}
		return num;
	}
	/*
	 *  i,j start from 0
	 */
	protected double similarity(int i, int j){
		double P2 = 100.0d;
		double personIJ = person(i, j);
		double numIJ = NumSame(i, j);
		return (numIJ*personIJ)/(numIJ + P2);
	}
	
	//The top-k items rated by u,which are most similar to i.
	private void computeSimSet(){
		for(IDBind idBind : this.target){
			System.out.println("computing simliarity : " + idBind.userID + " " + idBind.movieID);
			for(int index = 0 ; index < this.movieNumber;index++){
				if(this.MovieMetrix[index][idBind.userID - 1] > 0 && index != (idBind.movieID - 1)){
					double sim = similarity(index, idBind.movieID - 1);
					if(!this.SimSet.containsKey(idBind)){
						NeighborhoodList neig = new NeighborhoodList();
						this.SimSet.put(idBind, neig);
					}
					NeighborhoodList topSimList = this.SimSet.get(idBind);
					topSimList.add(new SimBind(idBind.movieID,index + 1,sim));
				}
			}
		}
	}
	
	public void storeSimSet(String simSetFile) throws IOException{
		FileWriter writer = new FileWriter(simSetFile);
		BufferedWriter bw = new BufferedWriter(writer);
		
		Iterator iter = this.SimSet.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			IDBind idBind = (IDBind) entry.getKey();
			NeighborhoodList neigs = (NeighborhoodList) entry.getValue();
			Iterator iter2 = neigs.iterator();
			while(iter2.hasNext()){
				SimBind simBind = (SimBind) iter2.next();
				bw.write(idBind.userID + "::" + idBind.movieID + "::" + simBind.j + "::" + simBind.similarity + "\n");
			}
		}
		
		bw.close();
	}
	
	public void loadSimSet(String simSetFile) throws IOException{
		this.SimSet.clear();
		FileReader reader = new FileReader(simSetFile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
		while((str = br.readLine())!=null){
			String[] tmp = str.split("::");
			if(tmp.length < 4)continue;
			int userID = Integer.parseInt(tmp[0]);
			int movieID = Integer.parseInt(tmp[1]);
			int movieJ = Integer.parseInt(tmp[2]);
			double similarity = Double.parseDouble(tmp[3]);
			IDBind idBind = new IDBind(userID , movieID);
			if(!this.SimSet.containsKey(idBind)){
				NeighborhoodList neig = new NeighborhoodList();
				this.SimSet.put(idBind, neig);
			}
			NeighborhoodList topSimList = this.SimSet.get(idBind);
			topSimList.add(new SimBind(idBind.movieID,movieJ,similarity));
		}
		
		br.close();
	}
	
	protected double predict(int userID , int movieID){
		IDBind idBind = new IDBind(userID,movieID);
		double baselineRat = this.baseline.get(idBind);
		
		NeighborhoodList neigs = this.SimSet.get(idBind);
		double sumSimilarity = neigs.sumSim();
		
		double sumMulti = 0.0d;
		for(SimBind simbind : neigs){
			double actualScore = getRatingOrgin(simbind.j , userID);
			double baselineScore = this.BaseLineEstimator.predict(userID, simbind.j);
			sumMulti += simbind.similarity*(actualScore - baselineScore);
		}
		
		//when there is no similar movie which means rated by only one person
		if(sumSimilarity == 0){
			return baselineRat;
		}
		else{
			return baselineRat + sumMulti/sumSimilarity;
		}
	}
	
	public void storeResultFile(String resultFile) throws IOException{
		if(this.SimSet.size() == 0)
			computeSimSet();
		
		FileWriter writer = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(writer);
		
		for(IDBind idBind : this.target){
			double predictResult = predict(idBind.userID , idBind.movieID);
			bw.write(idBind.userID + "::" + idBind.movieID + "::" + predictResult + "\n");
		}
		
		bw.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ItemBaseNeighborhood ibn = new ItemBaseNeighborhood(6040,3952);
		boolean ifloadedSimSet = false;
		File simsetfile = new File("data/neighborhood/simset.dat");//相似度文件
		if(!simsetfile.exists()){
			simsetfile.getParentFile().mkdirs();
		}
		else{
			ifloadedSimSet = true;
		}
		try {
			ibn.inputFiles("data/training.dat", "data/test.dat", "data/baseline/predict.dat");
			if(ifloadedSimSet)
				ibn.loadSimSet("data/neighborhood/simset.dat");
			
			ibn.storeResultFile("data/neighborhood/step1.dat");
			if(!ifloadedSimSet)
				ibn.storeSimSet("data/simset.dat");
			RMSE.storeToFile("data/test.dat", "data/neighborhood/step1.dat", "data/neighborhood/rmse1.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
