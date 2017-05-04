package com.thomsonreuters.oa.filing.contentMatch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.thomsonreuters.oa.filing.dataAccessor.FilingDataAccessor;
import com.thomsonreuters.oa.filing.dataAccessor.OracleDAO;
import com.thomsonreuters.oa.filing.exception.FillerException;
import com.thomsonreuters.oa.filing.invoker.ReadTestData;
import com.thomsonreuters.oa.filing.invoker.TestScenarious;
import com.thomsonreuters.oa.filing.queries.FilingQueries;
import com.thomsonreuters.oa.filing.resource.FilingInput;
import com.thomsonreuters.oa.filing.util.EdgarInput;
import com.thomsonreuters.oa.filing.util.RandomGenerator;

public class EdgarFileGenerator {
	private static final Logger logger = Logger.getLogger(EdgarFileGenerator.class);
	Properties prop = new Properties();
	InputStream inputStream;

	public static String propFileName = "config/ErrorMsg.properties";
	FilingDataAccessor FilingDataAccessor = new FilingDataAccessor();
	EdgarInput edgarInput = new EdgarInput();
	boolean errorID;
	boolean ignoreID;
	Date filingDate;
	String serviceException;
	long edgarFileName = 0;
	long cikNo;
	String confirmedName;
	String requestId;
	String requestCmt;
	String filingRuleMrg;
	public static final String IdValue = "-1";

	TestScenarious testScenarious;
	EdgarFileGenerator edgerFileGenerator;

	public List<FilingInput> invoke(FilingInput FilingData, String headerFileDirectory, List<FilingInput> DataList)
			throws InterruptedException, FillerException, IOException {
		logger.info("Invoking data");
		System.out.println("Reading test data from the excel to create header file....");
		return generateRandomNumber(FilingData, headerFileDirectory, DataList);

	}

