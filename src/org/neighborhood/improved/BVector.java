package org.neighborhood.improved;

import Jama.Matrix;

public class BVector {

	private int SIZE ;
	private int[] order;
	private double[] array;
	
	public BVector(int[] order){
		this.order = order.clone();
		this.SIZE = order.length;
		array = new double[SIZE];
	}
	
	public BVector(int[] order, double[] values){
		this.order = order.clone();
		this.SIZE = order.length;
		this.array = values;
	}
	public void insertElementByIndex(int i, double value){
		this.array[i] = value;
	}
	
	public double getByIndex(int i){
		return array[i];
	}
	public Matrix getMatrix(){
		return new Matrix(array,array.length);
	}
	
	public double[] getValues(){
		return this.array;
	}
	public int[] getOrder(){
		return order;
	}
	
	public double getSumValue(){
		double result = 0;
		for(double a : this.array){
			result += Math.abs(a);
		}
		return result;
	}
}
