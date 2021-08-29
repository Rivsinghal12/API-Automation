package com.sephora.service.bi.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.sephora.service.base.ATGBIBaseTest;
import com.sephora.service.base.IExpectedMessage;
import com.sephora.service.base.ITestConstants;
import com.sephora.service.bi.sql.BIQuery;
import com.sephora.service.test.util.DateHelper;
import com.sephora.service.test.util.DateHelper.DateFormats;
import com.sephora.service.test.util.GenericUtil;
import com.sephora.service.test.util.TestRailID;

import io.qameta.allure.Story;

public class ATGAccountHistoryTest extends ATGBIBaseTest {

	private static final Logger logger = LoggerFactory.getLogger(ATGAccountHistoryTest.class);

	String date_Z, orderNum;

	/**
	 * @category  This validates account History via card Number
	 */
	@Story("atg")
	@TestRailID(id ={ "C13433355"})
	@Parameters({ "environment" })
	@Test(groups = { "Teepti","atg", "regression", "Lakshmi"}, description = "")
	public void testAccountHistoryviaCardNumber(String environment) throws IOException, SQLException, InterruptedException {

		
		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");
		logger.info("usa ID : " + usaID + " email : " + email);

		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();
       
		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
        if (environment.equalsIgnoreCase("SDN")) {
			
		} else {
			
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
			
		}
		
		jsonParams.put("$.purchase.date", date_Z);
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);
	