	private List<FilingInput> generateRandomNumber(FilingInput FilingData, String headerFileDirectory,
			List<FilingInput> DataList) throws InterruptedException, FillerException, IOException {
		logger.info("Entering generateRandomNumber() ");

		inputStream = new FileInputStream(propFileName);
		prop.load(inputStream);

		long Time = System.currentTimeMillis();

		if (FilingData.getActionType().equalsIgnoreCase("Insert")) {
			confirmedName = FilingData.getConformedName();

			if (!(confirmedName.length() > 0)) {
				confirmedName = "Thomson" + Time;
				FilingData.setConformedName(confirmedName);
				System.out.println(FilingData.getConformedName());
				
			}
			cikNo = FilingData.getCik();
			if (cikNo == 0) {
				cikNo = fileNameCikNoGenerateIfNotExistance(8, "cikNoCheck", FilingDataAccessor);
				FilingData.setCik(cikNo);

			}
		} else if (FilingData.getActionType().equalsIgnoreCase("Update")) {

			FilingData.setIsInfValid(0);

			FilingDataAccessor.fetchDataForUpdate(FilingData, FilingData.getIsInfValid(), FilingQueries.ORGINFO);
			confirmedName = FilingData.getConformedName();
			cikNo = FilingData.getCik();

		}

		edgarFileName = fileNameCikNoGenerateIfNotExistance(8, "edgarFileCheck", FilingDataAccessor);

		long irs = FilingData.getIrsNumber();
		if (irs == 0) {
			irs = fileNameCikNoGenerateIfNotExistance(7, "irisNoCheck", FilingDataAccessor);
			FilingData.setIrsNumber(irs);

		}

		FilingData.setEdgarFileName(edgarFileName);

		// Edger File Creation
		edgarInput.getEdgarInfo(FilingData, headerFileDirectory);

		OracleDAO msg = null;
		Thread.sleep(60000);

		Integer docId = FilingDataAccessor.dbQueryToFetchDocIdValue(edgarFileName, FilingQueries.DOCID);
		if (docId != null) {
			FilingData.setDocId(String.valueOf(docId));
			System.out.println("Verifying header data in asscollected table.....");
			Thread.sleep(50000);
			String isAutoLock = FilingDataAccessor.fetchDataFromAscollect(FilingData, edgarFileName,
					"is_locked_for_autocreate", FilingQueries.IS_LOCKED_FOR_AUTOCREATE);

			if (isAutoLock != null && !(isAutoLock.equals(-1))) {
				FilingData.setIsAutoLock(isAutoLock);
				String entityid = null;
				try {
					entityid = FilingDataAccessor.fetchDataFromAscollect(FilingData, edgarFileName, "ife_entityid",
							FilingQueries.ENTITYID);

				} catch (Exception e) {
					e.getMessage();
				}

				if (entityid != null && entityid.length() > 0) {
					FilingData.setEntityId(entityid);
					requestId = FilingDataAccessor.fetchDataFromAscollect(FilingData, edgarFileName, "request_id",
							FilingQueries.REQUESTID);
					FilingData.setRequestId(requestId);
					
					FilingData.setComments("RequestID is present  : " + requestId);

				} else {

					errorID = FilingDataAccessor.dbQuery(edgarFileName, FilingQueries.ERRORID);
					if (errorID == true) {
						try {
							FilingData = msg.FilingRuleMsgFromEdgerErrorLog(FilingData, "filings_rule_msg",
									edgarFileName, FilingQueries.FILINGS_RULE_MSG);

							filingRuleMrg = FilingData.getFilingRuleMsg();
							System.out.println("filingRuleMrg : " + filingRuleMrg);

							FilingData.setComments(filingRuleMrg);

							System.out.println("FilingRuleMsgFromEdgerErrorLog: " + FilingData.getComments());
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						ignoreID = FilingDataAccessor.dbQuery(edgarFileName, FilingQueries.IGNOREID);

						if (ignoreID == true) {

							FilingData.setComments("EntityID present in Ignore data table ");

						}
					}

				}

			} else {
				serviceException = prop.getProperty("InjestionLevelException");
				FilingData.setComments(serviceException);
				throw new FillerException(serviceException);

			}
		} else {

			if (docId == null && cikNo > 10) {
				return DataList;

			} else if (docId == null) {

				ignoreID = FilingDataAccessor.dbQuery(edgarFileName, FilingQueries.IGNOREID);

				if (ignoreID == true) {
					return DataList;
				}

			} else {

				serviceException = prop.getProperty("ServerLevelException");

				throw new FillerException(serviceException);

			}
		}
		return DataList;

	}

	// Insert/OAUI_TC001/Verify address is verified address for New Create
	// Entity(ORG_HQ_IS_INF_VALID=1)
	public FilingInput CreateEntityWithInformaticaVerifiedAddress(List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		String entityId = fileData.get(0).getEntityId();
		for (Iterator<FilingInput> iterator = fileData.iterator(); iterator.hasNext();) {
			FilingInput filingInput = iterator.next();

			if (filingInput.getEntityId() != null && filingInput.getEntityId().length() > 1) {
				long hqValid = 3;
				hqValid = FilingDataAccessor.fetchDataToValidateEntity(filingData,

						FilingQueries.ORG_HQ_IS_INF_VALID, entityId);

				if (hqValid == 1) {
					filingData.setResult("PASS");
					System.out.println("TestCase: "+filingData.getTestCaseId());
					System.out.println("Testcase description: Entity created With Verifie Address");
					
					filingData.setComments("--");

				} else {
					filingData.setResult("FAILED");
					filingData.setComments(
							"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
					System.out.println("Testcase description : Failed to create Entity with Verified Address");
				}
				
			}else{
				filingData.setResult("FAILED");
				filingData.setComments(
						"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description : Failed to create Entity ");
			}
			
		}

		System.out.println("----------------------------------");
		return filingData;

	}

	// Insert/OAUI_TC002/Verify address is Informatica Unverified address for
	public FilingInput CreateEntityWithInformaticaUnVerifiedAddress(List<FilingInput> fileData,
			FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		// if(fileData!=null && fileData.size()<2){
		String entityId = fileData.get(0).getEntityId();

		System.out.println("TestCaseID: "+filingData.getTestCaseId());
		// System.out.println(filingData.getEntityId());
		for (Iterator<FilingInput> iterator = fileData.iterator(); iterator.hasNext();) {
			FilingInput filingInput = iterator.next();

			if (filingInput.getEntityId() != null && filingInput.getEntityId().length() > 2) {
				long hqValid = 3;
				hqValid = FilingDataAccessor.fetchDataToValidateEntity(filingData,

						FilingQueries.ORG_HQ_IS_INF_VALID, entityId);

				if (hqValid == 0) {
					filingData.setResult("PASS");
					System.out.println("Testcase description : Entity created With UnVerifie Address");
					filingData.setComments("--");

				} else {
					filingData.setResult("FAILED");
					filingData.setComments(
							"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
					System.out.println("Testcase description : Failed to create Entity with UnVerified Address");
				}

			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Failed: Entity not created..!");

			}
		}

		System.out.println("----------------------------------");

		return filingData;

	}

	// OAUI_TC003// Verify verified address changed to unverified address for
	// verified entity
	public FilingInput UpdateVerifiedAddressToUnverifiedAddressForVerifiedEntity(List<FilingInput> DataList,
			FilingInput filingData) {
		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {
			long hqValid = 3;
			hqValid = FilingDataAccessor.fetchDataToValidateEntity(filingData,

					FilingQueries.ORG_HQ_IS_INF_VALID, filingData.getEntityId());

			if (hqValid == 0) {
				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description : Updated Verified Address To Unverified Address For Verified Entity");
			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description : Failed to Update Verified Address To Unverified Address For Verified Entity");
			}
		}

		System.out.println("----------------------------------");
		return filingData;
	}

	// OAUI_TC004
	public FilingInput UpdateUnverifiedAddressToVerifiedAddressForVerifiedEntity(List<FilingInput> DataList,
			FilingInput filingData) {
		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {
			long hqValid = 3;
			hqValid = FilingDataAccessor.fetchDataToValidateEntity(filingData,

					FilingQueries.ORG_HQ_IS_INF_VALID, filingData.getEntityId());

			if (hqValid == 1) {
				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description : Updated Unverified Address to verified Address for VerifiedEntity");
			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description: Failed to  Update Unverified Address To verified Address For VerifiedEntity");
			}
		}

		System.out.println("----------------------------------");
		return filingData;
	}

	// Verify Entity not to be created with SIC and filing date less than 1987
	// and requestId should need to generate( select * from
	// domains.industry_scheme_date_rang)
	// OAUI_TC005
	public FilingInput EntityNotToBeCreatedAndRequestIDNeedToBeGererateWhenSICandFilingDateIsLessThan1Jan1987(
			List<FilingInput> fileData, FilingInput FilingData) throws IOException {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		inputStream = new FileInputStream(propFileName);
		prop.load(inputStream);

		String entityID = FilingData.getEntityId();
		if (entityID != null && entityID.equalsIgnoreCase(IdValue)) {

			long filename = FilingData.getEdgarFileName();
			String filingDate = FilingDataAccessor.fetchDataFromAscollect(FilingData, filename, "filing_date",
					FilingQueries.FILINGDATE);

			int filingYear = Integer.parseInt(filingDate.substring(0, 4));

			requestId = FilingData.getRequestId();

			// System.out.println("Request ID : " + requestId);

			if (filingYear < 1987 && !(requestId == (IdValue))) {
				FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(FilingData, requestId,
						"requester_comments", FilingQueries.REQUESTER_COMMENTS));

				requestCmt = i.getRequestCmt();

				// System.out.println("Request Comment : " + requestCmt);
				// System.out.println(prop.getProperty("SICrequestCmt"));
				if (requestCmt.contains(prop.getProperty("SICrequestCmt"))) {
					FilingData.setResult("PASS");
					FilingData.setComments("--");
					System.out
							.println("Testcase description :Created Entity With SIC and Filingdate Less Than Jan 1987");
				} else {
					FilingData.setResult("FAILED");
					FilingData.setComments(
							"Testcase description : Failed to  generating RequestID with requester comment : "
									+ requestCmt);
					System.out.println(
							"Testcase description: Failed to Create Entity With SIC and Filingdate Less Than Jan1987");

				}

			} else {
				FilingData.setResult("FAILED");
				FilingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description: Failed to Create Entity With SIC and Filingdate Less Than Jan1987");
			}
		}
		System.out.println("----------------------------------");

		return FilingData;
	}

	// OAUI_TC006
	public FilingInput EntityNeedToCreateWithoutRequestIDWhenSICandFilingDateIsGreaterThan1Jan1987(
			List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String filingDate = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "filing_date",
					FilingQueries.FILINGDATE);
			// filingDate.substring(0, 10);

			int filingYear = Integer.parseInt(filingDate.substring(0, 4));

			int filingMonth = Integer.parseInt(filingDate.substring(5, 7));
			int filingDay = Integer.parseInt(filingDate.substring(8, 10));
			int Calyear = Calendar.getInstance().get(Calendar.YEAR);

			/*
			 * System.out.println("Filing year : " + filingYear);
			 * System.out.println("Calendar Year : " + Calyear);
			 * 
			 * System.out.println("Day :" + filingDay);
			 * System.out.println("Month : " + filingMonth);
			 */

			requestId = filingData.getRequestId();

			System.out.println("Request ID : " + requestId);

			if (filingYear > 1987 && filingYear < Calyear) {
				// if(filingDay>1 && filingMonth>=1){

				if (requestId.equalsIgnoreCase("-1")) {
					filingData.setResult("PASS");
					filingData.setComments("--");
					System.out.println(
							"Testcase description :Created Entity With SIC and Filingdate Than greater Jan 1987");

				} else {

					FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId,
							"requester_comments", FilingQueries.REQUESTER_COMMENTS));
					requestCmt = i.getRequestCmt();

					if (!(requestCmt.contains(prop.getProperty("SICrequestCmt")))) {
						filingData.setResult("FAILED");
						filingData.setComments(
								"Testcase description : Failed by gererating RequestID with requester comment : "
										+ requestCmt);
						System.out.println("Testcase description: Failed -RequestID generated for requester comment : "
								+ requestCmt);

						filingData.setResult("FAILED");
						filingData.setComments(
								"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
						// System.out.println("Testcase description: Failed to
						// Create Entity With SIC and Filingdate greater Than
						// Jan1987");
					}
				}
			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description: Failed to Create Entity With SIC and Filingdate greater Than Jan1987");

			}
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC007
	public FilingInput AnalystRequestToBeGeneratedWhenPhoneNumberIsLessThan10digits(List<FilingInput> fileData,
			FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String phoneNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "phone",
					FilingQueries.PHONE);

			// System.out.println("Phone : " + phoneNumber);

			requestId = filingData.getRequestId();

			// System.out.println("Request ID : " + requestId);
			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();

			// System.out.println(phoneNumber.length());

			if (phoneNumber.length() < 10 && (!requestId.equalsIgnoreCase("-1"))) {
				// if ((requestCmt.contains("Check Phone Number"))) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description :Analyst Request Generated for phone When PhoneNumber Is Less Than 10 digits");
				/*
				 * } else { FilingData.setResult("FAILED");
				 * System.out.println("Testcase Failed : " + requestCmt);
				 * 
				 * }
				 */

			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description:Failed to Generate Analyst Request for phone When PhoneNumber Is Less Than 10 digits :  "
								+ requestCmt);

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC008
	public FilingInput AnalystRequestToBeGeneratedWhenPhoneNumberIsGreaterThan10digits(List<FilingInput> fileData,
			FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String phoneNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "phone",
					FilingQueries.PHONE);

			// System.out.println("Phone : " + phoneNumber);

			requestId = filingData.getRequestId();

			// System.out.println("Request ID : " + requestId);
			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();

			// System.out.println(phoneNumber.length());

			// System.out.println(prop.getProperty("PhoneRequestCmt"));

			if (phoneNumber.length() > 10 && (!requestId.equalsIgnoreCase("-1")
					&& (requestCmt.contains(prop.getProperty("PhoneRequestCmt"))))) {
				// if ((requestCmt.contains("Check Phone Number"))) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"estcase description :Analyst Request Generated for phone When PhoneNumber Is grater than 10 digits");
				/*
				 * } else { FilingData.setResult("FAILED");
				 * System.out.println("Testcase Failed : " + requestCmt);
				 * 
				 * }
				 */

			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description:Failed to Generate Analyst Request for phone When PhoneNumber Is Less Than 10 digits :  "
								+ requestCmt);

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC009
	public FilingInput AnalystRequestNotToBeGeneratedWhenPhoneNumbersIsEqaulTo10digitsAndNotStartsWith1(
			List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String phoneNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "phone",
					FilingQueries.PHONE);

			System.out.println("Phone : " + phoneNumber);

			requestId = filingData.getRequestId();

			System.out.println("Request ID : " + requestId);
			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();

			System.out.println(phoneNumber.length());

			if (phoneNumber.length() == 10 && (!phoneNumber.startsWith("1")) && (requestId.equalsIgnoreCase("-1"))) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description :Entity created without analyst request when Phone number is equal to 10 digits and starts without 1");
			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description: Failed : :Entity created with analyst request when Phone number is equal to 10 digits and starts without 1: "
								+ requestCmt);

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC0010
	public FilingInput AnalystRequestToBeGeneratedWhenPhoneNumbersIsEqaulTo10digitsAndStartsWith1(
			List<FilingInput> fileData, FilingInput filingData) throws IOException {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		inputStream = new FileInputStream(propFileName);
		prop.load(inputStream);
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String phoneNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "phone",
					FilingQueries.PHONE);

