package com.thomsonreuters.oa.filing.dataAccessor;

import java.util.List;

import com.thomsonreuters.oa.filing.resource.FilingInput;
import com.thomsonreuters.oa.filing.resource.FilingUpdateInput;
import com.thomsonreuters.oa.filing.resource.RequestToll;

public class FilingDataAccessor extends OracleDAO {

	public boolean dbQuery(long inputNo, String query) {

		boolean cikNoExist = false;
		int count = 0;
		try {
			count = executeQueryForInt(query, inputNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return cikNoExist;
	}

	public boolean dbQuery(String inputNo, String query) {

		boolean cikNoExist = false;
		int count = 0;
		try {
			count =executeQueryForInt(query, inputNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return cikNoExist;
	}
	
	public boolean dbQueryToFetchDocId(long inputNo, String query) {

		boolean docid = false;
		int count = 0;
		try {
			count = executeQueryForInt(query, inputNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (count > 0) {
			return true;
		}
		return docid;
	}
	
	public Integer dbQueryToFetchDocIdValue(long inputNo, String query) {

		Integer count = 0;
		try {
			count = executeQueryForInteger(query, inputNo);
			//System.out.println("Count Value "+ count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	
	

	/*public static void main(String[] args) {
		filingDataAccessor  od = new filingDataAccessor();
		try {
			od.executeQueryForInt("select * from ascollected.edgar_org_data where docid in (?)", "99611315");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
	public String fetchDataFromAscollect(FilingInput fileData,long inputNo,String column, String query) {
		String id=null;
		try {
			id = executeQueryfetchDataFromAscollect(fileData,query,column,inputNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List<DBfilingData> dbfilingDataList = executeQuery(query, fetchingDataHandler, inputNo);
		return id;

	

	}



	
	//Fetching existing data from DB to perform update.
	public FilingInput fetchDataForUpdate(FilingInput FilingData ,int infValidNo,String query) {
		
		//List<FilingUpdateInput> UpdateDataList = null;
		try {
			FilingData=fetchDBDataForUpdate(FilingData ,query, infValidNo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List<DBfilingData> dbfilingDataList = executeQuery(query, fetchingDataHandler, inputNo);
		return FilingData;
		
	}
	
	
	//FetchingDataRequestCmd
	
	public FilingInput FetchingDataFrmRequestQueue(FilingInput FilingData,String requestId, String column,
			String query) {
		try {
			FilingData=FetchingDataRequestCmd(FilingData,query,column,requestId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List<DBfilingData> dbfilingDataList = executeQuery(query, fetchingDataHandler, inputNo);
		return FilingData;
	}

	
/*
	
	

	@Override
	public void processRow(ResultSet rs) throws SQLException {
		RequestToll reqToll= new RequestToll();
		reqToll.setReq_comments(rs.getString("requester_comments"));
		requestTollList.add(reqToll);*/

	/*class FetchingDataHandler implements   ParameterizedRowMapper<DBfilingData> {
		@Override
		public DBfilingData mapRow(ResultSet rs, int arg1)
				throws SQLException {
			DBfilingData updateData = new DBfilingData();
			
			updateData.setEntityId(rs.getString("ife_entityID"));
			
			updateData.setIdValue(rs.getLong("id_value"));
			updateData.setOfficailName(rs.getString("org_official_name"));
			
			updateData.setReq_comments(rs.getString("requester_comments"));
			
		return updateData;
		
	}*/
	/*public RequestToll fetchReqComment(long reqId, String query) {

		FetchingData fetchingData=new FetchingData();
		getJdbcTemplate().query(query, fetchingData, reqId);
		return fetchingData.getRequestDetails().get(0);

	}*/

	/*class FetchingData implements RowCallbackHandler {

		public List<RequestToll> requestTollList = new ArrayList<RequestToll>();

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			RequestToll reqToll= new RequestToll();
			reqToll.setReq_comments(rs.getString("requester_comments"));
			requestTollList.add(reqToll);

		}
		
		public List<RequestToll> getRequestDetails() {
			return requestTollList;
		}

	}*/
	
		
public long fetchDataToValidateEntity(FilingInput fileData,String entityId,
		String orgHqIsInfValid) {
	// TODO Auto-generated method stub
	
	long data = 0;
	// Object[] parameters = new Object[] {entityId };
	 
	 try {
		 data=executeQueryTofetchDataToValidateEntity(fileData,entityId, orgHqIsInfValid);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return data;
}


public long fetchDataToValidateErrorMsg(FilingInput fileData,String entityId,
		long edgarFileName) {
	// TODO Auto-generated method stub
	
	long msg = 0;
	// Object[] parameters = new Object[] {entityId };
	 
	 try {
		msg=executeQueryTofetchDataToValidateEntity(fileData,entityId, edgarFileName);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return msg;
}

	
/*
		public long fetchDataToValidateEntity(String entityId,
				String orgHqIsInfValid) {
			// TODO Auto-generated method stub
			 Object[] parameters = new Object[] {entityId };
			 
			 executeQueryTofetchDataToValidateEntity(entityId, orgHqIsInfValid);
			
		}
*/


}
