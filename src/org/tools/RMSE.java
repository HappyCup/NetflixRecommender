package org.tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class RMSE {

	public static double computeFromFile(String exceptedfile, String actualfile) throws IOException{
		double sumNumber = 0.0d;
		double squareAdd = 0.0d;
		FileReader reader1 = new FileReader(exceptedfile);
		FileReader reader2 = new FileReader(actualfile);
		BufferedReader br1 = new BufferedReader(reader1);
		BufferedReader br2 = new BufferedReader(reader2);
		
		String str1 = null;
		String str2 = null;
		while((str1 = br1.readLine())!=null && (str2 = br2.readLine())!=null){
			String[] tmp1 = str1.split("::");
			String[] tmp2 = str2.split("::");
			
			if(!tmp1[0].equals(tmp2[0]) || !tmp1[1].equals(tmp2[1])){
				System.out.println("a error: "+ tmp1[0] + " " + tmp1[1] + "," + tmp2[0] + " " + tmp2[1]);
				continue;
			}
			sumNumber++;
			double exceptedScore = Double.parseDouble(tmp1[2]);
			double actualScore = Double.parseDouble(tmp2[2]);
			
			squareAdd += (exceptedScore - actualScore)*(exceptedScore - actualScore);
		}
		br1.close();
		br2.close();
		return Math.sqrt(squareAdd/sumNumber);
	}
	
	public static void storeToFile(String exceptedfile, String actualfile, String resultFile) throws IOException{
		FileWriter writer = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(writer);
		
		double result = computeFromFile(exceptedfile , actualfile);
		bw.write(result+"");
		
		bw.close();
	}
}
