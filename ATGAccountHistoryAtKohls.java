import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



import io.qameta.allure.Story;
import net.minidev.json.JSONArray;

public class ATGAccountHistoryAtKohls extends ATGBIBaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ATGAccountHistoryAtKohls.class);

	String date_Z, orderNum;

	/**
	 * @category  This validates account History via card Number
	 */
	@Story("kohls")
	@Story("kohls_online")
	@TestRailID(id = { "C13502524"})
	@Test(groups = { "Sayed", "kohls", "Sprint21.13", "regression", "kohls", "kohls_online","Loyals-1201" })
	public void testAccountHistoryAtKohlsOnline() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewPOSCustomerForKohlsLinkedAcc();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		
		// Posting request for ATG Order Create
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String OrderNum = generateTransactionNum();

		jsonParams.put("$.delayInterval", "10");
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.channel", ITestConstants.KOHLS_ONLINE);
		jsonParams.put("$.geoCode", IExpectedMessage.CounntryCodeUS);
		jsonParams.put("$.locationNumber", ITestConstants.KOHLS_ONLINE_LOCATION);
		jsonParams.put("$.order.orderNum", OrderNum);
		jsonParams.put("$.purchase.date", date_Z);
        jsonParams.put("$.purchase.itemLines[0].quantity", "2");
		jsonParams.put("$.purchase.itemLines[0].totalAmount", 100.00);
		jsonParams.put("$.purchase.itemLines[0].attrs.fulfillmentType", "SHIPTOHOME");
		jsonParams.put("$.purchase.itemLines[0].upc", "400014084186");
		
		jsonParams.put("$.purchase.itemLines[1].quantity", "2");
		jsonParams.put("$.purchase.itemLines[1].totalAmount", 10.00);
		jsonParams.put("$.purchase.itemLines[1].attrs.fulfillmentType", "PICKUP");
		jsonParams.put("$.purchase.itemLines[1].upc", "773602144938");
		
		jsonParams.put("$.purchase.itemLines[2].quantity", "1");
		jsonParams.put("$.purchase.itemLines[2].totalAmount", 0.0);
		jsonParams.put("$.purchase.itemLines[2].attrs.reward_offline", "1");
		jsonParams.put("$.purchase.itemLines[2].CBR", ITestConstants.CBR);
		
		jsonParams.put("$.purchase.itemLines[3].quantity", "1");
		jsonParams.put("$.purchase.itemLines[3].totalAmount", 0.0);
		jsonParams.put("$.purchase.itemLines[3].attrs.reward_offline", "1");
		jsonParams.put("$.purchase.itemLines[3].upc", "3522930003786");		
		
		response = atgCreateOrder.createNewOrderWithKohlsOnlineMerchOnly2(jsonParams);
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		Thread.sleep(10000);

		// Pass parameters and revoke the order
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.geoCode",IExpectedMessage.CounntryCodeUS);
		jsonParams.put("$.order.orderNum", OrderNum);
		jsonParams.put("$.channel",ITestConstants.KOHLS_ONLINE);
		jsonParams.put("$.purchase.date", date_Z);
		
		jsonParams.put("$.purchase.itemLines[0].quantity", "2");
		jsonParams.put("$.purchase.itemLines[0].totalAmount", 100.00);
		jsonParams.put("$.purchase.itemLines[0].attrs.fulfillmentType", "SHIPTOHOME");
		jsonParams.put("$.purchase.itemLines[0].upc", "400014084186");
		
		jsonParams.put("$.purchase.itemLines[1].quantity", "2");
		jsonParams.put("$.purchase.itemLines[1].totalAmount", 10.00);
		jsonParams.put("$.purchase.itemLines[1].attrs.fulfillmentType", "PICKUP");
		jsonParams.put("$.purchase.itemLines[1].upc", "773602144938");
		
		jsonParams.put("$.purchase.itemLines[2].quantity", "1");
		jsonParams.put("$.purchase.itemLines[2].totalAmount", 0.0);
		jsonParams.put("$.purchase.itemLines[2].attrs.reward_offline", "1");
		jsonParams.put("$.purchase.itemLines[2].CBR",ITestConstants.CBR);
		
		jsonParams.put("$.purchase.itemLines[3].quantity", "1");
		jsonParams.put("$.purchase.itemLines[3].totalAmount", 0.0);
		jsonParams.put("$.purchase.itemLines[3].attrs.reward_offline", "1");
		jsonParams.put("$.purchase.itemLines[3].upc", "3522930003786");		
		
		response = atgCreateOrder.revokeOrderKohlsV2(jsonParams);
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		Thread.sleep(10000);

		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();

		biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		logger.info("Query: " + query);
		Awaitility.waitAtMost(60000,TimeUnit.MILLISECONDS).pollInterval(1000, TimeUnit.MILLISECONDS)
		.until(()->biDB.selectBIAccountActivityTable(usaID).size()>3);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);
		logger.info("BIAccountActivity Table Data is: " + maps);
		for (Map<String, Object> map : maps) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		s_Assert.assertEquals(actualdbTypeCD.get(0), "7", " Actual TypeCD0: " + actualdbTypeCD.get(0));
		s_Assert.assertEquals(actualdbTypeCD.get(3), "8", " Actual TypeCD3: " + actualdbTypeCD.get(3));
		s_Assert.assertEquals(actualdbTypeCD.get(1), "7", " Actual TypeCD1: " + actualdbTypeCD.get(1));
		s_Assert.assertEquals(actualdbTypeCD.get(2), "8", " Actual TypeCD2: " + actualdbTypeCD.get(2));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -100.0,
				"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), 100.0,
				"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -500.0,
				"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
				"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(2));
		
		// Passing json params and send Account History Request with usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.channel", ITestConstants.KOHLS_ONLINE);
				
		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
				
		// Verify response for transactionStatus, emailJson, lifetimePoints,
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);	
	
		JSONArray activityDescription = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online redemption')].activityDescription");
		s_Assert.assertEquals("$10 Beauty Insider Cash redeemed", activityDescription.get(0),
				"ActivityDescription isn't equal to $10 Beauty Insider Cash redeemed, Actual: " + activityDescription.get(0));

		JSONArray pointsDelta = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online redemption')].pointsDelta");
		s_Assert.assertEquals(-500.0, pointsDelta.get(0), "pointsDelta isn't equal to 500");

		JSONArray pointsResult = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Cancelled redemption')].pointsResult");
		s_Assert.assertEquals(0.0, pointsResult.get(0), "pointsResult isn't equal to 0");

	}
	
	@Story("kohls")
	@Story("kohls_online")
	@TestRailID(id = { "C13441753","C13441754"})
	@Test(groups = { "Sayed", "kohls", "Sprint21.13", "regression", "kohls", "kohls_online","Loyals-493" })
	public void testAccountActivityDescriptionAtKohlsOnline() throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewPOSCustomerForKohlsLinkedAcc();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		
		// Posting request for ATG Order Create
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String OrderNum = generateTransactionNum();

		jsonParams.put("$.delayInterval", "10");
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.channel", ITestConstants.KOHLS_ONLINE);
		jsonParams.put("$.geoCode", IExpectedMessage.CounntryCodeUS);
		jsonParams.put("$.order.locationSK", ITestConstants.KOHLS_ONLINE_LOCATION_SK);
		jsonParams.put("$.order.orderNum", OrderNum);
		jsonParams.put("$.order.subtotalAmt", 16.0);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.purchase.itemLines[1].CBR", ITestConstants.CBR);
		
		response = atgCreateOrder.createNewOrderWithKohlsOnline(jsonParams);
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		Thread.sleep(10000);

		// Pass parameters and revoke the order
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.geoCode",IExpectedMessage.CounntryCodeUS);
		jsonParams.put("$.channel",ITestConstants.KOHLS_ONLINE);
		jsonParams.put("$.order.locationSK", ITestConstants.KOHLS_ONLINE_LOCATION_SK);
		jsonParams.put("$.order.orderNum", OrderNum);
		jsonParams.put("$.order.subtotalAmt", 2.0);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.purchase.itemLines[1].CBR", ITestConstants.CBR);
			
		response = atgCreateOrder.revokeOrderByV2Kohls(jsonParams);
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();

		biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		logger.info("Query: " + query);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);
		logger.info("BIAccountActivity Table Data is: " + maps);
		for (Map<String, Object> map : maps) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		s_Assert.assertEquals(actualdbTypeCD.get(0), "7", " Actual TypeCD0: " + actualdbTypeCD.get(0));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
				"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
		
		// Passing json params and send Account History Request with usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.channel", ITestConstants.KOHLS_ONLINE);
		jsonParams.put("$.fromDate","2021-01-01");
				
		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		
		// Verify response for transactionStatus, activityDescription, pointsDelta, pointsResult
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
				
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);

		JSONArray activityDescription = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online redemption')].activityDescription");
		s_Assert.assertEquals("$10 Beauty Insider Cash redeemed", activityDescription.get(0),
				"ActivityDescription isn't equal to $10 Beauty Insider Cash redeemed, Actual: " + activityDescription.get(0));

		JSONArray pointsDelta = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online redemption')].pointsDelta");
		s_Assert.assertEquals(-500.0, pointsDelta.get(0), "pointsDelta isn't equal to 500");

		JSONArray pointsResult = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Cancelled redemption')].pointsResult");
		s_Assert.assertEquals(0.0, pointsResult.get(0), "pointsResult isn't equal to 0");

	}
	
	@Story("kohls")
	@Story("kohls_store")
	@TestRailID(id = { "C13441756","C13441755"})
	@Test(groups = { "Sayed", "kohls", "Sprint21.13", "regression", "kohls", "kohls_store","Loyals-493" })
	public void testAccountActivityDescriptionAtKohlsStore() throws IOException, SQLException, InterruptedException {
		
		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String emailId = mapDetails.get("emailId");

		// Posting request for createStoreTlogOrder
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String date_SK = generateDateSK();
		String transactionNum = generateTransactionNum();
		String location_SK = generateLocationSK(010);
		
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", date_SK);
		jsonParams.put("$.order.transactionNumber", transactionNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.order.locationSK",location_SK);
		jsonParams.put("$.channel",  ITestConstants.KOHLS_STORE);

		response = tlogOrderCreate.createStoreTlogOrder(jsonParams, "OnlyMerchantdise");
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		// Positng request for createStoreTlogOrderReturn
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", date_SK);
		jsonParams.put("$.order.transactionNumber", transactionNum);
		jsonParams.put("$.order.locationSK",location_SK);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.channel", "KOHLS_STORE");
				
		response = tlogOrderReturn.createStoreTlogOrderReturn(jsonParams, "OnlyMerchantdise");
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");

		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		// Verify BI Database QA_RM_OND DB (BI_ACCOUNT_ACCTIVITY)
		listMapDetails = biDB.selectBIAccountActivityTable(usaID);
		logger.info("BIAccountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			Actual.add(map.get("ACA_TYPE_CD"));
			Actual.add(map.get("ACA_ADJUST_AMT"));
		}
		logger.info("Actual BIAcc Table Data is: " + Actual);
		s_Assert.assertEquals("D", Actual.get(0),
				"BI_ACCOUNT_ACCTIVITY ACA_TYPE_CD 'D' :: Actual Value : " + Actual.get(0));
		s_Assert.assertEquals(36.0, Actual.get(1),
				"BI_ACCOUNT_ACCTIVITY ACA_ADJUST_AMT 36 :: Actual Value : " + Actual.get(1));
		s_Assert.assertEquals("E", Actual.get(2),
				"BI_ACCOUNT_ACCTIVITY ACA_TYPE_CD 'E' :: Actual Value : " + Actual.get(2));
		s_Assert.assertEquals(-36.0, Actual.get(3),
				"BI_ACCOUNT_ACCTIVITY ACA_ADJUST_AMT -36 :: Actual Value : " + Actual.get(3));
		Actual.clear();

		// Verify response with BIUserPoints Table Data
		listMapDetails = biDB.selectBIUserPointsTable(usaID);
		logger.info("BIUserPoints Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			currentPoints = (double) map.get("CURRENT_POINTS");
			lifetimePoints = (double) map.get("LIFETIME_POINTS");
		}
		s_Assert.assertEquals(0.0, currentPoints,
				"Profile_BI_Summary_Table current_Points 0 :: Actual Value : " + currentPoints);
		s_Assert.assertEquals(0.0, lifetimePoints,
				"Profile_BI_Summary_Table lifetime_Points 0 :: Actual Value : " + lifetimePoints);

		// Verify response with ClientSummary Table Data
		listMapDetails = biDB.selectClientSummaryTable(usaID);
		logger.info("ClientSummary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			amt_YTD = "" + map.get("Amt_YTD");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("0", amt_YTD, "Client_Summary_Table amountYTD: 0:: Actual Value : " + currentPoints);
		s_Assert.assertEquals("36", points_Earned,
				"Client_Summary_Table PointsEarned: 36:: Actual Value : " + currentPoints);

		// Verify response with ProfileBISummary Table Data
		listMapDetails = profileDB.selectProfileBISummaryTable(emailId);
		logger.info("ProfileBISummary Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			current_Points = "" + map.get("CURRENT_POINTS");
			lifetime_Points = "" + map.get("LIFETIME_POINTS");
			points_Earned = "" + map.get("POINTS_EARNED");
		}
		s_Assert.assertEquals("0.0", current_Points,
				"Profile_BI_Summary_Table currentPoints:0:: Actual Value : " + current_Points);
		s_Assert.assertEquals("0.0", lifetime_Points,
				"Profile_BI_Summary_Table lifetimepoints:0:: Actual Value : " + lifetime_Points);
		s_Assert.assertEquals("36", points_Earned,
				"Profile_BI_Summary_Table pointsEarned:36:: Actual Value : " + points_Earned);
		Actual.clear();

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.channel", ITestConstants.KOHLS_STORE);
		jsonParams.put("$.fromDate","2021-01-01");
						
		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		
		// Verify response for transactionStatus, activityDescription, pointsDelta, pointsResult
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
						
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);

		JSONArray location = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Store purchase')].location");
		s_Assert.assertEquals("JCP STORE SUPPORT CENTER ", location.get(0),
				"Location isn't equal to JCP STORE SUPPORT CENTER, Actual: " + location.get(0));

		JSONArray pointsDelta = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Store return')].pointsDelta");
		s_Assert.assertEquals(-36.0, pointsDelta.get(0), "pointsDelta isn't equal to -36.0");

		JSONArray pointsResult = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Store return')].pointsResult");
		s_Assert.assertEquals(0.0, pointsResult.get(0), "pointsResult isn't equal to 0");
			
	}
		
	@Story("atg")
	@Story("atg_order")
	@TestRailID(id = { "C13441757","C13441758"})
	@Test(groups = { "Sayed", "kohls", "Sprint21.13", "regression", "atg", "atg_order","Loyals-493" })
	public void testAccountActivityDescriptionATGChannel() throws IOException, SQLException, InterruptedException {
		
		HashMap<String, String> mapDetails = getNewPOSCustomerForKohlsLinkedAcc();
		String usaID = mapDetails.get("usaId");

		// Posting request for NewOrderCreate for ATG
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();
		logger.info(date_Z);

		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.order.subtotalAmt", 91.0);
		jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");

		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		// Posting request for ProcessDelayedOrder
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);

		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
		
		// Posting request for revokeOrder
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put("$.order.subtotalAmt", 91.0);
		jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");

		response = atgCreateOrder.revokeATGOrderByV2_1SKU1Rew1CBR(jsonParams);

		// verifying Assertion for transactionStatus
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.channel", ITestConstants.CHANNEL_ATG);
		jsonParams.put("$.fromDate","2021-01-01");
						
		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		
		writeJsonInExtentReport(test.get(), response.getBody().prettyPrint());
		s_Assert.assertEquals(response.statusCode(), IExpectedMessage.responseCode200, "Verify if status code is 200");
		
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);

		JSONArray activityDescription = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online redemption')].activityDescription");
		s_Assert.assertEquals("$10 Beauty Insider Cash redeemed", activityDescription.get(0),
				"ActivityDescription isn't equal to $10 Beauty Insider Cash redeemed, Actual: " + activityDescription.get(0));

		JSONArray pointsDelta = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Online order')].pointsDelta");
		s_Assert.assertEquals(32.0, pointsDelta.get(0), "pointsDelta isn't equal to 32");

		JSONArray pointsResult = (JSONArray) extractJSON(response.getBody().asString(),
				"$.accountHistoryLines[?(@.activityType=='Cancelled order')].pointsResult");
		s_Assert.assertEquals(-500.0, pointsResult.get(0), "pointsResult isn't equal to -500.0");

	}
}
