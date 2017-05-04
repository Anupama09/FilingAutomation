package com.thomsonreuters.oa.filing.resource;

public class FilingUpdateInput {
	
	private long idValue;
	public long getIdValue() {
		return idValue;
	}
	public void setIdValue(long idValue) {
		this.idValue = idValue;
	}
	public String getOfficailName() {
		return officailName;
	}
	public void setOfficailName(String officailName) {
		this.officailName = officailName;
	}
	private String officailName;
	
	
}
