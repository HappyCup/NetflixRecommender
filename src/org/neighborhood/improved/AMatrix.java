package org.neighborhood.improved;

import Jama.Matrix;

public class AMatrix {

	private int SIZE ;
	private int[] order;
	private double[][] matrix;
	private double average = 0 ;
	
	public AMatrix(int[] order){
		this.order = order.clone();
		this.SIZE = order.length;
		this.matrix = new double[SIZE][SIZE];
	}
	
	protected int locate(int j){
		for(int i : order){
			if(i == j)
				return i;
		}
		return SIZE;
	}
	
	public void insertElement(int j, int k, double value){
		int x = locate(j);
		int y = locate(k);
		matrix[x][y] = value;
	}
	
	public void insertElementByIndex(int i, int j, double value){
		matrix[i][j] = value;
	}
	public int itemOfIndex(int i){
		return order[i];
	}
	
	public double valueOfIndex(int i, int j){
		return this.matrix[i][j];
	}
	
	public Matrix getMatrix(){
		return new Matrix(matrix);
	}
	
	public double[][] getValues(){
		return this.matrix;
	}
	public int[] getOrder(){
		return order;
	}
	
	public double getAverage(){
		if(average != 0){
			return average;
		}
		double sum = 0;
		for(int i = 0;i < SIZE;i++){
			for(int j = 0;j < SIZE;j++){
				sum += matrix[i][j];
			}
		}
		average = sum/(SIZE*SIZE);
		return average;
	}
}
