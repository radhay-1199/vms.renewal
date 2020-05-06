package com.gl.vms.renewal.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gl.vms.renewal.bean.ApiResp;
import com.gl.vms.renewal.bean.CerebroRespValues;
import com.gl.vms.renewal.bean.ReportData;
import com.gl.vms.renewal.config.Config;
import com.gl.vms.renewal.config.Constants;
import com.gl.vms.renewal.db.RenewalDb;
import com.gl.vms.renewal.db.VmsReportRepository;
import com.gl.vms.renewal.model.VmsUser;
import com.gl.vms.renewal.service.SmsUtil;
import com.gl.vms.renewal.service.VmsUsersService;

@Controller
public class MainController {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	RenewalDb renewalDb;
	@Autowired
	private Config config;
	@Autowired
	private SmsUtil smsUtil;
	@Autowired
	private VmsReportRepository vmsReportRepo;
	@Autowired VmsUsersService vmsUsersService;
	
	@RequestMapping(value = "/service-renewal", method = RequestMethod.GET)
	@ResponseBody
	public String vmsRenewalRequest(@RequestParam(value = "channel", defaultValue = "IVR",required = false) String channel) {
		try {
			logger.info("getting list of subscribed users for renewal");
			List<VmsUser> list=renewalDb.getAllSubscribers();
			int LowBal=0;
			int Successful=0;
			int PrematureRenewal=0;
			int RegistrationNotFound=0;
			logger.info("saving user details inside the list"+list);
			logger.info("The msisdn for renewal are as follows");
			for(VmsUser temp : list) {
				logger.info(temp.getMsisdn()+"\n");
			}
			for(VmsUser temp : list) {
					String uuid = getUUID();
					String cerebroServiceRenewal=RenewalURL(uuid,temp.getMsisdn(),temp.getPackPrice(),temp.getNextRenewalDate(),temp.getPackValidty());
					ApiResp resp = submitRequest(cerebroServiceRenewal);
					logger.info(resp.toString());
					if (resp.getRespCode() == 200) {
						CerebroRespValues cbValue = new ObjectMapper().readValue(resp.getRespText(), CerebroRespValues.class);
						if ("0".equals(cbValue.getProcessingResultCode())) {
							LowBal++;
							if("P3".equals(temp.getPackId())) {
								logger.info("Low Balance for pack P3 trying for P2");
								String cerebroServiceRenewal1=RenewalURL(uuid,temp.getMsisdn(),15,temp.getNextRenewalDate(),7);
								ApiResp resp1 = submitRequest(cerebroServiceRenewal1);
								logger.info(resp1.toString());
								if (resp1.getRespCode() == 200) {
									CerebroRespValues cbValue1 = new ObjectMapper().readValue(resp.getRespText(), CerebroRespValues.class);
									if ("0".equals(cbValue1.getProcessingResultCode())) {
										LowBal++;
										logger.info("Low Balance for pack P2");
										vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
												channel, "Low Balance", uuid));
										logger.info("Response To IVR For Low Balance |" + Constants.LOW_BALANCE);
									}else if("1".equals(cbValue1.getProcessingResultCode())) {
										Successful++;
										smsUtil.sendSMS(temp.getMsisdn(), config.getRenewalMsgText(), temp.getPackId(),
												temp.getPackPrice());
										logger.info("Low Balance for pack P3 but renewal successful for pack P2");
										vmsUsersService.renewalForSubscribedUsers(temp.getMsisdn(),"P2");
										logger.info("Response To IVR For Success |" + Constants.SUCCESS);
									}else if("2".equals(cbValue1.getProcessingResultCode())) {
										PrematureRenewal++;
										vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
												channel, "Processing denied due to Premature Renewal Request", uuid));
										logger.info("Processing denied due to Premature Renewal Request");
									}else if("3".equals(cbValue1.getProcessingResultCode())) {
										RegistrationNotFound++;
										vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
												channel, "Processing denied due to Registration Not Found", uuid));
										vmsUsersService.removalForSubscribedUsers(temp.getMsisdn());
										logger.info("Processing denied due to Registration Not Found");
									}
								}else {
									vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0, temp.getChannel(),
											"Invalid State-" + resp.getRespCode(), uuid));
									logger.info("Response To IVR Invalid State |" + Constants.INVALID_STATE);
								}
							}else {
								logger.info("Low Balance for pack P2");
								vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
										channel, "Low Balance", uuid));
								logger.info("Response To IVR For Low Balance |" + Constants.LOW_BALANCE);
							}
						}else if("1".equals(cbValue.getProcessingResultCode())) {
							Successful++;
							smsUtil.sendSMS(temp.getMsisdn(), config.getRenewalMsgText(), temp.getPackId(),
									temp.getPackPrice());
							logger.info("Renewal for msisdn "+temp.getMsisdn()+"with pack P3");
							vmsUsersService.renewalForSubscribedUsers(temp.getMsisdn(),"P3");
							logger.info("Response To IVR For Success |" + Constants.SUCCESS);
						}else if("2".equals(cbValue.getProcessingResultCode())) {
							PrematureRenewal++;
							vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
									channel, "Processing denied due to Premature Renewal Request", uuid));
							logger.info("Processing denied due to Premature Renewal Request");
						}else if("3".equals(cbValue.getProcessingResultCode())) {
							RegistrationNotFound++;
							vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0,
									channel, "Processing denied due to Registration Not Found", uuid));
							vmsUsersService.removalForSubscribedUsers(temp.getMsisdn());
							logger.info("Processing denied due to Registration Not Found");
						}
					}else {
						vmsReportRepo.insertIntoReports(new ReportData(temp.getMsisdn(), Constants.SUB_REQ, 0, temp.getChannel(),
								"Invalid State-" + resp.getRespCode(), uuid));
						logger.info("Response To IVR Invalid State |" + Constants.INVALID_STATE);
					}
			}
			logger.info("Responses: Low Balance=>"+LowBal+" Successful=>"+Successful+" Premature Renewal=>"+PrematureRenewal+" Registration Not Found=>"+RegistrationNotFound);
		}catch (Exception e) {
			logger.error("Exception in Service Renewal Controller: "+e);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getUUID() {
		// UUID uuid = UUID.fromString("xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx");
		return UUID.randomUUID().toString();
	}
	
	public String RenewalURL(String uuid,String msisdn,Integer packPrice,String nextRenewalDate,Integer packValidity){
		
		String cerebroServiceRenewalUrl = config.getCerebroServiceRenewalUrl();
		try {
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "RenewalId=" + uuid;
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&MSISDN=" + msisdn;
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&ServiceId=" + config.getServiceId();
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&Username=" + config.getCerebroUserName();
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&Password=" + config.getCerebroPassword();
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&Renewal_Cost=" + packPrice;
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&Service_Expiration=" + URLEncoder.encode(nextRenewalDate, "UTF-8");
			cerebroServiceRenewalUrl = cerebroServiceRenewalUrl + "&Service_Duration=" + packValidity;		
			logger.info("final url: "+cerebroServiceRenewalUrl);
			return cerebroServiceRenewalUrl;
		} catch (Exception e) {
			logger.error("Exception while making renewal url: "+e);
			e.printStackTrace();
		}
		return "OK";
	}
	
	public ApiResp submitRequest(String url) {
		ApiResp resp = new ApiResp();
		resp.setRespCode(0);
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			//HttpClient httpClient = HttpClients.createDefault();
			HttpGet getRequest = new HttpGet(url);
			HttpResponse response = httpClient.execute(getRequest);
			int respCode = response.getStatusLine().getStatusCode();
			resp.setRespCode(respCode);
			if (respCode == 200) {
				resp.setRespText(EntityUtils.toString(response.getEntity()));
				logger.info("Response: "+resp.getRespText());
			} else {
				logger.info("Response Code=" + response.toString());
			}

		} catch (Exception exp) {
			resp.setRespCode(-1);
			exp.printStackTrace();
		}
		return resp;
	}
}
