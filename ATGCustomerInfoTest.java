package com.sephora.service.bi.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.sephora.service.test.util.GenericUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.sephora.service.base.ATGBIBaseTest;
import com.sephora.service.base.IExpectedMessage;
import com.sephora.service.bi.sql.BIQuery;
import com.sephora.service.base.ITestConstants;
import com.sephora.service.test.util.DateHelper;
import com.sephora.service.test.util.TestRailID;

import io.qameta.allure.Story;

public class ATGCustomerInfoTest extends ATGBIBaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ATGCustomerInfoTest.class);
	


	@Story("atg")
	@TestRailID(id ={ "C13434094"})
	@Test(groups = { "regression", "Sandeep", "atg",
			"all_customers" }, description = "C9556847 :: Search via usaID")
	public void testSearchViaUsaId() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		usaIdJson = hashMapDetails.get("usaId");

		// Updating json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);
		// SDN Assertion as json output is not matching exactly
		

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg, nextSegment, currentSegment
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response Not blank, Actual customerMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response Not blank, Actual systemMsg: " + systemMsg);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, nextSegment,
				"Next Segment in Json Response Not VIB, Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierBI, currentSegment,
				"Current Segment in Json Response Not BI, Actual currentSegment: " + currentSegment);
		


	}

	@Story("atg")
	@TestRailID(id ={ "C13434093"})
	@Test(groups = { "regression", "sanity", "Sandeep", "atg",
			"all_customers" }, description = "C10916835 :: Search via Email")
	public void testSearchViaEmail() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		emailJson = hashMapDetails.get("emailId");

		// Updating json and Posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailJson);
		jsonParams.put("$.userAccount.cardNumber", null);

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg, nextSegment, currentSegment
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response not blank, Actual customerMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response not blank, Actual systemMsg: " + systemMsg);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, nextSegment,
				"Next Segment in Json Response not VIB, Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierBI, currentSegment,
				"Current Segment in Json Response not BI, Actual currentSegment: " + currentSegment);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434092"})
	@Test(groups = { "regression", "sanity", "Sandeep", "atg",
			"all_customers" }, description = "C9556846::Search via multiple optional parameters")
	public void testSearchViaMultiple() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		emailJson = hashMapDetails.get("emailId");

		// Updating json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailJson);
		jsonParams.put("$.userAccount.cardNumber", null);
		jsonParams.put("$.userAccount.firstName", "FNAutoTest");
		jsonParams.put("$.userAccount.lastName", "LNAutoTest");

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response not blank, Actual customerMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response not blank, Actual systemMsg: " + systemMsg);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434088"})
	@Test(groups = { "regression", "Sandeep", "atg",
			"all_customers" }, description = "C9556848 ::Invalid Email")
	public void testSearchViaInvalidEmail() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		emailJson = hashMapDetails.get("emailId");

		// Updating wrong email in json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailJson + "@com");

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams, "email");

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.CustomerInfoServiceMissingAccountCustomerMsg, customerMsg,
				"Custom Message in Json Response not CustomerInfoService Missing Account, Actual customerMsg: "
						+ customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.CustomerInfoServiceMissingAccountSystemMsg, systemMsg,
				"System Message in Json Response not CustomerInfoService Missing Account, Actual systemMsg: "
						+ systemMsg);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434089"})
	@Test(groups = { "regression", "Sandeep", "atg", "all_customers" }, description = "C9556847 :: usaID")
	public void testSearchViaInvalidUsaId() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		usaIdJson = hashMapDetails.get("usaId");

		// Updating wrong Usa ID in json and getting Response
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", "2828282828292929");

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.CustomerInfoServiceMissingAccountCustomerMsg, customerMsg,
				"Custom Message in Json Response not CustomerInfoService Missing Account, Actual customerMsg: "
						+ customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.CustomerInfoServiceMissingAccountSystemMsg, systemMsg,
				"System Message in Json Response not CustomerInfoService Missing Account, Actual systemMsg: "
						+ systemMsg);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434090"})
	@Test(groups = { "regression","Sandeep", "atg",
			"all_customers" }, description = " C9556849 :: Search VIB user ", enabled = false)
	public void testSearchForVib() throws IOException, SQLException, InterruptedException {

		// Extracting Order Details
		hashMapDetails = getAtgOrderDetails(350.0);
		usaIdJson = hashMapDetails.get("usaId");
		emailJson = hashMapDetails.get("emailId");
		long usaIdJsonLong = Long.parseLong(usaIdJson);

		// Updating Json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, nextSegment,
		// currentSegment
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: Success");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierRouge, nextSegment,
				"Next Segment in Json Response not Rouge, Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, currentSegment,
				"Current Segment in Json Response Not VIB, Actual currentSegment: " + currentSegment);

		// Assertion BI_USER_POINTS Table validation
		listMapDetails = biDB.selectBIUserPointsTable(usaIdJson);
		logger.info("BIUSerPoints Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			currentPoints = (double) map.get("CURRENT_POINTS");
			lifetimePoints = (double) map.get("LIFETIME_POINTS");
			usaID = (long) map.get("USA_ID");
		}
		s_Assert.assertEquals(points350, currentPoints, "Current Points in BIUserPointsTable  Mismatch, Actual: "
				+ points350 + " , " + " Expected:" + currentPoints);
		s_Assert.assertEquals(points350, lifetimePoints, "Life Time Points in BIUserPointsTable Mismatch, Actual: "
				+ points350 + " , " + " Expected:" + lifetimePoints);
		s_Assert.assertEquals(usaIdJsonLong, usaID,
				"USA_ID in BIUserPointsTable matches Json Response Mismatch, Actual: " + usaIdJsonLong + " , "
						+ " Expected:" + usaID);

		// Assertion CLIENT_SUMMARY Table validation
		listMapDetails = biDB.selectClientSummaryTable(usaIdJson);
		logger.info("BIClientSummary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			amountYTD = (BigDecimal) map.get("AMT_YTD");
			usaID = (long) map.get("USA_ID");
		}
		s_Assert.assertEquals(ytd350, amountYTD,
				"YTD Points in BIClientSummaryTable  Mismatch, Actual: " + ytd350 + " , " + " Expected:" + amountYTD);
		s_Assert.assertEquals(usaIdJsonLong, usaID,
				"USA_ID in BIClientSummaryTable matches Json Response  Mismatch, Actual: " + usaIdJsonLong + " , "
						+ " Expected:" + usaID);

		// Assertion BI_ACCOUNT_ACTIVITY Table validation
		listMapDetails = biDB.selectBIAccountActivityTable(usaIdJson);
		logger.info("BIAccountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			acaAdjustAmt = (double) map.get("ACA_ADJUST_AMT");
		}
		s_Assert.assertEquals(ytd350double, acaAdjustAmt, "YTD Points in BIAccountActivityTable  Mismatch, Actual: "
				+ ytd350double + " , " + " Expected:" + acaAdjustAmt);

		// TBL_PROFILE_BI_SUMMARY Table validation
		listMapDetails = profileDB.selectProfileBISummaryTable(emailJson);
		logger.info("ProfileBISummmary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			currentPoints = (double) map.get("CURRENT_POINTS");
			lifetimePoints = (double) map.get("LIFETIME_POINTS");
			ytdSpendAmount = (int) map.get("YTD_SPEND_AMOUNT");
		}
		s_Assert.assertEquals(points350, currentPoints, "Current Points in ProfileBISummaryTable Mismatch, Actual: "
				+ points350 + " , " + " Expected:" + currentPoints);
		s_Assert.assertEquals(points350, lifetimePoints, "Life Time Points in ProfileBISummaryTable Mismatch, Actual: "
				+ points350 + " , " + " Expected:" + lifetimePoints);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434091"})
	@Test(groups = { "regression","Sandeep", "atg",
			"all_customers" }, description = "C9556850 :: AtgCustomerInfo for ROUGE user", enabled = false)
	public void testSearchForRouge() throws IOException, SQLException, InterruptedException {

		// Extracting Order Details
		hashMapDetails = getAtgOrderDetails(1000.0);
		usaIdJson = hashMapDetails.get("usaId");
		emailJson = hashMapDetails.get("emailId");
		long usaIdJsonLong = Long.parseLong(usaIdJson);

		// Updating Json and posting request Response for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, nextSegment,
		// currentSegment
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: Success");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierRouge, nextSegment,
				"Next Segment in Json Response not Rouge, Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierRouge, currentSegment,
				"Current Segment in Json Response not Rouge, Actual currentSegment: " + currentSegment);

		// Assertion BI_USER_POINTS Table validation
		listMapDetails = biDB.selectBIUserPointsTable(usaIdJson);
		logger.info("BIUSerPoints Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			currentPoints = (double) map.get("CURRENT_POINTS");
			lifetimePoints = (double) map.get("LIFETIME_POINTS");
			usaID = (long) map.get("USA_ID");
		}
		s_Assert.assertEquals(points1000, currentPoints, "Current Points in BIUserPointsTable  Mismatch, Actual: "
				+ points1000 + " , " + " Expected:" + currentPoints);
		s_Assert.assertEquals(points1000, lifetimePoints, "Life Time Points in BIUserPointsTable  Mismatch, Actual: "
				+ points1000 + " , " + " Expected:" + lifetimePoints);
		s_Assert.assertEquals(usaIdJsonLong, usaID,
				"USA_ID in BIUserPointsTable matches Json Response  Mismatch, Actual: " + usaIdJsonLong + " , "
						+ " Expected:" + usaID);

		// Assertion CLIENT_SUMMARY Table validation
		listMapDetails = biDB.selectClientSummaryTable(usaIdJson);
		logger.info("ClientSummary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			amountYTD = (BigDecimal) map.get("AMT_YTD");
			usaID = (long) map.get("USA_ID");
		}
		s_Assert.assertEquals(ytd1000, amountYTD,
				"YTD Points in BIClientSummaryTable  Mismatch, Actual: " + ytd1000 + " , " + " Expected:" + amountYTD);
		s_Assert.assertEquals(usaIdJsonLong, usaID,
				"USA_ID in BIClientSummaryTable matches Json Response  Mismatch, Actual: " + usaIdJsonLong + " , "
						+ " Expected:" + usaID);

		// Assertion BI_ACCOUNT_ACTIVITY Table validation
		listMapDetails = biDB.selectBIAccountActivityTable(usaIdJson);
		logger.info("BIAccountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			acaAdjustAmt = (double) map.get("ACA_ADJUST_AMT");
		}
		s_Assert.assertEquals(ytd1000double, acaAdjustAmt, "YTD Points in BIAccountActivityTable Mismatch, Actual: "
				+ ytd1000double + " , " + " Expected:" + acaAdjustAmt);

		// TBL_PROFILE_BI_SUMMARY Table validation
		listMapDetails = profileDB.selectProfileBISummaryTable(emailJson);
		logger.info("ProfileBISummary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			currentPoints = (double) map.get("CURRENT_POINTS");
			lifetimePoints = (double) map.get("LIFETIME_POINTS");
			ytdSpendAmount = (int) map.get("YTD_SPEND_AMOUNT");
		}
		s_Assert.assertEquals(points1000, currentPoints, "Current Points in ProfileBISummaryTable Mismatch, Actual: "
				+ points1000 + " , " + " Expected:" + currentPoints);
		s_Assert.assertEquals(points1000, lifetimePoints, "Life Time Points in ProfileBISummaryTable Mismatch, Actual: "
				+ points1000 + " , " + " Expected:" + lifetimePoints);
	}

	@Story("atg")
	@TestRailID(id ={ "C13434095"})
	@Test(groups = { "regression","Sandeep", "atg",
			"all_customers" }, description = "C9866231 :: Request with Invalid Channel")
	public void testSearchWithInvalidChannel() throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails();
		usaIdJson = hashMapDetails.get("usaId");
		channel = hashMapDetails.get("channel");

		// Updating Json and getting Response
		jsonParams = new HashMap<>();
		jsonParams.put("$.channel", channel + 1);

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.InvalidchannelWasPassedOnlyATGChannelIsSupportedCustomerMsg, customerMsg,
				"Custom Message in Json Response :: Invalid channel was passed. Only 'atg' channel is supported!, Actual customerMsg: "
						+ customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.InvalidchannelWasPassedOnlyATGChannelIsSupportedSystemMsg, systemMsg,
				"System Message in Json Response :: Invalid channel was passed. Only 'atg' channel is supported!, Actual systemMsg: "
						+ systemMsg);
	}


	// @Test(groups = { "FunctionalNotComplete", "Rework","Sandeep","atg",
	// "all_customers"}, description = "C9866239 :: VIB previous year")
	@Story("pos")
	@Story("all_customers")
	@TestRailID(id ={ "C13434100"})
	@Test(groups = { "regression","pos", "Lakshmi", "all_customers" }, description = "")
	public void testAtgCustomerInfoViaCardNumber() throws IOException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		emailId = mapDetails.get("emailId");
		cardNumber = mapDetails.get("cardNumber");
		test.get().log(Status.INFO, " emailId: " + emailId);

		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", cardNumber);

		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434101"})
	@Parameters({ "environment" })
	@Test(groups = { "Teepti","regression", "Lakshmi", "atg", "all_customers" }, description = "")
	public void testSearchUserWithPasscodeXP(String environment) throws IOException, SQLException, InterruptedException {

		if (environment.equalsIgnoreCase("SDN")) {
			hashMapDetails = getSDNNewCustomerDetailsPOSCR();
		} else {
			hashMapDetails = getNewCustomerDetailsPOSCR();
		}
		usaId = hashMapDetails.get("usaId");
		emailId = hashMapDetails.get("emailId");
		cardNumber = hashMapDetails.get("cardNumber");

//		List<Object> actualdbAUSAUserID = new ArrayList<Object>();
		listMapDetails = biDB.selectBIUserAccountTable(usaId);
		logger.info("BIUserAccountTable Data is:" + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			USA_USER_ID = "" + map.get("USA_USERID");
		}

		String PassbookcodeXP = "XP00" + USA_USER_ID;
		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", PassbookcodeXP);

		response = posCustomerLookUp.customerLookup(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434096"})
	@Parameters({ "environment" })
	@Test(groups = { "Teepti","regression","Lakshmi", "atg", "all_customers" }, description = "")
	public void testSearchUserWithPasscodeXA(String environment) throws IOException, SQLException, InterruptedException {

		if (environment.equalsIgnoreCase("SDN")) {
			hashMapDetails = getSDNNewCustomerDetailsPOSCR();
		} else {
			hashMapDetails = getNewCustomerDetailsPOSCR();
		}
		System.out.println("hashMapDetails : "+hashMapDetails.toString());
		usaId = hashMapDetails.get("usaId");
		emailId = hashMapDetails.get("emailId");
		cardNumber = hashMapDetails.get("cardNumber");

//		List<Object> actualdbAUSAUserID = new ArrayList<Object>();
		listMapDetails = biDB.selectBIUserAccountTable(usaId);
		logger.info("BIUserAccountTable Data is:" + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			USA_USER_ID = "" + map.get("USA_USERID");
		}
        System.out.println("USA_USER_ID is "+ USA_USER_ID);
		String PassbookcodeXA = "XA00" + USA_USER_ID;
		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", PassbookcodeXA);

		response = posCustomerLookUp.customerLookup(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434098"})
	@Parameters({ "environment" })
	@Test(groups = { "Teepti","regression","Lakshmi", "atg", "all_customers" }, description = "")
	public void testSearchUserWithPasscodeXO(String environment) throws IOException, SQLException, InterruptedException {

		if (environment.equalsIgnoreCase("SDN")) {
			hashMapDetails = getSDNNewCustomerDetailsPOSCR();
		} else {
			hashMapDetails = getNewCustomerDetailsPOSCR();
		}
		usaId = hashMapDetails.get("usaId");
		emailId = hashMapDetails.get("emailId");
		cardNumber = hashMapDetails.get("cardNumber");

//		List<Object> actualdbAUSAUserID = new ArrayList<Object>();
		listMapDetails = biDB.selectBIUserAccountTable(usaId);
		logger.info("BIUserAccountTable Data is:" + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			USA_USER_ID = "" + map.get("USA_USERID");
		}

		String PassbookcodeXO = "XO00" + USA_USER_ID;
		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", PassbookcodeXO);

		response = posCustomerLookUp.customerLookup(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434097"})
	@Parameters({ "environment" })
	@Test(groups = {"regression","Lakshmi", "atg", "all_customers" }, description = "")
	public void testSearchUserWithPasscodeXQ(String environment) throws IOException, SQLException, InterruptedException {

		if (environment.equalsIgnoreCase("SDN")) {
			hashMapDetails = getSDNNewCustomerDetailsPOSCR();
		} else {
			hashMapDetails = getNewCustomerDetailsPOSCR();
		}
		usaId = hashMapDetails.get("usaId");
		emailId = hashMapDetails.get("emailId");
		cardNumber = hashMapDetails.get("cardNumber");

//		List<Object> actualdbAUSAUserID = new ArrayList<Object>();
		listMapDetails = biDB.selectBIUserAccountTable(usaId);
		logger.info("BIUserAccountTable Data is:" + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			USA_USER_ID = "" + map.get("USA_USERID");
		}
		String PassbookcodeXQ = "XQ00" + USA_USER_ID;

		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", PassbookcodeXQ);

		response = posCustomerLookUp.customerLookup(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434099"})
	@Parameters({ "environment" })
	@Test(groups = { "Teepti","regression","Lakshmi", "atg", "all_customers" }, description = "")
	public void testSearchUserWithPasscodeXI(String environment) throws IOException, SQLException, InterruptedException {

		if (environment.equalsIgnoreCase("SDN")) {
			hashMapDetails = getSDNNewCustomerDetailsPOSCR();
		} else {
			hashMapDetails = getNewCustomerDetailsPOSCR();
		}
		usaId = hashMapDetails.get("usaId");
		emailId = hashMapDetails.get("emailId");
		cardNumber = hashMapDetails.get("cardNumber");

//		List<Object> actualdbAUSAUserID = new ArrayList<Object>();
		listMapDetails = biDB.selectBIUserAccountTable(usaId);
		logger.info("BIUserAccountTable Data is:" + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			USA_USER_ID = "" + map.get("USA_USERID");
		}

		String PassbookcodeXI = "XI00" + USA_USER_ID;
		// Posting request for CustomerLookUp
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.cardNumber", PassbookcodeXI);
		response = posCustomerLookUp.customerLookup(jsonParams, "cardNumber");

		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion response for signupStoreNumber, transactionStatus, nextSegment,
		// ytdToNextLevel, Email, currentPoints, lifetimePoints
		signupStoreNumber = (String) extractJSON(response.getBody().asString(), "$.userAccount.signupStoreNumber");
		s_Assert.assertEquals(ITestConstants.usStoreNumber, signupStoreNumber,
				" US signupStoreNumber in Json Response :: 550, Actual: " + signupStoreNumber);

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals("S", transactionStatus,
				"Transaction Status in Json Response :: S, Actual: " + transactionStatus);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(nullPoints, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(nullPoints, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		s_Assert.assertAll();

	}

	@Story("atg")
	@TestRailID(id ={ "C13434099"})
	@Test(groups = { "regression","Lakshmi1", "atg", "all_customers" }, description = "")
	public void testSearchVIBPreviousYear() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(350.0);

//		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
//		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String previousYear = DateHelper.getpreviousYearinFormat();
		logger.debug(previousYear);

		Integer userID = biDB.updateUserTier(usaID, "1", previousYear);
		logger.info("Result: " + userID);

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);

		// Assertion Json Response validation for transactionStatus, nextSegment,
		// currentSegment
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: Success");

	}


	@Story("atg")
	@TestRailID(id ={ "C13434900"})
	@Test(groups = {"Vadzim", "atg", "Sprint20.23","regression"})
	public void testCcDiscountExpireDateCcStatusActiveAndApproved () throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails("VIB");
		usaIdJson = hashMapDetails.get("usaId");
		emailJson = hashMapDetails.get("emailId");
		logger.info("usaId : " + usaIdJson + " email : " + emailJson);

		// Posting Request for ATG CustomerUpdate
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		jsonParams.put("$.ccAccountInfoIn.ccApprovalStatus", "APPROVED");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimed", "true");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedChannel", "atg");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedDate", GenericUtil.getCurrTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		jsonParams.put("$.ccAccountInfoIn.ccStatus", "ACTIVE");
		jsonParams.put("$.ccAccountInfoIn.email", emailJson);

		response = atgCustUp.atgCustomerUpdateccFTDC(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response :: blank, Actual: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response :: blank, Actual: " + systemMsg);

		// Updating json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation
		Boolean ccFirstTimeDiscountClaimedActual = (Boolean) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.ccFirstTimeDiscountClaimed");
		s_Assert.assertEquals(ccFirstTimeDiscountClaimedActual, Boolean.TRUE, "Incorrect ccFirstTimeDiscountClaimed");

		// Assertion Json Response validation
		String  ccFirstTimeDiscountClaimedChannel = (String) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.ccFirstTimeDiscountClaimedChannel");
		s_Assert.assertEquals(ccFirstTimeDiscountClaimedChannel, "ATG", "Incorrect ccFirstTimeDiscountClaimedChannel");

		// Assertion Json Response validation
		String  ccFirstTimeDiscountClaimedDate = (String) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.ccFirstTimeDiscountClaimedDate");
		s_Assert.assertNotNull(ccFirstTimeDiscountClaimedDate);

		// Assertion Json Response validation
		String  ccFirstTimeDiscountExpireDate = (String) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.ccFirstTimeDiscountExpireDate");
		s_Assert.assertNotNull(ccFirstTimeDiscountExpireDate);

		// Assertion Json Response validation
		String  email = (String) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.email");
		s_Assert.assertEquals(email, emailJson, "Incorrect email");

		// Assertion Json Response validation
		String  ccStatus = (String) extractJSON(response.getBody().asString(), "$.ccAccountInfoOut.ccStatus");
		s_Assert.assertEquals(ccStatus, "ACTIVE", "Incorrect ccStatus");
	}

	@Story("atg")
	@TestRailID(id ={ "C13434901"})
	@Test(groups = {"Vadzim", "atg", "Sprint20.23","regression"})
	public void testCcDiscountExpireDateCcStatusClosedAndInProgress () throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails("VIB");
		usaIdJson = hashMapDetails.get("usaId");
		emailJson = hashMapDetails.get("emailId");
		logger.info("usaId : " + usaIdJson + " email : " + emailJson);

		// Posting Request for ATG CustomerUpdate
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		jsonParams.put("$.ccAccountInfoIn.ccApprovalStatus", "IN_PROGRESS");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimed", "true");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedChannel", "atg");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedDate", GenericUtil.getCurrTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		jsonParams.put("$.ccAccountInfoIn.ccStatus", "CLOSED");
		jsonParams.put("$.ccAccountInfoIn.email", emailJson);

		response = atgCustUp.atgCustomerUpdateccFTDC(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response :: blank, Actual: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response :: blank, Actual: " + systemMsg);

		// Updating json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion DiscountExpireDate
		String str = response.getBody().asString();
		System.out.println(str.contains("ccFirstTimeDiscountExpireDate"));
		s_Assert.assertTrue(!str.contains("ccFirstTimeDiscountExpireDate"), "json does not have this parameter");

	}

	@Story("atg")
	@TestRailID(id ={ "C13434902"})
	@Test(groups = {"Vadzim", "atg", "Sprint20.23","regression"})
	public void testCcDiscountExpireDateCcStatusClosedAndDeclined () throws IOException, SQLException, InterruptedException {

		// Extracting Customer Details
		hashMapDetails = getNewCustomerDetails("VIB");
		usaIdJson = hashMapDetails.get("usaId");
		emailJson = hashMapDetails.get("emailId");
		logger.info("usaId : " + usaIdJson + " email : " + emailJson);

		// Posting Request for ATG CustomerUpdate
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		jsonParams.put("$.ccAccountInfoIn.ccApprovalStatus", "DECLINED");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimed", "true");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedChannel", "atg");
		jsonParams.put("$.ccAccountInfoIn.ccFirstTimeDiscountClaimedDate", GenericUtil.getCurrTimeStamp("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		jsonParams.put("$.ccAccountInfoIn.ccStatus", "CLOSED");
		jsonParams.put("$.ccAccountInfoIn.email", emailJson);

		response = atgCustUp.atgCustomerUpdateccFTDC(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation for transactionStatus, customerMsg,
		// systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response :: blank, Actual: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response :: blank, Actual: " + systemMsg);

		// Updating json and posting request for ATGCustomerInfo
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaIdJson);
		response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion DiscountExpireDate
		String str = response.getBody().asString();
		System.out.println(str.contains("ccFirstTimeDiscountExpireDate"));
		s_Assert.assertTrue(!str.contains("ccFirstTimeDiscountExpireDate"), "json does not have this parameter");

	}

}
