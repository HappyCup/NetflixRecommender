package org.tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.baseline.Rating;


public class SpliteRatings {

	private Map<Integer,List<Rating>> data;
	
	public SpliteRatings(){
		data = new HashMap<Integer,List<Rating>>();
	}
	public void setInputFile(File ratingsFile) throws IOException{
		FileReader reader = new FileReader(ratingsFile);
		BufferedReader br = new BufferedReader(reader);
		String str = null;
        
        while((str = br.readLine()) != null) {
        	Rating rating = new Rating(str);
        	Integer userID = rating.getUserID();
        	if(!data.containsKey(userID)){
        		List<Rating> list = new LinkedList<Rating>();
        		data.put(userID, list);
        	}
        	List<Rating> ratings = data.get(userID);
        	ratings.add(rating);
        }
        br.close();
	}
	
	public void outputResult(String trainFile, String testFile) throws IOException{
		sortData();
		FileWriter writer = new FileWriter(trainFile);
        BufferedWriter bw = new BufferedWriter(writer);
        FileWriter writer2 = new FileWriter(testFile);
        BufferedWriter bw2 = new BufferedWriter(writer2);
        
        Set<Integer> userIDSet = data.keySet();
        List<Integer> userIDList = new ArrayList<Integer>(userIDSet);
        Collections.sort(userIDList);
        
        for(Integer userID : userIDList){
        	List<Rating> list = data.get(userID);
        	int gap = (int) (list.size()*0.9);
        	for(int i=0;i<gap;i++){
        		bw.write(list.get(i)+"\n");
        	}
        	for(int i=gap;i<list.size();i++){
        		bw2.write(list.get(i)+"\n");
        	}
        }
        
        bw.close();
        bw2.close();
	}
	
	private void sortData(){
		for(List<Rating> list : data.values()){
			Collections.sort(list);
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File ratings = new File("data/ratings.dat");
		SpliteRatings sp = new SpliteRatings();
		try {
			sp.setInputFile(ratings);
			sp.outputResult("data/training.dat", "data/test.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
