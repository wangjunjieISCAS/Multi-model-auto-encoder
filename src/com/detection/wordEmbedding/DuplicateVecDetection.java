package com.detection.wordEmbedding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.data.TestProjectVectors;
import com.dataProcess.TestProjectReader;


/*
 * Multi-model auto encoder 得到的编码，也可以用该类进行处理
 */
public class DuplicateVecDetection {

	public DuplicateVecDetection() {
		// TODO Auto-generated constructor stub
	}

	public Double computeSimilarityVectors ( ArrayList<Double> vector1, ArrayList<Double> vector2){
		double sim = 0.0;
		
		double multiply = 0.0;
		double vec1Add = 0.0, vec2Add = 0.0;
		if ( vector1.size() != vector1.size() ){
			System.out.println( "vector1 size is not equal with vector1 size!");
			return null;
		}
		
		for ( int i =0; i < vector1.size(); i++ ){
			double value1 = vector1.get(i);
			double value2 = vector2.get(i);
			
			multiply += value1 * value2;
			
			vec1Add += value1 * value1;
			vec2Add += value2 * value2;
		}
		double addMultiply = vec1Add * vec2Add;
		if ( addMultiply != 0.0 ){
			addMultiply = Math.sqrt( addMultiply );
			sim = multiply / addMultiply;
		}
		return sim;
	}
	
	/*
	 * TestProjectVectors projectVec存储的是项目所有的测试报告，index表示待test的测试报告
	 * 该类的作用是查找和index测试报告重复的测试报告
	 */
	public ArrayList<Integer>  detectDuplicateReportBasedWordEmbedding ( TestProjectVectors projectVec, int index  ){
		ArrayList<Integer> duplicateReports = new ArrayList<Integer>();
		
		HashMap<Integer, Double> simList = new HashMap<Integer, Double>();
		
		ArrayList<Double> testData = projectVec.getReportsVec().get( index );
		
		for ( int i = 0; i < projectVec.getReportsVec().size(); i++  ){
			if ( i == index )
				continue;
			
			ArrayList<Double> report = projectVec.getReportsVec().get( i );
			
			Double sim = this.computeSimilarityVectors( testData, report );
			simList.put( i , sim );
		}
		
		List<HashMap.Entry<Integer, Double>> newSimList = new ArrayList<HashMap.Entry<Integer, Double>>(simList.entrySet());

		Collections.sort( newSimList, new Comparator<HashMap.Entry<Integer, Double>>() {   
			public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		for ( int i =0; i < newSimList.size() ; i++ ){
			System.out.println( "top " + i + " report is " +  newSimList.get(i).getKey().toString() + " the similarity is "  + newSimList.get( i).getValue().toString()  );
			duplicateReports.add( newSimList.get(i).getKey() );
		}
		
		return duplicateReports;
	}
	
	/*
	 * 对整个项目中的所有测试报告进行重复检测
	 */
	public void detectDuplicateReportForProject ( TestProjectVectors projectVec ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/performance/duplicate.txt" )) , "GB2312"), 1024);
			for ( int i =0; i < projectVec.getReportsVec().size(); i++ ){
				ArrayList<Integer> duplicateReports = this.detectDuplicateReportBasedWordEmbedding(projectVec, i );
				
				output.write( i + ":");
				for ( int j =0; j < duplicateReports.size(); j++ ){
					output.write( duplicateReports.get( j ) + " ");
				}
				output.newLine();
			}
			output.flush();
			output.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void main ( String args[] ){
		DuplicateVecDetection detection = new DuplicateVecDetection();
		
		TestProjectReader reportReader = new TestProjectReader();
		TestProjectVectors projectVec = reportReader.loadTestProjectVectors( "data/performance/testTextTotal.txt");
		detection.detectDuplicateReportForProject( projectVec );
	}
}	
