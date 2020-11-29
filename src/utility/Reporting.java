package utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;


import config.Constants;
import executionEngine.DriverMembers;



@SuppressWarnings("deprecation")
public class Reporting {
	static ExtentHtmlReporter htmlReporter;
	static ExtentReports extent;
	ExtentTest test;
	public static String reportPath;
	
	/**
	 * This method is for creating a report at specified location
	 */
	public static void setExtent(){
		
		String timeStamp = ExcelUtils.getDate("yyyy-MM-dd_HH-mm-ss");
		reportPath = System.getProperty("user.dir")+"\\Reports\\"+Constants.SuiteName+"_"+timeStamp+".html";
		htmlReporter = new ExtentHtmlReporter(reportPath);
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setReportName(Constants.SuiteName+" - Functional Test Report");
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		
	}
	
	/**
	 * This method is for starting a new test case logging into report
	 */
	public synchronized void startTest(String testName, Reporting extObj){
		extObj.test=extent.createTest(testName);
	}
	
	/**
	 * This method is for logging intended details for a test case
	 * @throws Exception 
	 */
	public synchronized void recordTest(String result, String testName, String resultDetail, Reporting extObj) throws Exception{
		
		if(result==Constants.Key_Pass_Result){
			extObj.test.log(Status.PASS, testName+" is passed - "+resultDetail);
		}
		else{
			if (result==Constants.Key_Fail_Result){
				extObj.test.log(Status.FAIL, testName+" is failed - "+resultDetail);
			}
			else{
				extObj.test.log(Status.SKIP, testName+" is failed - "+resultDetail);
			}
			}
		}
		
	
		
	/**
	 * This method is for writing the the content into report
	 */
	public void endExtent(){
		extent.flush();
	}
	
	/**
	 * This method is for creating a node within test case in report
	 */
	public void addReportingNode(String stepName, Reporting extObj){
		extObj.test.createNode(stepName);
	}
	
	public static String getScreenshot(DriverMembers obj) throws Exception {
        //below line is just to append the date format with the screenshot name to avoid duplicate names 
        String dateName = new SimpleDateFormat("_yyyy_MM_dd_hh-mm-ss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) obj.driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        //after execution, you could see a folder "FailedTestsScreenshots" under src folder
        String destination = System.getProperty("user.dir") + "\\FailedTestsScreenshots\\"+obj.sTestCase+dateName+".png";
        File finalDestination = new File(destination);
        FileUtils.copyFile(source, finalDestination);
        //Returns the captured file path
        return destination;
	}
	
	public synchronized static String addScreencast(DriverMembers obj) throws Exception{
		String htmlTag="";
		{
			
		try {
			
				String screencastPath = getScreenshot(obj);
				obj.extObj.test.addScreenCaptureFromPath(screencastPath);
				htmlTag = ("<a href='"+screencastPath+"' target='_blank'>Screencast</a>");
//				 extObj.test.info(htmlTag); 
				//extObj.test.log(Status.FAIL, extObj.test.addScreenCaptureFromPath(screencastPath));
				//extObj.test.log(Status.FAIL, "Refer screencast",
                        //MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver,TestCaseName)).build());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return htmlTag;
	}

}
