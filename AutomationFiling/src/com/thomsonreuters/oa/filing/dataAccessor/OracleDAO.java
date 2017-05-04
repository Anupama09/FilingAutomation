/*
 * Copyright 2010: Thomson Reuters Global Resources. 
 * All Rights Reserved. Proprietary and Confidential information of TRGR. 
 * Disclosure, Use or Reproduction without the written authorization 
 * of TRGR is prohibited
 */
//package com.thomsonreuters.legal.lem.lpa.service.ew.dao;
package com.thomsonreuters.oa.filing.dataAccessor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.thomsonreuters.oa.filing.resource.FilingInput;
import com.thomsonreuters.oa.filing.resource.FilingUpdateInput;
import com.thomsonreuters.oa.filing.resource.RequestToll;
import com.thomsonreuters.oa.filing.util.EdgarInput;


// TODO: Auto-generated Javadoc
//import west.common.Pair;

/**
 * Superclass for all DAO objects that work with an oracle database. OracleDAO
 * contains several convenience methods for working with the database.
 * 
 */
public abstract class OracleDAO {
	 
	EdgarInput input=new EdgarInput();

	/** The Constant logger. */
	static final Logger logger = Logger.getLogger(
			OracleDAO.class); 
	
	OracleDAO Connect;

	/** The simple oca date from string format. */
	protected static final String theSimpleOcaDateFromStringFormat = "yyyy-MM-dd HH:mm:ss";
    
    /** The oracle format. */
    protected static final String theOracleFormat = "YYYY-MM-DD HH24:MI:SS";
    
    /** The Constant SERVER_TIMEZONE. */
    protected static final TimeZone SERVER_TIMEZONE = TimeZone.getTimeZone("America/Chicago");
        
	/**
	 * Property conn. Connection used to interact with an oracle database.
	 */
    
    
    
	private Connection myConn;
	
	/**
	 * Sets the conn.
	 *
	 * @param theConn the new conn
	 */
	public void setConn(Connection theConn) {
		myConn = theConn;
	}
	
	/**
	 * Gets the conn.
	 *
	 * @return the conn
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public Connection getConn() throws ClassNotFoundException, SQLException, IOException {
		return DBConnection();
	}
	
	/**
	 * Instantiates a new oracle dao.
	 */
	public OracleDAO() {
		super();
	}
	
	
	public Connection DBConnection() throws SQLException, IOException, ClassNotFoundException{
		
		
		Properties prop = new Properties();
	    String propFileName = "db-"+input.environment+".properties";

	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	    prop.load(inputStream);
	    if (inputStream == null) {
	        throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
	    }


	    // get the property value and print it out
	   
	   
	Class.forName("oracle.jdbc.driver.OracleDriver");

	 String URL = prop.getProperty("datasource.url"); 

	 myConn = DriverManager.getConnection(URL, prop.getProperty("datasource.user"), prop.getProperty("datasource.password"));
	 
	//Connect.setConn(myConn);
	 return myConn;
		
	}
	
	
	
	/**
	 * Instantiates a new oracle dao.
	 *
	 * @param theConnection the the connection
	 */
	public OracleDAO(Connection theConnection) {
		super();
		setConn(theConnection);
	}
	
	/**
	 * Simple helper-method to return an oracle-formatted sql statement
	 * to process a uuid.
	 * 
	 * @param uuid 	uuid to process. "I" prefix is optional. uuid is required. 
	 * @return "HEXTORAW('uuid')" where uuid is the uuid without any "I" prefix
	 */
	public static String oracleUUID(String uuid) {
		String formattedUUID = uuid;
		if(uuid.startsWith("I")) {
			formattedUUID = uuid.substring(1);
		}
		return ("HEXTORAW('" + formattedUUID + "')");
	}
	
	/**
	 * Simple helper-method to format strings for use in oracle SQL statements.
	 * @param theValue 	the string to format. Nullable. If null, a null string 
	 * 					string is returned
	 * @return a quoted string
	 */
	public static String oracleString(String theValue) {
        return theValue == null ? null : "'" + theValue.replaceAll("'", "''") + "'";
    }
	
