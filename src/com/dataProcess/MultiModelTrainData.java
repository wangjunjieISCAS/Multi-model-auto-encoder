package com.dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*
 * ����multi model��ѵ�����ݣ���ʽΪ��һ����Ŀ�洢��һ���ļ����У����С�txt�ļ��洢�����ı����ݣ�ÿһ�ж�Ӧһ�����Ա��棻ͼƬ�ļ����ڵ���ͼƬ��ͼƬ�ı�Ŷ�Ӧ�ı��в��Ա�����к�
 * ����һ�����Ա����ж��ͼƬ���������Ϊ������Ա��洦��
 * ���ڲ��Ա���û��ͼƬ������������д洢
 */
public class MultiModelTrainData {

	public MultiModelTrainData() {
		// TODO Auto-generated constructor stub
	}
	
	public Integer generateMultiModelTrainData( String folderName ){
		HashMap<Integer, ArrayList<String>> imageNameMap = new HashMap<Integer, ArrayList<String>>();
		String projectFile = "";
		
		File folder = new File ( folderName );
		String[] fileList = folder.list();
		for ( int i =0; i < fileList.length; i++ ){
			String fileName = fileList[i];
			System.out.println( fileName );
			
			if ( fileName.endsWith( ".csv")){
				projectFile = folderName + "/" + fileName;
			}
			if ( fileName.endsWith( ".jpg")){
				String[] temp = fileName.split( "-");
				int len = temp.length;
				
				int index = Integer.parseInt( temp[len-2]);
				if ( !imageNameMap.containsKey( index )){
					ArrayList<String> imageNames = new ArrayList<String>();
					imageNameMap.put( index, imageNames);			
				}
				imageNameMap.get(index).add( fileName );	
			}	
		}
		
		if ( projectFile.equals( "")){
			System.out.println( "Warning !!!!!!! The csv file does not exist! " + folderName );
		}
		
		TokenGeneration tokenGeneration = new TokenGeneration();
		//false��ʾ���洢���ɵ�ԭʼ��tokenListMap����
		HashMap<Integer, String[]> tokenListMap = tokenGeneration.generateTokens( projectFile, false );
		
		ArrayList<String[]> outputTokensList = new ArrayList<String[]>();
		
		for ( Integer index : imageNameMap.keySet() ){
			ArrayList<String> images = imageNameMap.get( index );
			
			String[] tokens = tokenListMap.get( index );
			//���һ�����Ա����Ӧ���ͼƬ�������Ϊ�������Ա���
			for ( int i =0; i < images.size(); i++ ){
				outputTokensList.add( tokens );
				
				String imageName = images.get( i );
				//��ͼƬ����������
				File file1=new File( folderName + "/" + imageName );   
				
				//ͼƬ��0��ʼ���
				Integer rank = outputTokensList.size()-1;
				File file2=new File(folderName +"/" + rank.toString() + ".jpg" ); 
				
				boolean flag = file1.renameTo(file2);  				
				if ( flag == false ){
					System.out.println( "Rename failed! ============================ " );
				}	
			}
		}
		
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( folderName + "/text.txt" ), true) , "utf-8"), 1024);
			for ( int i =0; i < outputTokensList.size(); i++ ){
				String[] temp = outputTokensList.get(i);
				for ( int j =0; j < temp.length; j++ ){
					output.write( temp[j] + " ");
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
		
		return outputTokensList.size();
	}
	
	public void generateInBatchMultiModelTrainData ( String folderTotalName ){
		int count = 0;
		//String folderTotal = "data/test";
		File folder = new File ( folderTotalName );
		String[] fileList = folder.list();
		for ( int i =0; i < fileList.length; i++ ){
			String folderName = folderTotalName + "/" + fileList[i];
			System.out.println( folderName );
			
			int countProject = this.generateMultiModelTrainData( folderName );
			System.out.println( "countProject: " + countProject );
			
			count += countProject;
		}
		
		System.out.println ( "total count: " + count );
	}
	
	public void generateTrainDataInOneFile( String folderName, String outTextFile, String outImageFolder ){
		try {
			BufferedWriter output = new BufferedWriter ( new OutputStreamWriter ( new FileOutputStream ( new File ( outTextFile ), true) , "utf-8"), 1024);
			
			int totalCount = 0;
			File folder = new File ( folderName );
			String[] fileList = folder.list();
			for ( int i =0; i < fileList.length; i++ ){
				String fileName = folderName + "/" + fileList[i];
				System.out.println( fileName );
				
				
				File folderInner = new File ( fileName ) ;
				String[] fileListInner = folderInner.list();
				
				//��ͼƬ���ļ�����projectBeginCount��ʼ
				int textCount = 0, imageCount = 0;
				for ( int j =0; j < fileListInner.length; j++ ){
					String fileNameInner = fileName + "/" + fileListInner[j];
					
					if ( fileNameInner.endsWith( ".txt")){
						BufferedReader br = new BufferedReader( new InputStreamReader ( new FileInputStream( new File ( fileNameInner )), "utf-8" ) );
						String line = "";
						while ( ( line = br.readLine() ) != null ) {
							output.write( line );
							//System.out.println (  line );
							/*
							String[] temp = line.split( " ");
							for ( int k =0; k < temp.length; k++ )
								output.write( temp[k] + " " );
								*/
							output.newLine();
							textCount ++;
						}
						br.close();
					}
					//copy /y c:\123 d:\123
					if ( fileNameInner.endsWith( ".jpg")){
						String source = "D:\\java-workstation\\TestReportDuplicate\\" + fileNameInner.replace( "/", "\\");
						String target = "D:\\java-workstation\\TestReportDuplicate\\" + outImageFolder.replace("/", "\\") + "\\" + totalCount + ".jpg";
						String str = "cmd /c copy /y " +  source + " "  + target ;
						Runtime.getRuntime().exec(str);
						//Thread.sleep( 1000*3  );
						totalCount ++;
						imageCount ++;
					}						
				}		
				if ( textCount != imageCount ){
					System.out.println ( "Warning !!!!!! textCount is not equal with imageCount in " + fileName  + " textCount is: " + textCount + " imageCount is: " + imageCount );
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
		} 
	}
	
	public static void main ( String args[] ){
		MultiModelTrainData trainData = new MultiModelTrainData();
		
		/*
		 * ���Ƚ������ӵ���Ŀ�������������ļ����У�����generateInBatchMultiModelTrainData��
		 * ����token�ġ�txt�ļ������ҽ�ȫ����ͼƬ����������������0-N��
		 */
		//trainData.generateInBatchMultiModelTrainData( "data/test");
		
		/*
		 * Ȼ������generateTrainDataInOneFile��
		 * ���յõ�multiTrainText.txt �� multiTrainImage�ļ���
		 */
		trainData.generateTrainDataInOneFile( "data/readyData", "data/multiTrainText.txt", "data/multiTrainImage");
	}
}
