package com.thomsonreuters.oa.filing.resource;

import java.util.Date;

public class FilingInput {
	
	private FilingInput filingInput;
	
	public FilingInput getfilingInput() {
		return filingInput;
	}
	public void setfilingInput(FilingInput filingInput) {
		this.filingInput = filingInput;
	}

	//DB Values 
	private String docId;
	private int counter;
	private String entityId;
	private String requestId;
	private String filingRuleMsg;
	private long hqValid;
	private long EdgarFileName;
	private String ignoreId;
	
	public String getIgnoreId() {
		return ignoreId;
	}
	public void setIgnoreId(String ignoreId) {
		this.ignoreId = ignoreId;
	}

	//private String execution;
	//private String status;
	private int isInfValid;
	private String requestCmt;
	

	//Filing Data
	private String conformedName;
	public FilingInput getFilingInput() {
		return filingInput;
	}
	public void setFilingInput(FilingInput filingInput) {
		this.filingInput = filingInput;
	}

	public String getRequestCmt() {
		return requestCmt;
	}
	public void setRequestCmt(String requestCmt) {
		this.requestCmt = requestCmt;
	}
	public String getFilingDate() {
		return filingDate;
	}

	private long cik;
	private int assignedSIC;
	private long irsNumber;
	private String stateOfIncorporation;
	private String street1;
	private String street2;
	private String city;
	private String state;
	private String zip;
	private long phone;
	private String type;
	private String formerConformedName;
	private long taxID;
	
	//TestCases Excel Column 
	private int serialNo;
	private String testCaseId;
	private String objective;
	private String expectedResults;
	private String actualRsesult;
	private String result;
	private String comments;
	
	//Log file Data
	private Date ingestionDateTime;
	private String actionType;
	private String FilingException ;
	
	
	public Date getIngestionDateTime() {
		return ingestionDateTime;
	}

	public void setIngestionDateTime(Date ingestionDateTime) {
		this.ingestionDateTime = ingestionDateTime;
	}

	public String getFilingException() {
		return FilingException;
	}

	public void setFilingException(String filingException) {
		FilingException = filingException;
	}


	
	
	
	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
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

	

	
	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}


	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	private String isAutoLock;

	public String getIsAutoLock() {
		return isAutoLock;
	}

	public void setIsAutoLock(String isAutoLock) {
		this.isAutoLock = isAutoLock;
	}


	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	

	public String getFilingRuleMsg() {
		return filingRuleMsg;
	}

	public void setFilingRuleMsg(String filingRuleMsg) {
		this.filingRuleMsg = filingRuleMsg;
	}

	

	public long getHqValid() {
		return hqValid;
	}

	public void setHqValid(long hqValid) {
		this.hqValid = hqValid;
	}
/*
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}*/

	

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}



	public int getIsInfValid() {
		return isInfValid;
	}

	public void setIsInfValid(int isInfValid) {
		this.isInfValid = isInfValid;
	}


	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public long getPhone() {
		return phone;
	}

	public void setPhone(long string) {
		this.phone = string;
	}

	
	public String getFormerConformedName() {
		return formerConformedName;
	}

	public void setFormerConformedName(String formerConformedName) {
		this.formerConformedName = formerConformedName;
	}

	public String getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(String dateChanged) {
		this.dateChanged = dateChanged;
	}

	private String dateChanged;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getEdgarFileName() {
		return EdgarFileName;
	}

	public void setEdgarFileName(long edgarFileName) {
		EdgarFileName = edgarFileName;
	}

	private String filingDate;

	public String getFillingDate() {
		return filingDate;
	}

	public void setFilingDate(String today) {
		this.filingDate = today;
	}

	public String getConformedName() {
		return conformedName;
	}

	public void setConformedName(String conformedName) {
		this.conformedName = conformedName;
	}

	public long getCik() {
		return cik;
	}

	public void setCik(long cik) {
		this.cik = cik;
	}

	public int getAssignedSIC() {
		return assignedSIC;
	}

	public void setAssignedSIC(int assignedSIC) {
		this.assignedSIC = assignedSIC;
	}

	public long getIrsNumber() {
		return irsNumber;
	}

	public void setIrsNumber(long irsNumber) {
		this.irsNumber = irsNumber;
	}

	public String getStateOfIncorporation() {
		return stateOfIncorporation;
	}

	public void setStateOfIncorporation(String stateOfIncorporation) {
		this.stateOfIncorporation = stateOfIncorporation;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public FilingInput() {
		// TODO Auto-generated constructor stub
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}
	public long getTaxID() {
		return taxID;
	}
	public void setTaxID(long taxID) {
		this.taxID = taxID;
	}
	

	/*public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}
*/

}
