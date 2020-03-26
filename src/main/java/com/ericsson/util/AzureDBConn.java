package com.ericsson.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ericsson.request.dto.RegisterQRRequestDTO;
import com.ericsson.response.dto.QRCodeDTO;
import com.ericsson.vo.AlappVO;
import com.ericsson.vo.DeviceVO;
import com.ericsson.vo.QRCodeVO;

public class AzureDBConn {

	private static final Logger logger = LoggerFactory.getLogger(AzureDBConn.class);

	private Connection connection;

	public AzureDBConn() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String hostName = "keychain-db";
		String dbName = "keychain-db";
		String user = "keychain-admin";
		String password = "Key@2020";
		String url = String.format(
				"jdbc:sqlserver://keychain-db.database.windows.net:1433;database=keychain-db;user=keychain-admin@keychain-db;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
				hostName, dbName, user, password);
		connection = DriverManager.getConnection(url, user, password);

	}

	public void closeConnection() throws SQLException {
		if (null != connection) {
			connection.close();
		}
	}

	public void insertQRCode(String qrText, String requesterDeviceId) throws SQLException {
		logger.debug("Inside insertQRCode");
		String registrationStatus = "not_registered";

		logger.debug("Insert QR Code in QR_DATA table");
		String insertInQRDataTableSQL = "INSERT INTO QR_DATA (QR_CODE,QR_REGISTRATION_STATUS,QR_CREATE_TIMESTAMP) VALUES (?,?,?)";
		PreparedStatement preparedStatement = connection.prepareStatement(insertInQRDataTableSQL);
		preparedStatement.setString(1, qrText);
		preparedStatement.setString(2, registrationStatus);
		preparedStatement.setString(3, KeychainUtils.getCurrentTimestamp());
		preparedStatement.execute();
		preparedStatement.close();

		logger.debug("Insert device id in USER_SYSTEM_DATA  table");
		String insertInUserSystemDataTableSQL = "INSERT INTO USER_SYSTEM_DATA (USER_SYSTEM_ID,QR_CODE,REGISTRATION_TIMESTAMP) VALUES (?,?,?)";
		PreparedStatement preparedStatement1 = connection.prepareStatement(insertInUserSystemDataTableSQL);
		preparedStatement1.setString(1, requesterDeviceId);
		preparedStatement1.setString(2, qrText);
		preparedStatement1.setString(3, KeychainUtils.getCurrentTimestamp());
		preparedStatement1.execute();
		preparedStatement1.close();

		connection.close();

		logger.debug("Leaving insertQRCode");
	}

	public void registerAlappIdWIthQRCode(RegisterQRRequestDTO registerQr, String animatedLoginAppId)
			throws SQLException {
		logger.debug("Inside registerAlapIdWIthQRCode");
		String reg_status = KeychainUtils.notEmpty(registerQr.getQr_code_status())?registerQr.getQr_code_status():"registered";
		logger.debug("Input QR Code Status : " + reg_status);
		
		if (!StringUtils.isEmpty(animatedLoginAppId)){
			logger.debug("insert animated login id in ALAPP_DATA table");
			String insertInAlappDataTableSQL = "INSERT INTO ALAPP_DATA (ALAPP_ID,ALAPP_REGISTRATION_STATUS,QR_CODE) VALUES (?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(insertInAlappDataTableSQL);
			preparedStatement.setString(1, animatedLoginAppId);
			preparedStatement.setString(2, reg_status);
			preparedStatement.setString(3, registerQr.getQr_code());
			preparedStatement.execute();
			preparedStatement.close();
		}
		
		logger.debug("Update QR code as registered in QR_DATA table");
		String updateQRCodeTable = "UPDATE QR_DATA set QR_REGISTRATION_STATUS =? WHERE QR_CODE=?";
		PreparedStatement preparedStatement1 = connection.prepareStatement(updateQRCodeTable);
		preparedStatement1.setString(1, reg_status);
		preparedStatement1.setString(2, registerQr.getQr_code());
		preparedStatement1.execute();
		preparedStatement1.close();

		connection.close();

		logger.debug("Leaving registerAlapIdWIthQRCode");
	}
	
	public void updateRegistrationAlappIdWIthQRCode(RegisterQRRequestDTO registerQr, String animatedLoginAppId)
			throws SQLException {
		logger.debug("Inside registerAlapIdWIthQRCode");
		String reg_status = KeychainUtils.notEmpty(registerQr.getQr_code_status())?registerQr.getQr_code_status():"registered";
		logger.debug("Input QR Code Status : " + reg_status);
		
		logger.debug("Update QR code as registered in QR_DATA table");
		String updateQRCodeTable = "UPDATE QR_DATA set QR_REGISTRATION_STATUS =? WHERE QR_CODE=?";
		PreparedStatement preparedStatement1 = connection.prepareStatement(updateQRCodeTable);
		preparedStatement1.setString(1, reg_status);
		preparedStatement1.setString(2, registerQr.getQr_code());
		preparedStatement1.execute();
		preparedStatement1.close();

		connection.close();

		logger.debug("Leaving registerAlapIdWIthQRCode");
	}
	
	public void updateQRCode(RegisterQRRequestDTO registerQr)
			throws SQLException {
		logger.debug("Inside update QR");
		String reg_status = KeychainUtils.notEmpty(registerQr.getQr_code_status())?registerQr.getQr_code_status():"registered";
		logger.debug("Input QR Code Status : " + reg_status);
		
		logger.debug("Update QR code as registered in QR_DATA table");
		String updateQRCodeTable = "UPDATE QR_DATA set QR_REGISTRATION_STATUS =? WHERE QR_CODE=?";
		PreparedStatement preparedStatement1 = connection.prepareStatement(updateQRCodeTable);
		preparedStatement1.setString(1, reg_status);
		preparedStatement1.setString(2, registerQr.getQr_code());
		preparedStatement1.execute();
		preparedStatement1.close();

		connection.close();

		logger.debug("Leaving Update QR");
	}

	public QRCodeDTO getQRCodeData(String requesterDeviceId) throws SQLException {
		logger.debug("Inside getQRCodeData");
		QRCodeDTO qrCodeDto = new QRCodeDTO();
		
		QRCodeVO qrCodeVO = getQRCodeInfo(requesterDeviceId);
		
		if (qrCodeVO != null) {
			qrCodeDto.setQr_code(qrCodeVO.getQrCode());
			qrCodeDto.setQr_registration_status(qrCodeVO.getQrRegistrationStatus());
			qrCodeDto.setUser_id(qrCodeVO.getUserId());
		}

		logger.debug("Leaving getQRCodeData");
		return qrCodeDto;
	}

	private QRCodeVO getQRCodeInfo(String requesterDeviceId) throws SQLException {
		QRCodeVO qrCodeVO = new QRCodeVO();

		/**
		 * SELECT TOP (1000) [QR_CODE] ,[QR_REGISTRATION_STATUS] ,[QR_CREATE_TIMESTAMP]
		 * ,[QR_LAST_UPDATE_TIMESTAMP] ,[QR_STATUS] ,[USER_ID] FROM [dbo].[QR_DATA]
		 */

		logger.debug("fetch QRCode data from QRCode table based on device ID");
		String getQRCodeDataSQL = "SELECT QR_CODE, QR_REGISTRATION_STATUS, QR_CREATE_TIMESTAMP,QR_LAST_UPDATE_TIMESTAMP, QR_STATUS, USER_ID FROM QR_DATA WHERE QR_CODE= (SELECT QR_CODE FROM USER_SYSTEM_DATA WHERE USER_SYSTEM_ID=?)";
		PreparedStatement preparedStatement = connection.prepareStatement(getQRCodeDataSQL);
		preparedStatement.setString(1, requesterDeviceId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			qrCodeVO.setQrCode(rs.getString("QR_CODE"));
			qrCodeVO.setQrRegistrationStatus(rs.getString("QR_REGISTRATION_STATUS"));
			qrCodeVO.setQrCreateTimestamp(rs.getString("QR_CREATE_TIMESTAMP"));
			qrCodeVO.setQrLastUpdatedTimestamp(rs.getString("QR_LAST_UPDATE_TIMESTAMP"));
			qrCodeVO.setQrStatus(rs.getString("QR_STATUS"));
			qrCodeVO.setUserId(rs.getString("USER_ID"));
		}
		preparedStatement.close();

		connection.close();
		return qrCodeVO;
	}

	public void registerUserProfile(String requeterDeviceId, String userName) throws SQLException {
		logger.debug("Inside registerUserProfile");
		logger.debug("update user id in QR Data table");
		String updateUserIdSQl = "UPDATE QR_DATA SET USER_ID=? WHERE QR_CODE= (SELECT QR_CODE FROM USER_SYSTEM_DATA WHERE USER_SYSTEM_ID=?)";
		PreparedStatement preparedStatement = connection.prepareStatement(updateUserIdSQl);
		preparedStatement.setString(1, userName);
		preparedStatement.setString(2, requeterDeviceId);
		preparedStatement.execute();
		preparedStatement.close();

		connection.close();

		logger.debug("Leaving registerUserProfile");
	}

	public String getAlappId(String animatedLoginAppId) throws SQLException {
		logger.debug("Inside getAlappId");
		String appID = null;
		AlappVO alappdto = getAlappIDInfo(animatedLoginAppId);
		if (alappdto != null) {
			appID = alappdto.getAlappId();
		}
		logger.debug("Leaving getAlappId");
		return appID;

	}

	private AlappVO getAlappIDInfo(String animatedLoginAppId) throws SQLException {
		logger.debug("fetch Alapp ID from ALAPP_DATA table");
		AlappVO alappdto = new AlappVO();
		String getAlappIdSQL = "SELECT ALAPP_ID,ALAPP_REGISTRATION_STATUS,QR_CODE FROM ALAPP_DATA WHERE ALAPP_ID=?";
		PreparedStatement preparedStatement = connection.prepareStatement(getAlappIdSQL);
		preparedStatement.setString(1, animatedLoginAppId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			alappdto.setAlappId(rs.getString("ALAPP_ID"));
			alappdto.setAlappRegistrationStatus(rs.getString("ALAPP_REGISTRATION_STATUS"));
			alappdto.setQrCode(rs.getString("QR_CODE"));

		}
		preparedStatement.close();
		connection.close();
		return alappdto;
	}

	public String getQRCodefromDeviceIdTable(String requesterDeviceId) throws SQLException {
		logger.debug("Inside getRequesterdeviceID");
		String qrCode = "";
		DeviceVO deviceDTO = getDeviceIdInfo(requesterDeviceId);
		if (deviceDTO != null) {
			qrCode = deviceDTO.getQrCode();
		}
		logger.debug("Leaving getRequesterdeviceID");
		return qrCode;
	}

	private DeviceVO getDeviceIdInfo(String requesterDeviceId) throws SQLException {
		logger.debug("fetch data from USER_SYSTEM_DATA table");
		DeviceVO deviceDTO = new DeviceVO();
		String getDeviceIdInfoSQL = "SELECT USER_SYSTEM_ID,QR_CODE,REGISTRATION_TIMESTAMP FROM USER_SYSTEM_DATA WHERE USER_SYSTEM_ID=?";
		PreparedStatement preparedStatement = connection.prepareStatement(getDeviceIdInfoSQL);
		preparedStatement.setString(1, requesterDeviceId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			deviceDTO.setDeviceID(rs.getString("USER_SYSTEM_ID"));
			deviceDTO.setDeviceRegistrationTimestamp(rs.getString("REGISTRATION_TIMESTAMP"));
			deviceDTO.setQrCode(rs.getString("QR_CODE"));
		}
		preparedStatement.close();
		connection.close();
		return deviceDTO;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		AzureDBConn azure = new AzureDBConn();
		azure.insertQRCode("shil1", "DEV2");
	}

	public void testAzureConnect() throws ClassNotFoundException, SQLException {

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		String hostName = "keychain-db";
		String dbName = "keychain-db";
		String user = "keychain-admin";
		String password = "Key@2020";
		String url = String.format(
				"jdbc:sqlserver://keychain-db.database.windows.net:1433;database=keychain-db;user=keychain-admin@keychain-db;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
				hostName, dbName, user, password);
		Connection conn = DriverManager.getConnection(url, user, password);
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select 1+1 as sum");
		while (rs.next()) {
			System.out.println(rs.getInt("sum"));
		}
		rs.close();
		stat.close();
		conn.close();
	}

}
