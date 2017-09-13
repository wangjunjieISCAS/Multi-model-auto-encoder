package com.data;

public interface Constants {
	
	final static Integer MAX_IMAGE_NUM = 2;
	
	final static String INPUT_FILE_STOP_WORD = "data/input/stopWordListBrief.txt";
	final static boolean IS_NORMALIZE_TFIDF = true;
	
	final static Integer TOP_X_DUPLICATE_REPORT = 10;
	
	final static Integer FIELD_INDEX_TEST_CASE_ID = 0;
	final static Integer FIELD_INDEX_TEST_CASE_NAME	 = 2;
	final static Integer FIELD_INDEX_BUG_DETAIL = 9;
	final static Integer FIELD_INDEX_REPRO_STEPS = 10;

	final static Integer FIELD_INDEX_GROUNG_TRUTH = 18;
}
