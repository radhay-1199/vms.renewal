package com.gl.vms.renewal.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gl.vms.renewal.model.VmsUser;

@Component
public class RenewalDb {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<VmsUser> getAllSubscribers(){ 
		String query = "Select msisdn,vms_users.pack_id,service_id,pack_price,next_renewal_date,pack_validity from vms_users inner join  vms_packs on vms_packs.pack_id = vms_users.pack_id where renewal_flag = 1 and next_renewal_date<=CURRENT_TIMESTAMP()";
		logger.info("query: "+query);
		 return jdbcTemplate.query(query,new ResultSetExtractor<List<VmsUser>>(){  
		    @Override  
		     public List<VmsUser> extractData(ResultSet rs) throws SQLException,  
		            DataAccessException {  
		      List<VmsUser> subscribedUsersDetailsList=new ArrayList<VmsUser>();
		      int i=0;
		        while(rs.next()){  
		        	VmsUser subscribedUsersDetails=new VmsUser(); 
		        	subscribedUsersDetails.setMsisdn(rs.getString("msisdn"));
		        	subscribedUsersDetails.setPackPrice(rs.getInt("pack_price"));
		        	subscribedUsersDetails.setServiceId(rs.getString("service_id"));
		        	String next_renewal_date1=rs.getString("next_renewal_date");
		        	String result=next_renewal_date1.substring(0, next_renewal_date1.length() - 2);
		        	subscribedUsersDetails.setNextRenewalDate(result);
		        	subscribedUsersDetails.setPackValidty(rs.getInt("pack_validity"));
		        	subscribedUsersDetails.setPackId(rs.getString("pack_id"));
		        	subscribedUsersDetailsList.add(subscribedUsersDetails);
		        	logger.info("Expired User "+(i++)+" :"+subscribedUsersDetails+"\n");
		        }  
		        logger.info("The size of the list is :"+subscribedUsersDetailsList.size());
		        return subscribedUsersDetailsList;  
		        }  
		    });  
	}

	public int updateUser(VmsUser vmsUsers) {
		try {
			if("P3".equals(vmsUsers.getPackId())) {
			logger.info("updating user details in vms_users table for renewal");
			String query="update vms_users set next_renewal_date=CURRENT_TIMESTAMP() + INTERVAL 30 DAY and pack_id='"+vmsUsers.getPackId()+"' where msisdn='"+vmsUsers.getMsisdn()+"'";
			logger.info("query: "+query);
			jdbcTemplate.execute(query);
			return 1;
			}
			else if("P2".equals(vmsUsers.getPackId())) {
					logger.info("updating user details in vms_users table for renewal");
					String query="update vms_users set next_renewal_date=CURRENT_TIMESTAMP() + INTERVAL 7 DAY and pack_id='"+vmsUsers.getPackId()+"' where msisdn='"+vmsUsers.getMsisdn()+"'";
					logger.info("query: "+query);
					jdbcTemplate.execute(query);
					return 2;
			}
		}catch(Exception e) {
			logger.info("exception while updating daily details in table com_subscribed_users_details table: "+e);
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	public int removeUser(VmsUser vmsUsers) {
		// TODO Auto-generated method stub
		try {
			logger.info("removing user for which msisdn not found in cerebro verify database");
			String query="DELETE from vms_users where msisdn='"+vmsUsers.getMsisdn()+"'";
			logger.info("query: "+query);
			jdbcTemplate.execute(query);
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("exception while removing user for which msisdn not found in cerebro verify database");
			e.printStackTrace();
			return 0;
		}
	}  
}
