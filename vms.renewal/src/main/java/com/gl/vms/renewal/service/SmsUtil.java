package com.gl.vms.renewal.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.vms.renewal.config.Config;



@Service
public class SmsUtil {

	private Logger log = LogManager.getRootLogger();

	@Autowired
	private Config config;

	public boolean sendSMS(String msisdn, String text, String packName, int packPrice){

		boolean status = false;
		try {
			log.info("SendSMS msisnd=" + msisdn + ",Text=" + text);

			if (!(msisdn.startsWith("93")))
				msisdn = "93" + msisdn;

			String date = getTodatDate();
			text = text.replaceAll("<DATE>", date );
			text = text.replaceAll("<PACK>", packName );
			text = text.replaceAll("<PRICE>", "" + packPrice );

			String enText = URLEncoder.encode(text, "UTF-8");
			String enMsisdn = URLEncoder.encode(msisdn, "UTF-8");

			String url = config.getSubmitUrl();

			url = url.replaceAll("<TO>", enMsisdn);
			url = url.replaceAll("<TEXT>", enText);
			log.info("MESSAGE SENT:"+url);
			String resp = callURL(url);
			log.info(callURL(url));
			resp = resp.toLowerCase();
			if (resp.indexOf("accept") != -1)
				status = true;
			

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return status;
	}

	public String callURL(String urlString) {
		String inputResponse = "";
		try {
			
			log.info(urlString);
			URL url = new URL(urlString);
			URLConnection urlConn = url.openConnection();
			urlConn.setConnectTimeout(30 * 1000);
			urlConn.setReadTimeout(30 * 1000);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null)
				inputResponse = inputResponse + inputLine;
			inputReader.close();
			
		} catch (Exception exp) {
			inputResponse = exp.getMessage();
		}
		log.info(inputResponse);
		return inputResponse;

	}

	public String getTodatDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
}
