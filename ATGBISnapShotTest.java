package com.sephora.service.bi.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.sephora.service.base.ATGBIBaseTest;
import com.sephora.service.base.IExpectedMessage;
import com.sephora.service.base.ITestConstants;
import com.sephora.service.test.util.DateHelper;
import com.sephora.service.test.util.DateHelper.DateFormats;
import com.sephora.service.test.util.GenericUtil;
import com.sephora.service.test.util.TestRailID;

import io.qameta.allure.Story;
import net.minidev.json.JSONArray;

public class ATGBISnapShotTest extends ATGBIBaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ATGBISnapShotTest.class);

	// Story : ILLUPH-137432
	/**
	 * @category  This validates BISnapshot via usaID
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434148"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "sanity", "regression" })
	public void testBiSnapShotViaUsaID() throws IOException {
		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");

	logger.info("email:: " + email);
		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", email); // Snapshot is using emailid then request should contain only
		

		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response :: Not blank" + " Actual customMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response :: Not blank" + " Actual systemMsg: " + systemMsg);

		// User Account Json Response
		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, verifyEmail,
				"email in Json response :: " + email + " Actual Email: " + verifyEmail);

		long verifyusaID = (long) extractJSON(response.getBody().asString(), "$.userAccount.usaID");
		s_Assert.assertEquals(Long.parseLong(usaID), verifyusaID,
				"Usaid in Json response :: " + usaID + " ActualUsaID: " + verifyusaID);

		// Current Segemnt Json Response validation
		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB" + " Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierBI, currentSegment,
				"Current Segment in Json Response :: BI" + " Actual currentSegment: " + currentSegment);

		// Snapshot Json Response
		int bonusPtsEarned = (int) extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bonusPtsEarned, "Bonus Points earned in Json response :: 0, Actual:" + bonusPtsEarned);

	}

	// Story : ILLUPH-137432
	/**
	 * @category  This validates BISnapshot via emailID
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434147"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "sanity", "regression" })
	public void testBiSnapShotViaEmail() throws IOException {
		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");

		// Posting request for BISnapshot via Email
		jsonParams = new HashMap<>();
	    jsonParams.put("$.userAccount.email", email); // Snapshot is using emailid then request should contain only
		

		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.noCustomerMsg, customerMsg,
				"Custom Message in Json Response :: Not blank" + " Actual customerMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.noSystemMsg, systemMsg,
				"System Message in Json Response :: Not blank" + " Actual systemMsg: " + systemMsg);

		// User Account Json Response validation
		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, verifyEmail,
				"email in Json response :: " + email + " Actual Email: " + verifyEmail);

		long verifyusaID = (long) extractJSON(response.getBody().asString(), "$.userAccount.usaID");
		s_Assert.assertEquals(Long.parseLong(usaID), verifyusaID,
				"Usaid in Json response :: " + usaID + " Actual verifyusaID: " + verifyusaID);

		// Current Segemnt Json Response validation
		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB" + " Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierBI, currentSegment,
				"Current Segment in Json Response :: BI" + " Actual currentSegment: " + currentSegment);

		// Snapshot Json Response
		int bonusPtsEarned = (int) extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bonusPtsEarned,
				"Bonus Points earned in Json response :: 0, Actual bonusPoints: " + bonusPtsEarned);

	}

	// Story : ILLUPH-137432
	/**
	 * @category  This validates BISnapshot via invalid Email
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434149"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShotViaInvalidEmail() throws IOException {
		// Generating invalid Email
		String invalidEmail = GenericUtil.generateInvalidEmail("yopmail.com", 10);

		// Posting request for BISnapshot with invaid email
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", invalidEmail); // Snapshot is using emailid then request should contain
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.AccountNotFoundForBISnapshotCustomerMsg, customerMsg,
				"Custom Message in Json Response :: CustomerInfoService Missing Account" + " Actual customerMsg: "
						+ customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.AccountNotFoundForBISnapshotSystemMsg, systemMsg,
				"System Message in Json Response :: CustomerInfoService Missing Account" + " Actual systemMsg: "
						+ systemMsg);

	}

	// Story : ILLUPH-137432
/**
	 * @category  This validates BISnapshot via Invalid Usaid
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434150"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" }, enabled = false)
	public void testVerifyBiSnapShotViaInvalidUSAID() throws IOException {

		// generate invalid usaid
		String invalidUsaID = GenericUtil.getUniqueNumeric(10);

		// Posting request for BISnapshot with invalid USAID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", invalidUsaID); // Snapshot is using emailid then request should contain
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Assertion Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals(IExpectedMessage.AccountNotFoundForBISnapshotCustomerMsg, customerMsg,
				"Custom Message in Json Response :: CustomerInfoService Missing Account" + " Actual customerMsg: "
						+ customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals(IExpectedMessage.AccountNotFoundForBISnapshotSystemMsg, systemMsg,
				"System Message in Json Response :: CustomerInfoService Missing Account" + " Actual systemMsg: "
						+ systemMsg);

	}

	// Story : ILLUPH-137432
	/**
	 * @category  This validates Dollars saved with $off promo, by passing discount amount in atgordercreate
	 * Validating Dollars saved in client summary table and in Profile DB
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434151"})
	@Test(groups = {"Teepti","Lakshmi", "bi_snapshot", "atg", "sanity", "regression" })
	public void testYTDDollarsSavedByUserWith$offPromo() throws SQLException, InterruptedException, IOException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");
		test.get().log(Status.INFO, "New user created. USA ID is :: " + usaID);

		// posting request for createnewOrder
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String OrderNum = generateOrderNum();
		String date_SK = generateDateSK();

		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.channel", "ATG");
		jsonParams.put("$.order.dateSK", date_SK);
		jsonParams.put("$.order.locationSK", "2293");
		jsonParams.put("$.order.orderNum", OrderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.purchase.itemLines[0].quantity", "1");
		jsonParams.put("$.purchase.itemLines[0].skuNumber", "447466");
		jsonParams.put("$.purchase.itemLines[0].totalAmount", "92.6");
		jsonParams.put("$.purchase.itemLines[0].productSK", "119813");
		jsonParams.put("$.purchase.itemLines[0].npdClass", "POS-MISC");
		jsonParams.put("$.purchase.itemLines[0].npdClass", "SUPPLIES");
		jsonParams.put("$.purchase.itemLines[0].brandNumber", "3902");

		response = atgCreateOrder.createNewOrder(jsonParams, "discountAmount");
		Thread.sleep(5000);
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());

		// Verify BI Database QA_RM_OND DB (BI_User_POINT)
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BI User Points Summary Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
		}
		s_Assert.assertEquals("30.0", current_Points, "Profile_BI_Summary_Table current_Points");
		logger.info("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "30.0");
		s_Assert.assertEquals("30.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
		logger.info("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "30.0");

		// Verify BI Database QA_RM_OND DB (Client_Summary_Table)
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
		}
		s_Assert.assertEquals("30", amt_YTD, "Client_Summary_Table amountYTD: 30");
		logger.info("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 30);
		s_Assert.assertEquals("30", points_Earned, "Client_Summary_Table PointsEarned: 30");
		logger.info("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 30);
		s_Assert.assertEquals("5.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 5");
		logger.info("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 0");
		logger.info("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("0", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 0");
		logger.info("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);

		// Verify BI Database QA_CCMS DB (Profile_BI_Summary_Table)
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBI Summary Data is: " + listMapDetails);
		Thread.sleep(15000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			ytd_SpendAmount = "" + map.get("YTD_SPEND_AMOUNT");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("30.0", current_Points, "Profile_BI_Summary_Table currentPoints:30");
		logger.info("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 30);
		s_Assert.assertEquals("30.0", lifetime_Points, "Profile_BI_Summary_Table lifetimepoints:30");
		logger.info("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 30);
		s_Assert.assertEquals("30", ytd_SpendAmount,"Profile_BI_Summary_Table YTD Spend Amount:30");
		logger.info("Actual Value : " + ytd_SpendAmount + ", " + " ExpectedValue : " + 30);

		s_Assert.assertEquals("5.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:5");
		logger.info("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:0");
		logger.info("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);

		s_Assert.assertEquals("0", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar:0");
		logger.info("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("30", points_Earned, "Profile_BI_Summary_Table pointsEarned:30");
		logger.info("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 30);

		// Posting request for BISnapshot service
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// Snapshot Json Response validation
		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		s_Assert.assertEquals(5.00, dollarsSaved,
				"dollar saved in Json response :: 5.00" + " Actual dollarSaved: " + dollarsSaved);
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(30, ptsEarned, "ptsearned in Json response :: 30" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(0, rougeRcDollar,
				"rougeDollar in Json response :: 0" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bnsPtsEarned,
				"bnsPtsearned in Json response :: 0" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		if(env.equals("SDN")) {
			Object cashRedemption = extractJSON(response.getBody().asString(),
					"$.clientSummary.snapShot[0].cashRedemption");
			s_Assert.assertEquals(0, cashRedemption,
					"cashRedemptions in Json response :: 0" + " Actual cashRedemption: " + cashRedemption);

			
		} else {
		Object cashRedemption = extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].cashRedemption");
		s_Assert.assertEquals(0.00, cashRedemption,
				"cashRedemptions in Json response :: 0.00" + " Actual cashRedemption: " + cashRedemption);
		}
	}

	// Story : ILLUPH-137432 // RRC+CBR+Discount
	/**
	 * @category  Placed order with RRC and CBR promo;
	 * Validating Dollars saved in client summary table and in Profile DB
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434152"})
	@Parameters({ "environment" })
	@Test(groups = {"Teepti2","Lakshmi3", "bi_snapshot", "sanity", "regression", "atg" })
	public void testYTDDollarsSavedByUserWithRRC_CBR_discount(String environment) throws SQLException, InterruptedException, IOException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(2500.0);
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");

		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();
		String date_SK = generateDateSK();

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.order.dateSK", date_SK);
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.order.subtotalAmt", 30.0);
		jsonParams.put("$.purchase.itemLines[0].totalAmount", 30.0);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.purchase.itemLines[0].discountAmount", 15.00);
		jsonParams.put("$.purchase.itemLines[1].skuNumber", ITestConstants.sku_RRC_2500);
		if(environment.equalsIgnoreCase("SDN")) {
			
		}else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

		// posting request for processDelayedOrder
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// Verify BI Database QA_RM_OND DB (BI_User_POINT)
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BI User Points Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
		}
		
		if(environment.equalsIgnoreCase("SDN")) {
			
			s_Assert.assertEquals("30.0", current_Points, "Profile_BI_Summary_Table current_Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "30.0");
			s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "2530.0");
			
		}else {
			s_Assert.assertEquals("-470.0", current_Points, "Profile_BI_Summary_Table current_Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "-470.0");
			s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "2530.0");
		}

		// Verify BI Database QA_RM_OND DB (Client_Summary_Table)
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");

		}
		s_Assert.assertEquals("2530", amt_YTD, "Client_Summary_Table amountYTD: 2530");
		logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 2530);
		s_Assert.assertEquals("2530", points_Earned, "Client_Summary_Table PointsEarned: 2530");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530);
		s_Assert.assertEquals("15.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 15.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 15.00);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 0");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("100", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 100");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 100);

		// Verify BI Database QA_CCMS DB (Profile_BI_Summary_Table)
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBI Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			ytd_SpendAmount = "" + map.get("YTD_SPEND_AMOUNT");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		
		if(environment.equalsIgnoreCase("SDN")) {
			s_Assert.assertEquals("30.0", current_Points, "Profile_BI_Summary_Table currentPoints:30.0");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 30.0);
		}else {
			s_Assert.assertEquals("-470.0", current_Points, "Profile_BI_Summary_Table currentPoints:-470.0");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + -470.0);
		}

		s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetimepoints: 2530.0");
		logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 2530.0);

		s_Assert.assertEquals("15.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:15.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 15.00);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:0");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);

		s_Assert.assertEquals("100", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar:100");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 100);
		s_Assert.assertEquals("2530", points_Earned, "Profile_BI_Summary_Table pointsEarned:2530");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530);

		// posting request for BISnapshot service
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId);
		response = atgBiSnapShot.getBiSnapShot(jsonParams); // Snapshot is using emailid then request should contain
				writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// Snapshot Json Response validation
		
		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		if(environment.equalsIgnoreCase("SDN")) {
			s_Assert.assertEquals(15.0, dollarsSaved,
					"dollarsSaved in Json response :: 15.00" + " Actual dollarsSaved: " + dollarsSaved);
		}else {
			s_Assert.assertEquals(5.0, dollarsSaved,
					"dollarsSaved in Json response :: 5.00" + " Actual dollarsSaved: " + dollarsSaved);
		}
		
		
		
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(2530, ptsEarned,
				"ptsEarned in Json response :: 2530" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(100, rougeRcDollar,
				"rougeRcDollar in Json response :: 100" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bnsPtsEarned,
				"bnsPtsEarned in Json response :: 0" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		if(env.equals("SDN")) {
			Object cashRedemption = extractJSON(response.getBody().asString(),
					"$.clientSummary.snapShot[0].cashRedemption");
			s_Assert.assertEquals(0, cashRedemption,
					"cashRedemption in Json response :: 0" + " Actual cashRedemption: " + cashRedemption);

		} else {
			Object cashRedemption = extractJSON(response.getBody().asString(),
					"$.clientSummary.snapShot[0].cashRedemption");
			s_Assert.assertEquals(10.0, cashRedemption,
					"cashRedemption in Json response :: 10.00" + " Actual cashRedemption: " + cashRedemption);

		}
		
		
	}

	// Story : ILLUPH-137432 // only Discount
	/**
	 * @category  Placed order with discount - no CBR;
	 * Validating Dollars saved in client summary table and in Profile DB
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434153"})
	@Parameters({ "environment" })
	@Test(groups = {"Teepti2", "Lakshmi2", "sanity", "regression", "atg" })
	public void testYTDDollarsSavedByUserWith$offPromoWithOrderCreateV2(String environment)
			throws SQLException, InterruptedException, IOException {
		HashMap<String, String> mapDetails = getAtgOrderDetails(2500.0);
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");

		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();
		

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.order.subtotalAmt", 30.0);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.purchase.itemLines[0].discountAmount", 5.00);
		response = atgCreateOrder.createNewOrderWithDiscountWithOrderCreateV2(jsonParams);
        System.out.println(response.asString());
        logger.info(response.asString());
		// Posting request for ProcessDelayedOrder
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// Verify BI Database QA_RM_OND DB (BI_User_POINT)
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BI User Points Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
		}
		
		if(environment.equalsIgnoreCase("SDN")) {
			s_Assert.assertEquals("2530.0", current_Points, "Profile_BI_Summary_Table current_Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "2530.0");
			s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "2530.0");
		}else {
			s_Assert.assertEquals("2559.0", current_Points, "Profile_BI_Summary_Table current_Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "2559.0");
			s_Assert.assertEquals("2559.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "2559.0");
		}
		

		// Verify BI Database QA_RM_OND DB (Client_Summary_Table)
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");

		}
		
		if(environment.equalsIgnoreCase("SDN")) {
			s_Assert.assertEquals("2530", amt_YTD, "Client_Summary_Table amountYTD: 2530.0");
			logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 2530.0);
			s_Assert.assertEquals("2530", points_Earned, "Client_Summary_Table PointsEarned: 2530.0");
			logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530.0);
			s_Assert.assertEquals("5.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 5.00");
			logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5.00);
			s_Assert.assertEquals("0", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 0");
			logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);
			s_Assert.assertEquals("0", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 0");
			logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
		}else {
			s_Assert.assertEquals("2559", amt_YTD, "Client_Summary_Table amountYTD: 2559.0");
			logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 2559.0);
			s_Assert.assertEquals("2559", points_Earned, "Client_Summary_Table PointsEarned: 2559.0");
			logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2559.0);
			s_Assert.assertEquals("5.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 5.00");
			logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5.00);
			s_Assert.assertEquals("0", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 0");
			logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);
			s_Assert.assertEquals("0", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 0");
			logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
		}
		

		// Verify BI Database QA_CCMS DB (Profile_BI_Summary_Table)
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBI Summary Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			ytd_SpendAmount = "" + map.get("YTD_SPEND_AMOUNT");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		if(environment.equalsIgnoreCase("SDN")) {
			s_Assert.assertEquals("2530.0", current_Points, "Incorrect current Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 2530.0);
			s_Assert.assertEquals("2530.0", lifetime_Points, "Incorrect lifetime points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 2530.0);

			s_Assert.assertEquals("5.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:5.00");
			logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5.00);
			s_Assert.assertEquals("0", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:0");
			logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);

			s_Assert.assertEquals("0", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar: 0");
			logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
			s_Assert.assertEquals("2530", points_Earned, "Profile_BI_Summary_Table pointsEarned:2530.0");
			logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530);
		}else {
			s_Assert.assertEquals("2559.0", current_Points, "Incorrect current Points");
			logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 2559.0);
			s_Assert.assertEquals("2559.0", lifetime_Points, "Incorrect lifetime points");
			logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 2559.0);

			s_Assert.assertEquals("5.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:5.00");
			logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 5.00);
			s_Assert.assertEquals("0", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:0");
			logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 0);

			s_Assert.assertEquals("0", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar: 0");
			logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
			s_Assert.assertEquals("2559", points_Earned, "Profile_BI_Summary_Table pointsEarned:2559.0");
			logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2559);
		}
		

		// posting request for BISnapshot service
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
				response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// Snapshot Json Response validation
		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		s_Assert.assertEquals(5.0, dollarsSaved,
				"dollarsSaved in Json response :: 5.00" + " Actual dollarsSaved: " + dollarsSaved);
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(2559, ptsEarned,
				"ptsEarned in Json response :: 2559" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(0, rougeRcDollar,
				"rougeRcDollar in Json response is not 0" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bnsPtsEarned,
				"bnsPtsEarned in Json response is not 0" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		if(env.equals("SDN")) {
		
		Object cashRedemption = extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].cashRedemption");
		s_Assert.assertEquals(0, cashRedemption,
		"cashRedemption in Json response is not 0" + " Actual cashRedemption: " + cashRedemption);
		}else {
			Object cashRedemption = extractJSON(response.getBody().asString(),
					"$.clientSummary.snapShot[0].cashRedemption");
			s_Assert.assertEquals(0.0, cashRedemption,
			"cashRedemption in Json response is not 0.00" + " Actual cashRedemption: " + cashRedemption);
		}
		
		
		}

	// Story : ILLUPH-137432 // only CBR
	/**
	 * @category  Placed order only CBR;
	 * Validating Dollars saved in client summary table and in Profile DB
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434154"})
	@Test(groups = {"Teepti2","Lakshmi4", "bi_snapshot", "sanity", "regression", "atg" })
	public void testDollarsSavedWithCBRWithOrderCreateV2() throws SQLException, InterruptedException, IOException {
		HashMap<String, String> mapDetails = getAtgOrderDetails(2500.0);
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");

		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.order.subtotalAmt", 30.0);
		jsonParams.put("$.purchase.itemLines[0].totalAmount", 30.0);
		jsonParams.put("$.purchase.date", date_Z);
		
		jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		jsonParams.put("$.purchase.itemLines[0].discountAmount", "10.00");
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

		// Posting request for processDelayedOrder
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// Verify BI Database QA_RM_OND DB (BI_User_POINT)
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BIUser Points Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
		}
		s_Assert.assertEquals("2030.0", current_Points, "Profile_BI_Summary_Table current_Points");
		logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "2046.0");
		s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
		logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "2530.0");

		// Verify BI Database QA_RM_OND DB (Client_Summary_Table)
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Table Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
		}
		s_Assert.assertEquals("2530", amt_YTD, "Client_Summary_Table amountYTD: 2530");
		logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 2530);
		s_Assert.assertEquals("2530", points_Earned, "Client_Summary_Table PointsEarned: 2530");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530);
		s_Assert.assertEquals("10.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 0.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 0.00);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 16");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 16);
		s_Assert.assertEquals("0", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 0");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);

		// Verify BI Database QA_CCMS DB (Profile_BI_Summary_Table)
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBI Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			ytd_SpendAmount = "" + map.get("YTD_SPEND_AMOUNT");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("2030.0", current_Points, "Profile_BI_Summary_Table currentPoints: 2046.0");
		logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 2046.0);
		s_Assert.assertEquals("2530.0", lifetime_Points, "Profile_BI_Summary_Table lifetimepoints: 2530.0");
		logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 2530.0);

		s_Assert.assertEquals("10.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:0.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 0.00);
		s_Assert.assertEquals("0", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:16");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 16);

		s_Assert.assertEquals("0", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar: 0");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("2530", points_Earned, "Profile_BI_Summary_Table pointsEarned:2530");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 2530);

		// posting request for BISnapshot service
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
				response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// Snapshot Json Response validation
		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		s_Assert.assertEquals(0.0, dollarsSaved,
				"dollarsSaved in Json response :: 0.00" + " Actual dollarsSaved: " + dollarsSaved);
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(2530, ptsEarned,
				"ptsEarned in Json response :: 2530" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(0, rougeRcDollar,
				"rougeRcDollar in Json response :: 0" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bnsPtsEarned,
				"bnsPtsEarned in Json response :: 0" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		Object cashRedemption = extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].cashRedemption");
		s_Assert.assertEquals(10.0, cashRedemption,
				"year earned in Json response :: 10.00" + " Actual cashRedemption: " + cashRedemption);

	}


	// Story : ILLUPH-137432
	/**
	 * @category  Updated User points with BiPointsUpdate;
	 * Validating Dollars saved in client summary table and in Profile DB
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434155"})
	@Test(groups = {"Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testYTDBonusPointsearnedWithCSCAdditions() throws SQLException, InterruptedException, IOException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");

		// Posting request for BI Point Update Service
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId);
		jsonParams.put("$.userAccount.usaID", usaId);
		jsonParams.put("$.userAccount.currentPoints", 40.0);

		response = cscBIPointsUpdate.biPointsUpdate(jsonParams);
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);

		// Verify BI Database QA_RM_OND DB (BI_User_POINT)
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BIUser Points Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
		}
		s_Assert.assertEquals("40.0", current_Points, "Profile_BI_Summary_Table current_Points");
		logger.info("Actual Value : " + current_Points + ", " + " ExpectedValue : " + "40.0");
		s_Assert.assertEquals("0.0", lifetime_Points, "Profile_BI_Summary_Table lifetime_Points");
		logger.info("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + "0.0");

		// Verify BI Database QA_RM_OND DB (Client_Summary_Table)
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
		}
		s_Assert.assertEquals("0", amt_YTD, "Client_Summary_Table amountYTD: 0");
		logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("0", points_Earned, "Client_Summary_Table PointsEarned: 0");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("0.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 0.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 0.00);
		s_Assert.assertEquals("40", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 40");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 40);
		s_Assert.assertEquals("0", rougeRC_Dollar, "Client_Summary_Table rougeRCDollar: 0");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);

		// Verify BI Database QA_CCMS DB (Profile_BI_Summary_Table)
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBI Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			ytd_SpendAmount = "" + map.get("YTD_SPEND_AMOUNT");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("40.0", current_Points, "Profile_BI_Summary_Table currentPoints: 40.0");
		logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 40.0);
		s_Assert.assertEquals("0.0", lifetime_Points, "Profile_BI_Summary_Table lifetimepoints: 0.0");
		logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 0.0);

		s_Assert.assertEquals("0.00", dollars_Saved, "Profile_BI_Summary_Table dollarsSaved:0.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 0.00);
		s_Assert.assertEquals("40", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:40");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 40);

		s_Assert.assertEquals("0", rougeRC_Dollar, "Profile_BI_Summary_Table rougeRCDollar: 0");
		logger.debug("Actual Value : " + rougeRC_Dollar + ", " + " ExpectedValue : " + 0);
		s_Assert.assertEquals("0", points_Earned, "Profile_BI_Summary_Table pointsEarned:0");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 0);

		// posting request for BISnapshot service request
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
				response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// Snapshot Json Response validation
		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		s_Assert.assertEquals(0.00, dollarsSaved,
				"dollarsSaved in Json response :: 0.00" + " Actual dollarsSaved: " + dollarsSaved);
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(0, ptsEarned, "ptsEarned in Json response :: 0" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(0, rougeRcDollar,
				"rougeRcDollar in Json response :: 0" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(40, bnsPtsEarned,
				"bnsPtsEarned in Json response :: 40" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		Object cashRedemption = extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].cashRedemption");
		if(env.equals("SDN")) {
			s_Assert.assertEquals(0, cashRedemption,
					"year earned in Json response :: 0" + " Actual cashRedemption: " + cashRedemption);

		} else {
		s_Assert.assertEquals(0.0, cashRedemption,
				"year earned in Json response :: 0" + " Actual cashRedemption: " + cashRedemption);

	}
	}

	/**
	 * Validating BISnapShot with invalid user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434156"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_UserNotfound() throws IOException {

		String newEmail = GenericUtil.generateEmail("yopmail.com", 15);

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
	jsonParams.put("$.userAccount.email", newEmail); // Snapshot is using emailid then request should contain only
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusfail, transactionStatus,
				"Transaction Status in Json Response :: F");

		customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals("Account Not Found for BI Snapshot", customerMsg,
				"Custom Message in Json Response :: Not blank" + " Actual customMsg: " + customerMsg);

		systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.systemMsg");
		s_Assert.assertEquals("Account Not Found for BI Snapshot", systemMsg,
				"System Message in Json Response :: Not blank" + " Actual systemMsg: " + systemMsg);

	}

	/**
	 * Validating BISnapShot with BI user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434157"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_BIUser() throws IOException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		
		String email = mapDetails.get("emailId");
	

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", email); // Snapshot is using emailid then request should contain only
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals("BI", currentSegment, "User segment is not BI");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals("VIB", nextSegment, "User next segment is not VIB");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[0].spendToQualify");
		s_Assert.assertEquals(350.0, spendToQualify, "User needs to spend 350 to qualify to vib");
	}

	/**
	 * Validating BISnapShot with VIB user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434158"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_VIBUser() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(350.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
		logger.info(emailId);

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
		
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals("VIB", currentSegment, "User segment is not BI");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals("ROUGE", nextSegment, "User next segment is not VIB");

		int pointsEarned = (int) extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(350, pointsEarned, "POint Earned is different");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[1].spendToQualify");
		s_Assert.assertEquals(650.0, spendToQualify, "User needs to spend 350 to qualify to vib");
	}

	/**
	 * Validating BISnapShot with Rouge user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434159"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_RougeUser() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(1000.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
		logger.info(emailId);

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
			
		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals("ROUGE", currentSegment, "User segment is not BI");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals("ROUGE", nextSegment, "User next segment is not VIB");

		int pointsEarned = (int) extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(1000, pointsEarned, "POint Earned is different");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[0].spendToQualify");
		s_Assert.assertEquals(0.0, spendToQualify, "user is not Rouge");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[1].spendToQualify");
		s_Assert.assertEquals(0.0, spendToQualify, "user is not Rouge");
	}

	/**
	 * Validating BISnapShot with ATG ID
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434164"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShotViaATGID() throws IOException {
		HashMap<String, String> mapDetails = getNewCustomerDetails();
		// HashMap<String, String> mapDetails = getNewCustomerDetailsPOSCR();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");

		/*
		 * jsonParams = new HashMap<>(); jsonParams.put("$.userAccount.usaID", usaID);
		 * response = atgCustomerInfo.getAtgCustomerInfo(jsonParams);
		 * 
		 * String atgCustomerID = (String) extractJSON(response.getBody().asString(),
		 * "$.userAccount.atgCustomerID");
		 */

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", email);
		jsonParams.put("$.userAccount.cardNumber", cardNumber);

		response = atgBiSnapShot.getBiSnapShot(jsonParams); // Snapshot is using emailid then request should contain
					writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		// User Account Json Response
		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, verifyEmail,
				"email in Json response :: " + emailId + " Actual Email: " + verifyEmail);

		// Current Segemnt Json Response validation
		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(IExpectedMessage.tierVIB, nextSegment,
				"Next Segment in Json Response :: VIB" + " Actual nextSegment: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(IExpectedMessage.tierBI, currentSegment,
				"Current Segment in Json Response :: BI" + " Actual currentSegment: " + currentSegment);

		// Snapshot Json Response
		int bonusPtsEarned = (int) extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(0, bonusPtsEarned, "Bonus Points earned in Json response :: 0, Actual:" + bonusPtsEarned);

	}

	/**
	 * Validating BISnapShot with VIB Previous user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434161"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_VIBPreviousYear() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(350.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
		
		String previousYear = DateHelper.getpreviousYearinFormat();
	logger.info("Prev Yr:" + previousYear);

		Integer userID = biDB.updateUserTier(usaID, "1", previousYear);
		logger.info("Result: " + userID);

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", emailId);

			response = atgBiSnapShot.getBiSnapShot(jsonParams); // Snapshot is using emailid then request should contain
				writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals("VIB", currentSegment, "User segment is not BI");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals("ROUGE", nextSegment, "User next segment is not VIB");

		int pointsEarned = (int) extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(350, pointsEarned, "POint Earned is different");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[1].spendToQualify");
		s_Assert.assertEquals(650.0, spendToQualify, "User needs to spend 350 to qualify to vib");

	}

	/**
	 * Validating BISnapShot with Rouge Previous user
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434162"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBiSnapShot_RougePreviousYear() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getAtgOrderDetails(1000.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
		
		String previousYear = DateHelper.getpreviousYearinFormat();
	logger.info("Prev yr:" + previousYear);

		Integer userID = biDB.updateUserTier(usaID, "1", previousYear);
		logger.info("Result: " + userID);

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", emailId); // Snapshot is using emailid then request should contain only
			

		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals("ROUGE", currentSegment, "User segment is not BI");

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals("ROUGE", nextSegment, "User next segment is not VIB");

		int pointsEarned = (int) extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(1000, pointsEarned, "POint Earned is different");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[0].spendToQualify");
		s_Assert.assertEquals(0.0, spendToQualify, "user is not Rouge");

		spendToQualify = (double) extractJSON(response.getBody().asString(),
				"$.segmentDetails.segmentDetail[1].spendToQualify");
		s_Assert.assertEquals(0.0, spendToQualify, "user is not Rouge");
	}

/**
	 * Validating BISnapShot response
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434160"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testUseraccountdetailsverification() throws SQLException, InterruptedException, IOException {
		HashMap<String, String> mapDetails = getAtgOrderDetails(350.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");
		logger.info(emailId);

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId);

		response = atgBiSnapShot.getBiSnapShot(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(350.0, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(350.0, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		nextSegment = (String) extractJSON(response.getBody().asString(), "$.nextSegment");
		s_Assert.assertEquals(ITestConstants.tierRouge, nextSegment,
				"Next Segment in Json Response :: ROUGE, Actual: " + nextSegment);

		currentSegment = (String) extractJSON(response.getBody().asString(), "$.currentSegment");
		s_Assert.assertEquals(ITestConstants.tierVIB, currentSegment,
				"Next Segment in Json Response :: VIB, Actual: " + nextSegment);

		String verifyEmail = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(emailId, verifyEmail, "email in Json response , Actual: " + verifyEmail);

	}

/**
	 * BD Gift Redeemed and validating BI Snapshot
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434163"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "sanity", "regression" })
	public void testBISnapshot_BDGiftRedemption() throws SQLException, InterruptedException, IOException {
		HashMap<String, String> mapDetails = getAtgOrderDetails(600.0);

		String emailId = mapDetails.get("emailId");
		String usaID = mapDetails.get("usaId");

		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		orderNum = generateOrderNum();

		// Updating json and placing ATG Order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.order.subtotalAmt", 100.0);
		jsonParams.put("$.purchase.itemLines[1].skuNumber", ITestConstants.sku_BDGift); // same for both QA1 and QA4
		jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		response = atgCreateOrder.createNewOrderWithatgOrdercreateV2BD(jsonParams);

		Thread.sleep(5000);
		// Posting request for ProcessedDelayedOrders
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// Posting request for BISnapshot via usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", emailId);

		response = atgBiSnapShot.getBiSnapShot(jsonParams); // Snapshot is using emailid then request should contain
															// only emailid, other fields shud not be passed(As per
															// Dev-Timmy Comments)
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Transaction Status Json Response validation
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(165.0, currentPoints, "currentPoints :: 0.0, Actual: " + currentPoints);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(665.0, lifetimePoints, "lifetimePoints :: 0.0, Actual: " + lifetimePoints);

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", emailId);

		response = atgAccountHistory.getAccountHistory(jsonParams, "email");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verify response for transactionStatus, emailJson, lifetimePoints,
		// currentPoints
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		JSONArray BDRedemption = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.skuCode=='2311686')].activityDescription");
		logger.debug(BDRedemption.toString().substring(2, 24));
		s_Assert.assertEquals("Birthday Gift redeemed", BDRedemption.toString().substring(2, 24),
				"BD gift Redemption ");

	}

	/**
	 * Perform PointsForAction and then Validating BISnapShot 
	 */
	@Story("atg")
	@Story("bi_snapshot")
	@TestRailID(id ={ "C13434165"})
	@Test(groups = { "Lakshmi", "bi_snapshot", "atg", "regression" })
	public void testBISnapshot_PointsForAction() throws SQLException, InterruptedException, IOException {
		HashMap<String, String> mapDetails = getAtgOrderDetails(350.0);
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");

		jsonParams = new HashMap<>();
		jsonParams.put("$.userId", email);
		jsonParams.put("$.storeNumber", "400128");
		jsonParams.put("$.actionName", "FIQ");
		jsonParams.put("$.channel", "ATG");
		jsonParams.put("$.actionDate", DateHelper.getTodayYYMMDD());

		response = pointsForAction.GetPointsForAction(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		Thread.sleep(60000);

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", email);

		response = atgBiSnapShot.getBiSnapShot(jsonParams); // Snapshot is using emailid then request should contain
															// only emailid, other fields shud not be passed(As per
															// Dev-Timmy Comments)
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus,
				"Transaction Status in Json Response :: S");

		Object dollarsSaved = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].dollarsSaved");
		s_Assert.assertEquals(0.0, dollarsSaved,
				"dollarsSaved in Json response :: 0.00" + " Actual dollarsSaved: " + dollarsSaved);
		Object ptsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].ptsEarned");
		s_Assert.assertEquals(350, ptsEarned, "ptsEarned in Json response :: 350" + " Actual ptsEarned: " + ptsEarned);
		Object rougeRcDollar = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].rougeRcDollar");
		s_Assert.assertEquals(0, rougeRcDollar,
				"rougeRcDollar in Json response :: 0" + " Actual rougeRcDollar: " + rougeRcDollar);
		Object bnsPtsEarned = extractJSON(response.getBody().asString(), "$.clientSummary.snapShot[0].bnsPtsEarned");
		s_Assert.assertEquals(10, bnsPtsEarned,
				"bnsPtsEarned in Json response :: 0" + " Actual bnsPtsEarned: " + bnsPtsEarned);
		
		if(env.equalsIgnoreCase("SDN")) {
			Object cashRedemption = extractJSON(response.getBody().asString(),
					"$.clientSummary.snapShot[0].cashRedemption");
			s_Assert.assertEquals(0, cashRedemption,
					"cashRedemption in Json response :: 0" + " Actual cashRedemption: " + cashRedemption);
		}else {
			
		
		Object cashRedemption = extractJSON(response.getBody().asString(),
				"$.clientSummary.snapShot[0].cashRedemption");
		s_Assert.assertEquals(0.00, cashRedemption,
				"cashRedemption in Json response :: 0.00" + " Actual cashRedemption: " + cashRedemption);
		}
		// Profile DB validation
		listMapDetails = profileDB.selectProfileBISummaryTable(email);
		logger.info("ProfileBI Summary Data is: " + listMapDetails);
		Thread.sleep(15000);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("360.0", current_Points, "Profile_BI_Summary_Table currentPoints:350.0");
		logger.debug("Actual Value : " + current_Points + ", " + " ExpectedValue : " + 350.0);
		s_Assert.assertEquals("350.0", lifetime_Points, "Profile_BI_Summary_Table lifetimepoints: 350.0");
		logger.debug("Actual Value : " + lifetime_Points + ", " + " ExpectedValue : " + 350.0);
		s_Assert.assertEquals(350, Integer.parseInt(points_Earned), "Profile_BI_Summary_Table points earned:350.00");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 350.00);
		s_Assert.assertEquals("10", bonusPoints_Earned, "Profile_BI_Summary_Table bonusPointsEarned:10");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 10);

		// client summary table
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("Client Summary Data is: " + listMapDetails);
		Thread.sleep(5000);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
			dollars_Saved = "" + map.get("DOLLARS_SAVED");
			bonusPoints_Earned = "" + map.get("BONUS_POINTS_EARNED");
			rougeRC_Dollar = "" + map.get("ROUGE_RC_DOLLAR");
		}
		s_Assert.assertEquals("350", amt_YTD, "Client_Summary_Table amountYTD: 350");
		logger.debug("Actual Value : " + amt_YTD + ", " + " ExpectedValue : " + 350);
		s_Assert.assertEquals("350", points_Earned, "Client_Summary_Table PointsEarned: 350");
		logger.debug("Actual Value : " + points_Earned + ", " + " ExpectedValue : " + 350);
		s_Assert.assertEquals("0.00", dollars_Saved, "Client_Summary_Table dollarsSaved: 0.00");
		logger.debug("Actual Value : " + dollars_Saved + ", " + " ExpectedValue : " + 0.00);
		s_Assert.assertEquals("10", bonusPoints_Earned, "Client_Summary_Table bonusPointsEarned: 10");
		logger.debug("Actual Value : " + bonusPoints_Earned + ", " + " ExpectedValue : " + 10);

	}
}