	/**
	 * Simple helper-method that takes a boolean and returns a string for use
	 * in an oracle varchar(1) column. 
	 * @param value the boolean value to format for oracle
	 * @return 'Y' if value == true, else 'N'
	 */
	public static String oracleBoolean(boolean value) {
		if( value ) {
			return "'Y'";
		} else {
			return "'N'";
		}
	}

	/**
	 * Formats a specified Date object in the Oracle date format used by the
	 * system and encloses the date in an Oracle TO_DATE() function call.
	 *
	 * @param theDate the the date
	 * @return the formatted date string inside a TO_DATE() function call
	 */
	public static String oracleDate(Date theDate) {
		SimpleDateFormat javaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    javaDateFormat.setTimeZone(SERVER_TIMEZONE);
	    
		return theDate == null ? null : "TO_DATE('"
				+ javaDateFormat.format(theDate) + "','" + theOracleFormat
				+ "')";
	}
	
	/**
	 * Given a sql statement string and a collection of parameters, 
	 * executeUpdate correctly places the parameters in the
	 * sql statement and executes a sql update. Note that this method may be
	 * used for inserts, updates, and deletes.
	 * 
	 * @param sql			string representing a sql update statement. This
	 * 						statement should have question marks wherever a
	 * 						parameter should be inserted.
	 * 						Example: "delete from document where id = ?"
	 * 						Non-nullable.
	 * @param parameters	zero or more parameters to subsititue into the
	 * 						statement. parameters.size must equal the number
	 * 						of placeholders (question marks) in statement. 
	 * 						Parameters may have the type: 
	 * 						<ul>
	 * 						<li>String (escaped with single quotes)</li>
	 * 						<li>Date	(Converted to oracle DATE)</li>
	 * 						<li>boolean	(Converted to 'Y' for true, or 'N' 
	 * 								for false)</li>
	 * 						</ul>
	 * 						For all other types, parameter.toString() is used.
	 * 						Note that parameters are not required, you can use
	 * 						executeUpdateStatement to execute a statement with
	 * 						no parameters. 
	 * 						For example: <code>executeUpdate(
	 * 						"delete from table")</code>. 
	 * 						Or if you choose to build the parameters into the 
	 * 						sql yourself, you may also use this method.
	 * 						For example:
	 * 						<code>executeUpdate("delete from table 
	 * 						where id = 'test')</code>
	 * @return the number of rows updated as a result of the update statement.
	 * @throws Exception an error occurs executing the statement on the
	 * 						database, or sql was not wellformed.
	 */
	public int executeUpdate(String sql, 
			final Object... parameters) throws Exception {
		if( logger.isDebugEnabled() ) {
			logger.debug("in executeUpdateStatement(), statement=" +
					sql);
		}
		
		Statement stmt = null; 
		int rowsUpdated = 0;
		try {
			stmt = getConn().createStatement();
			rowsUpdated = stmt.executeUpdate(bindVariables(sql, parameters));
		} catch (SQLException e) {
			throw new Exception(e);
		} finally {
			if( stmt != null ) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}

		logger.debug("out executeUpdateStatement()");
		
		return rowsUpdated;
	}
		
