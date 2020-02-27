package com.ericsson.response.dto;

public class GenerateQrResponseDTO {
	private String qr_code;
	private String requester_device_id;
	private String qr_code_url;

	
	public String getQr_code() {
		return qr_code;
	}
	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}
	public String getRequester_device_id() {
		return requester_device_id;
	}
	public void setRequester_device_id(String requester_device_id) {
		this.requester_device_id = requester_device_id;
	}
	public String getQr_code_url() {
		return qr_code_url;
	}
	public void setQr_code_url(String qr_code_url) {
		this.qr_code_url = qr_code_url;
	}
	

	
}
