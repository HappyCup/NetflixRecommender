package org.neighborhood;

import java.util.Iterator;

public class NeighborhoodList implements  Iterable<SimBind>{

	private final int Size;
	private SimBind[] simArray;
	private int sum = 0;
	
	public NeighborhoodList(){
		this.Size = 20;
		simArray = new SimBind[Size];
	}
	
	public NeighborhoodList(int size){
		this.Size = size;
		simArray = new SimBind[Size];
	}
	
	public void add(SimBind simBind){
		for(int i =0;i < sum;i++){
			if(simArray[i].similarity < simBind.similarity){
				insert(i,simBind);
				if(this.sum < this.Size)
					this.sum++;
				return;
			}
		}
		
		if(this.sum < this.Size){
			this.simArray[sum++] = simBind;
		}
		
	}
	
	private void insert(int index , SimBind simBind){
		if(sum < this.Size){
			for(int j = sum;j > index;j--){
				this.simArray[j] = this.simArray[j-1];
			}
		}
		else{
			for(int j = this.Size - 1;j > index;j--){
				this.simArray[j] = this.simArray[j-1];
			}
		}
		this.simArray[index] = simBind;
	}
	
	public double sumSim(){
		double sumSimilarity = 0.0d;
		for(int i = 0 ; i < this.sum ; i++){
			sumSimilarity += this.simArray[i].similarity;
		}
		return sumSimilarity;
	}

	/*
	 * @return attention the size is likely not 20
	 */
	public int[] getOrder(){
		int[] order = new int[sum];
		for(int i = 0;i < sum;i++){
			order[i] = this.simArray[i].j;
		}
		return order;
	}
	
	@Override
	public Iterator<SimBind> iterator() {
		// TODO Auto-generated method stub
		return new MyIterator();
	}
	
	class MyIterator implements Iterator<SimBind>{  
		  
        /**相当于索引*/  
        private int index =0;  
        
        @Override  
        public boolean hasNext() {  
            return index<sum;  
        }
  
        @Override  
        public SimBind next() {  
            return simArray[index++];  
        }

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}  
    }
}
