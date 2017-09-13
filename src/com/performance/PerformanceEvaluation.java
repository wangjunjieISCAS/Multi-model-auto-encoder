package com.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import com.csvreader.CsvReader;
import com.data.Constants;

public class PerformanceEvaluation {

	public PerformanceEvaluation() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * Ŀǰ���ڲ��Ա����ظ��ı���ǣ�����һ���ظ��Ĳ��Ա��棬�õ�ͬһ����ǣ����Ƕ���ĳ�����Ա�����ظ����棬Ŀǰ��û��ͳ��
	 * ���ｫԭʼ�ı�ע�����ת��ΪX: duplicate list����ʽ
	 */
	public void obtainGroundTruth ( String fileName ){
		ArrayList<String> tagList = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			
			CsvReader reader = new CsvReader( br, ',');
			
			// ������ͷ   �����Ҫ��ͷ�Ļ�����Ҫд��䡣  
			reader.readHeaders(); 
			//���ж������ͷ������      
			int index = 0;
	        while ( reader.readRecord() ){
	        	String[] temp = reader.getValues();
	        	
	        	String tag = "";
	        	if ( temp.length > Constants.FIELD_INDEX_GROUNG_TRUTH )
	        		tag = temp[Constants.FIELD_INDEX_GROUNG_TRUTH];
	        	
	        	//System.out.println ( "tag: "  + tag );
	        	tagList.add( tag.trim());
	        }	
	        
	        System.out.println( tagList.size() );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/performance/groundTruth.txt" )) , "GB2312"), 1024);
			for ( int i =0; i < tagList.size(); i++ ){
				String tag = tagList.get( i );
				output.write( i + ":");
				if ( tag.equals( "")){
					output.newLine();
					continue;
				}
				
				for ( int j =0; j < tagList.size(); j++){
					if ( i ==j )
						continue;
					
					if ( tag.equals( tagList.get(j))){
						output.write( j + " ");
					}					
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
	
	
	/*
	 * ����ground truth �� predict result���ж�ȡ�����ߴ洢��һ���ĸ�ʽ
	 */
	public HashMap<Integer, ArrayList<Integer>> readResults ( String fileName ){
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( fileName )));
			
			String line = "";
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( ":");
				
				int index = Integer.parseInt( temp[0].trim() );
				ArrayList<Integer> duplicates = new ArrayList<Integer>();
				
				if ( temp.length > 1 && temp[1].trim().length() > 0 ){
					String[] values = temp[1].split( " ");
					
					for ( int i =0; i < values.length; i++ ){
						int dup = Integer.parseInt( values[i].trim() );
						
						duplicates.add( dup );
					}
				}
				result.put( index, duplicates );		
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return result;
	}
	
	public void computePerformance ( String trueLabelFile, String predictFile ){
		HashMap<Integer, ArrayList<Integer>> trueLabel = this.readResults( trueLabelFile );
		HashMap<Integer, ArrayList<Integer>> predictLabel = this.readResults( predictFile );
		
		ArrayList<Double> recallFiveList = new ArrayList<Double>();
		ArrayList<Double> recallTenList = new ArrayList<Double>();
		ArrayList<Double> recallNumFiveList = new ArrayList<Double>();
		ArrayList<Double> recallNumTenList = new ArrayList<Double>();
		
		ArrayList<Double> averagePrecisionList = new ArrayList<Double>();
		ArrayList<Double> firstIndexList = new ArrayList<Double>();
		
		//����trueLabel�У�û��duplicate��reports�������к�����
		int validNum = 0;
		for ( Integer index: predictLabel.keySet() ){
			ArrayList<Integer> predicts = predictLabel.get( index );
			ArrayList<Integer> trues = trueLabel.get( index );
			
			if ( trues.size() == 0 )
				continue;
			
			double recall = 0.0;
			for ( int i =0; i < predicts.size() && i < 5; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recall = 1.0;
					break;
				}
			}
			recallFiveList.add( recall );
			
			recall = 0.0;
			for ( int i =0; i < predicts.size() && i < 10; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recall = 1.0;
					break;
				}
			}
			recallTenList.add( recall );
			
			double recallNum = 0.0;
			for ( int i =0; i < predicts.size() && i < 5; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallNum++;
				}
			}
			recallNumFiveList.add( recallNum/5 );
			
			recallNum = 0.0;
			for ( int i =0; i < predicts.size() && i < 10; i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					recallNum ++;
				}
			}
			recallNumTenList.add( recallNum/10 );
			
			double precisionTotal = 0.0;
			for ( int i =0; i < trues.size(); i++ ){	
			//for ( int i =0; i < trues.size() && i < 10; i++ ){	
				//top-I �� precision
				int count = 0;
				for ( int j =0; j < i ; j++ ){
					int dup = predicts.get(j);
					if ( trues.contains( dup )){
						count++;
					}
				}
				precisionTotal += (1.0*count) / (1.0*(i+1) );
			}
			
			precisionTotal = precisionTotal / trues.size();
			averagePrecisionList.add( precisionTotal );
			
			//�ͷ�
			int firstIndex = predicts.size()*2;
			for ( int i =0; i < predicts.size(); i++ ){
				int dup = predicts.get(i);
				if ( trues.contains( dup )){
					firstIndex = i+1;
					break;
				}
			}
			firstIndexList.add( (1.0 / firstIndex) );
			
			validNum ++;
		}	
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		double recallFiveTotal = 0.0, recallTenTotal = 0.0, recallNumFiveTotal=0.0, recallNumTenTotal = 0.0, MAPTotal = 0.0, MRRTotal = 0.0;
		
		if ( recallFiveList.size() != recallTenList.size() || recallFiveList.size() != averagePrecisionList.size() || recallFiveList.size() != firstIndexList.size() ){
			System.out.println ("Wrong list size!"); 
		}
		
		for ( int i=0; i < recallFiveList.size(); i++ ){
			recallFiveTotal += recallFiveList.get(i);
			recallTenTotal += recallTenList.get(i);
			
			recallNumFiveTotal += recallNumFiveList.get(i);
			recallNumTenTotal += recallNumTenList.get(i);
			
			MAPTotal += averagePrecisionList.get(i);
			
			MRRTotal += firstIndexList.get(i);
		}
		
		result.put( "recall-5", recallFiveTotal / validNum );
		result.put( "recall-10", recallTenTotal / validNum );
		result.put( "MAP", MAPTotal / validNum );
		result.put( "MRR", MRRTotal / validNum );
		
		System.out.println ( "recall-5 is " + recallFiveTotal / validNum); 
		System.out.println ( "recall-10 is " + recallTenTotal / validNum );
		//System.out.println ( "recallNum-5 is " + recallNumFiveTotal / validNum); 
		//System.out.println ( "recallNum-10 is " + recallNumTenTotal / validNum );
		
		System.out.println ( "MAP is " + MAPTotal / validNum );
		System.out.println ( "MRR is " + MRRTotal / validNum );
	}
	
	public static void main ( String args[] ){
		PerformanceEvaluation evaluation = new PerformanceEvaluation();
		
		//evaluation.obtainGroundTruth( "data/�ظ������ע/�ٶ��ֻ���ʿ��������_1463737109.csv" );
		evaluation.computePerformance( "data/performance/groundTruth.txt" , "data/performance/duplicate-tf.txt");
		//evaluation.computePerformance( "data/performance/groundTruth.txt" , "data/performance/duplicate-wordvec.txt");
		System.out.println ( );
		evaluation.computePerformance( "data/performance/groundTruth.txt" , "data/performance/duplicate.txt");
	}
}
