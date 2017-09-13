package com.detection.textMatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.data.TestProject;
import com.data.TestReport;
import com.dataProcess.WordSegment;



public class InverseDocFrequency {

	public InverseDocFrequency() {
		// TODO Auto-generated constructor stub
	}
	
	public HashMap countDocumentFrequencyProject ( TestProject project ){
		HashMap documentFrequency = new HashMap<String, Double>();
		
		ArrayList<String []> reportWord = new ArrayList<String []>();
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
			TestReport report = project.getTestReportsInProj().get( i );
			
			String[] words = WordSegment.segmentTestReport(report);
			reportWord.add( words );
		}
		
		for ( int i =0; i < reportWord.size(); i++ ){
			for ( int j =0; j < reportWord.get(i).length; j++ ){
				String word = reportWord.get(i)[j];
				if ( documentFrequency.containsKey( word ))
					continue;
				
				Integer frequency = this.getDocumentFrequency( reportWord, word );
				documentFrequency.put( word ,  frequency*1.0 );	
			}
		}
		return documentFrequency;
	}
	
	//遍历所有的feedbacksWord，得到word在几个文档里出现过
	public Integer getDocumentFrequency (ArrayList<String []> feedbacksWord, String word ){
		Integer documentFrequency = 0;
		for ( int i = 0; i< feedbacksWord.size(); i++ ){
			for ( int j = 0; j< feedbacksWord.get(i).length; j++ ){
				if ( feedbacksWord.get(i)[j].contains( word )){
					documentFrequency ++;
					break;
				}
					
			}
		}
		return documentFrequency;
	}
	
	//基于输入的documentFrequency和documentNum，计算Log(documentNum/documentFrequency)
	public HashMap getInverseDocumentFrequency( HashMap<String, Double> documentFrequency, Integer documentNum ){
		HashMap ivDocumentFrequency = new HashMap<String, Double>();
		
		Iterator iter = documentFrequency.entrySet().iterator();
		while ( iter.hasNext() ){
			Map.Entry<String, Double> entry = (Entry<String, Double>) iter.next();
			String word = entry.getKey();
			Double frequency = entry.getValue();
			
			frequency = (1.0* documentNum ) / (1.0*frequency);
			frequency = Math.log( frequency );
			
			ivDocumentFrequency.put( word , frequency );
		}		
		
		return ivDocumentFrequency;
	}
}
