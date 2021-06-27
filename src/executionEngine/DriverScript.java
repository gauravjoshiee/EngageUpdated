package executionEngine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

		Path_Executable = ScriptHelper.prepareExecutionSuite(Constants.Path_TestData);
		ExcelUtils mxlObj = new ExcelUtils();

		sSuiteLength = ScriptHelper.getExecutionCount(Path_Executable, Constants.Sheet_TransactionDefinition);

		// EmailListner.checkMail("pop3.mailtrap.io", "66350086b76120",
		// "a8dc73cd2b2784");

		DriverScript startEngine = new DriverScript();

		executeTransaction(startEngine, mxlObj);
		for (Thread thread : threadList) {
			try {
				thread.join();
				System.out.println(thread.getName() + " Finished its job");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
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

	public static void executeTransaction(DriverScript x, ExcelUtils xlObj) throws IOException {
		Reporting.setExtent();

		for (int iRow = 1; iRow <= sSuiteLength; iRow++) {
			System.out.println(Thread.currentThread().getName() + " - checking transaction row - " + iRow);

			try {
				xlObj.setExcelFile(Path_Executable, Constants.Sheet_TransactionDefinition);
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
	public void executeTestSuite(DriverMembers obj) throws Exception {

		obj.sSuiteLength = ScriptHelper.getExecutionCount(Path_Executable, Constants.Sheet_SuiteDefinition);
		for (int iRow = 1; iRow <= obj.sSuiteLength; iRow++) {
			processTestCaseRunModeAndProceed(iRow, obj);
		}

	}

	/**
	 * This method reads each row in test case sheet and parse test step data,
	 * keyword, element and calls functions to execute actions
	 * 
	 * @throws Exception
	 */
	private static void executeTestCase(String executionSheet, DriverMembers obj) throws Exception {

		int executionLength = ScriptHelper.getExecutionCount(Path_Executable, executionSheet);

		System.out.println(
				Thread.currentThread().getName() + " - Started Execution of test case - " + executionSheet + " ....");
		obj.sTestCaseStatus = Constants.Key_Pass_Result;

		for (int iRow = 1; iRow <= executionLength; iRow++) {

			if (obj.sTestCaseStatus == Constants.Key_Pass_Result) {
				ScriptHelper.setStepExecutionData(iRow, executionSheet, obj);
				Log.startTestStep(obj.sTestStepName);
				if (obj.sActionKeyword.equals("executeFunctionalBlock")) {
					executeTestCase(obj.sPageData, obj);
				} else {
					executeActions(iRow, executionSheet, obj);

					obj.extObj.recordTest(obj.sTestStepStatus, obj.sTestStepName, obj.sTestStepFailureDetail,
							obj.extObj, obj.driver);
					obj.sScreenshotPath = null;
				}
			} else {
				break;
			}
		}
		// Do not use driver.quit within execute_TestCase as it is recursive function
	}

	/**
	 * This method is to execute test step (Action)
	 */
	private static void executeActions(int stepnumber, String executionSheet, DriverMembers obj) throws Exception {
		obj.sTestCase = executionSheet;

		// This is a loop which will run for the number of actions in the Action Keyword
		// class
		// method variable contain all the method and method.length returns the total
		// number of methods
		for (int i = 0; i < method.length; i++) {

			// Comparing the method name with the ActionKeyword value got from
			// excel
			if (obj.method[i].getName().equals(obj.sActionKeyword)) {
				Log.info("Started action - " + obj.sActionKeyword);
				// In case of match found, it will execute the matched method
				try {
					System.out.println(Thread.currentThread().getName() + " - " + executionSheet + " - Executing: "
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
					} else {
						obj.xlObj.setStepResult(Constants.Key_Fail_Result, stepnumber, Constants.Col_TestStepResult,
								executionSheet, obj);
					}
				}
				// Once any method is executed, this break statement will take the flow outside
				// of for loop
				break;
			}
		}

	}

	/**
	 * This method sets result of test case in Test Suite sheet. Not intended for
	 * Extent Reports
	 */
	private static synchronized void setTestCaseResult(int testCaseRow, DriverMembers obj, ExcelUtils xlObj) {
		try {

			if (obj.sTestCaseStatus == Constants.Key_Pass_Result) {
				xlObj.setStepResult(Constants.Key_Pass_Result, testCaseRow, Constants.Col_TestCaseResult,
						Constants.Sheet_SuiteDefinition, obj);
			} else {
				if (obj.sTestCaseStatus == Constants.Key_Fail_Result) {
					xlObj.setStepResult(Constants.Key_Fail_Result, testCaseRow, Constants.Col_TestCaseResult,
							Constants.Sheet_SuiteDefinition, obj);
				} else {
					xlObj.setStepResult(Constants.Key_Block_Result, testCaseRow, Constants.Col_TestCaseResult,
							Constants.Sheet_SuiteDefinition, obj);
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String timeStamp = String.valueOf(dateFormat.format(date));
			xlObj.setStepResult(timeStamp, testCaseRow, Constants.Col_TestCaseTime, Constants.Sheet_SuiteDefinition,
					obj);

		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " - Unable to set Test Case Status");
		}

	}

	private boolean checkDependency(int testCaseRow, DriverMembers obj, ExcelUtils xlObj) {
		try {

			String dependentTC = xlObj.getSpecificCellData(testCaseRow, Constants.Col_Dependency,
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

	private void processTestCaseRunModeAndProceed(int iRow, DriverMembers obj) throws Exception {
		System.out.println(Thread.currentThread().getName() + " - checking suite row - " + iRow);
		try {
			String transactionMap = obj.xlObj.getSpecificCellData(iRow, Constants.Col_Transaction,
					Constants.Sheet_SuiteDefinition, Path_Executable);
			if (transactionMap.equalsIgnoreCase(obj.transactionName)) {
				obj.sRunMode = obj.xlObj.getSpecificCellData(iRow, Constants.Col_RunMode,
						Constants.Sheet_SuiteDefinition, Path_Executable);
				if (obj.sRunMode.equals("Yes")) {
					processTestCaseDependencyAndProceed(iRow, obj);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(Thread.currentThread().getName() + " - " + e.getMessage());
			System.out.println(Thread.currentThread().getName() + " - Test Case not found");
		}
	}

	private int processTestCaseDataFeeder(int iRow, DriverMembers obj) {
		int totalRecordCount = 0;
		try {

			String isTestIterable = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCaseIteration,
					Constants.Sheet_SuiteDefinition, Path_Executable);

			if (isTestIterable.equalsIgnoreCase("Yes")) {
				//Enhance code to use a data members hashmap of testCase<Key>, DataFeederPath<value> to facilitate multiple level of data feeder use
				obj.sDataFeeder = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCaseDataFeeder,
						Constants.Sheet_SuiteDefinition, Path_Executable);
				if (obj.sDataFeeder.equals("")) {
					System.out.println("Data feeder file path not provided");
				} else {
					totalRecordCount = ScriptHelper.getExecutionCount(obj.sDataFeeder, "DataFeeder");
				}
			} else {
				totalRecordCount = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalRecordCount;
	}

	private void processTestCaseDependencyAndProceed(int iRow, DriverMembers obj) {
		try {
			boolean noDependency = true;
			noDependency = checkDependency(iRow, obj, obj.xlObj);
			String targetExecutionTest = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestCasName,
					Constants.Sheet_SuiteDefinition, Path_Executable);
			if (noDependency) {
				int totalFeederRecords = processTestCaseDataFeeder(iRow, obj);
				if (totalFeederRecords > 0) {

					for (int iteration = 1; iteration <= totalFeederRecords; iteration++) {
						obj.extObj.startTest(targetExecutionTest, obj.extObj);
						obj.sCurrentIteration = iteration;
						DriverScript.executeTestCase(targetExecutionTest, obj);
						obj.extObj.recordTest(obj.sTestStepStatus, obj.sTestStepName, obj.sTestStepFailureDetail,
								obj.extObj, obj.driver);
					}
				} else {
					obj.extObj.recordTest(Constants.Key_Block_Result, obj.sTestCase, "No data records in Data Feeder",
							obj.extObj, obj.driver);
				}
			}else {
				obj.extObj.recordTest(Constants.Key_Block_Result, obj.sTestCase,
						"Test case dependency not resolved", obj.extObj, obj.driver);
			} 
			DriverScript.setTestCaseResult(iRow, obj, obj.xlObj);
			System.out.println(Thread.currentThread().getName() + " - Out of execution for - " + targetExecutionTest
					+ " status is " + obj.sTestCaseStatus);
			if (obj.driver != null) {
				obj.driver.quit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
