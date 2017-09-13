package com.detection.textMatch;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestProjectVectors;
import com.data.TestReport;
import com.dataProcess.TestProjectReader;
import com.detection.wordEmbedding.DuplicateVecDetection;


public class TextMatchDetection {
	TDIDFTool tfIdfTool;
	
	public TextMatchDetection( TestProject project ) {
		// TODO Auto-generated constructor stub
		tfIdfTool = new TDIDFTool ( project );
	}
	
	/*
	 * report是一个测试报告，
	 * project是一个项目中的所有测试报告，
	 * 该类的作用是在project的所有测试报告中，查找和report相似的缺陷报告
	 */
	public ArrayList<Integer> detectDuplicateReportsBasedTextMatch ( TestProject project, int index ){
		ArrayList<Integer> duplicateReports = new ArrayList<Integer>();
		
		HashMap<Integer, Double> simList = new HashMap<Integer, Double>();
		
		TestReport report = project.getTestReportsInProj().get( index );
		HashMap<String, Double> tfIdfReport = tfIdfTool.countTFIDF(report);
		
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
			if ( i == index )
				continue;
			
			TestReport curReport = project.getTestReportsInProj().get( i);
			
			HashMap<String, Double> curTfIdfReport = tfIdfTool.countTFIDF(curReport);
			
			double similarity = this.computeSimilarity(tfIdfReport, curTfIdfReport);
			simList.put( i, similarity );
			System.out.println( i + " : " + similarity );
		}
		
		List<HashMap.Entry<Integer, Double>> newSimList = new ArrayList<HashMap.Entry<Integer, Double>>(simList.entrySet());

		Collections.sort( newSimList, new Comparator<HashMap.Entry<Integer, Double>>() {   
			public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {      
			        //return (o2.getValue() - o1.getValue()); 
			        return o2.getValue().compareTo(o1.getValue() ) ;
			    }
			}); 
		
		for ( int i =0; i < newSimList.size() ; i++ ){
			System.out.println( "top " + i + " report is " + newSimList.get(i).getKey() + " the similarity is "  + newSimList.get( i).getValue().toString()  );
			duplicateReports.add( newSimList.get(i).getKey() );
		}
		return duplicateReports;
	}
	
	public Double computeSimilarity ( HashMap<String, Double> tfIdfReport, HashMap<String, Double> curTfIdfReport){
		Set<String> words = new HashSet<String>();
		words.addAll ( tfIdfReport.keySet() );
		words.addAll( curTfIdfReport.keySet() );
		
		double sum1 = 0.0, sum2 = 0.0, sum3 = 0.0;
		
		for ( String word : words ){
			double temp1 = 0.0, temp2 = 0.0;
			if ( tfIdfReport.containsKey( word )){
				temp1 = tfIdfReport.get( word );
			}
			if ( curTfIdfReport.containsKey( word ) ){
				temp2 = curTfIdfReport.get( word );
			}
			sum1 += temp1*temp2;
			sum2 += temp1*temp1;
			sum3 += temp2*temp2;
		}
		
		double temp = sum2*sum3;
		double similarity = 0.0;
		if ( temp != 0.0){
			temp = Math.sqrt(temp);
			similarity = sum1 / temp;
		}		
		
		return similarity;
	}
	
	public void detectDuplicateReportForProject ( TestProject project ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/performance/duplicate.txt" )) , "GB2312"), 1024);
			for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
				ArrayList<Integer> duplicateReports = this.detectDuplicateReportsBasedTextMatch(project, i);
				
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
		TestProjectReader reportReader = new TestProjectReader();
		TestProject project = reportReader.loadTestProject( "data/重复报告标注/百度手机卫士功能体验_1463737109.csv");
		
		TextMatchDetection detection = new TextMatchDetection( project);
		detection.detectDuplicateReportForProject(project);
	}
}
