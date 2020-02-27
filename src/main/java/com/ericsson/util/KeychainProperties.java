package com.ericsson.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class KeychainProperties {
	
	@Value("${qrcodes.path}")
	private String qrCodePath;
	
	@Value("${qrcode.url}")
	private String qrCodeURL;

	public String getQrCodeURL() {
		return qrCodeURL;
	}

	public void setQrCodeURL(String qrCodeURL) {
		this.qrCodeURL = qrCodeURL;
	}

	public String getQrCodePath() {
		return qrCodePath;
	}

	public void setQrCodePath(String qrCodePath) {
		this.qrCodePath = qrCodePath;
	}

}
