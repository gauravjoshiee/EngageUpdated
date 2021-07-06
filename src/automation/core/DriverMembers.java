package automation.core;

import java.lang.reflect.Method;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import automation.config.ActionKeywords;
import automation.config.Constants;
import automation.utility.DBUtils;
import automation.utility.EmailListner;
import automation.utility.ExcelUtils;
import automation.utility.Reporting;
import net.sf.json.JSONObject;

public class DriverMembers {

	public ActionKeywords actionKeywords;

	public String sActionKeyword;
	public String sPageObject;
	public String sPageData;
	public String sAppender;
	public int sSuiteLength;
	public int sTestLength;
	public int sTestStepNumber;

	public String sRunMode;
	public String sTSRunMode;

	public String sTestCase;
	public String sTestIteration;
	public int sIterationCount;
	public int sCurrentIteration;
	public String sDataFeeder;
	public String sBlockName;
	public String sTestStepName;
	public String sTestStepDesc;
	public String sStepDependency;
	public String sPreviousStepStatus;
	public static String sLocalDataVariable;
	public String sTestStepFailureDetail;
	public static String sBlockFailureDetail;
	public String sScreenshotPath;
	public String oldTab;
	public String sTestCaseStatus = Constants.Key_Pass_Result;
	public String sBStatus = Constants.Key_Pass_Result;
	public String sTestStepStatus = Constants.Key_Pass_Result;

	public String sTrRunMode;
	public String transactionName;
	public String generatedRandomString;

	public WebDriver driver;
	public static Method method[];
	public ExcelUtils xlObj;
	public Reporting extObj;
	public DBUtils dbObj;
	public ReadExcelDataWithDynamicColumn mdm;

	public EmailListner emlObj;
	public JSONObject jsonTable1;
	public JSONObject jsonRow1;

	public WebDriver remotedriver;

	public DriverMembers() {
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
		this.xlObj = new ExcelUtils();
		this.extObj = new Reporting();
		this.mdm = new ReadExcelDataWithDynamicColumn();
		this.jsonTable1 = new JSONObject();
		this.jsonRow1 = new JSONObject();
	}

}
