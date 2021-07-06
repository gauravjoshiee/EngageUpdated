package automation.core;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverThreads extends Thread {

	DriverScript mainDriver;
	String transactionName;
	XSSFWorkbook workbook;
	public WebDriver remotedriver;

	public void run() {

		String Service = System.getProperty("user.dir") + "\\chrome2\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", Service);
		DriverMembers obj = null;

		try {

			obj = new DriverMembers();
			obj.transactionName = transactionName;
			mainDriver.executeTestSuite(obj);
			obj.extObj.endExtent();

		} catch (Exception e) {
			e.printStackTrace();
			if (obj.driver != null) {
				obj.driver.close();
			}
		}
	}

	public DriverThreads(DriverScript x, String y) {
		mainDriver = x;
		transactionName = y;
	}

	public DriverThreads() {
	}

}
