package com.ericsson.vo;

public class DeviceVO {
	String deviceID;
	String qrCode;
	String deviceRegistrationTimestamp;

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getDeviceRegistrationTimestamp() {
		return deviceRegistrationTimestamp;
	}

	public void setDeviceRegistrationTimestamp(String deviceRegistrationTimestamp) {
		this.deviceRegistrationTimestamp = deviceRegistrationTimestamp;
	}

	@Override
	public String toString() {
		return "DeviceDTO [deviceID=" + deviceID + ", qrCode=" + qrCode + ", deviceRegistrationTimestamp="
				+ deviceRegistrationTimestamp + "]";

	}
}
