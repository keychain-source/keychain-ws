package com.ericsson;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.request.dto.RegisterQRRequestDTO;
import com.ericsson.response.dto.ErrorResponseDTO;
import com.ericsson.response.dto.GenerateQrResponseDTO;
import com.ericsson.response.dto.QRCodeDTO;
import com.ericsson.util.AzureDBConn;
import com.ericsson.util.KeychainProperties;
import com.ericsson.util.KeychainUtils;
import com.ericsson.util.QRGenerator;

@RestController
public class KeychainController {

	private static final Logger logger = LoggerFactory.getLogger(KeychainController.class);

	@Autowired
	KeychainProperties keychainProperties;

	@RequestMapping(value = "/keychain-ws/generateqr", method = RequestMethod.POST)
	public ResponseEntity<Object> generateQR(@RequestParam("requester_device_id") String requesterDeviceId) {
		logger.debug("Inside generateQR");
		ErrorResponseDTO errorResponeDTO = null;

		String deviceIdStatus = KeychainUtils.validateDeviceId(requesterDeviceId);

		if (deviceIdStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", deviceIdStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);
		}
		logger.debug("QR Code path : " + keychainProperties.getQrCodePath());
		logger.debug("Create QR Code");
		String qrText = "";
		logger.debug("QR Text :" + qrText);
		try {
			logger.debug("check if device ID already has a QR Code");

			qrText = new AzureDBConn().getQRCodefromDeviceIdTable(requesterDeviceId);
			logger.debug("QR code from DB :" + qrText);

			if (!KeychainUtils.notEmpty(qrText)) {
				logger.debug("Generate QR Code");
				qrText = new QRGenerator().createQRImage(keychainProperties.getQrCodePath());
				logger.debug("QRCode to be inserted in DB : " + qrText);
				new AzureDBConn().insertQRCode(qrText, requesterDeviceId);
				logger.debug("QR Code inserted in database");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		} catch (SQLException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		}

		GenerateQrResponseDTO responseDto = new GenerateQrResponseDTO();
		responseDto.setQr_code(qrText);
		responseDto.setRequester_device_id(requesterDeviceId);
		responseDto.setQr_code_url(keychainProperties.getQrCodeURL() + qrText);

		logger.debug("Returning Successful Response");
		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@RequestMapping(value = "/keychain-ws/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<Object> getImage(@RequestParam("qr_id") String qrId) throws IOException {
		logger.debug("inside getImage");
		String qrIdStatus = KeychainUtils.validateqrId(qrId);
		ErrorResponseDTO errorResponeDTO = null;
		if (qrIdStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", qrIdStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);

		}

		File imgPath = new File(keychainProperties.getQrCodePath());
		logger.debug("imageread :" + imgPath);

		BufferedImage bImage = ImageIO.read(new File(keychainProperties.getQrCodePath() + "/qrcode_" + qrId + ".png"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(bImage, "png", bos);
		byte[] bytes = bos.toByteArray();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes);
	}

	@RequestMapping(value = "/keychain-ws/registerqr", method = RequestMethod.POST)
	public ResponseEntity<Object> registerQR(@RequestBody RegisterQRRequestDTO registerQr,
			@RequestParam("alapp_id") String animatedLoginAppId) {
		logger.debug("Inside registerQR");
		logger.debug("animatedLoginAppId :" + animatedLoginAppId);
		logger.debug("RegisterQRRequestDTO :" + registerQr.toString());
		ErrorResponseDTO errorResponeDTO = null;
		String alappIdFromDB = "";
		String alappIdStatus = KeychainUtils.validateAppId(animatedLoginAppId);

		if (alappIdStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", alappIdStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);

		}

		try {
			logger.debug("check if Alapp Id is already existing");
			alappIdFromDB = new AzureDBConn().getAlappId(animatedLoginAppId);

			logger.debug("alappIdFromDB :" + alappIdFromDB);
			if (null == registerQr.getQr_code_status() || registerQr.getQr_code_status().trim().length() == 0){
				if (KeychainUtils.notEmpty(alappIdFromDB)) {
					errorResponeDTO = KeychainUtils.generateErrorResponse("bad_request",
							"Animated Login ID is already present in Database");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);
				}	
			}
			
			logger.debug("Register App Login Id");
			if (!StringUtils.isEmpty(alappIdFromDB)){
				new AzureDBConn().registerAlappIdWIthQRCode(registerQr, animatedLoginAppId);
			} else{
				new AzureDBConn().updateRegistrationAlappIdWIthQRCode(registerQr, animatedLoginAppId);
			}
			
			logger.debug("App login ID registered");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		} catch (SQLException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		}

		logger.debug("Returning Successful Response");
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

	@RequestMapping(value = "/keychain-ws/checkqrregistry", method = RequestMethod.POST)
	public ResponseEntity<Object> checkQRRegistry(@RequestParam("requester_device_id") String requesterDeviceId) {
		logger.debug("Inside checkQRRegistry");
		logger.debug("Device Id: " + requesterDeviceId);
		ErrorResponseDTO errorResponeDTO = null;
		QRCodeDTO qrCodeData = null;

		String deviceIdStatus = KeychainUtils.validateDeviceId(requesterDeviceId);

		if (deviceIdStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", deviceIdStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);

		}
		logger.debug("Fetch QR code registration status");
		try {

			qrCodeData = new AzureDBConn().getQRCodeData(requesterDeviceId);
			logger.debug("QR Code inserted in database");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		} catch (SQLException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		}

		logger.debug("Returning Successful Response");
		return ResponseEntity.status(HttpStatus.OK).body(qrCodeData);
	}

	@RequestMapping("/keychain-ws/registerprofile")
	public ResponseEntity<Object> registerprofile(@RequestParam("requester_device_id") String requesterDeviceId,
			@RequestParam("user_name") String userName) {
		logger.debug("Inside registerprofile");
		logger.debug("requeterDeviceId :" + requesterDeviceId);
		logger.debug("userName :" + userName);
		ErrorResponseDTO errorResponeDTO = null;

		String deviceIdStatus = KeychainUtils.validateDeviceId(requesterDeviceId);

		if (deviceIdStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", deviceIdStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);

		}

		String userNameStatus = KeychainUtils.validateUserName(userName);

		if (userNameStatus != null) {
			errorResponeDTO = KeychainUtils.generateErrorResponse("invalid_request", userNameStatus);
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponeDTO);

		}

		logger.debug("Register user profile");
		try {

			new AzureDBConn().registerUserProfile(requesterDeviceId, userName);
			logger.debug("User profile registered");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		} catch (SQLException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		}

		logger.debug("Returning Successful Response");
		return ResponseEntity.status(HttpStatus.OK).body("");
	}
	
	
	@RequestMapping(value = "/keychain-ws/scanqr", method = RequestMethod.POST)
	public ResponseEntity<Object> scanQR(@RequestBody RegisterQRRequestDTO registerQr) {
		logger.debug("Inside registerQR");
		logger.debug("RegisterQRRequestDTO :" + registerQr.toString());
		ErrorResponseDTO errorResponeDTO = null;
		try {
			logger.debug("Scan QR Code");
			new AzureDBConn().updateQRCode(registerQr);
			logger.debug("QR Code Scan updated");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		} catch (SQLException e) {
			e.printStackTrace();
			errorResponeDTO = KeychainUtils.generateErrorResponse("internal_server_error", e.getMessage());
			logger.debug("Returning Error Response");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponeDTO);
		}

		logger.debug("Returning Successful Response");
		return ResponseEntity.status(HttpStatus.OK).body("");
	}

}