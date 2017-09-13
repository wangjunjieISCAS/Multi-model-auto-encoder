package com.detection.textMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.data.Constants;
import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.WordSegment;

public class TDIDFTool {
	HashMap<String, Double> inverseDocFrequency;
	
	public TDIDFTool( TestProject project ) {
		// TODO Auto-generated constructor stub
		InverseDocFrequency inDocFrequency = new InverseDocFrequency();
		
		HashMap<String, Double> docFrequency = inDocFrequency.countDocumentFrequencyProject( project );
		inverseDocFrequency = inDocFrequency.getInverseDocumentFrequency(docFrequency, project.getTestReportsInProj().size() );
	}
	
	public HashMap<String, Double> countTFIDF ( TestReport report ){
		HashMap<String, Double> tfIdf = new HashMap<String, Double>();
		
		String[] words = WordSegment.segmentTestReport(report);
		List<String> wordsList = Arrays.asList( words );
		
		for ( int i = 0; i< wordsList.size(); i++ ){
			String word = wordsList.get(i);
			Integer tf = Collections.frequency( wordsList, word );
			
			Double idf = inverseDocFrequency.get( word );
			//Double tfIdfValue = tf * idf;
			Double tfIdfValue = tf * 1.0;
			tfIdf.put( word, tfIdfValue);
		}
		
		return tfIdf;
	}
}
