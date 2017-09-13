package com.dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.csvreader.CsvReader;
import com.data.Constants;
import com.data.TestReport;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/*
 * ���������������ѵ��word2vec��ѵ�����ݣ�
 * Ҳ���ǲ��Ա����token����
 */
public class Word2VecTrainData {
	private Integer[] textColumns = {1,2,3,4,5};                           //{2,3} ;                         //{2,3,4};                     
	//{9, 10};      //{7,8}
	
	public Word2VecTrainData() {
		// TODO Auto-generated constructor stub
	}
	
	public void prepareWord2VecTrainData( String folderName){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/train.txt" ), true) , "utf-8"), 1024);
			
			File folder = new File ( folderName );
			String[] fileList = folder.list();
			for ( int i =0; i < fileList.length; i++ ){
				String fileName = folder + "/" + fileList[i];
				
				File projectFolder = new File ( folder + "/" + fileList[i] );
				if ( projectFolder.isDirectory() )
					continue;
				
				System.out.println( fileName );
				BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
				CsvReader reader = new CsvReader( br, ',');
				reader.readHeaders(); 
				//���ж������ͷ������      
		        while ( reader.readRecord() ){
		        	String[] temp = reader.getValues();
		        	
		        	for ( int j =0; j < textColumns.length; j++ ){
		        		String text = temp[textColumns[j]];
		        		text = text.replaceAll( "\r\n", " " );
		        		text = text.replaceAll( "\r", " " );
		        		text = text.replaceAll( "\n", " " );
		        		
 		        		String[] words = WordSegment.segmentWordRestricted( text );
		        		
		        		for ( int k = 0; k < words.length; k++){
		        			output.write( words[k].trim() + " " );
		        			//System.out.print( words[k].trim() + "|");
		        		}	
		        		//System.out.println( );
		        	}
		        }
		        reader.close();
		        br.close();
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
	 * ��Щ���ݴ洢��excel��
	 * �����߼���prepareWord2VecTrainData��ֻ������Ҫ��excel�ж�ȡ����
	 */
	public void prepareWord2VecTrainDataXLS ( String folderName){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( "data/train-2.txt" ), true) , "utf-8"), 1024);
			
			File folder = new File ( folderName );
			String[] fileList = folder.list();
			for ( int i =0; i < fileList.length; i++ ){
				String fileName = folder + "/" + fileList[i];
				
				File projectFolder = new File ( folder + "/" + fileList[i] );
				if ( projectFolder.isDirectory() )
					continue;
				
				System.out.println( fileName );
				
				InputStream inStream = new FileInputStream( fileName );
				Workbook workbook = Workbook.getWorkbook( inStream );
				Sheet sheet = workbook.getSheets()[0];
				
				int rowNum = sheet.getRows();
				int columnNum = sheet.getColumns();
				
				for ( int j =0; j < rowNum; j++ ){		
					for ( int k = 0; k < textColumns.length; k++ ){
						Cell cell = sheet.getCell( textColumns[k], j );
						String text = cell.getContents().trim();
						
						String[] words = WordSegment.segmentWordRestricted( text );
						for ( int p = 0; p < words.length; p++){
		        			output.write( words[p].trim() + " " );
		        			//System.out.print( words[k].trim() + "|");
		        		}	
		        		//System.out.println( );
					}
	        	}
			}
			output.flush();
			output.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//G:\��ְ�������\�ڲ�-�ٶȺ���\����\baidu-crowdsourcing-2016.8\bug  J, K (9, 10)
	//G:\��ְ�������\�ڲ�-�ٶȺ���\����\baidu-crowdsourcing-2016.5.24   J, K (9, 10)
	//G:\��ְ�������\�ڲ�-�ٶȺ���\����\baidu-crowdsourcing-2016.4.21, H, I (7,8)
	//G:\��ְ�������\�ڲ�-�ٶȺ���\����\baidu_crowdsourcing-20150916\�ƶ����Է�������-20150916, K, L, xls format
	
	public static void main ( String args[] ){
		Word2VecTrainData trainData = new Word2VecTrainData();
		//trainData.prepareWord2VecTrainData("G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu-crowdsourcing-2016.8\\bug");
		//trainData.prepareWord2VecTrainData("G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu-crowdsourcing-2016.5.24");
		//trainData.prepareWord2VecTrainData("G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu-crowdsourcing-2016.4.21");
		
		//trainData.prepareWord2VecTrainDataXLS("G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\��Ŀ����-ALL\\CDE");
		//trainData.prepareWord2VecTrainDataXLS("G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\��Ŀ����-ALL\\CD");
		
		/*
		trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\BC");
		trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\�ٶ�ͨѶ¼-BC");
		trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\�ٶ��ڲ�-BC");	
		*/
		
		//trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\Ӱ������-CD" );
		//trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\C" );
		
		//trainData.prepareWord2VecTrainData ( "G:\\��ְ�������\\�ڲ�-�ٶȺ���\\����\\baidu_crowdsourcing-20150916\\���ɲ�������20150916\\����\\BCDEF" );
	}	
}