	/**
	 * Given a sql statement string and a collection of parameters,
	 * executeQuery correctly places the parameters in the
	 * sql statement and executes a sql query. Note that this method may be
	 * used for selects.
	 *
	 * @param <T> the generic type
	 * @param sql 		string representing a sql select statement. This
	 * statement should have question marks wherever a
	 * parameter should be inserted.
	 * Example: "select * from document where id = ?"
	 * Non-nullable.
	 * @param rowMapper 	RowMapper describing how to convert each row in the
	 * resultset to an object. Non-nullable.
	 * @param parameters zero or more parameters to subsititue into the
	 * statement. parameters.size must equal the number
	 * of placeholders (question marks) in statement.
	 * Parameters may have the type:
	 * <ul>
	 * <li>String (escaped with single quotes)</li>
	 * <li>Date	(Converted to oracle DATE)</li>
	 * <li>boolean	(Converted to 'Y' for true, or 'N'
	 * for false)</li>
	 * </ul>
	 * For all other types, parameter.toString() is used.
	 * Note that parameters are not required, you can use
	 * executeQuery to execute a statement with
	 * no parameters.
	 * For example: <code>executeQuery(
	 * "select name from table")</code>.
	 * Or if you choose to build the parameters into the
	 * sql yourself, you may also use this method.
	 * For example:
	 * <code>executeQuery("select name from table
	 * where id = 'test')</code>
	 * @return 	a list of objects that match the specified query, or an empty
	 * list if no such objects could be found.
	 * @throws Exception an error occurs executing the statement on the
	 * database, or sql was not wellformed.
	 */
	public <T> List<T> executeQuery( String sql,
			final ParameterizedRowMapper<T> rowMapper,
			final Object... parameters)
		throws Exception {
		
		if( logger.isDebugEnabled() ) {
			logger.debug("in executeQuery(), statement=" +
					sql);
		}
		
		Statement stmt = null; 
		ResultSet rs = null;
		List<T> retVal = new LinkedList<T>();
		
		Connection con=null;
		
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
			
			
		con =getConn();
			stmt = con.createStatement();
			rs = stmt.executeQuery(bindVariables(sql, parameters));
			int index=0;
			while(rs.next()) {
				T object = rowMapper.mapRow(rs, index);
				retVal.add(object);
				index++;
			}
		} catch (SQLException e) {
			throw new Exception(e);
		} finally {
			if( rs != null ) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
			if(con!=null){
				con.close();
			}
			if( stmt != null ) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}

