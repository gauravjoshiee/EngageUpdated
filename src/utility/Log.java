package utility;


import org.apache.logging.log4j.*;



public class Log {
	
	
	private static Logger Log = LogManager.getLogger(Log.class.getName());
	 
	
	public static void startTestCase(String sTestCaseName){
		 
		 Log.info("****************************************************************************************");
		 
		 Log.info("Started -  "+sTestCaseName);
		 
	}
	
	public static void endTestCase(String sTestCaseName){
		 
		Log.info("Completed -  "+sTestCaseName);
		 Log.info("****************************************************************************************");
		 
	}
	
	public static void endTestStep(String sTestStepName){
		 
		Log.info("Completed -  "+sTestStepName);
		 
	}
	
	public static void startTestStep(String sTestStepName){
		 
		Log.info("Completed -  "+sTestStepName);
		 
	}
	
	public static void info(String message){
		 
		Log.info(message);
		 
	}
	
	

	
	
}
