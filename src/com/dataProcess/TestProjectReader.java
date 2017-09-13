package com.dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.data.Constants;
import com.data.TestProject;
import com.data.TestProjectVectors;
import com.data.TestReport;

/*
 * 从文件中读取测试报告，并作为一个testProject存储
 */
public class TestProjectReader {

	public TestProjectReader() {
		// TODO Auto-generated constructor stub
	}
	
	public String transferString ( String str ){
		String result = str;
		result = result.replaceAll( "\r\n", " " );
		result = result.replaceAll( "\r", " " );
		result = result.replaceAll( "\n", " " );
		
		return result;
	}
	
	public TestProject loadTestProject ( String fileName ){
		TestProject testProject = new TestProject ( fileName );
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			
			CsvReader reader = new CsvReader( br, ',');
			
			// 跳过表头   如果需要表头的话，不要写这句。  
			reader.readHeaders(); 
			//逐行读入除表头的数据      
			int index = 0;
	        while ( reader.readRecord() ){
	        	String[] temp = reader.getValues();
	        	
	        	int id = index ++;
	        	int testCaseId = Integer.parseInt( temp[Constants.FIELD_INDEX_TEST_CASE_ID]);
	        	String testCaseName = this.transferString( temp[Constants.FIELD_INDEX_TEST_CASE_NAME] );
	        	String bugDetail = this.transferString( temp[Constants.FIELD_INDEX_BUG_DETAIL] ) ;
	        	String reproSteps = this.transferString( temp[Constants.FIELD_INDEX_REPRO_STEPS] );
	        	
	        	TestReport report = new TestReport ( id, testCaseId, testCaseName, bugDetail, reproSteps );
	        	
	        	testProject.getTestReportsInProj().add( report );
	        }
			
	        reader.close();
			System.out.println ( "testProject size: " + testProject.getTestReportsInProj().size()  );
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return testProject;
	}
	
	public TestProjectVectors loadTestProjectVectors ( String fileName ){
		TestProjectVectors projectVec = new TestProjectVectors();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( fileName )));
			
			String line = "";
			int index = 0;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( " ");
				
				ArrayList<Double> vector = new ArrayList<Double>();
				for ( int i =0; i < temp.length; i++ ){
					Double value = Double.parseDouble( temp[i]);
					vector.add( value );
				}
				projectVec.getReportsVec().put( index, vector );
				index++;
			}			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return projectVec;		
	}
}
