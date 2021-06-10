package executionEngine;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;
import utility.Log;
import utility.Reporting;

@SuppressWarnings("static-access")
public class DriverScript {

	public static ActionKeywords actionKeywords;
	public static String fileName;

	public static String sActionKeyword;
	public static String sPageObject;
	public static String sPageData;

	public static int sSuiteLength;

	public static String sRunMode;
	public static String sTrRunMode;
	public static String Path_Executable;
	public static Thread mTH;
	public static String sTSRunMode;

	public static List<Thread> threadList = new ArrayList<>();

	public static Method method[];

	///////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {

		// Constants.Path_TestData = args[0];

		Path_Executable = DriverScript.prepareExecutionSuite(Constants.Path_TestData);
		ExcelUtils mxlObj = new ExcelUtils();

		// Counting test steps in sheet
		// FileInputStream fs = new FileInputStream(Path_Executable);
		// XSSFWorkbook workbook = new XSSFWorkbook (fs);
		XSSFSheet sheet = ExcelUtils.setExcelFile(Path_Executable, Constants.Sheet_TransactionDefinition);
		sSuiteLength = sheet.getLastRowNum();

		// EmailListner.checkMail("pop3.mailtrap.io", "66350086b76120",
		// "a8dc73cd2b2784");

		DriverScript startEngine = new DriverScript();

		execute_Transaction(startEngine, mxlObj);

		// workbook.close();
		for (Thread thread : threadList) {
			try {
				thread.join();
				System.out.println(thread.getName() + " Finished its job");
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception thrown by : " + thread.getName());
			}
		}