		logger.debug("out executeQuery()");
		return retVal;
	}
	
	
	
	public FilingInput fetchDBDataForUpdate(FilingInput FilingData, String sql,final Object... parameters)
			throws Exception {
			
			if( logger.isDebugEnabled() ) {
				logger.debug("in executeQuery(), statement=" +
						sql);
			}
			
			Statement stmt = null; 
			ResultSet rs = null;
			Connection con=null;
			
			try {
				//Class.forName("oracle.jdbc.driver.OracleDriver");
				//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
				System.out.println("");
				con =getConn();
				
				stmt = con.createStatement();
				System.out.println("Execute query...");
				rs = stmt.executeQuery(bindVariables(sql, parameters));
				while(rs.next()) {
					
					FilingData.setCik(rs.getLong("id_value"));
					
					FilingData.setConformedName(rs.getString("org_official_name"));
					
				}
			} catch (SQLException e) {
				throw new Exception(e);
			} finally {
				if( rs != null ) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
				if(con!=null){
					con.close();
				}
				if( stmt != null ) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
			}

			logger.debug("out executeQuery()");
			return FilingData;
		}
	
	
	
	public FilingInput FetchingDataRequestCmd( FilingInput fileData,String sql,String column,final Object... parameters)
			throws Exception {
			
			if( logger.isDebugEnabled() ) {
				logger.debug("in executeQuery(), statement=" +
						sql);
			}
			
			Statement stmt = null; 
			ResultSet rs = null;
			Connection con=null;
			List<RequestToll> retVal = new ArrayList<RequestToll>();
			
			try {
				//Class.forName("oracle.jdbc.driver.OracleDriver");
				//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
				con =getConn();
				stmt =con.createStatement();
				rs = stmt.executeQuery(bindVariables(sql, parameters));
				while(rs.next()) {
					
					fileData.setRequestCmt((rs.getString("requester_comments")));
				
					
				}
			} catch (SQLException e) {
				throw new Exception(e);
			} finally {
				if( rs != null ) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
				if(con!=null){
					con.close();
				}
				if( stmt != null ) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
			}

			logger.debug("out executeQuery()");
			return fileData;
		}
	
	
	
	
	
	public String executeQueryfetchDataFromAscollect( FilingInput fileData,String sql,String column,final Object... parameters)
		throws Exception {
		
		if( logger.isDebugEnabled() ) {
			logger.debug("in executeQuery(), statement=" +
					sql);
		}
		
		Statement stmt = null; 
		ResultSet rs = null;
		Connection con=null;
//		List<filingInput> retVal = new ArrayList<filingInput>();

		String id = null;
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
			con =getConn();
			stmt =con.createStatement();
			
			
			rs = stmt.executeQuery(bindVariables(sql, parameters));
			while(rs.next()) {
				id = rs.getString(column);
				
				
				
				/*filingInput DBFilingdataList = new filingInput();
				
				DBFilingdataList.setEntityId(rs.getString("ife_entityID"));
				DBFilingdataList.setIsAutoLock(rs.getString("is_locked_for_autocreate"));
				DBFilingdataList.setRequestId(rs.getString("request_id"));
			
				retVal.add(DBFilingdataList);*/
				
			}
		} catch (SQLException e) {
			throw new Exception(e);
		} finally {
			if( rs != null ) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
			if(con!=null){
				con.close();
			}
			if( stmt != null ) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}

		logger.debug("out executeQuery()");
		return id;
	}
	
	public long executeQueryTofetchDataToValidateEntity( FilingInput fileData ,String sql,final Object... parameters)
			throws Exception {
			
			Statement stmt = null; 
			ResultSet rs = null;
			Connection con=null;
			long  retVal = 0;
			
			try {
				//Class.forName("oracle.jdbc.driver.OracleDriver");
				//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
				con =getConn();
				stmt =con.createStatement();
				rs = stmt.executeQuery(bindVariables(sql, parameters));
				while(rs.next()) {
					//FilingInput DBFilingdataList = new FilingInput();
					
					fileData.setHqValid((rs.getLong("org_hq_is_inf_valid")));
					retVal=fileData.getHqValid();
					
				}
			} catch (SQLException e) {
				throw new Exception(e);
			} finally {
				if( rs != null ) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
				if(con!=null){
					con.close();
				}
				if( stmt != null ) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
			}

			logger.debug("out executeQuery()");
			return retVal;
		}
		
		
	public FilingInput FilingRuleMsgFromEdgerErrorLog( FilingInput fileData,String sql,long column,final Object... parameters)
			throws Exception {
			
			Statement stmt = null; 
			ResultSet rs = null;
			Connection con=null;
			String  filingRule = null;
			
			try {
				//Class.forName("oracle.jdbc.driver.OracleDriver");
				//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
				con =getConn();
				stmt =con.createStatement();
				rs = stmt.executeQuery(bindVariables(sql, parameters));
				while(rs.next()) {
					fileData.setFilingRuleMsg(rs.getString("filings_rule_msg"));
				}
			} catch (SQLException e) {
				throw new Exception(e);
			} finally {
				if( rs != null ) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
				if(con!=null){
					con.close();
				}
				if( stmt != null ) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.warn(e);
					}
				}
			}

			logger.debug("out executeQuery()");
			return fileData;
		}	
	
	
	
	

	/**
	 * <p>Given a sql statement string and a collection of parameters,
	 * executeUniqueQuery correctly places the parameters in the
	 * sql statement and executes a sql query that returns a set of unique
	 * objects. Note that the main difference between this method and
	 * <code>executeQuery</code> is that <code>executeQuery</code> returns a
	 * list while <code>executeUniqueQuery</code> returns a set.</p>
	 * 
	 * <p>Note that this method may be used for selects where no duplicate objects
	 * are returned and order is not important.</p>
	 *
	 * @param <T> the generic type
	 * @param sql 		string representing a sql select statement. This
	 * statement should have question marks wherever a
	 * parameter should be inserted.
	 * Example: "select * from document where id = ?"
	 * Non-nullable.
	 * @param rowMapper 	RowMapper describing how to convert each row in the
	 * resultset to an object. Non-nullable.
	 * @param parameters zero or more parameters to subsititue into the
	 * statement. parameters.size must equal the number
	 * of placeholders (question marks) in statement.
	 * Parameters may have the type:
	 * <ul>
	 * <li>String (escaped with single quotes)</li>
	 * <li>Date	(Converted to oracle DATE)</li>
	 * <li>boolean	(Converted to 'Y' for true, or 'N'
	 * for false)</li>
	 * </ul>
	 * For all other types, parameter.toString() is used.
	 * Note that parameters are not required, you can use
	 * executeQuery to execute a statement with
	 * no parameters.
	 * For example: <code>executeQuery(
	 * "select name from table")</code>.
	 * Or if you choose to build the parameters into the
	 * sql yourself, you may also use this method.
	 * For example:
	 * <code>executeQuery("select name from table
	 * where id = 'test')</code>
	 * @return 	a list of objects that match the specified query, or an empty
	 * list if no such objects could be found.
	 * @throws Exception an error occurs executing the statement on the
	 * database, or sql was not wellformed.
	 */
	
	/**
	 * <p>Given a sql statement string and a collection of parameters,
	 * executeQueryForObject correctly places the parameters in the
	 * sql statement and executes a sql query that returns a single object,
	 * or null if the query returned 0 results. </p>
	 * 
	 * <p>This method is useful for executing select count(*) statements</p>
	 *
	 * @param <T> the generic type
	 * @param sql 		string representing a sql select statement. This
	 * statement should have question marks wherever a
	 * parameter should be inserted.
	 * Example: "select ? from document where id = ?"
	 * Non-nullable.
	 * @param rowMapper 	RowMapper describing how to convert each row in the
	 * resultset to an object. Non-nullable.
	 * @param parameters zero or more parameters to subsititue into the
	 * statement. parameters.size must equal the number
	 * of placeholders (question marks) in statement.
	 * Parameters may have the type:
	 * <ul>
	 * <li>String (escaped with single quotes)</li>
	 * <li>Date	(Converted to oracle DATE)</li>
	 * <li>boolean	(Converted to 'Y' for true, or 'N'
	 * for false)</li>
	 * </ul>
	 * For all other types, parameter.toString() is used.
	 * Note that parameters are not required, you can use
	 * executeQueryForObject to execute a statement with
	 * no parameters.
	 * @return 	the object that matches the query or null if no such object
	 * exists.
	 * @throws Exception an error occurs executing the statement on the
	 * database, or sql was not wellformed.
	 */
	public <T> T executeQueryForObject( String sql,
			final ParameterizedRowMapper<T> rowMapper,
			final Object... parameters) throws Exception {
		T retVal = null;
		
		if( logger.isDebugEnabled() ) {
			logger.debug("in executeQueryForObject(), statement=" +
					sql);
		}
		
		List<T> results = executeQuery(sql, rowMapper, parameters);
		if( results.size() > 1 ) {
			throw new Exception("Query [" + sql + "] was expected to " +
					"return 0 or 1 rows, but " + results.size() + 
					" were returned");
		}
		
		if( results.size() > 0 ) {
			retVal = results.get(0);
		} 

		logger.debug("out executeQueryForObject()");
		return retVal;
	}
	
	
	/**
	 * <p>Given a sql statement string and a collection of parameters,
	 * executeQueryForString correctly places the parameters in the
	 * sql statement and executes a sql query that returns a string.</p>
	 *
	 * @param sql 		String representing a sql select statement. This
	 * statement should have question marks wherever a
	 * parameter should be inserted.
	 * Example: "select pub_authority from pubid_exclude where pub_id = ?"
	 * Non-nullable.
	 * @param parameters zero or more parameters to substitute into the
	 * statement.  parameters.size must equal the number
	 * of placeholdes (question marks) in statement.
	 * Parameters may have the type:
	 * <ul>
	 * <li>String (escaped with single quotes)</li>
	 * <li>Date	(Converted to oracle DATE)</li>
	 * <li>boolean	(Converted to 'Y' for true, or 'N'
	 * for false)</li>
	 * </ul>
	 * For all other types, parameter.toString() is used.
	 * Note that parameters are not required, you can use
	 * executeQueryForString to execute a statement with
	 * no parameters.
	 * @return the string result returned from the query, or null if no result
	 * was returned.
	 * @throws Exception an error occurs executing the statement on the
	 * database, sql was not wellformed, or more than one
	 * row was returned by the query.
	 */
	
	
	/**
	 * <p>Given a sql statement string and a collection of parameters,
	 * executeQueryForInt correctly places the parameters in the
	 * sql statement and executes a sql query that returns an integer. </p>
	 * 
	 * <p>Note that this method should be used only for queries that are
	 * expected to return exactly 0 or 1 results.
	 *
	 * @param sql 		string representing a sql select statement. This
	 * statement should have question marks wherever a
	 * parameter should be inserted.
	 * Example: "select count(*) from document where id = ?"
	 * Non-nullable.
	 * @param parameters zero or more parameters to subsititue into the
	 * statement. parameters.size must equal the number
	 * of placeholders (question marks) in statement.
	 * Parameters may have the type:
	 * <ul>
	 * <li>String (escaped with single quotes)</li>
	 * <li>Date	(Converted to oracle DATE)</li>
	 * <li>boolean	(Converted to 'Y' for true, or 'N'
	 * for false)</li>
	 * </ul>
	 * For all other types, parameter.toString() is used.
	 * Note that parameters are not required, you can use
	 * executeQueryForInt to execute a statement with
	 * no parameters.
	 * @return 	the integer result returned from the query, or 0 if no such
	 * result could be found.
	 * @throws Exception an error occurs executing the statement on the
	 * database, or sql was not wellformed.
	 */
	public int executeQueryForInt( String sql,
			final Object... parameters) throws Exception {		
		if( logger.isDebugEnabled() ) {
			logger.debug("in executeQueryForInt(), statement=" +
					sql);
		}
		
		Integer integerValue = executeQueryForInteger(sql, parameters);
		return integerValue == null ? 0 : integerValue;
	}
	
	
	/**
	 * Similiar to executeQueryForInt, but returns a null if no matching rows.
	 *
	 * @param sql the sql
	 * @param parameters the parameters
	 * @return the integer
	 * @throws DAOException the dAO exception
	 */
	public Integer executeQueryForInteger( String sql,
			final Object... parameters) throws Exception {		
		if( logger.isDebugEnabled() ) {
		///	System.out.println("Execute query...");
			logger.debug("in executeQueryForInteger(), statement=" +
					sql);
		}
		
		Integer retVal = null;
		Statement stmt = null;
		ResultSet rs = null;
		Connection con =null;
		
		try {
			//Class.forName("oracle.jdbc.driver.OracleDriver");
			con =getConn();
			
			//System.out.println("Entering to connect DB..");
			//con  = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.43.152.190)(PORT=1521)))(CONNECT_DATA=(SERVER = DEDICATED)(SERVICE_NAME=OATEST.tr.com)))","df","df1");
			stmt = con.createStatement();
			//System.out.println("Execute query...");
			rs = stmt.executeQuery(bindVariables(sql, parameters));
			if (rs.next()) {
				retVal = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new Exception(e);
		} finally {
			if( rs != null ) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
			if(con!=null){
				con.close();
			}
			if( stmt != null ) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}

		logger.debug("out executeQueryForInteger()");
		return retVal;
	}

	
	
	
	public static String bindVariables(String sql, final Object... parameters) {
		int previousEnd = 0;
		if(parameters != null){
		for (Object parameter : parameters) {
			int offset = sql.indexOf( '?', previousEnd );
			String replacementText = getReplacementText( parameter );
			sql = replace( offset, offset + 1, sql, 
					replacementText );
			previousEnd = offset+replacementText.length();
		}
		}
		logger.debug("SQL: " + sql);
		//System.out.println("SQL: " + sql);
		return sql;
	}
	
	/**
	 * Return replacement object formatted for use in a sql statement.
	 *
	 * @param replacement the replacement
	 * @return the replacement text
	 */
    public static String getReplacementText( Object replacement ) {
        if ( replacement == null )
        {
            return "NULL";
        } else if ( replacement instanceof String ) {
        	return oracleString((String)replacement);
        } else if ( replacement instanceof Date ) {
        	return oracleDate((Date)replacement);
        } else if ( replacement instanceof Boolean ) {
        	return oracleBoolean((Boolean)replacement);
        } else {
            return replacement.toString();
        }
    }
    
    /**
     * Replaces the characters in a substring of the specified String
     * with characters in the specified replacement String.
     *
     * @param start The beginning index, inclusive
     * @param end The ending index, exclusive
     * @param str String that will be modified
     * @param replacement String that will replace previous contents
     * @return The modified string
     */
    private static String replace( int start, int end, String str, String replacement ) {
        StringBuffer buffer = new StringBuffer( str.substring( 0, start ) );

        buffer.append( replacement );
        buffer.append( str.substring( end ) );

        return buffer.toString();
    }
    
    /**
	 * Describes how to create a string from a resultset whose rows
	 * contain a single string column.
	 */
	protected class StringMapper implements ParameterizedRowMapper<String> {
		
		/**
		 * Map row.
		 *
		 * @param rset the rset
		 * @param rown the rown
		 * @return the string
		 * @throws SQLException the sQL exception
		 */
		public String mapRow(ResultSet rset, int rown) throws SQLException {
			return rset.getString(1);
		}
	}
	
}
