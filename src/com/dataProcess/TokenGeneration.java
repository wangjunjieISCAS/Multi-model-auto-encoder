package com.dataProcess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.data.TestProject;
import com.data.TestReport;

/*
 * 为某测试报告生成token序列，作为multi-model中的text源数据
 * 仅用测试报告中的description和reproduce step的文本
 */
public class TokenGeneration {
	
	public TokenGeneration() {
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<Integer, String[] > generateTokens ( String fileName , boolean tag){
		LinkedHashMap<Integer, String[] > tokenListMap = new LinkedHashMap<Integer, String[] >();
		
		TestProjectReader reader = new TestProjectReader ();
		TestProject project = reader.loadTestProject(fileName);
		
		System.out.println( project.getTestReportsInProj().size() );
		
		for ( int i =0; i < project.getTestReportsInProj().size(); i++ ){
			TestReport report = project.getTestReportsInProj().get( i );
			
			String[] words = WordSegment.segmentTestReport(report);
			tokenListMap.put( i, words );
		}
		
		if ( tag ){
			//String outFileName = fileName.substring( 0, fileName.length() - 4 ) + "-token.txt";
			int nameIndex = fileName.lastIndexOf( "/");
			String outFileName = "data/testData/" + fileName.substring( nameIndex+1, fileName.length() - 4 ) + ".txt";
			
			try {
				BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( outFileName )) , "utf-8"), 1024);
				
				for ( Integer index: tokenListMap.keySet() ){
					String[] words = tokenListMap.get( index );
					
					for ( int j =0; j < words.length; j++ ){
						output.write( words[j] + " ");
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
		
		
		return tokenListMap;
	}
	
	public static void main ( String args[] ){
		TokenGeneration tokenGenerator = new TokenGeneration();
		//tokenGenerator.generateTokens( "data/readyData/药师360测试_1463737715/药师360测试_1463737715.csv", true);
		//tokenGenerator.generateTokens( "data/readyData/百度浏览器视频播放体验_1463737380/百度浏览器视频播放体验_1463737380.csv", true );
		
		tokenGenerator.generateTokens( "data/readyData/百度手机卫士功能体验_1463737109/百度手机卫士功能体验_1463737109.csv", true );
	}
}
