package com.ericsson.vo;

public class AlappVO {
	String alappId;
	String qrCode;
	String alappRegistrationStatus;

	public String getAlappId() {
		return alappId;
	}

	public void setAlappId(String alappId) {
		this.alappId = alappId;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getAlappRegistrationStatus() {
		return alappRegistrationStatus;
	}

	public void setAlappRegistrationStatus(String alappRegistrationStatus) {
		this.alappRegistrationStatus = alappRegistrationStatus;
	}

	@Override
	public String toString() {
		return "AlappDTO [alappId=" + alappId + ", qrCode=" + qrCode + ", alappRegistrationStatus="
				+ alappRegistrationStatus + "]";
	}
}
