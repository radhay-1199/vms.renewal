package com.gl.vms.renewal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.vms.renewal.db.RenewalDb;
import com.gl.vms.renewal.model.VmsUser;

@Service
public class VmsUsersService {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	RenewalDb renewalDb;
	
	public int renewalForSubscribedUsers(String msisdn,String packId) {
		VmsUser vmsUsers = new VmsUser(msisdn,packId);
		return renewalDb.updateUser(vmsUsers);
	}

	public int removalForSubscribedUsers(String msisdn) {
		VmsUser vmsUsers = new VmsUser(msisdn);
		return renewalDb.removeUser(vmsUsers);
	}
}
