import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ImageRename {

	public ImageRename() {
		// TODO Auto-generated constructor stub
	}
	
	//处理图片的名字，为testData做准备
	public void renameImageForTestData ( String folderName ){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		File folder = new File ( folderName );
		String[] fileList = folder.list();
		
		System.out.println ( fileList.length );
		for ( int i =0; i < fileList.length; i++ ){
			String fileName = fileList[i];
			System.out.println( folderName +"/" + fileName );
			
			File file1 = new File ( folderName +"/" + fileName );
			if ( fileName.endsWith( ".jpg")){
				String[] temp = fileName.split( "-");
				int len = temp.length;
				
				Integer index = Integer.parseInt( temp[len-2] );
				if ( !indexList.contains( index )){
					indexList.add( index );
					
					File file2=new File( folderName +"/" + index.toString() + ".jpg" ); 
					
					boolean flag = file1.renameTo(file2);  				
					if ( flag == false ){
						System.out.println( "Rename failed! ============================ " );
					}	
				}				
			}	
		}
	}
	
	public static void main ( String args[] ){
		ImageRename imageTool = new ImageRename();
		imageTool.renameImageForTestData( "data/testData/百度手机卫士功能体验_1463737109");
	}
}
