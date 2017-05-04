package com.thomsonreuters.oa.filing.invoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.thomsonreuters.oa.filing.contentMatch.EdgarFileGenerator;
import com.thomsonreuters.oa.filing.exception.FillerException;
import com.thomsonreuters.oa.filing.resource.FilingInput;

public class ReadTestData {

	static Properties prop = new Properties();
	// static String propFileName = "ErrorMsg.properties";

	static DateFormat format = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
	static String timeStamp = format.format(new Date());

	private static final Logger logger = Logger.getLogger(ReadTestData.class);
	static PrintWriter out;
	public static File logFilepath = new File("C:\\FilingAutomation\\Log\\log_"
			+ timeStamp + ".txt");

	ReadTestData readDataInvoker;

	static EdgarFileGenerator edFileGenerator = new EdgarFileGenerator();

	static Workbook TestcasesWorkbook;

	static Row sheet1row;

	public static void readTestCaseData(String TestcaseDirectory,
			String FilingDataDirectory, String headerFileDirectory,
			List<FilingInput> DataList) throws BiffException, IOException,
			WriteException, InterruptedException, ParseException {
		logger.info("Enter readTestCaseData()");
		FilingInput FilingData = null;
		FileInputStream file = new FileInputStream(TestcaseDirectory);
		TestcasesWorkbook = new XSSFWorkbook(file);

		// Get first sheet from the workbook
		XSSFSheet sheet = (XSSFSheet) TestcasesWorkbook.getSheetAt(0);
		// Iterate through each rows from first sheet
		Iterator<Row> rowIterator = sheet.iterator();
		int counter = 0;
		while (rowIterator.hasNext()) {
			// filingInput = new filingInput();
			sheet1row = rowIterator.next();
			int x = sheet1row.getRowNum();
			if (x == 0) {
				continue;
			}

			String condition = sheet1row.getCell(6).getStringCellValue();

			if (condition.equalsIgnoreCase("YES")) {
				FilingData = new FilingInput();
				FilingData.setActionType(sheet1row.getCell(0)
						.getStringCellValue());
				FilingData.setTestCaseId(sheet1row.getCell(1)
						.getStringCellValue());
				FilingData.setTestCaseId(sheet1row.getCell(1)
						.getStringCellValue());
				FilingData.setObjective(sheet1row.getCell(2)
						.getStringCellValue());
				FilingData.setExpectedResults(sheet1row.getCell(3)
						.getStringCellValue());
				FilingData.setActualRsesult(sheet1row.getCell(4)
						.getStringCellValue());

				DataList.add(FilingData);
				counter++;

				fetchFilingTestData(FilingDataDirectory, headerFileDirectory,
						counter, FilingData, DataList);

			}

		}
	}

	public static void fetchFilingTestData(String FilingDataDirectory,
			String headerFileDirectory, int counter, FilingInput FilingData,
			List<FilingInput> DataList) throws IOException,
			InterruptedException, ParseException {
		logger.info(" Entering fetchFilingTestData()");

		InputStream inputStream = new FileInputStream(
				edFileGenerator.propFileName);
		prop.load(inputStream);
		// prop.load(inputStream);

		Date sysDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
		String today = sdf.format(sysDate);
		FileInputStream file = new FileInputStream(FilingDataDirectory);
		Workbook filingDataWorkbook = new XSSFWorkbook(file);
		// Get first sheet from the workbook
		XSSFSheet sheet = (XSSFSheet) filingDataWorkbook.getSheetAt(0);
		XSSFRow sheet2row = sheet.getRow(sheet1row.getRowNum());
		// System.out.println("Date"+sheet2row.getCell(0));
		String filingDate = String.valueOf(sheet2row.getCell(0));
		// System.out.println("Date from Excel :" + filingDate);

		if (filingDate != null && filingDate.length() > 0) {
			Date date = new SimpleDateFormat("dd/MM/yyyy").parse(filingDate);
			String format = new SimpleDateFormat("YYYYMMdd").format(date);
			FilingData.setFilingDate(format);
		} else {

			FilingData.setFilingDate(today);
		}
		FilingData.setConformedName(sheet2row.getCell(1).getStringCellValue());
		
		FilingData.setCik((long) sheet2row.getCell(2).getNumericCellValue());
		FilingData.setAssignedSIC((int) sheet2row.getCell(3)
				.getNumericCellValue());
		FilingData.setIrsNumber((int) sheet2row.getCell(4)
				.getNumericCellValue());
		FilingData.setStateOfIncorporation(sheet2row.getCell(5)
				.getStringCellValue());
		FilingData.setStreet1(sheet2row.getCell(6).getStringCellValue());
		FilingData.setStreet2(sheet2row.getCell(7).getStringCellValue());
		FilingData.setCity(sheet2row.getCell(8).getStringCellValue());
		FilingData.setState(sheet2row.getCell(9).getStringCellValue());
		XSSFCell cell = sheet2row.getCell(10);
		if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
			FilingData.setZip(String.valueOf((int) sheet2row.getCell(10)
					.getNumericCellValue()));
		} else {
			FilingData.setZip(sheet2row.getCell(10).getStringCellValue());
		}

		long x = 0;
		x = (long) sheet2row.getCell(11).getNumericCellValue();
		FilingData.setPhone(x);
		FilingData.setFormerConformedName(sheet2row.getCell(12)
				.getStringCellValue());
		FilingData.setDateChanged(sheet2row.getCell(14).getStringCellValue());
		String dateChanged = FilingData.getDateChanged();
		if (dateChanged == null) {
			dateChanged = sdf.format(sysDate);
			FilingData.setDateChanged(dateChanged);
		}
		FilingData.setCounter(counter);

		ReadTestData.CallingInvoker(FilingData, headerFileDirectory, DataList);
		TestScenarious.TestCases(FilingData, DataList);

	}

	public static void CallingInvoker(FilingInput FilingData,
			String headerFileDirectory, List<FilingInput> DataList)
			throws InterruptedException, IOException {
		
		//System.out.println("data list size :"+DataList.size());

		try {
			DataList = (List<FilingInput>) edFileGenerator.invoke(FilingData,
					headerFileDirectory, DataList);
			FilingData.setFilingException("No Exception");
		} catch (FillerException e) {

			String exceptionMsg = e.getMessage();

			if (exceptionMsg.equals(prop.getProperty("ServerLevelException"))) {
				System.out.println(prop.getProperty("ServerLevelException"));
				FilingData.setFilingException(exceptionMsg);

				System.exit(0);
			} else if (exceptionMsg.contains(prop
					.getProperty("InjestionLevelException"))) {
				FilingData.setFilingException(exceptionMsg);
				System.out.println(prop.getProperty("InjestionLevelException"));
			} else if (exceptionMsg.contains("")) {
				System.out.println("Other exception: " + exceptionMsg);
				FilingData.setFilingException(exceptionMsg);
			}

		}

	}

}
