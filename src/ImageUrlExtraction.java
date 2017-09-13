

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.csvreader.CsvReader;
import com.data.Constants;

/*
 * image url 基本格式
 * ["http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362154_24ae39a4-38ea-48fa-bfc3-757fe4a02cfb.jpg",
 * "http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362153_21e216c7-7bea-4253-b63c-d8feab3a67bb.jpg",
 * "http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362152_e9b733fd-f48c-4cbe-a433-40a955344a1e.jpg",
 * "http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362151_10d964b9-43a3-4c36-bd79-c02502306b4a.jpg",
 * "http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362150_74791e26-e2de-46c4-b623-6aa40227f19d.jpg",
 * "http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/5362149_ca1f5323-8b1d-4e81-be51-a2877f174ab8.jpg"]
 * 
 * 在文件中看，这些url是在一个格里面的，但因为csv是用逗号分割，所以他们会被分割成不同的
 */
public class ImageUrlExtraction {
	
	public ImageUrlExtraction() {
		// TODO Auto-generated constructor stub
	}
	
	public void extractImageUrl ( String fileName ){
		ArrayList<String[]> imageUrlList = new ArrayList<String[]>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			
			String line = "";
			boolean isFirstLine = true;
			while ( ( line = br.readLine() ) != null ) {
				if ( isFirstLine == true ){
					isFirstLine = false;
					continue;
				}
				
				String[] temp = line.split( ",");
				
				String[] storeUrls = new String[Constants.MAX_IMAGE_NUM];
				int index = 0;
				for ( int i = 0; i< temp.length; i++ ){				
					if ( temp[i].contains( "http") && temp[i].contains( "jpg")){
						//包含image url的temp[i]
						String imageUrl = temp[i];
						System.out.println( imageUrl );
						
						//imageUrls = imageUrls.replaceAll( "[", "" );
						//imageUrls = imageUrls.replaceAll ( "]", "" );
						imageUrl = imageUrl.replaceAll ( "[\\[\\]]", "" );
						
						imageUrl = imageUrl.replaceAll( "\"", "" );
						imageUrl = imageUrl.replaceAll ( "\"", "" );
						
						storeUrls[index%Constants.MAX_IMAGE_NUM] = imageUrl;
						index++;
					}
				}
				imageUrlList.add( storeUrls );
			}
			//System.out.println ( results.size() );
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		for ( int i =0; i < imageUrlList.size(); i++ ){
			System.out.println ( i ); 
			for ( int j =0; j < imageUrlList.get(i).length; j++ ){
				System.out.print ( imageUrlList.get(i)[j] + " | ");
			}
			System.out.println ( "\n");
		}
	}
	
	
	public ArrayList<String[]> extractImageUrlCSV ( String fileName ){
		ArrayList<String[]> imageUrlList = new ArrayList<String[]>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader( new File ( fileName )));
			
			CsvReader reader = new CsvReader( br, ',');
			
			// 跳过表头   如果需要表头的话，不要写这句。  
			reader.readHeaders(); 
			//逐行读入除表头的数据      
	        while ( reader.readRecord() ){
	        	String[] temp = reader.getValues();
	        	
	        	String[] storeUrls = new String[Constants.MAX_IMAGE_NUM];
				for ( int i = 0; i< temp.length; i++ ){			
					if ( temp[i].contains( "http") && temp[i].contains( "jpg")){
						//包含image url的temp[i]
						String imageUrl = temp[i];
						System.out.println( i + ": " + imageUrl );
						
						//imageUrls = imageUrls.replaceAll( "[", "" );
						//imageUrls = imageUrls.replaceAll ( "]", "" );
						imageUrl = imageUrl.replaceAll ( "[\\[\\]]", "" );
						
						imageUrl = imageUrl.replaceAll( "\"", "" );
						imageUrl = imageUrl.replaceAll ( "\"", "" );
						
						String[] urls = imageUrl.split( ",");
						
						
						for ( int j = urls.length-1, k = 0; j >=0 && k < Constants.MAX_IMAGE_NUM ; j--, k++ ){
							storeUrls[k] = urls[j];
						}
					}
				}
				imageUrlList.add( storeUrls );	
				//System.out.println(  storeUrls.toString() );
	        }
			
