package com.gl.vms.renewal.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CerebroRespValues {
	@JsonProperty("processingResultCode")
	private String processingResultCode;

	@JsonProperty("cbsResponse")
	private String cbsResponse;
	
	@JsonProperty("amountCharged")
	private Integer amountCharged;
	
	@JsonProperty("lastChargeTimestamp")
	private String lastChargeTimestamp;
	
	@JsonProperty("lastChargedAmount")
	private String lastChargedAmount;

	public String getProcessingResultCode() {
		return processingResultCode;
	}

	public void setProcessingResultCode(String processingResultCode) {
		this.processingResultCode = processingResultCode;
	}

	public String getCbsResponse() {
		return cbsResponse;
	}

	public void setCbsResponse(String cbsResponse) {
		this.cbsResponse = cbsResponse;
	}

	public Integer getAmountCharged() {
		return amountCharged;
	}

	public void setAmountCharged(Integer amountCharged) {
		this.amountCharged = amountCharged;
	}

	public String getLastChargeTimestamp() {
		return lastChargeTimestamp;
	}

	public void setLastChargeTimestamp(String lastChargeTimestamp) {
		this.lastChargeTimestamp = lastChargeTimestamp;
	}

	public String getLastChargedAmount() {
		return lastChargedAmount;
	}

	public void setLastChargedAmount(String lastChargedAmount) {
		this.lastChargedAmount = lastChargedAmount;
	}
	
}
