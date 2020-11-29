package executionEngine;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverThreads extends Thread{
	
	DriverScript mainDriver;
	String transactionName;
	XSSFWorkbook workbook;
//	ChromeDriver driver;
	public WebDriver remotedriver;
	
	
	public void run(){
		
		String Service = System.getProperty("user.dir")+"\\ChromeDriver\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", Service);
		DriverMembers obj = null;
		
		try {
			
			obj = new DriverMembers();
			obj.transactionName = transactionName;
			mainDriver.execute_TestSuite(obj);
			obj.extObj.endExtent();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			obj.driver.close();
		} 
	}
	
	public DriverThreads(DriverScript x, String y){
		mainDriver=x;
		transactionName=y;
	}

	public DriverThreads() {
		// TODO Auto-generated constructor stub
	}

}
