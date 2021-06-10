package executionEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import config.Constants;
import utility.ExcelUtils;
import utility.Log;

public class ScriptHelper {

	public static int getExecutionCount(String FilePath, String SheetName) {

		FileInputStream fs;
		XSSFWorkbook workbook;
		XSSFSheet worksheet;
		int rowCount = 0;
		try {
			fs = new FileInputStream(FilePath);
			workbook = new XSSFWorkbook(fs);
			worksheet = workbook.getSheet(SheetName);
			rowCount = worksheet.getLastRowNum();
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rowCount;
	}

	@SuppressWarnings("static-access")
	public static void setStepExecutionData(int iRow, String SheetName, DriverMembers obj) {
		obj.sTestStepFailureDetail = null;
		obj.sTestCaseStatus = Constants.Key_Pass_Result;
		try {
			obj.sTSRunMode = ExcelUtils.getSpecificCellData(iRow, Constants.Col_TestStepRunMode, obj.sTestCase,
					DriverScript.Path_Executable);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (obj.sTSRunMode.equals("Yes")) {
			try {
				obj.sActionKeyword = ExcelUtils.getSpecificCellData(iRow, Constants.Col_ActionKeyword, obj.sTestCase,
						DriverScript.Path_Executable);
				obj.sPageObject = ExcelUtils.getSpecificCellData(iRow, Constants.Col_Xpath, obj.sTestCase,
						DriverScript.Path_Executable);
				obj.sPageData = ExcelUtils.getSpecificCellData(iRow, Constants.Col_Data, obj.sTestCase,
						DriverScript.Path_Executable);
				obj.sTestStepStatus = Constants.Key_Pass_Result;
				obj.sTestStepNumber = iRow;
				obj.sTestStepName = ExcelUtils.getSpecificCellData(iRow, Constants.Col_TestStepName, obj.sTestCase,
						DriverScript.Path_Executable);
				obj.sTestStepDesc = ExcelUtils.getSpecificCellData(iRow, Constants.Col_TestDescription, obj.sTestCase,
						DriverScript.Path_Executable);
				obj.sAppender = ExcelUtils.getSpecificCellData(iRow, Constants.Col_Appender, obj.sTestCase,
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
