package com.thomsonreuters.oa.filing.dataAccessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DBDataAccessor extends JdbcDaoSupport {
		
	public <T> List<T> executeQueryForList( 
			String sqlString,final RowMapper<T> rowMapper){
		 List<T> retVal = (List<T>) getJdbcTemplate().query(sqlString,rowMapper);
			return  retVal;
	}
	
	
	
	public <T> List<T> executeQueryForList( 
			String sqlString,final RowMapper<T> rowMapper,Object... params){
		 List<T> retVal = (List<T>) getJdbcTemplate().query(sqlString,params,rowMapper);
			return  retVal;
	}
	
	public <T> List<T> executeQueryForList( 
			String sqlString,final Class<T> classType,Object... params){
		 List<T> retVal = (List<T>) getJdbcTemplate().queryForList(sqlString, classType, params);
			return  retVal;
	}
	
	public <T> T executeQueryForObject( 
			String sqlString,final RowMapper<T> rowMapper){
		T retVal = (T) getJdbcTemplate().queryForObject(sqlString,rowMapper);
			return  retVal;
	}
	
	public <T> T executeQueryForObject( 
			String sqlString,final RowMapper<T> rowMapper,Object... params){
		T retVal = (T) getJdbcTemplate().queryForObject(sqlString,params,rowMapper);
			return  retVal;
	}
	
	public <T> Object executeQueryForObject( 
			String sqlString,Class<T> classType){
		Object retVal = (T) getJdbcTemplate().queryForObject(sqlString,classType);
			return  retVal;
	}
		
	public <T> Object executeQueryForObject( 
			String sqlString,Class<T> classType,Object... params){
		Object retVal = (T) getJdbcTemplate().queryForObject(sqlString,classType,params);
			return  retVal;
	}
	
	public int executeQueryForInt( 
			String sqlString,Object... params){
		int retVal =  getJdbcTemplate().queryForInt(sqlString,params);
			return  retVal;
	}
	
	public int executeQueryForInt( 
			String sqlString){
		int retVal =  getJdbcTemplate().queryForInt(sqlString);
			return  retVal;
	}
	
	public long executeQueryForLong( 
			String sqlString,Object... params){
		long retVal =  getJdbcTemplate().queryForLong(sqlString,params);
			return  retVal;
	}
	
	public long executeQueryForLong( 
			String sqlString){
		long retVal =  getJdbcTemplate().queryForLong(sqlString);
			return  retVal;
	}
	
	public List<Map<String,Object>> executeQueryForMapList( 
			String sqlString,Object... params){
		 List<Map<String,Object>> retVal = executeQueryForList(sqlString, new RowMapMapper(), params);
			return  retVal;
	}
	
	class RowMapMapper  implements RowMapper<Map<String,Object>> {
		HashSet<String> myColumns = null;
		@Override
		public Map<String,Object> mapRow(ResultSet resultSet, int arg) throws SQLException {
			if (myColumns == null) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				myColumns = new LinkedHashSet<String>();
				
				for (int index = 1; index <= metaData.getColumnCount(); index++) {
					myColumns.add((String) metaData.getColumnName(index));
				}
			}
			
			Iterator<String> itr = myColumns.iterator();
			Map<String, Object> row = new HashMap<String, Object>();
			while (itr.hasNext()) {
				String column = itr.next();
				row.put( column, resultSet.getObject(column));
			}
			return row;
		}
	}
	
	
}
