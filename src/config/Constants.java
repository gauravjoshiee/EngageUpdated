package config;

public class Constants {
	
		//List of System Variables
		
		public static String Path_TestData = System.getProperty("user.dir")+"//src//dataEngine//CZ_RMS_TestSuite.xlsx";
		
		public static final String File_TestResult = "TestSuite";
		public static final String Path_ReportPath =System.getProperty("user.dir")+"\\Reports\\";
		public static final String SuiteName = "ITR_MDM";
		
		
		//List of columns in Transaction sheet
		public static final int Col_TrLastExecution=3;
		public static final int Col_TrStatus=2;
		public static final int Col_TrRunMode=1;
		public static final int Col_TrName=0;
		
		//List of Data Engine Excel sheets
		public static final String Sheet_SuiteDefinition = "TestSuiteDefinition";
		public static final String Sheet_DataVariables = "DataVariables";
		public static final String Sheet_TransactionDefinition = "TransactionDefinition";
		public static final String Sheet_RunConfig = "RunConfig";
		
		//List of Column Numbers in Test Suite sheet
		public static final int Col_TestCaseDataFeeder=7;
		public static final int Col_TestCaseIteration=6;
		public static final int Col_TestCaseTime=5;
		public static final int Col_TestCaseResult=4;
		public static final int Col_Dependency=3;
		public static final int Col_RunMode=2;
		public static final int Col_TestCasName=1;
		public static final int Col_Transaction=0;
		
		//List of columns in RunConfig sheet
		public static final int Col_RunConfigName=0;
		public static final int Col_RunConfigValue=1;
		
		
		//List of Column Numbers in Test Case sheets
		public static final int Col_TestCaseID = 0;	
		public static final int Col_TestScenarioID = 1;
		public static final int Col_TestDescription=2;
		public static final int Col_ActionKeyword = 3;
		public static final int Col_Data = 4;
		public static final int Col_TestStepResult=5;
		public static final int Col_Xpath=6;
		public static final int Col_Appender=7;
		
		
		//List of Column Numbers in DataVariables sheet
		public static final int Col_TestStepName=1;
		public static final int Col_DataVariableName=2;
		public static final int Col_DataVariableValue=3;

		//List of result strings - Used for result logging into excel, and not in Extent Report
		public static final String Key_Pass_Result="Pass";
		public static final String Key_Fail_Result="Fail";
		public static final String Key_Block_Result="Blocked";
		
		
	 
		//List of wait
		public static final int Global_Timeout=120;

}
