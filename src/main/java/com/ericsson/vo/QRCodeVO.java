package com.ericsson.vo;

/**
 * SELECT TOP (1000) [QR_CODE] ,[QR_REGISTRATION_STATUS] ,[QR_CREATE_TIMESTAMP]
 * ,[QR_LAST_UPDATE_TIMESTAMP] ,[QR_STATUS] ,[USER_ID] FROM [dbo].[QR_DATA]
 * 
 * @author eshiban
 *
 */

public class QRCodeVO {
	String qrCode;
	String qrRegistrationStatus;
	String qrCreateTimestamp;
	String qrLastUpdatedTimestamp;
	String qrStatus;

	public String getQrStatus() {
		return qrStatus;
	}

	public void setQrStatus(String qrStatus) {
		this.qrStatus = qrStatus;
	}

	String userId;

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getQrRegistrationStatus() {
		return qrRegistrationStatus;
	}

	public void setQrRegistrationStatus(String qrRegistrationStatus) {
		this.qrRegistrationStatus = qrRegistrationStatus;
	}

	public String getQrCreateTimestamp() {
		return qrCreateTimestamp;
	}

	public void setQrCreateTimestamp(String qrCreateTimestamp) {
		this.qrCreateTimestamp = qrCreateTimestamp;
	}

	public String getQrLastUpdatedTimestamp() {
		return qrLastUpdatedTimestamp;
	}

	public void setQrLastUpdatedTimestamp(String qrLastUpdatedTimestamp) {
		this.qrLastUpdatedTimestamp = qrLastUpdatedTimestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "QRCodeVO [qrCode=" + qrCode + ", qrRegistrationStatus=" + qrRegistrationStatus + ", qrCreateTimestamp="
				+ qrCreateTimestamp + ", qrLastUpdatedTimestamp=" + qrLastUpdatedTimestamp + ", qrStatus=" + qrStatus
				+ ", userId=" + userId + "]";
	}

}
