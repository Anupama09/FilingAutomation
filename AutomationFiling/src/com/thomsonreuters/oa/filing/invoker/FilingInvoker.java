package com.thomsonreuters.oa.filing.invoker;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import org.apache.log4j.Logger;

import com.thomsonreuters.oa.filing.invoker.ReadTestData;
import com.thomsonreuters.oa.filing.report.EdgarLogReport;
import com.thomsonreuters.oa.filing.resource.FilingInput;

public class FilingInvoker {

	private static final Logger logger = Logger.getLogger(FilingInvoker.class);

	public static void main(String[] args) throws BiffException, IOException,
			WriteException, InterruptedException, ParseException {

		logger.info("Entering Main()");

		List<FilingInput> DataList = new ArrayList<FilingInput>();
		String testcaseDirectory;
		String filingDataDirectory;
		String headerFileDirectory;

		long startTime = System.currentTimeMillis();
		if (args != null & args.length > 0) { 	 	
			testcaseDirectory = args[0];
			filingDataDirectory = args[1];
			headerFileDirectory = args[2];

			ReadTestData.readTestCaseData(testcaseDirectory,
					filingDataDirectory, headerFileDirectory, DataList);
			System.out.println("");
			System.out
					.println("---------Testcase scenarios are done..!! -----------------");
			System.out.println("");
		} else {
			logger.info("Input provided is empty");
			System.out.println("Input provided is empty");
		}

		EdgarLogReport.logAndReport(DataList);

		long endTime = System.currentTimeMillis();
		long totalTimeInms = endTime - startTime;
		long totalTimeInSec = (endTime - startTime) / 1000;
		long totalTimeInMints = TimeUnit.MILLISECONDS.toMinutes(totalTimeInms);

		logger.info("Total time in mints : " + totalTimeInMints);
		System.out.println("Execution Completed");
		System.out.println("Total time taken : " + totalTimeInMints + "Mints");
		System.out.println("--------------END-----------------");

	}
}
