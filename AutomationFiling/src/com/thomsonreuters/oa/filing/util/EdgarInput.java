package com.thomsonreuters.oa.filing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.thomsonreuters.oa.filing.resource.FilingInput;


public class EdgarInput {
	
	public String environment="qa";

	private static final Logger logger = Logger.getLogger(FilingInput.class);
	Properties prop = new Properties();
	
    String propFileName = "ftp-"+environment+".properties";  
	FilingInput filInput;
	String dateTime=null;


	public String getEdgarInfo(FilingInput FilingData,
			String headerFileDirectory) {
	
		logger.info("Header file creation started...");
		System.out.println("Preparing a header file.....");
		// FileInputStream headerFile=new FileInputStream(headerFileDirectory);

		File file = new File(headerFileDirectory+"/"+FilingData.getEdgarFileName()+".header");
		try {
			FileWriter writedata = new FileWriter(file);
			writedata
					.write("0001083839-13-000201NSAR-A Nuveen Equity Premium Advantage Fund 013082920130829112827112827112827 0 "
							+ "\n");
			writedata.write("<SUBMISSION>" + "\n");
			writedata.write("<ACCESSION-NUMBER>0001083839-13-000201" + "\n");
			writedata.write("<TYPE>NSAR-A" + "\n");
			writedata.write("<PUBLIC-DOCUMENT-COUNT>1" + "\n");
			writedata.write("<PERIOD>" + FilingData.getFillingDate() + "\n");
			writedata.write("<FILING-DATE>" + FilingData.getFillingDate() + "\n");
			writedata.write("<DATE-OF-FILING-DATE-CHANGE>"
					+ FilingData.getFillingDate() + "\n");
			writedata.write("<EFFECTIVENESS-DATE>" + FilingData.getFillingDate()
					+ "\n");
			writedata.write("<ISSUER>" + "\n");
			writedata.write("<COMPANY-DATA>" + "\n");
			writedata
					.write("<CONFORMED-NAME>" + FilingData.getConformedName() + "\n");
			writedata.write("<CIK>" + FilingData.getCik() + "\n");
			writedata.write("<ASSIGNED-SIC>" + FilingData.getAssignedSIC() + "\n");
			if (FilingData.getIrsNumber()!= 0 &&  !(FilingData.getTestCaseId().equals("OAUI_TC023")))  {
				writedata.write("<IRS-NUMBER>" + FilingData.getIrsNumber() + "\n");
			}else {
				FilingData.setIrsNumber(0);
				System.out.println("IRS Number"+FilingData.getIrsNumber());
				writedata.write("<IRS-NUMBER>" + FilingData.getIrsNumber() + "\n");
			}
			if(FilingData.getStateOfIncorporation()!=null){
			
			writedata.write("<STATE-OF-INCORPORATION>"
					+ FilingData.getStateOfIncorporation() + "\n");
			}
			//writedata.write("<FISCAL-YEAR-END>1231" + "\n");
			writedata.write("</COMPANY-DATA>" + "\n");
			writedata.write("<FILING-VALUES>" + "\n");
			writedata.write("<FORM-TYPE>N-CSRS" + "\n");
			writedata.write("<ACT>40"+"\n");
			writedata.write("<FILE-NUMBER>811-02661"+"\n");
			writedata.write("<FILM-NUMBER>131067889"+"\n");
			writedata.write("</FILING-VALUES>"+"\n");
			writedata.write("<BUSINESS-ADDRESS>"+"\n");
			if((FilingData.getStreet2().length()>0))
			{
			writedata.write("<STREET1>"+FilingData.getStreet1()+"\n");
			}
			if(!(FilingData.getStreet2()==null)){
			writedata.write("<STREET2>"+FilingData.getStreet2()+"\n");
			}
			writedata.write("<CITY>"+FilingData.getCity()+"\n");
			writedata.write("<STATE>"+FilingData.getState()+"\n");
			writedata.write("<ZIP>"+FilingData.getZip()+"\n");
			writedata.write("<PHONE>"+FilingData.getPhone()+"\n");
			writedata.write("</BUSINESS-ADDRESS>"+"\n");
			if(FilingData.getFormerConformedName().length()>0){
			writedata.write("<FORMER-COMPANY>"+"\n");
			writedata.write("<FORMER-CONFORMED-NAME>"+FilingData.getFormerConformedName()+"\n");
			writedata.write("<DATE-CHANGED>"+FilingData.getFillingDate()+"\n");
			writedata.write("</FORMER-COMPANY>"+"\n");
			}
			writedata.write("</ISSUER>");
		
			writedata.flush();
			writedata.close();
		
			//Posting file to ftp
			dateTime =ftpEdgarInput(file);
		} catch (IOException e) {
			logger.info("Issue in header file creation.");
			System.out.println("Issue in header file creation.");
			e.printStackTrace();
		}
		return dateTime;
		
	}
		


	private String ftpEdgarInput(File headerFilePath) throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	    prop.load(inputStream);
	    if (inputStream == null) {
	        throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
	    }
		String path = headerFilePath.getPath();
		FTPWindows fTPUtil = new FTPWindows();
		//System.out.println("FTP Details : "+prop.getProperty("host"));
		return 
				fTPUtil.FtpConnect(prop.getProperty("host"),
				prop.getProperty("username"), prop.getProperty("password"),
				prop.getProperty("path"), path);
	}
	
	
}
