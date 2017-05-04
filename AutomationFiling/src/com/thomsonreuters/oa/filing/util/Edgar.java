package com.thomsonreuters.oa.filing.util;

public class Edgar {
	
	private long edgarFileName;
	public long getEdgarFileName() {
		return edgarFileName;
	}
	public void setEdgarFileName(long edgarFileName) {
		this.edgarFileName = edgarFileName;
	}
	public long getCikNo() {
		return cikNo;
	}
	public void setCikNo(long cikNo) {
		this.cikNo = cikNo;
	}
	public long getIrs() {
		return irs;
	}
	public void setIrs(long irs) {
		this.irs = irs;
	}
	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	private long cikNo;
	private long irs;
	private String cName;
	
	
	
}
