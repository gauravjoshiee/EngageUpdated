package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import config.Constants;
import executionEngine.DriverMembers;

public class Reporting {
	static ExtentSparkReporter htmlReporter;
	static ExtentReports extent;
	ExtentTest test;
	static String reportPath;
	static Properties p = new Properties();

	static void loadProperties() throws IOException {

		try (FileInputStream reader = new FileInputStream((".\\Properties//ExtentProperties.properties"))) {
			p.load(reader);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			p.clear();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is for creating a report at specified location
	 * 
	 * @throws IOException
	 */
	public static void setExtent() throws IOException {

		loadProperties();

		String timeStamp = ExcelUtils.getDate("yyyy-MM-dd_HH-mm-ss");
		reportPath = (".\\" + p.getProperty("reportBaseFolder") + "\\" + timeStamp + "\\index.html");

		htmlReporter = new ExtentSparkReporter(reportPath);
		htmlReporter.config().setDocumentTitle("Automation Report");
		htmlReporter.config().setReportName(Constants.SuiteName + "Functional Test Report");

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

		if (result.equalsIgnoreCase(Constants.Key_Pass_Result)) {
			extObj.test.log(Status.PASS, testName + " is passed - " + resultDetail);
		} else {
			String callingFunction = Thread.currentThread().getStackTrace()[2].getMethodName();
			if (result.equalsIgnoreCase(Constants.Key_Fail_Result)) {
				extObj.test.log(Status.FAIL, testName + " is failed - " + resultDetail);
				// Taking screenshot only if extent report property states Yes and the record
				// test is called at test case level, not at test suite level
				if (p.getProperty("attachScreenshot").equalsIgnoreCase("Yes")
						&& callingFunction.equals("execute_TestCase")) {
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

		String timeStamp = ExcelUtils.getDate("yyyy-MM-dd_HH-mm-ss");
		TakesScreenshot ts = (TakesScreenshot) driver;
		File rawFile = ts.getScreenshotAs(OutputType.FILE);

		String screenShotFileLocation = ".\\" + p.getProperty("reportFailScreenshotFolder") + "\\" + timeStamp
				+ p.getProperty("reportFailScreenshotFormat");

		File screenShotFile = new File(screenShotFileLocation);
		FileUtils.copyFile(rawFile, screenShotFile);

		return screenShotFile.getCanonicalPath();
	}

	public static synchronized void addScreencast(DriverMembers obj) throws Exception {

		String path = getScreenshotPath(obj.driver);
		obj.extObj.test.info("Refer Screenshot - ", MediaEntityBuilder.createScreenCaptureFromPath(path).build());

	}

}
