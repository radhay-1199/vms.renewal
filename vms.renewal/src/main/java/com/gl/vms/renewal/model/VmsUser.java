package com.gl.vms.renewal.model;

import java.util.Date;


public class VmsUser
{
  private String msisdn;
  private String packId;
  private int status;
  private int hlrFlag;
  private Integer packPrice;
  private String serviceId;
  private Integer packValidty;
  private String nextRenewalDate;
  private String channel;
  
  public VmsUser() {}
  
  public VmsUser(String msisdn,String packId) {
	  this.msisdn=msisdn;
	  this.packId=packId;
  }
  
  public VmsUser(String msisdn) {
	  this.msisdn=msisdn;
  }
public String getMsisdn() {
	return msisdn;
}
public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
}
public String getPackId() {
	return packId;
}
public void setPackId(String packId) {
	this.packId = packId;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public int getHlrFlag() {
	return hlrFlag;
}
public void setHlrFlag(int hlrFlag) {
	this.hlrFlag = hlrFlag;
}
public Integer getPackPrice() {
	return packPrice;
}
public void setPackPrice(Integer packPrice) {
	this.packPrice = packPrice;
}
public String getServiceId() {
	return serviceId;
}
public void setServiceId(String serviceId) {
	this.serviceId = serviceId;
}
public Integer getPackValidty() {
	return packValidty;
}
public void setPackValidty(Integer packValidty) {
	this.packValidty = packValidty;
}
public String getNextRenewalDate() {
	return nextRenewalDate;
}
public void setNextRenewalDate(String nextRenewalDate) {
	this.nextRenewalDate = nextRenewalDate;
}
public String getChannel() {
	return channel;
}
public void setChannel(String channel) {
	this.channel = channel;
}
@Override
public String toString() {
	return "VmsUser [msisdn=" + msisdn + ", packId=" + packId + ", status=" + status + ", hlrFlag=" + hlrFlag
			+ ", packPrice=" + packPrice + ", serviceId=" + serviceId + ", packValidty=" + packValidty
			+ ", nextRenewalDate=" + nextRenewalDate + ", channel=" + channel + "]";
}


}