	        reader.close();
			//System.out.println ( results.size() );
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return imageUrlList;
	}
	
	public void downloadImage (  String fileName, String imageFolderIn, String imageFolderOut  ){
		ArrayList<String[]> imageUrlList = this.extractImageUrlCSV(fileName);
		
		Set<String> downloadedImageUrl = new HashSet<String>();
		
		int beginIndex = fileName.lastIndexOf( "/");
		int endIndex = fileName.lastIndexOf( ".csv");		
		String projectName = fileName.substring( beginIndex+1, endIndex ); 		
		
		String projectImageFolderName = imageFolderOut + projectName + "/";
		File projectImageFolder = new File ( projectImageFolderName );
		if ( projectImageFolder.mkdir() ){
			System.out.println ( "======================= mkdir success!");
		}else{
			projectImageFolderName = imageFolderOut;
		}
		
		for ( int i = 174; i < imageUrlList.size(); i++ ){
			System.out.println ( i ); 
			for ( int j =0; j < imageUrlList.get(i).length; j++ ){
				System.out.print ( imageUrlList.get(i)[j] + " | ");			
				
				if ( imageUrlList.get( i)[j] == null )
					continue;
				
				if ( downloadedImageUrl.contains( imageUrlList.get(i)[j] ))
					continue;
				
				downloadedImageUrl.add( imageUrlList.get(i)[j]);
				
				String url = imageUrlList.get( i)[j].replaceAll( "/", "");
				System.out.println( url );
				
				String str = "cmd /c start chrome " + url ;
				try {
					Runtime.getRuntime().exec(str);
					
					Random rand = new Random();
					int interval = 30 + rand.nextInt( 60 );
					Thread.sleep( 1000*interval  );
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//下载结果后，直接进行rename
				int index = url.lastIndexOf( "\\");
				System.out.println( "index: " + index );
				String imageFile = url.substring( index+1 );
				
				File file1=new File( imageFolderIn + imageFile );    
				File file2=new File( projectImageFolderName +  projectName + "-" + i + "-" + j + ".jpg");  
				
				System.out.println( projectImageFolderName +  projectName + "-" + i + "-" + j + ".jpg" );
				boolean flag = file1.renameTo(file2);  
				
				if ( flag == false ){
					System.out.println( "Rename failed! ============================ " );
				}				
			}
			System.out.println ( "\n");
		}
	}
	
	/*
	 * http:\/\/bos.bj.baidubce.com\/json-api\/v1\/crowdtest-online\/upload\/7090353_405d48e4-319e-4893-94be-8f9d3dd4968c.jpg
	 * 暂时不用了，已经将重命名集成到downloadImage下面了
	 */
	public void renameImage ( String fileName, String imageFolderIn, String imageFolderOut  ){
		ArrayList<String[]> imageUrlList = this.extractImageUrlCSV(fileName);
		
		for ( int i =0; i < imageUrlList.size(); i++ ){
			System.out.println ( i ); 
			for ( int j =0; j < imageUrlList.get(i).length; j++ ){
				System.out.print ( imageUrlList.get(i)[j] + " | ");			
				
				if ( imageUrlList.get( i)[j] == null )
					continue;
				
				String url = imageUrlList.get( i)[j].replaceAll( "/", "");
				System.out.println( url );
				
				int index = url.lastIndexOf( "\\");
				System.out.println( "index: " + index );
				String imageFile = url.substring( index+1 );
				
				File file1=new File( imageFolderIn + imageFile );    
				File file2=new File( imageFolderOut  +  i + "-" + j + ".jpg");    
				boolean flag = file1.renameTo(file2);  
				
				if ( flag == false ){
					System.out.println( "Rename failed! ============================ " );
				}				
			}
			System.out.println ( "\n");
		}
	}
	
	public void testOpenBrowser ( ){
		String str = "cmd /c start iexplore http://blog.csdn.net/powmxypow";
		try {
			Runtime.getRuntime().exec(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main ( String args[] ) throws FileNotFoundException, IOException{
		ImageUrlExtraction extraction = new ImageUrlExtraction();
		
		String imageFolderIn = "C:/Users/lenovo/Downloads/";
		String imageFolderOut = "data/image/";
		
		String reportFolder = "data/report/";
		File projectsFolder = new File ( reportFolder );
		if ( projectsFolder.isDirectory() ){
			String[] projectList = projectsFolder.list();
			for ( int i = 0; i< projectList.length; i++ ){
				String fileName = reportFolder +  projectList[i];
				
				extraction.downloadImage ( fileName, imageFolderIn, imageFolderOut );	
			}				
		} 
		
		/*
		String fileName = "data/百度浏览器V3.0有奖测试_1463737331.csv";
		//extraction.downloadImage ( fileName );		
		
		String imageFolderIn = "data/image/input/";
		String imageFolderOut = "data/image/output/";
			
		extraction.renameImage(fileName, imageFolderIn, imageFolderOut);
		//extraction.testOpenBrowser();
		*/
	}
}
