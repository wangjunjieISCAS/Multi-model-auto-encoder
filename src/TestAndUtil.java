import java.io.File;

import com.dataProcess.WordSegment;

public class TestAndUtil {

	public TestAndUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/*ͳ��ͼƬ������
	 * Ŀǰ��ͳ�Ƶ������ļ�����Ŀ������ÿ���ļ����а������������ļ�
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
	
	
	//�����Ѿ����ɺõ�multiModelTrainData��text�ĵ�����ÿ���ı�������Ŀ���Ƶķִʺ��token
	public void refineTestInMultiModelTrainData ( ){
		String folderName = "�ٶ�Ŵ��v5.9.1�������_1463737315";
		
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
