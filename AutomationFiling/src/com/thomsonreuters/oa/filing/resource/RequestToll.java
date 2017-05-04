package com.thomsonreuters.oa.filing.resource;

public class RequestToll {
	
	private long reqID;
	public long getReqID() {
		return reqID;
	}
	public void setReqID(long reqID) {
		this.reqID = reqID;
	}
	public String getReq_comments() {
		return req_comments;
	}
	public void setReq_comments(String req_comments) {
		this.req_comments = req_comments;
	}
	private String req_comments;
	@Override
	public String toString() {
		return "RequestToll [reqID=" + reqID + ", req_comments=" + req_comments
				+ "]";
	}
	
	
	

}
