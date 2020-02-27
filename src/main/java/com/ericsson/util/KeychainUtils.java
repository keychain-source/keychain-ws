package com.ericsson.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.response.dto.ErrorResponseDTO;

public class KeychainUtils {
	private static final Logger logger = LoggerFactory.getLogger(KeychainUtils.class);
	private static final String idRegex="^[a-zA-Z\\d-]+$";
	private static final String qrIdRegex = "^[a-zA-Z0-9]*$";

	public static String getAlphaNumericString() {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(12);

		for (int i = 0; i < 12; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		logger.debug("alphanumeric String : " + sb.toString());
		return sb.toString();
	}

	public static String validateDeviceId(String requesterDeviceId) {
		logger.debug("Inside validateDeviceId");

		if (!notEmpty(requesterDeviceId)) {
			logger.debug("Device Id is not present");
			return "Device Id is not present";
		}

		if (!requesterDeviceId.matches(idRegex)) {
			logger.debug("Device Id is not alphanumeric");
			return "Device Id is not alphanumeric";
		}
		logger.debug("Leaving validateDeviceId");
		return null;

	}

	public static boolean notEmpty(String str) {
		logger.debug("Checking Input String as Empty or not : " + str);
		if (str != null && !str.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static ErrorResponseDTO generateErrorResponse(String errorCode, String ErrorDesc) {
		ErrorResponseDTO errorResponeDTO;
		errorResponeDTO = new ErrorResponseDTO();
		errorResponeDTO.setErrorCode(errorCode);
		errorResponeDTO.setErrorDesc(ErrorDesc);
		return errorResponeDTO;
	}

	public static String validateqrId(String qrId) {

		logger.debug("Inside validateqrId");

		if (!notEmpty(qrId)) {
			logger.debug("QR Code Id is not present");
			return "QR Code Id is not present";
		}		
		if (!qrId.matches(qrIdRegex)) {
			logger.debug("QR Code Id is not valid");
			return "QR Code Id is not valid";
		}
		logger.debug("Leaving validateqrId");
		return null;

	}

	public static String validateUserName(String userName) {

		logger.debug("Inside validateUserName");

		if (!notEmpty(userName)) {
			logger.debug("user name is not present");
			return "user name is not present";
		}
		logger.debug("Leaving validateUserName");
		return null;

	}

	public static String validateAppId(String animatedLoginAppId) {
		logger.debug("Inside validateAppId");
		if (!notEmpty(animatedLoginAppId)) {
			logger.debug("App Id is not present");
			return "App Id is not present";
		}

		if (!animatedLoginAppId.matches(idRegex)) {
			logger.debug("App Id is not valid");
			return "App Id is not valid";
		}
		logger.debug("Leaving validateAppId");
		return null;	
	}

	public static String getCurrentTimestamp(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(Calendar.getInstance().getTime());
	} 
	
	public static void main(String[] args) {
		String str = "shilpi1";
		validateAppId(str);
	}
}