		// EmailListner.sendMail();

	}

	/**
	 * This method is to instantiate 'ActionKeywords' class and create array of
	 * method (using reflection class)
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public DriverScript() throws NoSuchMethodException, SecurityException {
		actionKeywords = new ActionKeywords();
		// This will load all the methods of the class 'ActionKeywords' in it.
		// It will be like array of method, use the break point here and do the watch
		method = actionKeywords.getClass().getMethods();
	}

	public static void execute_Transaction(DriverScript x, ExcelUtils xlObj) {
		Reporting.setExtent();

		for (int iRow = 1; iRow <= sSuiteLength; iRow++) {
			System.out.println(Thread.currentThread().getName() + " - checking transaction row - " + iRow);

			try {
				ExcelUtils.setExcelFile(Path_Executable, Constants.Sheet_TransactionDefinition);
				sTrRunMode = xlObj.getSpecificCellData(iRow, Constants.Col_TrRunMode,
						Constants.Sheet_TransactionDefinition, Path_Executable);
				if (sTrRunMode.equals("Yes")) {
					String transactionName = xlObj.getSpecificCellData(iRow, Constants.Col_TrName,
							Constants.Sheet_TransactionDefinition, Path_Executable);
					DriverThreads thread = new DriverThreads(x, transactionName);
					thread.start();
					threadList.add(thread);
					thread.setName(transactionName);
					Thread.sleep(10000);
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println(Thread.currentThread().getName() + " - " + e.getMessage());
				System.out.println(Thread.currentThread().getName() + " - " + "Test Case not found");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * This method reads each row in test suit sheet and invokes test case execution
	 * for each test case with run mode = yes
	 * 
	 * @throws Exception
	 */
	public void execute_TestSuite(DriverMembers obj) throws Exception {

		boolean noDependency = true;

		// FileInputStream fs = new FileInputStream(Constants.Path_TestData);
		// XSSFWorkbook workbook1 = new XSSFWorkbook (fs);
		XSSFSheet sheet1 = ExcelUtils.setExcelFile(Path_Executable, Constants.Sheet_SuiteDefinition);
		obj.sSuiteLength = sheet1.getLastRowNum();
		// int suiteLength =34;

		for (int iRow = 1; iRow <= obj.sSuiteLength; iRow++) {
			System.out.println(Thread.currentThread().getName() + " - checking suite row - " + iRow);
			try {
				String transactionMap = obj.xlObj.getSpecificCellData(iRow, Constants.Col_Transaction,
						Constants.Sheet_SuiteDefinition, Path_Executable);
				if (transactionMap.equalsIgnoreCase(obj.transactionName)) {
					obj.sRunMode = obj.xlObj.getSpecificCellData(iRow, Constants.Col_RunMode,
							Constants.Sheet_SuiteDefinition, Path_Executable);
					if (obj.sRunMode.equals("Yes")) {
						obj.sTestCase = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCasName,
								Constants.Sheet_SuiteDefinition, Path_Executable);
						obj.sIterationCount = 1;
						obj.sTestIteration = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCaseIteration,
								Constants.Sheet_SuiteDefinition, Path_Executable);

						if (obj.sTestIteration.equalsIgnoreCase("Yes")) {
							obj.sDataFeeder = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCaseDataFeeder,
									Constants.Sheet_SuiteDefinition, Path_Executable);
							if (obj.sDataFeeder.equals("")) {
								System.out.println("Data feeder file path not provided");
							} else {
								XSSFSheet feeder = ExcelUtils.setExcelFile(obj.sDataFeeder, "DataFeeder");
								obj.sIterationCount = feeder.getLastRowNum();
							}
						}
						obj.extObj.startTest(obj.sTestCase, obj.extObj);
						noDependency = checkDependency(iRow, obj, obj.xlObj);
						if (obj.sIterationCount > 0) {
							if (noDependency) {

								for (int iteration = 1; iteration <= obj.sIterationCount; iteration++) {
									obj.sCurrentIteration = iteration;
									DriverScript.execute_TestCase(obj);
									obj.extObj.recordTest(obj.sTestStepStatus, obj.sTestStepName,
											obj.sTestStepFailureDetail, obj.extObj, obj.driver);
								}
							} else {
								obj.extObj.recordTest(Constants.Key_Block_Result, obj.sTestCase,
										"Test case dependency not resolved", obj.extObj, obj.driver);
							}
						} else {
							obj.extObj.recordTest(Constants.Key_Block_Result, obj.sTestCase,
									"No data records in Data Feeder", obj.extObj, obj.driver);
						}
						DriverScript.setTestCaseResult(iRow, obj, obj.xlObj);
						System.out.println(Thread.currentThread().getName() + " - Out of execution for - "
								+ obj.sTestCase + " status is " + obj.sTestCaseStatus);
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println(Thread.currentThread().getName() + " - " + e.getMessage());
				System.out.println(Thread.currentThread().getName() + " - Test Case not found");
			}

		}

	}

	/**
	 * This method reads each row in test case sheet and parse test step data,
	 * keyword, element and calls functions to execute actions
	 * 
	 * @throws Exception
	 */
	private static void execute_TestCase(DriverMembers obj) throws Exception {

		int TestLength = ScriptHelper.getExecutionCount(Path_Executable, obj.sTestCase);

		System.out.println(
				Thread.currentThread().getName() + " - Started Execution of test case - " + obj.sTestCase + " ....");
		obj.sTestCaseStatus = Constants.Key_Pass_Result;

		for (int iRow = 1; iRow <= TestLength; iRow++) {
			ScriptHelper.setStepExecutionData(iRow, obj.sTestCase, obj);

				if (obj.sTestCaseStatus == Constants.Key_Pass_Result) {
					Log.startTestStep(obj.sTestStepName);
					{
						execute_Actions(iRow, obj);
						obj.extObj.recordTest(obj.sTestStepStatus, obj.sTestStepName, obj.sTestStepFailureDetail,
								obj.extObj, obj.driver);
						obj.sScreenshotPath = null;
					}
				} else {
					// obj.driver.quit();
					break;
				}
			}
		obj.driver.quit();
		}	
	

	/**
	 * This method is to execute test step (Action)
	 */
	private static void execute_Actions(int stepnumber, DriverMembers obj) throws Exception {

		// This is a loop which will run for the number of actions in the Action Keyword
		// class
		// method variable contain all the method and method.length returns the total
		// number of methods
		for (int i = 0; i < method.length; i++) {

			// This is now comparing the method name with the ActionKeyword value got from
			// excel
			if (obj.method[i].getName().equals(obj.sActionKeyword)) {
				Log.info("Started action - " + obj.sActionKeyword);
				// In case of match found, it will execute the matched method
				try {
					System.out.println(Thread.currentThread().getName() + " - " + obj.sTestCase + " - Executing: "
							+ obj.sTestStepDesc);
					obj.method[i].invoke(obj.actionKeywords, obj.sPageObject, obj.sPageData, obj);
					Log.info("Completed action - " + obj.sActionKeyword);
				}

				catch (InvocationTargetException e) {
					e.getCause().printStackTrace();
					obj.sTestStepFailureDetail = e.getMessage();
					obj.sTestStepStatus = Constants.Key_Fail_Result;
					obj.sTestCaseStatus = Constants.Key_Fail_Result;
					Log.info("Aborted action - " + obj.sActionKeyword);
				}

				finally {
					if (obj.sTestStepStatus == Constants.Key_Pass_Result) {
						obj.sTestStepFailureDetail = ("Successfully completed action - " + obj.sTestStepDesc + " : "
								+ obj.sTestStepFailureDetail);
						// obj.xlObj.setStepResult(Constants.Key_Pass_Result,stepnumber,
						// Constants.Col_TestStepResult, obj.sTestCase,obj);
					} else {
						// obj.extObj.addScreencast(obj);
						obj.xlObj.setStepResult(Constants.Key_Fail_Result, stepnumber, Constants.Col_TestStepResult,
								obj.sTestCase, obj);

					}
				}
				// Once any method is executed, this break statement will take the flow outside
				// of for loop
				break;
			}
		}

	}

	public static void execute_Block(String BlockName, DriverMembers obj) throws Exception {

		String thisBlockName = BlockName;
		
		int bLength = ScriptHelper.getExecutionCount(Path_Executable, thisBlockName);

		if (bLength>0) {
			System.out.println(
					Thread.currentThread().getName() + " - Started Execution of block - " + thisBlockName + " ....");
			obj.sTestCaseStatus = Constants.Key_Pass_Result;
			for (int iRow = 1; iRow <= bLength; iRow++) {
				ScriptHelper.setStepExecutionData(iRow, thisBlockName, obj);
				if (obj.sTestCaseStatus == Constants.Key_Pass_Result) {
					Log.startTestStep(obj.sTestStepName);
					{
						execute_Actions(iRow, obj);
						obj.extObj.recordTest(obj.sTestStepStatus, obj.sTestStepName, obj.sTestStepFailureDetail,
								obj.extObj, obj.driver);
						obj.sScreenshotPath = null;
					}
				} else {
					// obj.driver.quit();
					break;
				}
			} 
		}
		else {
			System.out.println("No steps within mentioned block");
		}
	}

	/**
	 * This method sets result of test case in Test Suite sheet. Not intended for
	 * Extent Reports
	 */
	private synchronized static void setTestCaseResult(int TestCaseRow, DriverMembers obj, ExcelUtils xlObj) {
		try {

			if (obj.sTestCaseStatus == Constants.Key_Pass_Result) {
				xlObj.setStepResult(Constants.Key_Pass_Result, TestCaseRow, Constants.Col_TestCaseResult,
						Constants.Sheet_SuiteDefinition, obj);
			} else {
				if (obj.sTestCaseStatus == Constants.Key_Fail_Result) {
					xlObj.setStepResult(Constants.Key_Fail_Result, TestCaseRow, Constants.Col_TestCaseResult,
							Constants.Sheet_SuiteDefinition, obj);
				} else {
					xlObj.setStepResult(Constants.Key_Block_Result, TestCaseRow, Constants.Col_TestCaseResult,
							Constants.Sheet_SuiteDefinition, obj);
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String timeStamp = String.valueOf(dateFormat.format(date));
			xlObj.setStepResult(timeStamp, TestCaseRow, Constants.Col_TestCaseTime, Constants.Sheet_SuiteDefinition,
					obj);

		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " - Unable to set Test Case Status");
		}

	}

	private boolean checkDependency(int TestCaseRow, DriverMembers obj, ExcelUtils xlObj) {
		try {

			String dependentTC = xlObj.getSpecificCellData(TestCaseRow, Constants.Col_Dependency,
					Constants.Sheet_SuiteDefinition, Path_Executable);
			if (dependentTC != "") {
				int parentRow = xlObj.getTargetRow(Constants.Sheet_SuiteDefinition, dependentTC,
						Constants.Col_TestCasName);
				String parentStatus = xlObj.getSpecificCellData(parentRow, Constants.Col_TestCaseResult,
						Constants.Sheet_SuiteDefinition, Path_Executable);
				if (parentStatus.equalsIgnoreCase("Pass")) {
					return true;
				} else {
					obj.sTestCaseStatus = Constants.Key_Block_Result;
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " - " + e.getMessage());
			return false;
		}
	}

	public static String prepareExecutionSuite(String sourcePath) throws Exception {
		// below line is just to append the date format with the screenshot name to
		// avoid duplicate names
		String dateName = new SimpleDateFormat("yyyy_MMM_dd_hhmmss").format(new Date());

		File source = new File(sourcePath);
		// after execution, you could see a folder "FailedTestsScreenshots" under src
		// folder
		String destination = System.getProperty("user.dir") + "\\ExecutedSuite\\" + "ExecutedTest_" + dateName
				+ ".xlsx";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		// Returns the captured file path
		return destination;
	}

}