		Thread.sleep(10000);
		// Pass parameters and process Delayed order
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);

		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);
		Thread.sleep(10000);

		// Pass parameters and revoke the order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		 if (environment.equalsIgnoreCase("SDN")) {
				
			} else {
				
				jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
				
			}
		
		response = atgCreateOrder.revokeOrderByV2(jsonParams);
		Thread.sleep(10000);
		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();
		
		List<Map<String, Object>> maps = biDB.selectBIAccountActivityTableByAdjustAmtinASCOrder(usaID);
		logger.info("BIAccountActivity Table Data is: " + maps);
		for (Map<String, Object> map : maps) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		 if (environment.equalsIgnoreCase("SDN")) {
			 
			
				
				//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
				s_Assert.assertEquals(actualdbTypeCD.get(0), "C", " Actual TypeCD1: " + actualdbTypeCD.get(0));
				s_Assert.assertEquals(actualdbTypeCD.get(1), "O", " Actual TypeCD2: " + actualdbTypeCD.get(1));
				//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
				
				//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
						//"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -130.0,
						"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(0));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0,
						"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(1));
				//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
						//"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));

				
				
			} else {
				
				
				
				s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
				s_Assert.assertEquals(actualdbTypeCD.get(1), "C", " Actual TypeCD1: " + actualdbTypeCD.get(1));
				s_Assert.assertEquals(actualdbTypeCD.get(2), "O", " Actual TypeCD2: " + actualdbTypeCD.get(2));
				s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
				
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
						"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0,
						"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), 130.0,
						"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(2));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
						"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));

			
				
			}
		// Thread.sleep(10000);
			// Passing json params and send Account History Request
			jsonParams = new HashMap<>();
			jsonParams.put("$.userAccount.cardNumber", cardNumber);

			response = atgAccountHistory.getAccountHistory(jsonParams, "cardNumber");
			writeJsonInExtentReport(test.get(), response.getBody().asString());

			// Verify response for transactionStatus, emailJson, lifetimePoints,
			// currentPoints
			transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
			s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

			emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
			s_Assert.assertEquals(email, emailJson, "emailID: " + email + " Actual email: " + emailJson);

			lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
			s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

			currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
			s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
		

	}

	@Story("atg")
	@TestRailID(id ={ "C13433354"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Lakshmi","sanity" }, description = "")
	public void testAccountHistoryviaEmail(String environment) throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");
		logger.info("usa ID : " + usaID + " email : " + email);

		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		   if (environment.equalsIgnoreCase("SDN")) {
				
			} else {
				
				jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
				
			}
		
		jsonParams.put("$.purchase.date", date_Z);
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);
	

		Thread.sleep(10000);
		// Pass parameters and process Delayed order
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);

		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);
		Thread.sleep(10000);

		// Pass parameters and revoke the order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		   if (environment.equalsIgnoreCase("SDN")) {
				
			} else {
				
				jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
				
			}
		
		response = atgCreateOrder.revokeOrderByV2(jsonParams);
		Thread.sleep(10000);

		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();
		
		/*
		biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		logger.info("Query: " + query);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);
		logger.info("BIAccountActivity Table Data is: " + maps);
			for (Map<String, Object> map : maps) {
		*/
		listMapDetails = biDB.selectBIAccountActivityTableByAdjustAmtinASCOrder(usaID);
		logger.info("BIACcountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		if (environment.equalsIgnoreCase("SDN")) {
			
			//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(0), "C", " Actual TypeCD1: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "O", " Actual TypeCD2: " + actualdbTypeCD.get(1));
			//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					//"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -130.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					//"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}else {
			
			s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "C", " Actual TypeCD1: " + actualdbTypeCD.get(1));
			s_Assert.assertEquals(actualdbTypeCD.get(2), "O", " Actual TypeCD2: " + actualdbTypeCD.get(2));
			s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), 130.0,
					"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(2));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}
	
		
		
		
		// Passing json params and send Account History Request
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", email);

		response = atgAccountHistory.getAccountHistory(jsonParams, "email");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verify response for transactionStatus, emailJson, lifetimePoints,
		// currentPoints
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, emailJson, "emailID: " + email + " Actual email: " + emailJson);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
	}

	@Story("atg")
	@TestRailID(id ={ "C13433350"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Lakshmi","sanity" }, description = "")
	public void testAccountHistoryviausaID(String environment) throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");
		logger.info("usa ID : " + usaID + " email : " + email);

		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		
		
		if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		jsonParams.put("$.purchase.date", date_Z);
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);
	

		Thread.sleep(10000);
		// Pass parameters and process Delayed order
		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);

		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);
		Thread.sleep(10000);

		// Pass parameters and revoke the order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		response = atgCreateOrder.revokeOrderByV2(jsonParams);
		Thread.sleep(10000);

		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();

		/*biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		logger.info("Query: " + query);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);*/
		List<Map<String, Object>> maps = biDB.selectBIAccountActivityTableByAdjustAmtinASCOrder(usaID);
		logger.info("BIAccountActivity Table Data is: " + maps);
		for (Map<String, Object> map : maps) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		if (environment.equalsIgnoreCase("SDN")) {
			
			//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(0), "C", " Actual TypeCD1: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "O", " Actual TypeCD2: " + actualdbTypeCD.get(1));
			//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					//"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -130.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					//"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}else {
			
			s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "C", " Actual TypeCD1: " + actualdbTypeCD.get(1));
			s_Assert.assertEquals(actualdbTypeCD.get(2), "O", " Actual TypeCD2: " + actualdbTypeCD.get(2));
			s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), 130.0,
					"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(2));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}
		// Passing json params and send Account History Request with usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);

		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verify response for transactionStatus, emailJson, lifetimePoints,
		// currentPoints
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

		emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
		s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
		s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
		s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);

	}

	@Story("atg")
	@TestRailID(id ={ "C13433353"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Lakshmi"},description = "")
	public void testSearchviaMultipleOptionaParameters(String environment) throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");

		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		orderNum = generateOrderNum();

		// Posting request with json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		jsonParams.put("$.purchase.date", date_Z);
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// posting request parameters and revoke the order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
		if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		response = atgCreateOrder.revokeOrderByV2(jsonParams);

		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();

		/*biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);*/
		
		
		listMapDetails = biDB.selectBIAccountActivityTableByAdjustAmtinASCOrder(usaID);
		logger.info("BIAccountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
		/*
		Thread.sleep(3000);
		listMapDetails = biDB.selectBIAccountActivityTable(usaID);
		logger.info("BIACcountActivity Table Data is: " + listMapDetails);
		for (Map<String, Object> map : listMapDetails) {
		*/
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		Thread.sleep(4000);
		if (environment.equalsIgnoreCase("SDN")) {
			
			//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(0), "C", " Actual TypeCD1: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "O", " Actual TypeCD2: " + actualdbTypeCD.get(1));
			//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					//"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -130.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					//"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}else {
			
			s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: " + actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "C", " Actual TypeCD1: " + actualdbTypeCD.get(1));
			s_Assert.assertEquals(actualdbTypeCD.get(2), "O", " Actual TypeCD2: " + actualdbTypeCD.get(2));
			s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: " + actualdbTypeCD.get(3));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0,
					"Actual AdjustAmount 0: " + actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0,
					"Actual AdjustAmount 1: " + actualdbACAAdjustAmount.get(1));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), 130.0,
					"Actual AdjustAmount 2: " + actualdbACAAdjustAmount.get(2));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0,
					"Actual AdjustAmount 3: " + actualdbACAAdjustAmount.get(3));
			
		}

		// Passing json params and send Account History Request
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);
		jsonParams.put("$.userAccount.email", email);
		jsonParams.put("$.userAccount.cardNumber", cardNumber);

		response = atgAccountHistory.getAccountHistory(jsonParams, "allParams");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

        if(environment.equalsIgnoreCase("SDN")) {
        	
    		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
    		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

    		emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
    		s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual email: " + emailJson);

    		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
    		s_Assert.assertEquals(0.0, lifetimePoints,
    				"lifetimepoints are not 0, Actual LifeTimePoints: " + lifetimePoints);

    		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
    		s_Assert.assertEquals(0.0, currentPoints, "current points are 0, Actual CurrentPoints: " + currentPoints);

    		// "$.accountHistoryLines[?(@.activityType == 'Cancelled order')].atgOrderID"
    		String OrderID_CancelOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[0].atgOrderID");
    		s_Assert.assertEquals(orderNum, OrderID_CancelOrder,
    				"orderNum: " + orderNum + "Actual OrderID_CancelOrder: " + OrderID_CancelOrder);

    		String activityType_CancelOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[0].activityType");
    		s_Assert.assertEquals(activityType_CancelOrder, "Cancelled order",
    				"Activity Type: cancelled Order, Actual : " + activityType_CancelOrder);

    		double points_Delta_CancelOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[0].pointsDelta");
    		s_Assert.assertEquals(points_Delta_CancelOrder, -130.0,
    				"Points reduced after order revoke: -30, Actual: " + points_Delta_CancelOrder);

    		double ytdDelta_CancelOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[0].ytdDelta");
    		s_Assert.assertEquals(ytdDelta_CancelOrder, -130.0,
    				"Points reduced after order revoke: -30, Actual: " + ytdDelta_CancelOrder);

    		String OrderID_OnlineOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].atgOrderID");
    		s_Assert.assertEquals(orderNum, OrderID_OnlineOrder,
    				"orderNum: " + orderNum + "Actual OrderID_OnlineOrder: " + OrderID_OnlineOrder);

    		String activityType_OnlineOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].activityType");
    		s_Assert.assertEquals(activityType_OnlineOrder, "Online order",
    				"Activity Type: Online Order, Actual: " + activityType_OnlineOrder);

    		double points_Delta_OnlineOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].pointsDelta");
    		s_Assert.assertEquals(points_Delta_OnlineOrder, 130.0,
    				"Points online reduced after order revoke: 30, Actual: " + points_Delta_OnlineOrder);

    		double ytdDelta_OnlineOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].ytdDelta");
    		s_Assert.assertEquals(ytdDelta_OnlineOrder, 130.0,
    				"Points reduced after order revoke: 30, Actual: " + ytdDelta_OnlineOrder);
        	
        }else {
    		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
    		s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");

    		emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
    		s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual email: " + emailJson);

    		lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
    		s_Assert.assertEquals(0.0, lifetimePoints,
    				"lifetimepoints are not 0, Actual LifeTimePoints: " + lifetimePoints);

    		currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
    		s_Assert.assertEquals(0.0, currentPoints, "current points are 0, Actual CurrentPoints: " + currentPoints);

    		// "$.accountHistoryLines[?(@.activityType == 'Cancelled order')].atgOrderID"
    		String OrderID_CancelOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].atgOrderID");
    		s_Assert.assertEquals(orderNum, OrderID_CancelOrder,
    				"orderNum: " + orderNum + "Actual OrderID_CancelOrder: " + OrderID_CancelOrder);

    		String activityType_CancelOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].activityType");
    		s_Assert.assertEquals(activityType_CancelOrder, "Cancelled order",
    				"Activity Type: cancelled Order, Actual : " + activityType_CancelOrder);

    		double points_Delta_CancelOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].pointsDelta");
    		s_Assert.assertEquals(points_Delta_CancelOrder, -130.0,
    				"Points reduced after order revoke: -30, Actual: " + points_Delta_CancelOrder);

    		double ytdDelta_CancelOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[1].ytdDelta");
    		s_Assert.assertEquals(ytdDelta_CancelOrder, -130.0,
    				"Points reduced after order revoke: -30, Actual: " + ytdDelta_CancelOrder);

    		String OrderID_OnlineOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[2].atgOrderID");
    		s_Assert.assertEquals(orderNum, OrderID_OnlineOrder,
    				"orderNum: " + orderNum + "Actual OrderID_OnlineOrder: " + OrderID_OnlineOrder);

    		String activityType_OnlineOrder = (String) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[2].activityType");
    		s_Assert.assertEquals(activityType_OnlineOrder, "Online order",
    				"Activity Type: Online Order, Actual: " + activityType_OnlineOrder);

    		double points_Delta_OnlineOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[2].pointsDelta");
    		s_Assert.assertEquals(points_Delta_OnlineOrder, 130.0,
    				"Points online reduced after order revoke: 30, Actual: " + points_Delta_OnlineOrder);

    		double ytdDelta_OnlineOrder = (double) extractJSON(response.getBody().asString(),
    				"$.accountHistoryLines[2].ytdDelta");
    		s_Assert.assertEquals(ytdDelta_OnlineOrder, 130.0,
    				"Points reduced after order revoke: 30, Actual: " + ytdDelta_OnlineOrder);
        }

	}

	@Story("atg")
	@TestRailID(id ={ "C13433352"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Lakshmi" }, description = "")
	public void testSearchViaInvalidUsaId(String environment) throws IOException {

		// Posting request for getAccountHistory
		String invalidUsaID = GenericUtil.getUniqueNumeric(10);
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", invalidUsaID);
		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verifying transactionStatus, customMsg, systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(ITestConstants.transactionStatusFailure, transactionStatus, "Transaction Status :: F");

		String customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals("Account Not Found for Account History", customerMsg,
				"Customer Message for invalidUsaId :: Account Not Found for Account History, ActualCustMsg: "
						+ customerMsg);

		String systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals("Account Not Found for Account History", systemMsg,
				"System Message for invalidUsaId :: Account Not Found for Account History, ActualSysMsg: " + systemMsg);

	}

	@Story("atg")
	@TestRailID(id ={ "C13433351"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Lakshmi" }, description = "")
	public void testSearchViaInvalidEmailId(String environment) throws IOException {

		// Posting request for getAccountHistory
		String newEmail = GenericUtil.generateEmail("yopmail.com", 15);
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.email", newEmail);

		response = atgAccountHistory.getAccountHistory(jsonParams, "email");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verifying transactionStatus, customMsg, systemMsg
		transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
		s_Assert.assertEquals(ITestConstants.transactionStatusFailure, transactionStatus, "Transaction Status :: F");

		String customerMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals("Account Not Found for Account History", customerMsg,
				"Customer Message for invalidEmailId :: Account Not Found for Account History, ActualCustMsg: "
						+ customerMsg);

		String systemMsg = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.customerMsg");
		s_Assert.assertEquals("Account Not Found for Account History", systemMsg,
				"System Message for invalidEmailId :: Account Not Found for Account History, ActualSysMsg: "
						+ systemMsg);

	}

	
/*
 * @Author - kanhaiya.chaubey
 * @description - Account History for Google Checkout	
 */
	@Story("atg")
	@TestRailID(id ={ "C13433350"})
	@Parameters({ "environment" })
	@Test(groups = { "atg", "regression", "Kanhaiya","sanity" }, description = "LT-1702- Google/YouTube - account history")
	public void accountHistoryForGoogleCheckout(String environment) throws IOException, SQLException, InterruptedException {

		HashMap<String, String> mapDetails = getNewCustomerDetails();
		String usaID = mapDetails.get("usaId");
		String email = mapDetails.get("emailId");
		String cardNumber = mapDetails.get("cardNumber");
		//test.get().log(Status.INFO, "usa ID : " + usaID + " email : " + email);
		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		String orderNum = generateOrderNum();

		// generate Data for online order
		jsonParams = new HashMap<String, Object>();
		date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
		orderNum = generateOrderNum();

		// Passing json params and send atgOrderCreate Request
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.channel", googleChannel);
		jsonParams.put("$.order.dateSK", generateDateSK());
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		jsonParams.put("$.purchase.date", date_Z);
		response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

		jsonParams = new HashMap<String, Object>();
		jsonParams.put("$.orderNum", orderNum);
		jsonParams.put("$.usaID", usaID);
		jsonParams.put("$.channel", googleChannel);
		response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

		// Pass parameters and post requesting revoke the order
		jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
		jsonParams.put("$.channel", googleChannel);
		jsonParams.put("$.order.orderNum", orderNum);
		jsonParams.put("$.purchase.date", date_Z);
		jsonParams.put(".itemLines[0].skuNumber", "487694");
		jsonParams.put(".itemLines[0].totalAmount", 65.0);
		jsonParams.put(".itemLines[1].skuNumber", "2210482");
		jsonParams.put(".itemLines[1].totalAmount", 65.0);
		jsonParams.put("$.order.subtotalAmt", 130.0);
if (environment.equalsIgnoreCase("SDN")) {
			
		}
		else {
			jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
		}
		
		response = atgCreateOrder.revokeOrderByV2(jsonParams);

		// BI Account Activity DB Validation
		List<Object> actualdbTypeCD = new ArrayList<Object>();
		List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
		List<Object> actualdbACAOrderID = new ArrayList<Object>();
		List<Object> actualdbACAUSAID = new ArrayList<Object>();

		biDBUtil.connectToBIDB();
		String query = BIQuery.getPointsBIAccountActivitytable;
		query = query.replace("%s", usaID);
		List<Map<String, Object>> maps = biDBUtil.readDatabase(query);
		logger.info("BIAccountActivity Table Data is: " + maps);
		for (Map<String, Object> map : maps) {
			actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
			actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
			actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
			actualdbACAUSAID.add(map.get("ACA_USA_ID"));
		}
		
		if(environment.equalsIgnoreCase("SDN")) { 
			
			//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: "+actualdbTypeCD.get(0));
			//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: "+actualdbTypeCD.get(3));
			s_Assert.assertEquals(actualdbTypeCD.get(0), "1", " Actual TypeCD0: "+actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "2", " Actual TypeCD1: "+actualdbTypeCD.get(1));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0, "Actual AdjustAmount 0: "+actualdbACAAdjustAmount.get(0));
			//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0, "Actual AdjustAmount 3: "+actualdbACAAdjustAmount.get(3));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), 130.0, "Actual AdjustAmount0: "+actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0, "Actual AdjustAmount 1: "+actualdbACAAdjustAmount.get(1));
			
			
		}else {
		s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: "+actualdbTypeCD.get(0));
		s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: "+actualdbTypeCD.get(3));
		s_Assert.assertEquals(actualdbTypeCD.get(1), "1", " Actual TypeCD1: "+actualdbTypeCD.get(1));
		s_Assert.assertEquals(actualdbTypeCD.get(2), "2", " Actual TypeCD2: "+actualdbTypeCD.get(2));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0, "Actual AdjustAmount 0: "+actualdbACAAdjustAmount.get(0));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0, "Actual AdjustAmount 3: "+actualdbACAAdjustAmount.get(3));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0, "Actual AdjustAmount1: "+actualdbACAAdjustAmount.get(1));
		s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), -130.0, "Actual AdjustAmount 2: "+actualdbACAAdjustAmount.get(2));
		}
		// Passing json params and send Account History Request with usaID
		jsonParams = new HashMap<>();
		jsonParams.put("$.userAccount.usaID", usaID);

		response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
		writeJsonInExtentReport(test.get(), response.getBody().asString());

		// Verify response for transactionStatus, emailJson, lifetimePoints,
		// currentPoints
		
		if(environment.equalsIgnoreCase("SDN")) {
			
			transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
			s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
			
			String location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[0].location");
			s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForGoogle, location, "accountHistoryLines[0].location for merch :: Google");
			location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[1].location");
			s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForGoogle, location, "accountHistoryLines[1].location for merch :: Google");
			
			
			emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
			s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

			lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
			s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

			currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
			s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
			
		}else {
			
			transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
			s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
			
			String location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[1].location");
			s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForGoogle, location, "accountHistoryLines[1].location for merch :: Google");
			location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[2].location");
			s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForGoogle, location, "accountHistoryLines[2].location for merch :: Google");
			
			
			emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
			s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

			lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
			s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

			currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
			s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
			
		}
		

	}
	
	
	
	/*
	 * @Author - kanhaiya.chaubey
	 * @description - Account History for Youtube	
	 */
		@Story("atg")
	    @TestRailID(id ={ "C13433350"})
		@Parameters({ "environment" })
		@Test(groups = { "atg", "regression", "Kanhaiya","sanity" }, description = "LT-1702- Google/YouTube - account history")
		public void accountHistoryForYoutube(String environment) throws IOException, SQLException, InterruptedException {

			HashMap<String, String> mapDetails = getNewCustomerDetails();
			String usaID = mapDetails.get("usaId");
			String email = mapDetails.get("emailId");
			String cardNumber = mapDetails.get("cardNumber");
			//test.get().log(Status.INFO, "usa ID : " + usaID + " email : " + email);
			// generate Data for online order
			jsonParams = new HashMap<String, Object>();
			String date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
			String orderNum = generateOrderNum();

			// generate Data for online order
			jsonParams = new HashMap<String, Object>();
			date_Z = DateHelper.getDesiredFormat(DateFormats.YYYYMMDDHHMMssSSSZ);
			orderNum = generateOrderNum();

			// Passing json params and send atgOrderCreate Request
			jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
			jsonParams.put("$.channel", youtubeChannel);
			jsonParams.put("$.order.dateSK", generateDateSK());
			jsonParams.put("$.order.orderNum", orderNum);
			jsonParams.put(".itemLines[0].skuNumber", "2210482");
			jsonParams.put(".itemLines[0].totalAmount", 65.0);
			jsonParams.put(".itemLines[1].skuNumber", "487694");
			jsonParams.put(".itemLines[1].totalAmount", 65.0);
			jsonParams.put("$.order.subtotalAmt", 130.0);
			if (environment.equalsIgnoreCase("SDN")) {
				
			}
			else {
				jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
			}
			
			jsonParams.put("$.purchase.date", date_Z);
			response = atgCreateOrder.createNewOrderWithatgOrdercreatev2(jsonParams);

			jsonParams = new HashMap<String, Object>();
			jsonParams.put("$.orderNum", orderNum);
			jsonParams.put("$.usaID", usaID);
			jsonParams.put("$.channel", youtubeChannel);
			response = atgCreateOrder.ProcessDelayedOrder(jsonParams);

			// Pass parameters and post requesting revoke the order
			jsonParams.put("$.userAccount.usaID", Long.parseLong(usaID));
			jsonParams.put("$.channel", youtubeChannel);
			jsonParams.put("$.order.orderNum", orderNum);
			jsonParams.put("$.purchase.date", date_Z);
			jsonParams.put(".itemLines[0].skuNumber", "2210482");
			jsonParams.put(".itemLines[0].totalAmount", 65.0);
			jsonParams.put(".itemLines[1].skuNumber", "487694");
			jsonParams.put(".itemLines[1].totalAmount", 65.0);
			jsonParams.put("$.order.subtotalAmt", 130.0);
			if (environment.equalsIgnoreCase("SDN")) {
				
			}
			else {
				jsonParams.put("$.purchase.itemLines[2].attrs..CBR", "CBR_10_500");
			}
			
			response = atgCreateOrder.revokeOrderByV2(jsonParams);

			// BI Account Activity DB Validation
			List<Object> actualdbTypeCD = new ArrayList<Object>();
			List<Object> actualdbACAAdjustAmount = new ArrayList<Object>();
			List<Object> actualdbACAOrderID = new ArrayList<Object>();
			List<Object> actualdbACAUSAID = new ArrayList<Object>();

			biDBUtil.connectToBIDB();
			String query = BIQuery.getPointsBIAccountActivitytable;
			query = query.replace("%s", usaID);
			List<Map<String, Object>> maps = biDBUtil.readDatabase(query);
			logger.info("BIAccountActivity Table Data is: " + maps);
			for (Map<String, Object> map : maps) {
				actualdbTypeCD.add(map.get("ACA_TYPE_CD"));
				actualdbACAAdjustAmount.add(map.get("ACA_ADJUST_AMT"));
				actualdbACAOrderID.add(map.get("ACA_ATG_ORDER_ID"));
				actualdbACAUSAID.add(map.get("ACA_USA_ID"));
			}
			if(environment.equalsIgnoreCase("SDN")) {
				
				//s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: "+actualdbTypeCD.get(0));
				//s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: "+actualdbTypeCD.get(3));
				s_Assert.assertEquals(actualdbTypeCD.get(0), "5", " Actual TypeCD0: "+actualdbTypeCD.get(0));
				s_Assert.assertEquals(actualdbTypeCD.get(1), "6", " Actual TypeCD1: "+actualdbTypeCD.get(1));
				//s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0, "Actual AdjustAmount 0: "+actualdbACAAdjustAmount.get(0));
				//s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0, "Actual AdjustAmount 3: "+actualdbACAAdjustAmount.get(3));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), 130.0, "Actual AdjustAmount0: "+actualdbACAAdjustAmount.get(0));
				s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), -130.0, "Actual AdjustAmount 1: "+actualdbACAAdjustAmount.get(1));
				
			}else {
			s_Assert.assertEquals(actualdbTypeCD.get(0), "R", " Actual TypeCD0: "+actualdbTypeCD.get(0));
			s_Assert.assertEquals(actualdbTypeCD.get(3), "B", " Actual TypeCD3: "+actualdbTypeCD.get(3));
			s_Assert.assertEquals(actualdbTypeCD.get(1), "5", " Actual TypeCD5: "+actualdbTypeCD.get(1));
			s_Assert.assertEquals(actualdbTypeCD.get(2), "6", " Actual TypeCD6: "+actualdbTypeCD.get(2));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(0), -500.0, "Actual AdjustAmount 0: "+actualdbACAAdjustAmount.get(0));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(3), 500.0, "Actual AdjustAmount 3: "+actualdbACAAdjustAmount.get(3));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(1), 130.0, "Actual AdjustAmount1: "+actualdbACAAdjustAmount.get(1));
			s_Assert.assertEquals(actualdbACAAdjustAmount.get(2), -130.0, "Actual AdjustAmount 2: "+actualdbACAAdjustAmount.get(2));
			}
			// Passing json params and send Account History Request with usaID
			jsonParams = new HashMap<>();
			jsonParams.put("$.userAccount.usaID", usaID);

			response = atgAccountHistory.getAccountHistory(jsonParams, "usaId");
			writeJsonInExtentReport(test.get(), response.getBody().asString());

			// Verify response for transactionStatus, emailJson, lifetimePoints,
			// currentPoints
			
			if(environment.equalsIgnoreCase("SDN")) {
				
				transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
				s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
				
				String location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[0].location");
				s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForYoutube, location, "accountHistoryLines[0].location for merch :: Youtube");
				location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[1].location");
				s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForYoutube, location, "accountHistoryLines[1].location for merch :: Youtube");
				
				
				emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
				s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

				lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
				s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

				currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
				s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
				
			}else {
				
				transactionStatus = (String) extractJSON(response.getBody().asString(), "$.transactionStatus.status");
				s_Assert.assertEquals(IExpectedMessage.transactionStatusSuccess, transactionStatus, "Transaction Status :: S");
				
				String location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[1].location");
				s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForYoutube, location, "accountHistoryLines[1].location for merch :: Youtube");
				location = (String) extractJSON(response.getBody().asString(), "$.accountHistoryLines[2].location");
				s_Assert.assertEquals(IExpectedMessage.accHistoryLocationForYoutube, location, "accountHistoryLines[2].location for merch :: Youtube");
				
				
				emailJson = (String) extractJSON(response.getBody().asString(), "$.userAccount.email");
				s_Assert.assertEquals(email, emailJson, "emailID: " + email + "Actual Email:" + emailJson);

				lifetimePoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.lifetimePoints");
				s_Assert.assertEquals(0.0, lifetimePoints, "lifetimepoints are not 0, Actual: " + lifetimePoints);

				currentPoints = (double) extractJSON(response.getBody().asString(), "$.userAccount.currentPoints");
				s_Assert.assertEquals(0.0, currentPoints, "current points are not 0, Actual: " + currentPoints);
				
			}
			

		}
	
}
