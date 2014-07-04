package org.neighborhood.improved;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.neighborhood.IDBind;
import org.neighborhood.ItemBaseNeighborhood;
import org.neighborhood.NeighborhoodList;
import org.tools.RMSE;

import Jama.Matrix;

public class ImprovedNeig extends ItemBaseNeighborhood{

	protected final double shrinkvalue = 500;
	
	public ImprovedNeig(int userNumber, int movieNumber) {
		super(userNumber, movieNumber);
	}
	
	/*
	 * j,k start from 1
	 */
	protected double Iweight(int j , int k){
		double mutliSum = 0.0d;
		for(int index = 0; index < this.userNumber;index++){
			mutliSum += this.MovieMetrix[j-1][index] * this.MovieMetrix[k-1][index];
		}
		
		double numSim = this.NumSame(j - 1,k - 1);
		
		if(numSim == 0)
			return 0;
		else 
			return mutliSum/numSim;
	}
	
	protected AMatrix ConstructMatrixA(IDBind idBind){
		double average = this.BaseLineEstimator.getOverAverage();
		NeighborhoodList simlist = this.SimSet.get(idBind);
		int[] order = simlist.getOrder();
		AMatrix aMatrix = new AMatrix(order);
		for(int j = 0 ;j < order.length;j++){
			for(int k = j ;k < order.length;k++){
				double weight = Iweight(order[j],order[k]);
				double numSim = this.NumSame(order[j] - 1,order[k] - 1);
				double newWeight = (numSim * weight + shrinkvalue * average)/(numSim + shrinkvalue);
				aMatrix.insertElementByIndex(j, k, newWeight);
				if(k != j){
					aMatrix.insertElementByIndex(k, j, newWeight);
				}
			}
		}
		
		return aMatrix;
	}
	
	protected BVector ConstructVectorB(IDBind idBind){
		double avg = this.BaseLineEstimator.getOverAverage();
		NeighborhoodList simlist = this.SimSet.get(idBind);
		int[] order = simlist.getOrder();
		BVector bv = new BVector(order);
		for(int j = 0; j < order.length; j++){
			double weight = Iweight(order[j] , idBind.movieID);
			double numSim = this.NumSame(order[j] - 1, idBind.movieID - 1);
			double newWeight = (numSim * weight + shrinkvalue * avg)/(numSim + shrinkvalue);
			bv.insertElementByIndex(j, newWeight);
		}
		
		return bv;
	}
	
	protected BVector ComputeVectorW(IDBind idBind){
		AMatrix A = ConstructMatrixA(idBind);
		BVector b = ConstructVectorB(idBind);
		
		Matrix W = A.getMatrix().solve(b.getMatrix());
		
//		A.getMatrix().print(20, 2);
//		W.print(20, 4);
//		b.getMatrix().print(20, 2);
		
		double[] values = W.getRowPackedCopy();
		BVector wVector = new BVector(A.getOrder(),values);
		return wVector;
	}
	
	public double predict(IDBind idBind){
		double baselineRat = this.baseline.get(idBind);
		BVector W = ComputeVectorW(idBind);
		int[] order = W.getOrder();
		
		double devation = 0;
		for(int j = 0 ;j < order.length; j++){
			double orginRating = getRatingOrgin(order[j],idBind.userID);
			double baselineRating = this.BaseLineEstimator.predict(idBind.userID, order[j]);
			devation += W.getByIndex(j)*(orginRating - baselineRating);
		}
		
		return baselineRat + devation/W.getSumValue();
	}
	
	public void ProcessAndStoreAsFile(String resultFile) throws IOException{
		FileWriter writer = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(writer);
		
		for(IDBind idBind : this.target){
			double predictResult = predict(idBind);
			bw.write(idBind.userID + "::" + idBind.movieID + "::" + predictResult + "\n");
		}
		
		bw.close();
	}
	
	public static void main(String[] args){
		File file = new File("data/neighborhood/step2.dat");
		if(!file.exists()){
			file.getParentFile().mkdirs();
		}
		try {
			ImprovedNeig neig = new ImprovedNeig(6040,3952);
			
			neig.inputFiles("data/training.dat", "data/test.dat", "data/baseline/predict.dat");
			neig.loadSimSet("data/neighborhood/simset.dat");
			
			neig.ProcessAndStoreAsFile("data/neighborhood/step2.dat");
			RMSE.storeToFile("data/test.dat", "data/neighborhood/step2.dat", "data/neighborhood/rmse2.dat");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
