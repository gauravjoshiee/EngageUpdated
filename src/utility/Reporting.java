package utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
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
	public static void setExtent() {

		String timeStamp = ExcelUtils.getDate("yyyy-MM-dd_HH-mm-ss");
		reportPath = System.getProperty("user.dir") + "\\Reports\\" + Constants.SuiteName + "_" + timeStamp + ".html";
		htmlReporter = new ExtentHtmlReporter(reportPath);
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setReportName(Constants.SuiteName + " - Functional Test Report");

		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

	}

	/**
	 * This method is for starting a new test case logging into report
	 */
	public synchronized void startTest(String testName, Reporting extObj) {
		extObj.test = extent.createTest(testName);
	}

	/**
	 * This method is for logging intended details for a test case
	 * 
	 * 
	 */
	public synchronized void recordTest(String result, String testName, String resultDetail, Reporting extObj,
			WebDriver driver) throws Exception {

		if (result == Constants.Key_Pass_Result) {
			extObj.test.log(Status.PASS, testName + " is passed - " + resultDetail);
		} else {
			if (result == Constants.Key_Fail_Result) {
				extObj.test.log(Status.FAIL, testName + " is failed - " + resultDetail);
				if (Constants.Attach_Screenshot) {
					String path = getScreenshotPath(driver);
					extObj.test.fail(result, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
				}
			} else {
				extObj.test.log(Status.SKIP, testName + " is failed - " + resultDetail);
			}
		}
	}

	/**
	 * This method is for writing the the content into report
	 */
	public void endExtent() {
		extent.flush();
	}

	/**
	 * This method is for creating a node within test case in report
	 */
	public void addReportingNode(String stepName, Reporting extObj) {
		extObj.test.createNode(stepName);
	}

	public static String getScreenshotPath(WebDriver driver) throws Exception {

		String dateStamp = new SimpleDateFormat("_yyyy_MM_dd_hh-mm-ss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File rawFile = ts.getScreenshotAs(OutputType.FILE);

		String screenShotFileLocation = System.getProperty("user.dir") + "\\FailedTestsScreenshots\\" + dateStamp
				+ ".png";

		File screenShotFile = new File(screenShotFileLocation);
		FileUtils.copyFile(rawFile, screenShotFile);

		return screenShotFileLocation;
	}

	public static synchronized void addScreencast(DriverMembers obj) throws Exception {

		String path = getScreenshotPath(obj.driver);
		obj.extObj.test.info("Refer Screenshot - ", MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

}
