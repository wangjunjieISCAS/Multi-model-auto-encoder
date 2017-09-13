package com.data;

import java.util.ArrayList;

public class TestProject {
	String projectName;
	ArrayList<TestReport> testReportsInProj;
	
	public TestProject( String projectName ) {
		// TODO Auto-generated constructor stub
		this.projectName = projectName;
		
		testReportsInProj = new ArrayList<TestReport>();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public ArrayList<TestReport> getTestReportsInProj() {
		return testReportsInProj;
	}

	public void setTestReportsInProj(ArrayList<TestReport> testReportsInProj) {
		this.testReportsInProj = testReportsInProj;
	}
	
	
}
