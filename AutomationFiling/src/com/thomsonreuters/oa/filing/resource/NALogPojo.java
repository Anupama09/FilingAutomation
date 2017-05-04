package com.thomsonreuters.oa.filing.resource;

import java.util.Date;

public class NALogPojo {
	
	private Date ingestionDateTimeValue ;
	private String resultValue;
	
	private FilingInput filingInput;
	
	private String TestcaseId;
	
	public String getTestcaseId() {
		return TestcaseId;
	}
	public void setTestcaseId(String testcaseId) {
		TestcaseId = testcaseId;
	}
	public FilingInput getfilingInput() {
		return filingInput;
	}
	public void setfilingInput(FilingInput filingInput) {
		this.filingInput = filingInput;
	}
	public String getResultValue() {
		return resultValue;
	}
	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}
	private String docidValue;
	public String getDocidValue() {
		return docidValue;
	}
	public void setDocidValue(String docidValue) {
		this.docidValue = docidValue;
	}
	private long cikValue;
	private String actionTypeValue;
	private String entityIdValue;
	private String requestIdValue;
	private String FilingExceptionValue ;
	public Date getIngestionDateTimeValue() {
		return ingestionDateTimeValue;
	}
	public void setIngestionDateTimeValue(Date ingestionDateTimeValue) {
		this.ingestionDateTimeValue = ingestionDateTimeValue;
	}
	public long getCikValue() {
		return cikValue;
	}
	public void setCikValue(long cikValue) {
		this.cikValue = cikValue;
	}
	public String getActionTypeValue() {
		return actionTypeValue;
	}
	public void setActionTypeValue(String actionTypeValue) {
		this.actionTypeValue = actionTypeValue;
	}
	public String getEntityIdValue() {
		return entityIdValue;
	}
	public void setEntityIdValue(String entityIdValue) {
		this.entityIdValue = entityIdValue;
	}
	public String getRequestIdValue() {
		return requestIdValue;
	}
	public void setRequestIdValue(String requestIdValue) {
		this.requestIdValue = requestIdValue;
	}
	public String getFilingExceptionValue() {
		return FilingExceptionValue;
	}
	public void setFilingExceptionValue(String filingExceptionValue) {
		FilingExceptionValue = filingExceptionValue;
	}
	
	


}
