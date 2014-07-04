package org.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.neighborhood.IDBind;
import org.neighborhood.ItemBaseNeighborhood;
import org.neighborhood.NeighborhoodList;
import org.neighborhood.SimBind;
import org.tools.RMSE;

public class ItemBaseNeigTest extends ItemBaseNeighborhood{

	public ItemBaseNeigTest(int userNumber, int movieNumber) {
		super(userNumber, movieNumber);
		// TODO Auto-generated constructor stub
		this.baseline.put(new IDBind(2,2), 2.34);
	}

	public void test(){
		System.out.print(this.baseline.get(new IDBind(2,2)));
	}
	
	public void test2(){
		System.out.print(this.baseline.containsKey(new IDBind(2,2)));
	}
	
	public void test3(){
		IDBind idBind = new IDBind(1713,3354);
		for(int index = 0 ; index < this.movieNumber;index++){
			if(this.MovieMetrix[index][idBind.userID-1] > 0 && index!=(idBind.movieID-1)){
				double sim = similarity(index, idBind.movieID-1);
				if(!this.SimSet.containsKey(idBind)){
					NeighborhoodList neig = new NeighborhoodList();
					this.SimSet.put(idBind, neig);
				}
				NeighborhoodList topSimList = this.SimSet.get(idBind);
				topSimList.add(new SimBind(idBind.movieID,index + 1,sim));
			}
		}
		
		Iterator iter = this.SimSet.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			IDBind idBind2 = (IDBind) entry.getKey();
			NeighborhoodList neigs = (NeighborhoodList) entry.getValue();
			Iterator iter2 = neigs.iterator();
			while(iter2.hasNext()){
				SimBind simBind = (SimBind) iter2.next();
				System.out.println(idBind2.userID + "::" + idBind2.movieID + "::" + simBind.j + "::" + simBind.similarity + "\n");
			}
		}
		
		System.out.println(this.predict(idBind.userID, idBind.movieID));
	}
	
	
	public static void main(String[] args) {
		ItemBaseNeigTest it = new ItemBaseNeigTest(6040,3952);
		try {
			it.inputFiles("data/training.dat", "data/test.dat", "data/baseline/predict.dat");
			it.test3();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
