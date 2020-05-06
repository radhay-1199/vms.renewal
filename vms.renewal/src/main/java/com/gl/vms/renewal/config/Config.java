package com.gl.vms.renewal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Config {

	@Autowired
	private Environment env;	
	@Value("${cerebro.login.username}")
	private String cerebroUserName;
	@Value("${cerebro.login.password}")
	private String cerebroPassword;
	@Value("${cerebro.service.renewal.api}")
	private String cerebroServiceRenewalUrl;
	@Value("${vms.service.id}")
	private String serviceId;
	@Value("${sub.renewal.msg.text}")
	private String renewalMsgText;
	@Value("${sms.submit.url}")
	private String submitUrl;
	
	
	public String getSubmitUrl() {
		return submitUrl;
	}
	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}
	public Environment getEnv() {
		return env;
	}
	public void setEnv(Environment env) {
		this.env = env;
	}
	public String getCerebroUserName() {
		return cerebroUserName;
	}
	public void setCerebroUserName(String cerebroUserName) {
		this.cerebroUserName = cerebroUserName;
	}
	public String getCerebroPassword() {
		return cerebroPassword;
	}
	public void setCerebroPassword(String cerebroPassword) {
		this.cerebroPassword = cerebroPassword;
	}
	public String getCerebroServiceRenewalUrl() {
		return cerebroServiceRenewalUrl;
	}
	public void setCerebroServiceRenewalUrl(String cerebroServiceRenewalUrl) {
		this.cerebroServiceRenewalUrl = cerebroServiceRenewalUrl;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getRenewalMsgText() {
		return renewalMsgText;
	}
	public void setRenewalMsgText(String renewalMsgText) {
		this.renewalMsgText = renewalMsgText;
	}

}
