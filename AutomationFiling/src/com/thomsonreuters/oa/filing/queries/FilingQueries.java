package com.thomsonreuters.oa.filing.queries;

public class FilingQueries {

	public static final String EDGARFILENAME = "select count(*) from ascollected.edgar_org_data where docid in (?)";
	
	public static final String CIKNO = "select count(*) from oa.org_identifiers A where A.id_type=1 and id_value in (?)";
	
	public static final String IRISNO = "select count(*) from oa.org_identifiers A where A.id_type=4 and id_value in (?)";
	
	public static final String ERRORID = "select count(*) from OA.edgar_error_log where docid in (?)";
	
	public static final String FILINGS_RULE_MSG= "select filings_rule_msg from OA.edgar_error_log  where docid in (?)";
	
	public static final String IGNOREID = "select count(*) from ASCOLLECTED.edgar_ignore_data where docid in (?)";	
	
	public static final String DOCID="select docid from ascollected.edgar_org_data where docid in (?)";
	
	public static final String ENTITYID = "select ife_entityid from ascollected.edgar_org_data where docid in (?)";
	
	public static final String REQUESTID = "select request_id from ascollected.edgar_org_data where docid=(?) and rownum<2";
	
	public static final String  IS_LOCKED_FOR_AUTOCREATE ="select is_locked_for_autocreate from ascollected.edgar_org_data where docid in (?)";
	
	public static final String ORG_HQ_IS_INF_VALID ="select org_hq_is_inf_valid from OA.org_headquarters_address where orgid in (?)";
	
	public static final String 	REQUESTER_COMMENTS = "select requester_comments from REQUESTTOOL.REQUEST_QUEUE where request_id  IN (?)";
	
	public static final String DATACONSUMPTION="select count(*) from ascollected.edgar_org_data where docid=(?) and request_id is not null  and ife_entityid is not null and ife_entityid <> -1";
	
	public static final String ORGINFO="select c.org_official_name ,b.id_value from oa.org_headquarters_address a,oa.org_identifiers b,oa.organizations c where a.orgid= b.orgid and a.orgid= c.orgid and a.org_hq_is_inf_valid=(?) and c.org_is_verified='1'  and c.org_is_active='1' and c.org_tier_id='4' and c.org_country_of_domicile='US' and b.id_type='1'and  rownum<2";
			//"select c.orgid ,c.org_official_name ,b.id_value from oa.org_headquarters_address a,oa.org_identifiers b,oa.organizations c where c.orgid in (select orgid from oa.org_counterparty where org_cp_tax_file_id is not null) and a.orgid= b.orgid and a.org_hq_is_inf_valid=(?) and c.org_is_verified='1' and c.org_is_active='1' and c.org_country_of_domicile='US' and c.org_data_provider in (1,2,3) and b.id_type='1' and rownum<2";
			
	
	public static final String FILINGDATE = "select filing_date from ascollected.edgar_org_data where docid in (?)";
	
	public static final String PHONE = "select phone from ascollected.edgar_org_data where docid in (?)";
	
	public static final String IRS_NUMBER = "select irs_number from ascollected.edgar_org_data where docid in (?)";
	
	public static final String ORG_CP_TAX_FILE_ID= "select org_cp_tax_file_id from oa.org_counterparty  where orgid in (?)";
	
	public static final String ORGID= "";
	
	

}

