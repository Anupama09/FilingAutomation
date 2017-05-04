package com.thomsonreuters.oa.filing.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.thomsonreuters.oa.filing.resource.FilingInput;


public class VelocityHtmlReportGeneration {
	private static final Logger logger = Logger.getLogger(VelocityHtmlReportGeneration.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh-mm-ss");
	private static SimpleDateFormat folderDate = new SimpleDateFormat("dd_MM_yyyy");

	public void reportGeneration(List<FilingInput> report){
		System.out.println("Generating HTML report file.... ");
		String path="C:/FilingAutomation/Results/"+folderDate.format(new Date());
		System.out.println("HTML Report Path :"+path);
		
		File dir = new File(path);
		
		if (!dir.exists()) {
			if (dir.mkdir());
				
		}
				PrintWriter p1 = null;
				//String path = "C:/FilingAutomation/Report/";
				String fileName=path+"/"+"TestResults"+ sdf.format(new Date())+".html";
				
				
				 VelocityEngine ve = new VelocityEngine();
			     ve.init();

		        VelocityContext context = new VelocityContext();
		        context.put("reportGenerationList", report);
//System.out.println(report.size());

		       Template t = ve.getTemplate( "./config/vm_htmlReport.vm" );
		       // Template t = ve.getTemplate( reportpath );
		        
		        System.out.println("Report is ready ..!!");

				
				try {
					p1 = new PrintWriter(new FileWriter(fileName));
					t.merge( context, p1 );
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					p1.close();
				}
				
			
			}
		}
		
		
		//System.out.println("done");
        
        
