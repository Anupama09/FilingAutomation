package com.thomsonreuters.oa.filing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPWindows 
{
	/*private static final Logger logger = Logger.getLogger(
			FTPWindows.class); 
	*/
	 public String FtpConnect(String ftpHost,String ftpUserName, String ftpPassword,String ftpRemoteDirectory,String fileToTransmit){
		

		  String dateTime=null;
		 FTPClient ftp = new FTPClient();
		 
		 
			 
			 try {
		            /*String ftpHost = "10.43.152.180";
		            String ftpUserName = "ectdev_ftpuser@tr";
		            String ftpPassword = "D3vU$3r";
		            String ftpRemoteDirectory = "OA/FilerAutomationTest/";
		            String fileToTransmit = "C:\\Users\\U0128448\\Desktop\\yrdy\\tester.txt";*/
		            
		            int reply;
		            ftp.connect(ftpHost);
		            reply = ftp.getReplyCode();
		            if(!FTPReply.isPositiveCompletion(reply)) {
		                try {
		                    ftp.disconnect();
		                } catch (Exception e) {
		                	/*logger.error("Unable to disconnect from FTP server " +
		                                       "after server refused connection. "+e.toString());*/
		                    System.err.println("Unable to disconnect from FTP server " +
		                                       "after server refused connection. "+e.toString());
		                }
		                throw new Exception ("FTP server refused connection.");
		
		            }
		                
		            if (!ftp.login(ftpUserName, ftpPassword)) {
		            /*	logger.error("Unable to login to FTP server " +
		                        "using username "+ftpUserName+" " +
		                        "and password "+ftpPassword);*/
		            	throw new Exception ("Unable to login to FTP server " +
		                        "using username "+ftpUserName+" " +
		                        "and password "+ftpPassword);
		            }
		       
		            ftp.setFileType(FTP.BINARY_FILE_TYPE);
		            if (ftpRemoteDirectory != null && ftpRemoteDirectory.trim().length() > 0) {
		            	//logger.info("Posting header file in to the FTP remote dir: " + ftpHost+"/"+ftpRemoteDirectory);
		              //  System.out.println("Posting header file in to the FTP remote dir: " + ftpHost+"/"+ftpRemoteDirectory);
		                ftp.changeWorkingDirectory(ftpRemoteDirectory);
		                reply = ftp.getReplyCode();
//		                System.out.println(reply);
		                if(!FTPReply.isPositiveCompletion(reply)) {
		                /*	logger.error("Unable to change working directory " +
		                                         "to:"+ftpRemoteDirectory);*/
		                    throw new Exception ("Unable to change working directory " +
		                                         "to:"+ftpRemoteDirectory);
		                }
		            }
		            
		          
		            	File f = new File(fileToTransmit);
		                InputStream input = new FileInputStream(new File(fileToTransmit));
		                
		                ftp.storeFile(f.getName(), input);
		           	 
		                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		                dateTime = dateFormat.format(new Date()); 
		   		    // System.out.println("date and time : "+dateTime);
				        
		         
		            try {
		                //ftp.logout();
		                ftp.disconnect();
		            } catch (Exception exc) {
		                System.err.println("Unable to disconnect from FTP server. " + exc.toString());
		            }

		        } catch (Exception e) {
		            System.err.println("Error: "+e.toString());
		        }
		
		       
			 //	logger.info("FTP Process Complete.");
		        System.out.println("Placing header file in to FTP location...");
		        System.out.println("Processing the header extraction..");
		       
		       return dateTime;
//		        System.exit(0);
	}

	 
}