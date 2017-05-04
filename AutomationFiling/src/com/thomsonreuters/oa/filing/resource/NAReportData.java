package com.thomsonreuters.oa.filing.resource;

public class NAReportData {
	
	String testCaseId;
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	public String getObjecttive() {
		return objecttive;
	}
	public void setObjecttive(String objecttive) {
		this.objecttive = objecttive;
	}
	public String getExpectedResults() {
		return expectedResults;
	}
	public void setExpectedResults(String expectedResults) {
		this.expectedResults = expectedResults;
	}
	public String getActualRsesult() {
		return actualRsesult;
	}
	public void setActualRsesult(String actualRsesult) {
		this.actualRsesult = actualRsesult;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	String	objecttive;
	String expectedResults;
	String actualRsesult;
	String result;
	String comments;
	

}
