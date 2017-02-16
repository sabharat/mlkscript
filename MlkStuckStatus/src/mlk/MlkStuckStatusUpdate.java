package mlk;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mlk.db.DBHandler;

/*In Promo delivery update, status 3 is the intermediate status during change to status 4 or 5. 
 * But sometimes some loads stuck at status 3 without updating. We have to later find these loads and manually 
 * update them from status 3 to status 4 and 5 according to the previous service_type. 
 * This script has been created for checking the stuck loads and updating them to status 4 and 5 accordingly as required.

@author - Bharat Saini*/

public class MlkStuckStatusUpdate {
	static JdbcTemplate jdbcTemplate = null;

	public static void main(String[] args) {
	Logger lgr = Logger.getLogger(MlkStuckStatusUpdate.class);
	lgr.debug("Starting Application");
		DBHandler dbc = new DBHandler();
		jdbcTemplate = dbc.getJdbcTemplate();
		if (jdbcTemplate == null) {
			System.out.print("Unable to establish DB Connection. Exiting");
			return;
		}
		
		List<Integer> msisdnto5 = new ArrayList<Integer>();
		List<Integer> msisdnto4 = new ArrayList<Integer>();

		Map<Integer, String> status3map = getStuckRecordsStatus3();
		for (Map.Entry<Integer, String> entry : status3map.entrySet()) {
			String oldservicetype = getOldServiceType(entry.getKey());
			if (oldservicetype == null || oldservicetype == "") {
				msisdnto4.add(entry.getKey());
			} else if (oldservicetype.equalsIgnoreCase(entry.getValue())) {
				msisdnto5.add(entry.getKey());
			} else if (!oldservicetype.equalsIgnoreCase(entry.getValue())) {
				msisdnto4.add(entry.getKey());
			}
		}
       int status5updated=0;
       int status4updated=0;

		if (msisdnto5.size() > 0) {
			status5updated = updateToStatus5(msisdnto5);
		}
		if (msisdnto4.size() > 0) {
			status4updated = updateToStatus4(msisdnto4);
		}
		System.out.println("UPDATION INFO :::: Status5Updated="+status5updated+", Status4Updated="+status4updated);
		
	}

	private static Map<Integer, String> getStuckRecordsStatus3() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		String sql = "select SERVICE_TYPE, msisdn from prepaidmlk_owner.services"
				+ " where status ='3'and Expiry_date > sysdate() order by created_date desc";		
		
		 List<Service> listService = jdbcTemplate.query(sql, new RowMapper<Service>() {
			 
	            public Service mapRow(ResultSet result, int rowNum) throws SQLException {
	            	Service service = new Service();
	            	service.setServiceType(result.getString("SERVICE_TYPE"));
	            	service.setMsisdn(result.getInt("MSISDN"));	            	
	                return service;
	            }
	             
	        });
	         
	        for (Service aService : listService) {
	            map.put(aService.getMsisdn(), aService.getServiceType());
	        }
	        return map;
	}

	private static String getOldServiceType(Integer msisdn) {
		String oldServiceType=null;
		String sql = "select SERVICE_TYPE from prepaidmlk_owner.services where MSISDN=" + msisdn
				+ " AND STATUS=6 ORDER BY CREATED_DATE DESC LIMIT 1";
		
		 List<Service> listService = jdbcTemplate.query(sql, new RowMapper<Service>() {
			 
	            public Service mapRow(ResultSet result, int rowNum) throws SQLException {
	            	Service service = new Service();
	            	service.setServiceType(result.getString("SERVICE_TYPE"));
	                return service;
	            }
	             
	        });
	         
	        for (Service aService : listService) {
	           oldServiceType = aService.getServiceType();
	        }
		return oldServiceType;
	
	}

	// WILL UPDATE CONTENTS OF @msisdnto5 TO STATUS 5
	private static int updateToStatus5(List<Integer> msisdnto5) {
		int rowsAffected=0;
		String commaSeparated = join(msisdnto5, ",");
		String sql = "update prepaidmlk_owner.services set status = 5 where "
				+ "status = 3 and msisdn in ("+commaSeparated+")";
		
		rowsAffected=jdbcTemplate.update(sql);
			System.out.println("Executed for Status5 - "+commaSeparated);
			return rowsAffected;
		
	}

	// WILL UPDATE CONTENTS OF @msisdnto4 TO STATUS 4
	private static int updateToStatus4(List<Integer> msisdnto4) {
		int rowsAffected=0;
		String commaSeparated = join(msisdnto4, ",");
		String sql = "Update prepaidmlk_owner.services set status = 4, promo_delivery_count=0, "
				+ "last_promo_delivery_time= sysdate() where status = 3 and msisdn in ("+commaSeparated+")";
		rowsAffected = jdbcTemplate.update(sql);
			System.out.println("Executed for Status4 - "+commaSeparated);
			return rowsAffected;
	}

	public static String join(List list, String delim) {
		StringBuilder sb = new StringBuilder();
		Iterator iter = list.iterator();
		if (iter.hasNext())
			sb.append(iter.next());
		while (iter.hasNext()) {
			sb.append(delim);
			sb.append(iter.next());
		}
		return sb.toString();
	}
}
