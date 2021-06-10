package config;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import executionEngine.DriverMembers;
import executionEngine.DriverScript;
import io.github.bonigarcia.wdm.WebDriverManager;
import utility.ExcelUtils;
//import utility.Logger;
import utility.readMDMConfig;

//import org.openqa.selenium.ie.InternetExplorerDriver;
@SuppressWarnings("static-access")
public class ActionKeywords {

	public static WebElement form;

	/**
	 * This function used to click on a button/ input/ link Provide object in test
	 * case sheet
	 * 
	 * @param object
	 * @param data
	 */
	public static void click_button(String object, String data, DriverMembers obj) {
		try {
			// this.obj.driver.findElement(By.xpath(OR.getProperty(object))).click();
			Thread.sleep(1000);
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).click();

		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			// Logger.writeLog(Thread.currentThread().getName() +
			// Thread.currentThread().isAlive());
		}
	}

	public void waitUntilDisplayed(String object, String data, DriverMembers obj) {

		boolean initiate = false;

		for (int i = 0; i <= 5; i++) {
			initiate = isElementPresent(By.xpath(object), obj);
			if (initiate) {
				try {
					Thread.sleep(1000);
					int counter = 0;
					while (counter <= Constants.Global_Timeout) {
						if (obj.driver.findElement(By.xpath(object)).isDisplayed()) {
							Thread.sleep(1000);
							counter++;
						}
					}
				} catch (Exception e) {

					obj.sTestStepFailureDetail = "Element to wait appeared and removed in";
					// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
					// - " + e.getMessage());
					System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
					// Logger.writeLog(Thread.currentThread().getName() +
					// Thread.currentThread().isAlive());
					// Logger.writeLog(Thread.currentThread().getName() +
					// Thread.currentThread().isAlive());
				}
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage());
					System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
				}
			}
		}
		if (!initiate) {
			obj.sTestStepFailureDetail = "Element to wait did not appear";
			// Logger.writeLog("Element did not appear at - " + object);
		}

	}

	/**
	 * This function closes browser session
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized void closeBrowser(String object, String data, DriverMembers obj) {
		try {
			obj.driver.close();
			obj.driver.quit();

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void captureContent(String object, String data, DriverMembers obj) {
		try {
			String capturedText = obj.driver.findElement(By.xpath(object)).getText();
			ExcelUtils.insertDataVariable(obj.sTestCase, obj.sTestStepName, data, capturedText,
					Constants.Sheet_DataVariables, obj);

		} catch (Exception e) {

			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function is to assert DataVariable value from excel sheet to value of
	 * target xpath
	 */

	public synchronized void assertDataVariable(String object, String data, DriverMembers obj) {
		String expectedData = "";
		String actualData = obj.driver.findElement(By.xpath(object)).getText();
		try {
			expectedData = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);
			if (actualData.equals(expectedData)) {
				System.out.println(
						"Actual value - " + actualData + " matches with expected value - " + expectedData + "");
			} else {
				obj.sTestStepFailureDetail = ("Actual value - " + actualData + " NOT matches with expected value - "
						+ expectedData + "");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
			}
		}

		catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
		}

	}

	public synchronized static void inputDataVariable(String object, String data, DriverMembers obj) {

		String value = "";
		try {

			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);

			if (value != null) {
				obj.driver.findElement(By.xpath(object)).sendKeys(value);
			} else {
				obj.sTestStepFailureDetail = ("Unable to fetch data variable - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;

			}
		}

		catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	/**
	 * This function selects from multi select drop down on Charitable request form.
	 * Provide object and data in test case sheet*
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized static void drpdwnSelect(String object, String data, DriverMembers obj) {
		try {
			// WebElement element = obj.driver.findElement(By.id(OR.getProperty(object)));
			// Select drpSelect = new Select(element);
			// drpSelect.selectByVisibleText(data);
			Thread.sleep(1000);

			String listitem = "//*[normalize-space(text())='" + data + "']";
			String xpath = object + listitem;
			obj.driver.findElement(By.xpath(xpath)).getLocation();
			obj.driver.findElement(By.xpath(xpath)).click();

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(Thread.currentThread().getName() + " - Undable to select from drop down - " + object);
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function used to print stack trace if waiting for specific element fails
	 * 
	 * @param x
	 */
	private void fail(String x) {
		System.out.println(x);
	}

	/**
	 * This function takes screenshot and saves at defined location Currently only
	 * appends time stamp. Can be modified to append test step ID/ Description
	 * 
	 * @param object
	 * @param data
	 * @throws Exception
	 */
	public synchronized void getscreenshot(String object, String data, DriverMembers obj) throws Exception {
		try {
			File scrnsht = ((TakesScreenshot) obj.driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrnsht, new File("D:\\Automation POC/BVT Automation/RMSDefault_May2017/src/screenshots"
					+ System.currentTimeMillis() + ".png"));

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function used to send text value to input field. Provide object and data
	 * in test case sheet
	 * 
	 * @param object
	 * @param data
	 */
	public void input_text(String object, String data, DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).clear();
			obj.driver.findElement(By.xpath(object)).sendKeys(data);
		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).clear();
			obj.driver.findElement(By.xpath(object)).sendKeys(data);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function used for waiting for specific element to be visible
	 * 
	 * @param by
	 * @return
	 */
	private static boolean isElementPresent(By by, DriverMembers obj) {
		try {
			obj.driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isElementEnabled(By by, DriverMembers obj) {
		try {
			obj.driver.findElement(by).isEnabled();
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isElementSelected(By by, DriverMembers obj) {
		try {
			obj.driver.findElement(by).isSelected();
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public synchronized void launchApp(String object, String data, DriverMembers obj) {
		try {

			obj.driver.get(data);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Launching application failed - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function sets system property for browser driver exe and instantiates
	 * browser session. Currently hard coded, will be driven by configuration sheet
	 * later
	 * 
	 * @param object
	 * @param data
	 */

	public synchronized void openBrowser(String object, String data, DriverMembers obj) {

		try {
			if (data.equalsIgnoreCase("chrome")) {
				WebDriverManager.chromedriver().setup();
				obj.driver = new ChromeDriver();
				obj.driver.manage().window().maximize();
				obj.sTestStepFailureDetail = "Chrome browser started";
			} else {
				if (data.equalsIgnoreCase("firefox")) {
					WebDriverManager.firefoxdriver().setup();
					obj.driver = new FirefoxDriver();
					obj.sTestStepFailureDetail = "Firefox driver started";
				} else {
					obj.driver = null;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	/*
	 * 
	 * public synchronized void openBrowser(String object, String data,
	 * DriverMembers obj){
	 * 
	 * try{
	 * 
	 * 
	 * /* ChromeOptions chromeOptions = new ChromeOptions();
	 * chromeOptions.addArguments("--headless");
	 */
	// DesiredCapabilities cap = DesiredCapabilities.chrome();
	// cap.setCapability("disable-restore-session-state", true);
	// obj.remotedriver = new RemoteWebDriver (new
	// URL("http://192.168.225.113:5555/wd/hub"),cap);
	/*
	 * String Service =
	 * System.getProperty("user.dir")+"\\ChromeDriver\\chromeobj.driver.exe";
	 * System.setProperty("webobj.driver.chrome.driver", Service); ChromeOptions
	 * options = new ChromeOptions();
	 * options.setExperimentalOption("useAutomationExtension", false);
	 * options.addArguments("start-maximized"); options.addArguments("no-sandbox");
	 * options.addArguments("disable-extensions");
	 * options.addArguments("--disable-notifications"); obj.driver = new
	 * ChromeDriver(options);
	 * 
	 * 
	 * // obj.driver = new ChromeDriver(); obj.driver.manage().deleteAllCookies();
	 * obj.driver.manage().window().maximize();
	 * //((JavascriptExecutor)obj.driver).executeScript(
	 * "document.body.style.zoom='80%';");
	 * obj.driver.manage().timeouts().pageLoadTimeout(Constants.Global_Timeout,
	 * TimeUnit. SECONDS);
	 * 
	 * }
	 * 
	 * catch(Exception e){ e.printStackTrace();
	 * obj.sTestStepFailureDetail=e.getMessage();
	 * obj.sTestStepStatus=Constants.Key_Fail_Result;
	 * System.out.print(Thread.currentThread().getName()+Thread.currentThread().
	 * isAlive()); }
	 * 
	 * } /*
	 * 
	 * /**
	 * 
	 * This function uploads a file. Provide upload element object in test case
	 * sheet.
	 * 
	 * @param object
	 * 
	 * @param data
	 */
	public synchronized static void uploadByRobot(String object, String data, DriverMembers obj) {
		try {
			Thread.holdsLock(DriverScript.threadList);
			obj.driver.findElement(By.xpath(object)).click();

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();

			}
			// put path to your image in a clipboard
			// StringSelection ss = new StringSelection(Constants.txtUploadPath);
			StringSelection ss = new StringSelection(data);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			// imitate mouse events like ENTER, CTRL+C, CTRL+V
			Robot robot;

			robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

		} catch (AWTException e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
		}

	}

	public synchronized static void uploadRunConfig(String object, String data, DriverMembers obj) {
		try {
			String inputPath = obj.xlObj.getRunConfig(data);
			uploadByRobot(object, inputPath, obj);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This function waits for specific seconds. Can be used where page reloads for
	 * dependent fields. Provide time in second to wait in test case sheet
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized void waitForSeconds(String object, String data, DriverMembers obj) {

		for (int second = 0; second < Integer.parseInt(data); second++) {

			try {

				Thread.sleep(1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				// Logger.writeLog("Wait got intercepted - " + e.getMessage());
				System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());

			}
		}

	}

	/**
	 * This function waits for specific element to be visible on page. Timeouts
	 * afters 60 seconds. Provide object in test case sheet for which to wait
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized void waitForVisible(String object, String data, DriverMembers obj) {
		for (int second = 0;; second++) {
			if (second >= Constants.Global_Timeout) {
				obj.sTestStepFailureDetail = ("Unable to load in " + Constants.Global_Timeout + " seconds");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				// Logger.writeLog("Element not visible within global timeout limit");
				break;

			}
			try {
				if (isElementPresent(By.xpath(object), obj)) {
					// Logger.writeLog("Element is now visible at - " + object);
					break;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				obj.sTestStepFailureDetail = e.getMessage();
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
				// - " + e.getMessage());
				System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			}
		}
	}

	public synchronized void waitForEnabled(String object, String data, DriverMembers obj) {
		for (int second = 0;; second++) {
			if (second >= Constants.Global_Timeout) {
				fail("timeout");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				// Logger.writeLog("Element not enabled within global timeout limit.");
				break;

			}
			try {
				if (isElementEnabled(By.xpath(object), obj)) {
					// Logger.writeLog("Element is now enabled at - " + object);
					break;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				obj.sTestStepFailureDetail = e.getMessage();
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
				// - " + e.getMessage());
				System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			}
		}
	}

	public synchronized static void clickandSwitchTabSF(String object, String data, DriverMembers obj) {
		try {
			// Thread.holdsLock(Thread.currentThread());
			String oldTab = obj.driver.getWindowHandle();
			System.out.println("Current window handle saved successfully.");

			WebElement ele = obj.driver.findElement(By.xpath(object));
			JavascriptExecutor executor = (JavascriptExecutor) obj.driver;
			executor.executeScript("arguments[0].click();", ele);

			// obj.driver.findElement(By.xpath(object)).click();
			System.out.println("Clicked on element successfully");
			ArrayList<String> newTab = null;

			Thread.sleep(5000);
			newTab = new ArrayList<String>(obj.driver.getWindowHandles());

			newTab.remove(oldTab);
			// change focus to new tab
			System.out.println("Trying to switch");
			obj.driver.switchTo().window(newTab.get(0));
			System.out.println("Moved to new window handle successfully");

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Logger.writeLog("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized void setForm(String object, String data, DriverMembers obj) {
		try {
			form = obj.driver.findElement(By.xpath(object));
			// form.sendKeys(Keys.ARROW_DOWN);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Logger.writeLog("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized static void clickOnFrame(String object, String data, DriverMembers obj) {
		try {
			if (!data.isEmpty()) {
				String regex = "^[0-9]$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(data);
				if (matcher.find()) {
					int frameIndex = Integer.valueOf(data);
					obj.driver.switchTo().frame(frameIndex);
				} else {
					obj.driver.switchTo().frame(data);
				}
			} else {
				obj.driver.switchTo().frame(0);
			}
			obj.driver.findElement(By.xpath(object)).click();
			// form.findElement(By.xpath(object)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Logger.writeLog("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized void resetForm(String object, String data, DriverMembers obj) {
		try {
			form = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Logger.writeLog("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized static void clickLinkHavingText(String object, String data, DriverMembers obj) {
		String xpath = null;
		try {
			xpath = (object + "//a[contains(text(),normalize-space('" + data + "'))]");
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (ElementNotInteractableException ei) {
			setScroll(xpath, obj);
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on link having text - " + data);
			// Logger.writeLog("Unable to click on link having text - " + data);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void clickDataVariableText(String object, String data, DriverMembers obj) {
		String value = "";
		String xpath = null;
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);
			if (value == "") {
				System.out.println("No value found for data variable - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = ("No value found for data variable - " + data);
			} else {

				if (object != null) {
					xpath = (object + "//a[contains(normalize-space(text()),'" + value + "')]");
				} else {
					xpath = ("//a[contains(normalize-space(text()),'" + value + "')]");
				}
				obj.driver.findElement(By.xpath(xpath)).click();
			}
		} catch (ElementNotInteractableException ei) {
			setScroll(xpath, obj);
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on link having data variable text - " + value);
			obj.sLocalDataVariable = null;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void inputDataVariableText(String object, String data, DriverMembers obj) {
		String value = "";
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);
			if (value == null) {
				System.out.println("No value found for data variable - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = ("No value found for data variable - " + data);
			} else {
				obj.driver.findElement(By.xpath(object)).sendKeys(value);
			}
		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).sendKeys(value);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to input data variable text - " + value);
			obj.sLocalDataVariable = null;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void input_randomNumber(String object, String data, DriverMembers obj) {
		String inputStream;
		try {
			inputStream = String.valueOf(obj.xlObj.randomNumber(Integer.parseInt(data)));
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).sendKeys(inputStream);
		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).getLocation();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to input data");
		}
	}

	public synchronized void assertText(String object, String data, DriverMembers obj) {
		try {
			String actualText = null;
			highlightElement(obj);
			actualText = obj.driver.findElement(By.xpath(object)).getText();
			if (actualText.equals(data)) {
				obj.sTestStepFailureDetail = ("Actual data - " + actualText + " matches with Expected data - " + data);
			} else {
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = ("Actual data - " + actualText + " does not match with Expected data - "
						+ data);

			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to assert text");
			// Logger.writeLog("Unable to assert text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void assertPartialText(String object, String data, DriverMembers obj) {
		try {
			String actualText = null;
			actualText = obj.driver.findElement(By.xpath(object)).getText();
			if (actualText.contains(data)) {
				obj.sTestStepFailureDetail = ("Actual data - " + actualText + " contains Expected data - " + data);
				obj.sLocalDataVariable = null;
			} else {
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = ("Actual data - " + actualText + " does not contain Expected data - "
						+ data);
				obj.sLocalDataVariable = null;
			}
		} catch (Exception e) {
			setFailResult(e, obj, "Unablet to assert due to exception");
		}

	}

	public synchronized void assertPartialDataVariable(String object, String data, DriverMembers obj) {
		String value = "";
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);

			if (value == null) {
				System.out.println("No value found for data variable - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = ("No value found for data variable - " + data);
			} else {
				String actualText = null;
				actualText = obj.driver.findElement(By.xpath(object)).getText();
				if (actualText.contains(value)) {
					obj.sTestStepFailureDetail = ("Actual data - " + actualText + " contains Expected data - " + data);
				} else {
					obj.sTestStepStatus = Constants.Key_Fail_Result;
					obj.sTestCaseStatus = Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail = ("Actual data - " + actualText
							+ " does not contain with Expected data - " + value);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to assert text");
			// Logger.writeLog("Unable to assert text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void getTextFromAlert(String object, String data, DriverMembers obj) {
		try {

			Alert alert = obj.driver.switchTo().alert();
			String alertMessage = alert.getText();
			System.out.println(alertMessage);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to fetch alert box text");
			// Logger.writeLog("Unable to fetch alert box text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void acceptFromAlert(String object, String data, DriverMembers obj) {
		try {

			Alert alert = obj.driver.switchTo().alert();
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to accept from alert box");
			// Logger.writeLog("Unable to accept from alert box");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void setDataVariableContext(String object, String data, DriverMembers obj) {

		try {
			obj.sLocalDataVariable = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to set data variable context");
			// Logger.writeLog("Unable to set data variable context");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void resetDataVariableContext(String object, String data, DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = ("Unable to reset data variable context");
			// Logger.writeLog("Unable to reset data variable context");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void updateDataVariableValue(String object, String data, DriverMembers obj) {

		String value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data, obj);
		obj.xlObj.setDataVariable(Constants.Sheet_DataVariables, object, value, obj);
	}

	public synchronized static void clickLinkInDynamicRow(String object, String data, DriverMembers obj) {
		String xpath = null;
		try {
			String lookupText = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, object, obj);
			xpath = ("//*[normalize-space(text())='" + lookupText + "']//parent::*//following-sibling::td[" + data
					+ "]//*[text()]");
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (ElementNotInteractableException ei) {
			setScroll(xpath, obj);
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on expected link");
			// Logger.writeLog("Unable to accept from alert box");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void getDataFromDynamicRow(String object, String data, DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
			String containerRow = (object + "//tr[./td[normalize-space(text())='" + obj.sLocalDataVariable + "']]//td["
					+ data + "]//a[text()]");
			obj.sLocalDataVariable = obj.driver.findElement(By.xpath(containerRow)).getText();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on expected link");

			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void validateDataVariable(String object, String data, DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
			String containerRow = (object + "//tr[./td[normalize-space(text())='" + obj.sLocalDataVariable + "']]//td["
					+ data + "]//a[text()]");
			obj.sLocalDataVariable = obj.driver.findElement(By.xpath(containerRow)).getText();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on expected link");
			// Logger.writeLog("Unable to accept from alert box");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void waitUntilClickable(String object, String data, DriverMembers obj) {
		try {
			WebDriverWait wait = new WebDriverWait(obj.driver, Constants.Global_Timeout);
			By item = By.xpath(object);
			WebElement expected = wait.until(ExpectedConditions.presenceOfElementLocated(item));
			;
			wait.until(ExpectedConditions.visibilityOf(expected));
			wait.until(ExpectedConditions.elementToBeClickable(expected));

			wait = null;
			expected = null;
			item = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to wait for element to be clickable - trying next step"
					+ e.getMessage());
			// Logger.writeLog("Unable to wait for element to be clickable");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized static void clearField(String object, String data, DriverMembers obj) {
		WebElement toClear = obj.driver.findElement(By.xpath(object));
		toClear.sendKeys(Keys.CONTROL + "a");
		toClear.sendKeys(Keys.DELETE);
	}

	public synchronized void launchRunConfig(String object, String data, DriverMembers obj) {
		try {
			String launchURL = obj.xlObj.getRunConfig(data);
			if (launchURL != null) {
				obj.driver.get(launchURL);
			} else {
				obj.sTestStepFailureDetail = ("No value for run config - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("No value for run config - " + data);
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void inputRunConfig(String object, String data, DriverMembers obj) {
		String inputText = null;
		try {
			inputText = obj.xlObj.getRunConfig(data);
			if (inputText != null) {
				obj.driver.findElement(By.xpath(object)).sendKeys(inputText);
			} else {
				obj.sTestStepFailureDetail = ("No value for run config - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("No value for run config - " + data);
			}
		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).sendKeys(inputText);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void inputRandomEmail(String object, String data, DriverMembers obj) {
		try {
			String userPrefix = obj.xlObj.getRunConfig("NewGCPEmailPrefix");
			if (userPrefix.equals("")) {
				userPrefix = "test";
			}

			String randomEmail;
			String domain = obj.xlObj.getRunConfig(data);
			String regex = "^@[a-zA-Z0-9]*+[.]+[a-zA-Z]*$";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(domain);

			if (obj.generatedRandomString == null) {
				obj.generatedRandomString = String.valueOf(obj.xlObj.randomNumber(4));
			}

			if (matcher.find()) {
				randomEmail = (userPrefix + obj.generatedRandomString + domain);
			} else {
				randomEmail = (userPrefix + obj.generatedRandomString + "@yopmail.com");
			}
			System.out.println(randomEmail);
			obj.xlObj.updateRunConfig("NewGCPEmail", randomEmail, obj);
			obj.driver.findElement(By.xpath(object)).sendKeys(randomEmail);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void inputRandomUsername(String object, String data, DriverMembers obj) {
		try {
			String userPrefix = obj.xlObj.getRunConfig(data);
			if (obj.generatedRandomString == null) {
				obj.generatedRandomString = String.valueOf(obj.xlObj.randomNumber(4));
			}
			String randomUsername = (userPrefix + obj.generatedRandomString);
			obj.driver.findElement(By.xpath(object)).sendKeys(randomUsername);
			;
			obj.xlObj.updateRunConfig("NewGCPUsername", randomUsername, obj);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void validateElementPresent(String object, String data, DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(object), obj)) {
				System.out.println("Expected element displayed on UI");
			} else {
				obj.sTestStepFailureDetail = ("Expected element not displayed on UI");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("Expected element not displayed on UI");
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void validateElementNotPresent(String object, String data, DriverMembers obj) {
		try {
			if (!isElementPresent(By.xpath(object), obj)) {
				obj.sTestStepFailureDetail = ("Expected element not displayed on UI...");
				obj.sTestStepStatus = Constants.Key_Pass_Result;
				obj.sTestCaseStatus = Constants.Key_Pass_Result;
				System.out.println("Expected element not displayed on UI");
			} else {
				obj.sTestStepFailureDetail = ("Element displayed on UI...");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("Element displayed on UI...");
			}
		} catch (NoSuchElementException ne) {
			obj.sTestStepFailureDetail = ("Expected element not displayed on UI" + ne.getMessage());
			obj.sTestStepStatus = Constants.Key_Pass_Result;
			obj.sTestCaseStatus = Constants.Key_Pass_Result;
			System.out.println("Expected element not displayed on UI" + ne.getMessage());
		}

		catch (Exception e) {
			setFailResult(e, obj, "");
		}

	}

	public synchronized void inputRunConfigValue(String object, String data, DriverMembers obj) {
		try {
			String inputText = (obj.xlObj.getRunConfig(data));
			if (inputText != null) {
				obj.driver.findElement(By.xpath(object)).sendKeys(String.valueOf(inputText));
				;
			} else {
				obj.sTestStepFailureDetail = ("No value for run config - " + data);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("No value for run config - " + data);
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized void updateRunConfig(String object, String data, DriverMembers obj) {
		try {
			String runValue = obj.driver.findElement(By.xpath(object)).getText();
			obj.xlObj.updateRunConfig(data, runValue, obj);
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized void executeFunctionalBlock(String object, String data, DriverMembers obj) {
		try {
			DriverScript.execute_Block(data, obj);
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized void inputDataFeeder(String object, String data, DriverMembers obj) {
		String inputDataFeed = null;
		try {
			obj.xlObj.setExcelFile(obj.sDataFeeder, "DataFeeder");
			inputDataFeed = obj.xlObj.getSpecificCellData(obj.sCurrentIteration, Integer.parseInt(data), "DataFeeder",
					obj.sDataFeeder);
			obj.driver.findElement(By.xpath(object)).sendKeys(inputDataFeed);
			obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
		} catch (ElementNotInteractableException ei) {
			try {
				setScroll(object, obj);
				obj.driver.findElement(By.xpath(object)).sendKeys(inputDataFeed);
				obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
			} catch (Exception e) {
				setFailResult(e, obj, "");
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}

	}

	public synchronized static void setScroll(String object, DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(object));
		je.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public synchronized static void highlightElement(DriverMembers obj) {
		try {
			JavascriptExecutor je = (JavascriptExecutor) obj.driver;
			WebElement element = obj.driver.findElement(By.xpath(obj.sPageObject));
			je.executeScript("arguments[0].setAttribute('style', 'border: 2px solid red;');", element);
			Thread.sleep(1000);
			je.executeScript("arguments[0].setAttribute('style', '');", element);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void assertFontHexColor(String object, String data, DriverMembers obj) {
		try {
			String expectedColor = obj.xlObj.getRunConfig(data);

			highlightElement(obj);

			String color = obj.driver.findElement(By.xpath(object)).getCssValue("color");
			String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

			int hexValue1 = Integer.parseInt(hexValue[0]);
			hexValue[1] = hexValue[1].trim();
			int hexValue2 = Integer.parseInt(hexValue[1]);
			hexValue[2] = hexValue[2].trim();
			int hexValue3 = Integer.parseInt(hexValue[2]);

			String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
			if (!actualColor.equalsIgnoreCase(expectedColor)) {
				obj.sTestStepFailureDetail = ("Actual color -" + actualColor + " not matches Expected color - "
						+ expectedColor);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
			} else {
				obj.sTestStepFailureDetail = ("Actual color -" + actualColor + " matches Expected color - "
						+ expectedColor);
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized static void assertBackgroundHexColor(String object, String data, DriverMembers obj) {
		try {
			String expectedColor = obj.xlObj.getRunConfig(data);

			highlightElement(obj);
			Thread.sleep(1000);
			String color = obj.driver.findElement(By.xpath(object)).getCssValue("background-color");
			String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

			int hexValue1 = Integer.parseInt(hexValue[0]);
			hexValue[1] = hexValue[1].trim();
			int hexValue2 = Integer.parseInt(hexValue[1]);
			hexValue[2] = hexValue[2].trim();
			int hexValue3 = Integer.parseInt(hexValue[2]);

			String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
			if (!actualColor.equalsIgnoreCase(expectedColor)) {
				obj.sTestStepFailureDetail = ("Actual color -" + actualColor + " not matches Expected color - "
						+ expectedColor);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
			} else {
				obj.sTestStepFailureDetail = ("Actual color -" + actualColor + " matches Expected color - "
						+ expectedColor);
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	// public synchronized static void mouseHover(String object, String data,
	// DriverMembers obj){
	// Actions actions = new Actions(obj.driver);
	// WebElement target = obj.driver.findElement(By.xpath(object));
	//
	// actions.moveToElement(target).perform();
	// }

	public synchronized static void setFailResult(Exception e, DriverMembers obj, String customFailureMessage) {
		if (customFailureMessage.isEmpty()) {
			obj.sTestStepFailureDetail = e.getMessage();
		} else {
			obj.sTestStepFailureDetail = customFailureMessage + " - " + e.getMessage();
		}
		obj.sTestStepStatus = Constants.Key_Fail_Result;
		// obj.sTestCaseStatus=Constants.Key_Fail_Result;
		System.out.println(e.getMessage());
	}

	public synchronized static void writeMDMFile(String object, String data, DriverMembers obj) {
		try {
			readMDMConfig.writeFile();
		} catch (Exception e) {
			setFailResult(e, obj, e.getMessage());
		}
	}

	public synchronized static void validateToggleSetting(String object, String data, DriverMembers obj) {
		obj.dbObj.validateToggleSetting(data, obj);
	}

	public synchronized static void validateRMSLOVLoad(String object, String data, DriverMembers obj) {
		try {
			obj.dbObj.validateLOVLoad(obj);
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public synchronized static void verifyEmailTriggered(String object, String data, DriverMembers obj) {
		obj.emlObj.getMail(obj);
	}

	public synchronized static void mouseHover(String object, String data, DriverMembers obj) {

		try {
			Actions actions = new Actions(obj.driver);
			WebElement target = obj.driver.findElement(By.xpath(object));

			actions.moveToElement(target).perform();
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public synchronized static void performDragAndDrop(String object, String data, DriverMembers obj)
			throws InterruptedException {

		WebElement from = obj.driver.findElement(By.xpath(data));
		WebElement to = obj.driver.findElement(By.xpath(object));

		Actions actions = new Actions(obj.driver);

		try {
			actions.dragAndDrop(from, to).build().perform();

			// actions.clickAndHold(from).build().perform();
			// actions.pause(Duration.ofSeconds(1));
			// actions.moveToElement(to).build().perform();
			// //actions.moveByOffset(-1, 0).build().perform();
			// actions.pause(Duration.ofSeconds(1));
			// actions.release().build().perform();
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}

		// .pause(Duration.ofSeconds(1))
		// .clickAndHold(element)
		// .pause(Duration.ofSeconds(1))
		// .moveByOffset(1, 0)
		// .moveToElement(target)
		// .moveByOffset(1, 0)
		// .pause(Duration.ofSeconds(1))
		// .release().perform();;
		//
		// actions.clickAndHold(from).build().perform();
		// .waitFor(1).seconds();
		// act.moveToElement(to).build().perform();
		// Timeout.waitFor(1).seconds();
		// act.moveByOffset(-1, -1).build().perform();
		// Timeout.waitFor(1).seconds();
		// act.release().build().perform();`

	}

	public synchronized static void performDragAndDropJS(String object, String data, DriverMembers obj)
			throws InterruptedException {

		WebElement From = obj.driver.findElement(By.xpath(data));
		WebElement To = obj.driver.findElement(By.xpath(object));

		final String java_script = "var src=arguments[0],tgt=arguments[1];var dataTransfer={dropEffe"
				+ "ct:'',effectAllowed:'all',files:[],items:{},types:[],setData:fun"
				+ "ction(format,data){this.items[format]=data;this.types.append(for"
				+ "mat);},getData:function(format){return this.items[format];},clea"
				+ "rData:function(format){}};var emit=function(event,target){var ev"
				+ "t=document.createEvent('Event');evt.initEvent(event,true,false);"
				+ "evt.dataTransfer=dataTransfer;target.dispatchEvent(evt);};emit('"
				+ "dragstart',src);emit('dragenter',tgt);emit('dragover',tgt);emit("
				+ "'drop',tgt);emit('dragend',src);";
		try {
			((JavascriptExecutor) obj.driver).executeScript(java_script, From, To);
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public synchronized static void acceptAlert(String object, String data, DriverMembers obj) {
		try {
			Alert alert = obj.driver.switchTo().alert(); // switch to alert
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public static void clickButtonSalesforce(String object, String data, DriverMembers obj) {
		try {
			// this.obj.driver.findElement(By.xpath(OR.getProperty(object))).click();
			Thread.sleep(1000);

			WebElement ele = obj.driver.findElement(By.xpath(object));
			JavascriptExecutor executor = (JavascriptExecutor) obj.driver;
			executor.executeScript("arguments[0].click();", ele);

		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void switchToFrame(String object, String data, DriverMembers obj) {
		try {
			WebDriverWait wait = new WebDriverWait(obj.driver, 10);
			WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(object)));

			obj.driver.switchTo().frame(iframe);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void validateDropDownOptions(String object, String data, DriverMembers obj) {
		boolean match = false;
		String[] dataOptions = data.split(",");
		WebElement dropDown = obj.driver.findElement(By.xpath(object));
		Select check = new Select(dropDown);
		List<WebElement> allOptions = check.getOptions();
		List<String> options = new ArrayList<String>();

		for (WebElement e : allOptions) {
			options.add(e.getText());
		}

		if (options.contains(null) || options.contains("")) {
			options.remove(0);
		}

		try {
			if (options.size() == dataOptions.length) {
				for (int j = 0; j < dataOptions.length; j++) {
					if (options.contains(dataOptions[j])) {
						match = true;
						System.out.println(dataOptions[j] + " " + "- option matches..");
					} else {
						match = false;
						obj.sTestStepStatus = Constants.Key_Fail_Result;
						obj.sTestCaseStatus = Constants.Key_Fail_Result;
						// obj.sScreenshotPath = obj.extObj.addScreencast(obj);
						// obj.sTestStepFailureDetail = (dataOptions[j] + " " + "- option does not
						// match" +
						// "\n" + obj.sScreenshotPath);
						// Logger.writeLog("Unable to assert all dropdown options");
						obj.sTestStepFailureDetail = (dataOptions[j] + " " + "- option does not match");
						Assert.fail(dataOptions[j] + " " + "- option does not match");
						System.out.println(dataOptions[j] + " " + "- option does not match");
					}
				}
			} else {
				Assert.fail("Expected & Actual # of options do not match ...");
				// Logger.writeLog("Expected & Actual # of options do not match ...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void validateElementSelected(String object, String data, DriverMembers obj) {

		try {
			// boolean isSelected = obj.driver.findElement(By.xpath(object)).isSelected();
			if (isElementSelected(By.xpath(object), obj)) {
				System.out.println("Expected element is selected on UI");
			} else {
				obj.sTestStepFailureDetail = ("Expected element is not selected on UI");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("Expected element not selected on UI");
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void navigateBack(String object, String data, DriverMembers obj) {
		obj.driver.navigate().back();
	}

	// Get The Current Day
	public synchronized static String getCurrentDay() {
		// Create a Calendar Object
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		// Get Current Day as a number
		int todayInt = calendar.get(Calendar.DAY_OF_MONTH);

		// Integer to String Conversion
		String todayStr = Integer.toString(todayInt);

		return todayStr;
	}

	// Selects date in date picker
	public synchronized static void selectCurrentDate(String object, String data, DriverMembers obj) {

		// Get Today's number
		String today = getCurrentDay();
		// date picker table
		WebElement dateWidgetFrom = obj.driver.findElement(By.xpath(object));
		// List<WebElement> rows = dateWidgetFrom.findElements(By.tagName("tr"));
		// columns from date picker table
		List<WebElement> columns = dateWidgetFrom
				.findElements(By.xpath("//td[not(contains(@class,'xdsoft_other_month'))]"));

		// Wait for 4 Seconds to see Today's date selected.
		try {
			for (WebElement cell : columns) {
				// Select Today's Date
				if (cell.getText().equals(today)) {
					cell.click();
					break;
				}
			}
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void selectAnyDate(String object, String data, DriverMembers obj) {

		// date picker table
		WebElement dateWidgetFrom = obj.driver.findElement(By.xpath(object));
		// List<WebElement> rows = dateWidgetFrom.findElements(By.tagName("tr"));
		// columns from date picker table
		List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));

		try {
			for (WebElement cell : columns) {
				if (cell.getText().equals(data)) {
					System.out.println(cell.getText());
					cell.click();
					break;
				}
			}
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized static String getCurrentMonth() {
		YearMonth thisMonth = YearMonth.now();
		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

		return thisMonth.format(monthYearFormatter);
	}

	public synchronized static void validateOptionsOrder(String object, String data, DriverMembers obj) {

		List<String> tabOptions = new ArrayList<String>(Arrays.asList(data.split(",")));

		List<WebElement> originalList = new ArrayList<>(obj.driver.findElements(By.xpath(object)));

		List<String> options = new ArrayList<String>();
		for (WebElement e : originalList) {
			options.add(e.getText());
		}

		try {
			if (options.size() == tabOptions.size()) {
				for (int i = 0; i < tabOptions.size(); i++) {
					if (tabOptions.get(i).replaceAll("\\s", "")
							.equalsIgnoreCase(options.get(i).replaceAll("\\s", ""))) {
						System.out.println(tabOptions.get(i) + " - displayed in expected order");
					} else {
						Assert.fail(tabOptions.get(i) + " - not displayed in expected order");
					}
				}
			} else {
				Assert.fail("Number of expected and actual options do not match...");
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}

	}

	public synchronized void validateElementEnabled(String object, String data, DriverMembers obj) {

		try {
			if (isElementEnabled(By.xpath(object), obj)) {
				System.out.println("Expected element is enabled on UI");
			} else {
				obj.sTestStepFailureDetail = ("Expected element is not enabled on UI");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("Expected element not enabled on UI");
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void handleLastCallAccountOption(String object, String data, DriverMembers obj) {

		try {
			// obj.driver.findElement(By.xpath(data)).click();
			if (isElementPresent(By.xpath(data), obj)) {
				if (new WebDriverWait(obj.driver, 20)
						.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(data))).getAttribute("innerHTML")
						.contains("Current call is the most recent one for")) {
					System.out.println("Message displayed on UI - "
							+ "Current call is the latest call for this account and no last call exists");

				} else {
					Assert.fail("Message not displayed on UI");
				}

			} else {
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<String>(obj.driver.getWindowHandles());
				Thread.sleep(5000);
				newTab.remove(oldTab);
				obj.driver.switchTo().window(newTab.get(0));

				// WebDriverWait wait = new WebDriverWait(obj.driver, 10);
				// WebElement iframe =
				// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(object)));
				obj.driver.switchTo().frame(0);

				if (obj.driver.findElement(By.xpath(object)).isDisplayed()) {
					System.out.println("User is navigated to Last Call page");
				}

				// Navigate back to call page
				obj.driver.navigate().back();
				Thread.sleep(30000);
				// Switch to frame in call page
				obj.driver.switchTo().frame(0);

			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void handleNewOrderOption(String object, String data, DriverMembers obj) {

		try {
			if (isElementPresent(By.xpath(object), obj)) {
				obj.driver.findElement(By.xpath(object)).click();
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<String>(obj.driver.getWindowHandles());
				Thread.sleep(5000);
				newTab.remove(oldTab);
				obj.driver.switchTo().window(newTab.get(0));
			} else {
				System.out.println("User is navigated to a new tab");
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<String>(obj.driver.getWindowHandles());
				Thread.sleep(5000);
				newTab.remove(oldTab);

			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void switchToPreviousTab(String object, String data, DriverMembers obj) {
		try {

			ArrayList<String> tabs = new ArrayList<String>(obj.driver.getWindowHandles());
			System.out.println(tabs.size());
			// Use the list of window handles to switch between windows
			obj.driver.switchTo().window(tabs.get(0));

			/*
			 * //Switch back to original window String mainWindowHandle;
			 * obj.driver.switchTo().window(mainWindowHandle);
			 */

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Logger.writeLog("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void handleCallSaveAndSubmit(String object, String data, DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(data), obj)) {
				obj.driver.findElement(By.xpath(data)).click();
				Thread.sleep(40000);

				// Verify call is saved and user is navigated to Account page
				if (isElementPresent(By.xpath(object), obj)) {
					System.out.println("Call saved successfully & user navigated to account page");
				} else {
					Assert.fail("Call save not successful");
				}

			} else {
				Thread.sleep(40000);

				// Verify call is saved and user is navigated to Account page
				if (isElementPresent(By.xpath(object), obj)) {
					System.out.println("Call saved successfully & user navigated to account page");
				} else {
					Assert.fail("Call save not successful");
				}
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void enterQuantity(String object, String data, DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(object)).click();
			Thread.sleep(10000);

			obj.driver
					.findElement(By.xpath(
							"//div[contains(@class,'number-item backspace')]//button[contains(@class,'btn btn-link')]"))
					.click();
			Thread.sleep(5000);
			obj.driver.findElement(By.xpath("//button[contains(text(),'" + data + "')]")).click();
			Thread.sleep(5000);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void handleSampleLimitFeature(String object, String data, DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath("//button[contains(text(),'Confirm')]"), obj)) {
				obj.driver.findElement(By.xpath("//button[contains(text(),'Confirm')]")).click();
				Thread.sleep(20000);

				if (isElementPresent(By.xpath(data), obj)) {
					if (new WebDriverWait(obj.driver, 20)
							.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(data)))
							.getAttribute("innerHTML").contains("Quantity exceeds sample limits")) {
						System.out.println(
								"Message displayed on UI - " + "Sample Limit has reached and Call can not be saved");

					} else {
						System.out.println("Message not displayed on UI");
					}

				} else {
					Thread.sleep(40000);
					// Verify call is saved and user is navigated to Account page
					if (isElementPresent(By.xpath(object), obj)) {
						System.out.println("Sample Limit is allowed and call saved successfully");
					} else {
						Assert.fail("Call save not successful");
					}
				}

			} else {
				if (isElementPresent(By.xpath(data), obj)) {
					if (new WebDriverWait(obj.driver, 20)
							.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(data)))
							.getAttribute("innerHTML").contains("Quantity exceeds sample limits")) {
						System.out.println(
								"Message displayed on UI - " + "Sample Limit has reached and Call can not be saved");

					} else {
						System.out.println("Message not displayed on UI");
					}

				} else {
					Thread.sleep(40000);
					// Verify call is saved and user is navigated to Account page
					if (isElementPresent(By.xpath(object), obj)) {
						System.out.println("Sample Limit is allowed and call saved successfully");
					} else {
						Assert.fail("Call save not successful");
					}
				}
			}

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void handleAddDocumentID(String object, String data, DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(data)).click();
			Thread.sleep(10000);

			if (isElementPresent(By.xpath(object), obj)) {
				obj.driver.findElement(By.xpath(object)).click();
				Thread.sleep(40000);
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void validateErrorMessage(String object, String data, DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(object), obj)) {
				if (new WebDriverWait(obj.driver, 20)
						.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(object)))
						.getAttribute("innerHTML").contains(data)) {
					System.out.println("Error Message Displayed - " + data);

				} else {
					System.out.println("Error Message not displayed");
				}

			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void assertColorLegendPlanner(String object, String data, DriverMembers obj) {
		try {
			Thread.sleep(10000);

			String scheduledTime = "//span[contains(text(),'" + data + "')]//following::div[1]";
			String xpath = object + scheduledTime;
			obj.driver.findElement(By.xpath(xpath)).click();
			;
			// obj.driver.findElementByXPath(xpath).getAttribute();
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void inputTextRandomString(String object, String data, DriverMembers obj) {
		try {
			String inputText = data;
			if (obj.generatedRandomString == null) {
				obj.generatedRandomString = String.valueOf(obj.xlObj.randomNumber(1));
			}
			String randomString = (inputText + " " + obj.generatedRandomString);
			obj.driver.findElement(By.xpath(object)).sendKeys(randomString);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized static void selectAnyDatePlanner(String object, String data, DriverMembers obj) {

		try {
			// WebElement element = obj.driver.findElement(By.id(OR.getProperty(object)));
			// Select drpSelect = new Select(element);
			// drpSelect.selectByVisibleText(data);
			Thread.sleep(1000);

			String listitem = "/a[text()='" + data + "']";
			String xpath = object + listitem;
			obj.driver.findElement(By.xpath(xpath)).getLocation();
			obj.driver.findElement(By.xpath(xpath)).click();

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(Thread.currentThread().getName() + " - Undable to select from date picker - " + object);
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void selectPMCall(String object, String data, DriverMembers obj) {

		try {
			// WebElement element = obj.driver.findElement(By.id(OR.getProperty(object)));
			// Select drpSelect = new Select(element);
			// drpSelect.selectByVisibleText(data);
			if (obj.driver.findElement(By.xpath(object)).getText().equalsIgnoreCase("AM")) {
				obj.driver.findElement(By.xpath(object)).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(Thread.currentThread().getName() + " - Undable to select from drop down - " + object);
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void selectAMCall(String object, String data, DriverMembers obj) {

		try {
			// WebElement element = obj.driver.findElement(By.id(OR.getProperty(object)));
			// Select drpSelect = new Select(element);
			// drpSelect.selectByVisibleText(data);
			if (obj.driver.findElement(By.xpath(object)).getText().equalsIgnoreCase("PM")) {
				obj.driver.findElement(By.xpath(object)).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(Thread.currentThread().getName() + " - Undable to select from drop down - " + object);
			// Logger.writeLog("Event unsuccessful at - " + object + " \\n Error description
			// - " + e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized static void setScrolltoPage(String object, String data, DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(object));
		je.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static void clickButtonAction(String object, String data, DriverMembers obj) {
		try {
			// this.obj.driver.findElement(By.xpath(OR.getProperty(object))).click();
			Thread.sleep(1000);
			Actions actions = new Actions(obj.driver);
			WebElement ele = obj.driver.findElement(By.xpath(object));
			actions.clickAndHold(ele).perform();
			Thread.sleep(1000);
			actions.release().perform();
			// JavascriptExecutor executor = (JavascriptExecutor) obj.driver;
			// executor.executeScript("arguments[0].click();", ele);

		} catch (ElementNotInteractableException ei) {
			setScroll(object, obj);
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void openBrowserWithoutSecurity(String object, String data, DriverMembers obj) {

		try {

			String Service = System.getProperty("user.dir") + "\\chrome2\\chromedriver.exe";
			System.setProperty("webobj.driver.chrome.driver", Service);
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("useAutomationExtension", false);
			options.addArguments("start-maximized");
			options.addArguments("no-sandbox");
			options.addArguments("disable-extensions");
			options.addArguments("disable-popup-blocking");
			options.addArguments("--disable-web-security");
			obj.driver = new ChromeDriver(options);

			// obj.driver = new ChromeDriver();
			obj.driver.manage().deleteAllCookies();
			obj.driver.manage().window().maximize();
			// ((JavascriptExecutor)obj.driver).executeScript("document.body.style.zoom='80%';");
			obj.driver.manage().timeouts().pageLoadTimeout(Constants.Global_Timeout, TimeUnit.SECONDS);

		}

		catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized static void scrollwithinElement(String object, String data, DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(object));
		je.executeScript("arguments[0].scrollTo(0, arguments[0].scrollHeight)", element);

	}

	public synchronized static void clickAndSwitchTab(String object, String data, DriverMembers obj) {
		try {
			// Thread.holdsLock(Thread.currentThread());
			String oldTab = obj.driver.getWindowHandle();
			System.out.println("Current window handle saved successfully.");
			obj.driver.findElement(By.xpath(object)).getLocation();
			obj.driver.findElement(By.xpath(object)).click();
			System.out.println("Clicked on element successfully");
			ArrayList<String> newTab = null;

			Thread.sleep(5000);
			newTab = new ArrayList<String>(obj.driver.getWindowHandles());

			newTab.remove(oldTab);
			// change focus to new tab
			System.out.println("Trying to switch");
			obj.driver.switchTo().window(newTab.get(0));
			System.out.println("Moved to new window handle successfully");

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			// Log.info("Unable to complete window switch");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized static void scrollTillPageEnd(String object, String data, DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		// je.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		je.executeScript(
				"window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
	}

	public synchronized static void placeMDMTestData(String object, String data, DriverMembers obj) {
		try {
			obj.mdm.prepareAndTransferMDMFile(obj);
		} catch (Exception e) {
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
		}
	}

	public synchronized static void checkMDMExecution(String object, String data, DriverMembers obj) {
		try {
			obj.dbObj.checkMDMStatus(obj);
		} catch (Exception e) {
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
		}
	}
}
