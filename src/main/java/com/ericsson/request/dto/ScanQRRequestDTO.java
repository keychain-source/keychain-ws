package com.ericsson.request.dto;

public class ScanQRRequestDTO {

	private String qr_code;
	private String requester_device_id;
	private String qr_code_status;

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

	public String getQr_code_status() {
		return qr_code_status;
	}

	public void setQr_code_status(String qr_code_status) {
		this.qr_code_status = qr_code_status;
	}

	@Override
	public String toString() {
		return "RegisterQRRequestDTO [qr_code=" + qr_code + ", requester_device_id=" + requester_device_id
				+ ", qr_code_status=" + qr_code_status + "]";
	}

}
