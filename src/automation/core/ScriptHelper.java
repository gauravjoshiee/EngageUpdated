package automation.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import automation.config.Constants;
import automation.utility.ExcelUtils;
import automation.utility.Log;

public class ScriptHelper {

	public static String prepareExecutionSuite(String sourcePath) throws IOException {

		String dateName = new SimpleDateFormat("yyyy_MMM_dd_hhmmss").format(new Date());

		File source = new File(sourcePath);

		String preparedPath = System.getProperty("user.dir") + "\\ExecutedSuite\\" + "ExecutedTest_" + dateName
				+ ".xlsx";
		File preparedFile = new File(preparedPath);
		FileUtils.copyFile(source, preparedFile);
		// Returns the captured file path
		return preparedPath;
	}

	public static int getExecutionCount(String filePath, String sheetName) {

		XSSFWorkbook workbook;
		XSSFSheet worksheet;
		int rowCount = 0;
		try (FileInputStream fs = new FileInputStream(filePath)) {

			workbook = new XSSFWorkbook(fs);
			worksheet = workbook.getSheet(sheetName);
			rowCount = worksheet.getLastRowNum();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rowCount;
	}

	public static void setStepExecutionData(int iRow, String sheetName, DriverMembers obj) {
		obj.sTestStepFailureDetail = null;
		obj.sTestCaseStatus = Constants.Key_Pass_Result;
		try {
			obj.sTSRunMode = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestStepRunMode, sheetName,
					DriverScript.Path_Executable);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (obj.sTSRunMode.equals("Yes")) {
			try {
				obj.sActionKeyword = obj.xlObj.getSpecificCellData(iRow, Constants.Col_ActionKeyword, sheetName,
						DriverScript.Path_Executable);
				obj.sPageObject = obj.xlObj.getSpecificCellData(iRow, Constants.Col_Xpath, sheetName,
						DriverScript.Path_Executable);
				obj.sPageData = obj.xlObj.getSpecificCellData(iRow, Constants.Col_Data, sheetName,
						DriverScript.Path_Executable);
				obj.sTestStepStatus = Constants.Key_Pass_Result;
				obj.sTestStepNumber = iRow;
				obj.sTestStepName = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestStepName, sheetName,
						DriverScript.Path_Executable);
				obj.sTestStepDesc = obj.xlObj.getSpecificCellData(iRow, Constants.Col_TestDescription, sheetName,
						DriverScript.Path_Executable);
				obj.sAppender = obj.xlObj.getSpecificCellData(iRow, Constants.Col_Appender, sheetName,
						DriverScript.Path_Executable);
				obj.sTestStepFailureDetail = "";
				Log.info("Successfully read step - " + iRow);
			}

			catch (Exception e) {
				obj.sTestStepFailureDetail = e.getMessage();
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
			}
		}
	}
}