			// System.out.println("Phone : " + phoneNumber);

			requestId = filingData.getRequestId();

			// System.out.println("Request ID : " + requestId);
			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();
			// System.out.println(requestCmt);
			// System.out.println(prop.getProperty("PhoneRequestCmt"));
			// System.out.println(phoneNumber.length());

			if (phoneNumber.length() == 10 && (phoneNumber.startsWith("1")) && (!requestId.equalsIgnoreCase("-1")
					&& (requestCmt.contains(prop.getProperty("PhoneRequestCmt"))))) {
				// if ((requestCmt.contains("Check Phone Number"))) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description :Entity created with analyst request when Phone number is equal to 10 digits and starts with 1");
				/*
				 * } else { FilingData.setResult("FAILED");
				 * System.out.println("Testcase Failed : " + requestCmt);
				 * 
				 * }
				 */

			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description: Failed to genarate Analyst Request When Phone number Is Eqaul To 10 digits And Starts With 1  ");

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC0011
	public FilingInput VerifyEntityToBeCreatedWithNewIRSnumber(List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			long irsNum = filingData.getIrsNumber();

			System.out.println("irsNumber : " + irsNum);

			if (irsNumber.length() > 0 && irsNumber != null) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println("Testcase description : Entity Created With New IRS number");

			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description: Failed to Create new IRS number");

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC0012
	public FilingInput VerifyEntiytIsNotCreatedForStateAsNull(List<FilingInput> fileData, FilingInput filingData)
			throws IOException {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		inputStream = new FileInputStream(propFileName);
		prop.load(inputStream);

		String entityID = filingData.getEntityId();
		if (entityID != null && entityID.equalsIgnoreCase(IdValue)) {

			requestId = filingData.getRequestId();

			// System.out.println("Request ID : " + requestId);

			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();

			// System.out.println("Request Comment : " + requestCmt);
			// System.out.println(prop.getProperty("ExceptionForStateAsNull"));

			if (requestCmt.contains(prop.getProperty("ExceptionForStateAsNull"))) {
				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println("Testcase description :Entity Not Created For State As Null");
			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description: Failed :" + requestCmt);
			}
		} else {

			filingData.setResult("FAILED");
			filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
			System.out.println("Testcase description: Failed as entity is created : " + entityID);

		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC0013
	public FilingInput VerifyAddressIsVerifiedAddressWhenEdgarCityIsNullforVerifiedEntity(List<FilingInput> fileData,
			FilingInput filingData) {
		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		requestId = filingData.getRequestId();
		// && requestId != IdValue
		if (filingData.getEntityId() != null && filingData.getEntityId() != IdValue) {
			long hqValid = 3;
			hqValid = FilingDataAccessor.fetchDataToValidateEntity(filingData,

					FilingQueries.ORG_HQ_IS_INF_VALID, filingData.getEntityId());

			if (hqValid == 1) {
				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description : Address Is Verified Address When Edgar City Is Null for Verified Entity");
			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println(
						"Testcase description:Failed: Address Is UnVerified Address When Edgar City Is Null for Verified Entity");
			}
		}

		System.out.println("----------------------------------");
		return filingData;

	}

	// OAUI_TC0014
	public FilingInput VerifyOldSICinHistoryTableWhenNewSICadded(List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			long filename = filingData.getEdgarFileName();
			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			long irsNum = filingData.getIrsNumber();

			System.out.println("irsNumber : " + irsNum);

			if (irsNumber.length() > 0 && irsNumber != null) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println("Testcase description : Entity Created With New IRS number");

			} else {

				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description: Failed to Create new IRS number");

			}

		} else {
			System.out.println("Failed : Entity not created ..");
		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC015
	public FilingInput VerifyEntityNotToBeUpdatedWithExistingIRSnumberWhichIsMappedToAnotherEntity(
			List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		return filingData;
	}

	// OAUI_TC016
	public FilingInput VerifyEntityShouldNotCreateIfCIKgreaterThan10Digits(List<FilingInput> fileData,
			FilingInput FilingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		errorID = FilingDataAccessor.dbQuery(FilingData.getEdgarFileName(), FilingQueries.ERRORID);
		ignoreID = FilingDataAccessor.dbQuery(FilingData.getEdgarFileName(), FilingQueries.IGNOREID);

		if (FilingData.getEntityId() == null && errorID == false && ignoreID == false) {

			FilingData.setResult("PASS");
			FilingData.setComments("--");
			System.out.println("Testcase description : Pass: Entity not create for CIK greater than 10 digits");

		} else {

			FilingData.setResult("FAILED");
			FilingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
			System.out.println("Testcase description: Failed : Entity created for CIK greater than 10 digits");

		}

		return FilingData;
	}

	// OAUI_TC017
	public FilingInput VerifyEntityShouldNotCreateAndRequestShouldGeneratedForHeaderHavingConfirmedNameWhichIsAlreadyExistingWithDifferentCIK(
			List<FilingInput> fileData, FilingInput filingData) throws IOException {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		inputStream = new FileInputStream(propFileName);
		prop.load(inputStream);

		String entityID = filingData.getEntityId();
		if (entityID != null) {

			requestId = filingData.getRequestId();

			// System.out.println("Request ID : " + requestId);

			FilingInput i = (FilingDataAccessor.FetchingDataFrmRequestQueue(filingData, requestId, "requester_comments",
					FilingQueries.REQUESTER_COMMENTS));
			requestCmt = i.getRequestCmt();

			// System.out.println("Request Comment : " + requestCmt);
			// System.out.println(prop.getProperty("ExceptionForStateAsNull"));

			if (requestCmt.contains(prop.getProperty("DuplicateConformedName"))) {
				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println(
						"Testcase description:PASS :Entity Not Created For duplicate conformed name which has different CIK");
			} else {
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description: Failed :" + requestCmt);
			}
		} else {

			filingData.setResult("FAILED");
			filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
			System.out.println("Testcase description: Failed as entity is created : " + entityID);

		}

		System.out.println("----------------------------------");

		return filingData;
	}

	// OAUI_TC018
	public FilingInput VerifyDuplicateDataShouldMoveToINGOREtable(List<FilingInput> fileData, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		edgerFileGenerator = new EdgarFileGenerator();

		// Call TC01 test case to create entity
		// FilingInput InsetrData =
		// edgerFileGenerator.CreateEntityWithInformaticaVerifiedAddress(fileData,
		// FilingData);

		FilingInput UpdateData = edgerFileGenerator.CreateEntityWithInformaticaVerifiedAddress(fileData, filingData);

		ignoreID = FilingDataAccessor.dbQuery(UpdateData.getEdgarFileName(), FilingQueries.IGNOREID);

		if (UpdateData.getEntityId() == null && ignoreID == true) {

			filingData.setResult("PASS");
			filingData.setComments("--");
			System.out.println(
					"Testcase description : Pass: Entity not created and header data move to IGNORE table for Assigned_SIC=8880");

		} else {

			filingData.setResult("FAILED");
			filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
			System.out.println("Testcase description: Failed :");

		}

		return filingData;

	}

	// OAUI_TC019
	public FilingInput VerifyHeaderDataShouldMoveToIGNOREtableForAssigned_SIC8880(List<FilingInput> fileData,
			FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		ignoreID = FilingDataAccessor.dbQuery(filingData.getEdgarFileName(), FilingQueries.IGNOREID);

		if (filingData.getEntityId() == null && ignoreID == true) {

			filingData.setResult("PASS");
			filingData.setComments("--");
			System.out.println(
					"Testcase description : Pass: Entity not created and header data move to IGNORE table for Assigned_SIC=8880");

		} else {

			filingData.setResult("FAILED");
			filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
			System.out.println("Testcase description: Failed : Entity created for Assigned_SIC=8880");

		}

		return filingData;
	}

	// OAUI_TC020
	public FilingInput VerifyTAXIDaddedForNewEntityUsingIRSnumber(List<FilingInput> dataList, FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			System.out.println(filingData.getEntityId());

			long filename = filingData.getEdgarFileName();
			long entityID = Long.parseLong(filingData.getEntityId());
			// long irsNumber=FilingData.getIrsNumber();

			String TAXID = FilingDataAccessor.fetchDataFromAscollect(filingData, entityID, "org_cp_tax_file_id",
					FilingQueries.ORG_CP_TAX_FILE_ID);

			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			// System.out.println("irsNumber : " + FilingData.getIrsNumber());
			// System.out.println("TAXID : " + TAXID );

			if (irsNumber.length() > 0 && irsNumber != null && TAXID.length() > 0 && TAXID != null) {

				if (irsNumber.equalsIgnoreCase(TAXID)) {

					filingData.setResult("PASS");
					filingData.setComments("--");
					System.out.println("Testcase description : Entity Created With New IRS number");

				} else {

					filingData.setResult("FAILED");
					filingData.setComments(
							"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
					System.out.println("Testcase description: Failed");

				}

			} else {
				System.out.println("Failed :TAX ID not gererated ..");
			}

			System.out.println("----------------------------------");
		}
		return filingData;

	}

	// OAUI_TC021
	public FilingInput VerifyTAXIDAddedForExistingEntityWhichIsNotHaivngTAXIDusingIRSNumber(List<FilingInput> fileData,
			FilingInput filingData) {

		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			System.out.println(filingData.getEntityId());

			long filename = filingData.getEdgarFileName();
			long entityID = Long.parseLong(filingData.getEntityId());
			// long irsNumber=FilingData.getIrsNumber();

			String TAXID = FilingDataAccessor.fetchDataFromAscollect(filingData, entityID, "org_cp_tax_file_id",
					FilingQueries.ORG_CP_TAX_FILE_ID);

			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			// System.out.println("irsNumber : " + FilingData.getIrsNumber());
			// System.out.println("TAXID : " + TAXID );

			if (irsNumber.length() > 0 && irsNumber != null && TAXID.length() > 0 && TAXID != null) {

				if (irsNumber.equalsIgnoreCase(TAXID)) {

					filingData.setResult("PASS");
					filingData.setComments("--");
					System.out.println("Testcase description : Entity updated With New IRS number");

				} else {

					filingData.setResult("FAILED");
					filingData.setComments(
							"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
					System.out.println("Testcase description: Failed");

				}

			} else {
				System.out.println("Failed :TAX ID not gererated ..");
			}

			System.out.println("----------------------------------");
		}
		return filingData;

	}

	// OAUI_TC022
	public FilingInput VerifyTAXIDAddedForExistingEntityWhichIsHaivngTAXIDusingIRSNumber(List<FilingInput> fileData,
			FilingInput filingData) {
		System.out.println("Verifying  entity created/updated  based on the SEF result...");
		String entityId = fileData.get(0).getEntityId();
		System.out.println(entityId);

		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			System.out.println(filingData.getEntityId());

			long filename = filingData.getEdgarFileName();
			long entityID = Long.parseLong(filingData.getEntityId());
			// long irsNumber=FilingData.getIrsNumber();

			String TAXID = FilingDataAccessor.fetchDataFromAscollect(filingData, entityID, "org_cp_tax_file_id",
					FilingQueries.ORG_CP_TAX_FILE_ID);

			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			System.out.println("TAXID:" + TAXID);
			System.out.println("IRS_NUMBER" + irsNumber);

			// System.out.println("irsNumber : " + FilingData.getIrsNumber());
			// System.out.println("TAXID : " + TAXID );

			if (irsNumber.length() > 0 && irsNumber != null && TAXID.length() > 0 && TAXID != null) {

				if (irsNumber.equalsIgnoreCase(TAXID)) {

					filingData.setResult("PASS");
					filingData.setComments("--");
					System.out.println("Testcase description : Entity updated With New IRS number");

				} else {

					filingData.setResult("FAILED");
					filingData.setComments(
							"<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
					System.out.println("Testcase description: Failed");

				}

			} else {
				System.out.println("Failed :TAX ID not gererated ..");
			}

			System.out.println("----------------------------------");
		}
		return filingData;
	}

	// OAUI_TC023
	public FilingInput VerifyTAXIDnotUpdatedWithNullForExistingEntityWhichIsHavingTAXIDusingIRSnumberAsNull(
			List<FilingInput> fileData, FilingInput filingData) {
		System.out.println("Verifying  entity created/updated  based on the SEF result...");

		String entityId = fileData.get(0).getEntityId();
		System.out.println(entityId);

		if (filingData.getEntityId() != null && filingData.getEntityId().length() > 1) {

			System.out.println(filingData.getEntityId());

			long filename = filingData.getEdgarFileName();
			long entityID = Long.parseLong(filingData.getEntityId());
			// long irsNumber=FilingData.getIrsNumber();

			String TAXID = FilingDataAccessor.fetchDataFromAscollect(filingData, entityID, "org_cp_tax_file_id",
					FilingQueries.ORG_CP_TAX_FILE_ID);

			String irsNumber = FilingDataAccessor.fetchDataFromAscollect(filingData, filename, "irs_number",
					FilingQueries.IRS_NUMBER);

			System.out.println("irsNumber : " + filingData.getIrsNumber());
			System.out.println("TAXID : " + TAXID);

			if (irsNumber == null && TAXID.length() > 0 && TAXID != null) {

				filingData.setResult("PASS");
				filingData.setComments("--");
				System.out.println("Testcase description :PASS: Entity not updated With Null value");

			} else {
				// System.out.println("Failed :TAX ID not gererated ..");
				filingData.setResult("FAILED");
				filingData.setComments("<HTML><a href=" + ReadTestData.logFilepath.toString() + ">Log Path</a></HTML>");
				System.out.println("Testcase description: Failed");

			}

			System.out.println("----------------------------------");
		}
		return filingData;
	}


	private long fileNameCikNoGenerateIfNotExistance(int noOfDigit, String input,
			FilingDataAccessor FilingDataAccessor) {
		long randomNoOutput;
		RandomGenerator randomGenerator = new RandomGenerator();
		while (true) {
			randomNoOutput = randomGenerator.randomGenerator(noOfDigit);
			boolean checkIfExist = existanceCheck(randomNoOutput, input, FilingDataAccessor);
			if (!checkIfExist) {
				break;
			}
		}
		return randomNoOutput;
	}

	private boolean existanceCheck(long randomNoOutput, String input, FilingDataAccessor FilingDataAccessor) {
		switch (input) {
		case "edgarFileCheck":
			boolean edgarFileNameExist = FilingDataAccessor.dbQuery(randomNoOutput, FilingQueries.EDGARFILENAME);
			return edgarFileNameExist;
		case "cikNoCheck":
			boolean cikN0Exist = FilingDataAccessor.dbQuery(String.valueOf(randomNoOutput), FilingQueries.CIKNO);
			return cikN0Exist;
		case "irisNoCheck":
			boolean irisN0Exist = FilingDataAccessor.dbQuery(String.valueOf(randomNoOutput), FilingQueries.IRISNO);
			return irisN0Exist;
		}
		return true;
	}

}
