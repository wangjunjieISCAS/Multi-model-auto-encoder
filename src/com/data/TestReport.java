package com.data;

public class TestReport {
	int id;
	int testCaseId;
	String testCaseName;
	
	String bugDetail;
	String reproSteps;
	
	public TestReport( int id, int testCaseId, String testCaseName, String bugDetail, String reproSteps ) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.testCaseId = testCaseId;
		this.testCaseName = testCaseName;
		
		this.bugDetail = bugDetail;
		this.reproSteps = reproSteps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(int testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getBugDetail() {
		return bugDetail;
	}

	public void setBugDetail(String bugDetail) {
		this.bugDetail = bugDetail;
	}

	public String getReproSteps() {
		return reproSteps;
	}

	public void setReproSteps(String reproSteps) {
		this.reproSteps = reproSteps;
	}
	
	
}
