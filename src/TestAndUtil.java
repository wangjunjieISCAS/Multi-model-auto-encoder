import java.io.File;

import com.dataProcess.WordSegment;

public class TestAndUtil {

	public TestAndUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/*统计图片个数，
	 * 目前是统计的所有文件的数目，其中每个文件夹中包含两个其他文件
	 */
	public void countFigures ( ){
		int count = 0;
		String folderTotal = "data/readyData";
		File folder = new File ( folderTotal );
		String[] fileList = folder.list();
		for ( int i =0; i < fileList.length; i++ ){
			String folderName = folderTotal + "/" + fileList[i];
			System.out.println( folderName );
			
			File folderInner = new File ( folderName );
			String[] fileListInner = folderInner.list();
			
			count += fileListInner.length;
		}
		
		System.out.println( "toal file count: " + count );
	}
	
	
	//对于已经生成好的multiModelTrainData的text文档，将每条文本加上项目名称的分词后的token
	public void refineTestInMultiModelTrainData ( ){
		String folderName = "百度糯米v5.9.1体验测试_1463737315";
		
		String[] words = WordSegment.segmentWordRestricted( folderName );
		for ( int i =0; i < words.length; i++ ){
			System.out.println ( words[i] + " ");
		}
	}
	
	public static void main ( String[] args ){
		TestAndUtil util = new TestAndUtil();
		//util.countFigures();
		
		util.refineTestInMultiModelTrainData();
	}
}
