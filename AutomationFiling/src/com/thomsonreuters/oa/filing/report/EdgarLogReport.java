package com.thomsonreuters.oa.filing.report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import com.thomsonreuters.oa.filing.invoker.ReadTestData;
import com.thomsonreuters.oa.filing.resource.FilingInput;

public class EdgarLogReport {
	
	public static void logAndReport(List<FilingInput> DataList) {
		PrintWriter out = null;
		// System.out.println("");
		System.out.println("Writing a log file...");

		for (Iterator<FilingInput> iterator = DataList.iterator(); iterator
				.hasNext();) {

			FilingInput logPojo1 = iterator.next();
			
			String testCaseID = logPojo1.getTestCaseId();
			String actionType = logPojo1.getActionType();
			long cik = logPojo1.getCik();
			// System.out.println("CIK : " + cik);
			String docId = logPojo1.getDocId();
			// System.out.println("DOCID : " + docId);
			String entityId = logPojo1.getEntityId();
			// System.out.println("entityId : " + entityId);
			String exception = logPojo1.getFilingException();
			// System.out.println("Exception : " + exception);
			String requestId = logPojo1.getRequestId();
			// System.out.println("requestId : " + requestId);
			String result = logPojo1.getResult();
			// System.out.println("Result : " + result);
		
			

			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(
						ReadTestData.logFilepath, true)));

				out.println("-----------------Start----------------------");
				out.println();
				out.println("TestCaseID: "+testCaseID);
				out.println("Action Type: " + actionType);
				out.println("CIK : " + cik);
				out.println("DocId: " + docId);
				out.println("EntityID: " + entityId);
				// out.println("Exception: " + exception);
				out.println("RequestId: " + requestId);
				out.println("Result :" + result);

				out.println("-----------------END----------------------");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.close();
			}

			VelocityHtmlReportGeneration velocityHtmlReportGeneration = new VelocityHtmlReportGeneration();
			velocityHtmlReportGeneration.reportGeneration(DataList);

		}

	}

}
