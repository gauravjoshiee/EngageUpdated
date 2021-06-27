package com.infobeans.automation.config;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.infobeans.automation.core.DriverMembers;
import com.infobeans.automation.core.DriverScript;
import com.infobeans.automation.utility.ExcelUtils;
import com.infobeans.automation.utility.readMDMConfig;

import io.github.bonigarcia.wdm.WebDriverManager;

@SuppressWarnings("static-access")
public class ActionKeywords {

	static WebElement form;
	public static final String NO_DATA_VARIABLE = "No value found for data variable - ";
	public static final String UNCLICKABLE = "Unable to click on expected link";
	public static final String ACTUAL_DATA = "Actual value - ";
	public static final String NO_RUN_CONFIG = "No value for run config - ";
	public static final String NO_DISPLAY = "Expected element not displayed on UI";
	public static final String NO_MATCH = "- does not match";

	/**
	 * This function used to click on a button/ input/ link Provide object in test
	 * case sheet
	 * 
	 * @param object
	 * @param data
	 */
	public static void click_button(DriverMembers obj) {
		try {
			Thread.sleep(1000);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();

		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}
	}

	public void waitUntilDisplayed(DriverMembers obj) {
		
		WebDriverWait wait = new WebDriverWait(obj.driver, Constants.Global_Timeout);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageObject)));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(obj.sPageObject)));

	}

	/**
	 * This function closes browser session
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized void closeBrowser(DriverMembers obj) {
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

	public synchronized void captureContent(DriverMembers obj) {
		try {
			String capturedText = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
			ExcelUtils.insertDataVariable(obj.sTestCase, obj.sTestStepName, obj.sPageData, capturedText,
					Constants.Sheet_DataVariables, obj);

		} catch (Exception e) {

			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	/**
	 * This function is to assert DataVariable value from excel sheet to value of
	 * target xpath
	 */

	public synchronized void assertDataVariable(DriverMembers obj) {
		String expectedData = "";
		String actualData = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
		try {
			expectedData = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);
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
		}

	}

	public static synchronized void inputDataVariable(DriverMembers obj) {

		String value = "";
		try {

			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);

			if (value != null) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(value);
			} else {
				obj.sTestStepFailureDetail = ("Unable to fetch data variable - " + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;

			}
		}

		catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
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
	public static synchronized void drpdwnSelect(DriverMembers obj) {
		try {

			String listitem = "//*[normalize-space(text())='" + obj.sPageData + "']";
			String xpath = obj.sPageObject + listitem;
			obj.driver.findElement(By.xpath(xpath)).getLocation();
			obj.driver.findElement(By.xpath(xpath)).click();

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(
					Thread.currentThread().getName() + " - Undable to select from drop down - " + obj.sPageObject);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
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
	public synchronized void getscreenshot(DriverMembers obj) {
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
	public void input_text(DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).clear();
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(obj.sPageData);
		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).clear();
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(obj.sPageData);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
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

	public synchronized void launchApp(DriverMembers obj) {
		try {

			obj.driver.get(obj.sPageData);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
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

	public synchronized void openBrowser(DriverMembers obj) {

		try {
			if (obj.sPageData.equalsIgnoreCase("chrome")) {
				WebDriverManager.chromedriver().setup();
				obj.driver = new ChromeDriver();
				obj.driver.manage().window().maximize();
				obj.sTestStepFailureDetail = "Chrome browser started";
			} else {
				if (obj.sPageData.equalsIgnoreCase("firefox")) {
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

	/**
	 * 
	 * This function uploads a file. Provide upload element object in test case
	 * sheet.
	 * 
	 * @param object
	 * 
	 * @param data
	 */
	public static synchronized void uploadByRobot(DriverMembers obj) {
		try {
			Thread.holdsLock(DriverScript.threadList);
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
			wait(2, obj);

			StringSelection ss = new StringSelection(obj.sPageData);
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

			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			Thread.currentThread().interrupt();

		}

	}

	public static synchronized void uploadRunConfig(DriverMembers obj) {
		try {
			obj.sPageData = obj.xlObj.getRunConfig(obj.sPageData);
			uploadByRobot(obj);
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
	public synchronized void waitForSeconds(DriverMembers obj) {

		for (int second = 0; second < Integer.parseInt(obj.sPageData); second++) {

			wait(Integer.parseInt(obj.sPageData), obj);
		}

	}

	private static void wait(int seconds, DriverMembers obj) {
		obj.driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
	}

	/**
	 * This function waits for specific element to be visible on page. Timeouts
	 * afters 60 seconds. Provide object in test case sheet for which to wait
	 * 
	 * @param object
	 * @param data
	 */
	public synchronized void waitForVisible(DriverMembers obj) {
		try {
			WebDriverWait wait = new WebDriverWait(obj.driver, Constants.Global_Timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageObject)));

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}

	}

	public synchronized void waitForEnabled(DriverMembers obj) {
		for (int second = 0;; second++) {
			if (second >= Constants.Global_Timeout) {
				fail("timeout");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				break;

			}
			try {
				if (isElementEnabled(By.xpath(obj.sPageObject), obj)) {
					break;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				obj.sTestStepFailureDetail = e.getMessage();
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
				Thread.currentThread().interrupt();
			}
		}
	}

	public static synchronized void clickandSwitchTabSF(DriverMembers obj) {
		try {
			String oldTab = obj.driver.getWindowHandle();
			System.out.println("Current window handle saved successfully.");

			WebElement ele = obj.driver.findElement(By.xpath(obj.sPageObject));
			JavascriptExecutor executor = (JavascriptExecutor) obj.driver;
			executor.executeScript("arguments[0].click();", ele);

			System.out.println("Clicked on element successfully");
			ArrayList<String> newTab = null;

			Thread.sleep(5000);
			newTab = new ArrayList<>(obj.driver.getWindowHandles());

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
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}

	}

	public synchronized void setForm(DriverMembers obj) {
		try {
			form = obj.driver.findElement(By.xpath(obj.sPageObject));
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public static synchronized void clickOnFrame(DriverMembers obj) {
		try {
			if (!obj.sPageData.isEmpty()) {
				String regex = "^[0-9]$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(obj.sPageData);
				if (matcher.find()) {
					int frameIndex = Integer.parseInt(obj.sPageData);
					obj.driver.switchTo().frame(frameIndex);
				} else {
					obj.driver.switchTo().frame(obj.sPageData);
				}
			} else {
				obj.driver.switchTo().frame(0);
			}
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public synchronized void resetForm(DriverMembers obj) {
		try {
			form = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public static synchronized void clickLinkHavingText(DriverMembers obj) {
		String xpath = null;
		try {
			xpath = (obj.sPageObject + "//a[contains(text(),normalize-space('" + obj.sPageData + "'))]");
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (ElementNotInteractableException ei) {
			setScroll(xpath, obj);
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to click on link having text - " + obj.sPageData);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public static synchronized void clickDataVariableText(DriverMembers obj) {
		String value = "";
		String xpath = null;
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);
			if (value.equals("")) {
				System.out.println(NO_DATA_VARIABLE + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = (NO_DATA_VARIABLE + obj.sPageData);
			} else {

				if (obj.sPageObject != null) {
					xpath = (obj.sPageObject + "//a[contains(normalize-space(text()),'" + value + "')]");
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

	public synchronized void inputDataVariableText(DriverMembers obj) {
		String value = "";
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);
			if (value == null) {
				System.out.println(NO_DATA_VARIABLE + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = (NO_DATA_VARIABLE + obj.sPageData);
			} else {
				obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(value);
			}
		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(value);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to input data variable text - " + value);
			obj.sLocalDataVariable = null;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public static synchronized void input_randomNumber(DriverMembers obj) {
		String inputStream;
		try {
			inputStream = String.valueOf(obj.xlObj.randomNumber(Integer.parseInt(obj.sPageData)));
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(inputStream);
		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to input data");
		}
	}

	public synchronized void assertText(DriverMembers obj) {
		try {
			String actualText = null;
			highlightElement(obj);
			actualText = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
			if (actualText.equals(obj.sPageData)) {
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText + " matches with Expected data - "
						+ obj.sPageData);
			} else {
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText + " does not match with Expected data - "
						+ obj.sPageData);

			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to assert text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void assertPartialText(DriverMembers obj) {
		try {
			String actualText = null;
			actualText = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
			if (actualText.contains(obj.sPageData)) {
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText + " contains Expected data - "
						+ obj.sPageData);
				obj.sLocalDataVariable = null;
			} else {
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText + " does not contain Expected data - "
						+ obj.sPageData);
				obj.sLocalDataVariable = null;
			}
		} catch (Exception e) {
			setFailResult(e, obj, "Unablet to assert due to exception");
		}

	}

	public synchronized void assertPartialDataVariable(DriverMembers obj) {
		String value = "";
		try {
			value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);

			if (value == null) {
				System.out.println(NO_DATA_VARIABLE + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = (NO_DATA_VARIABLE + obj.sPageData);
			} else {
				String actualText = null;
				actualText = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
				if (actualText.contains(value)) {
					obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText + " contains Expected data - "
							+ obj.sPageData);
				} else {
					obj.sTestStepStatus = Constants.Key_Fail_Result;
					obj.sTestCaseStatus = Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail = (ACTUAL_DATA + actualText
							+ " does not contain with Expected data - " + value);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to assert text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void getTextFromAlert(DriverMembers obj) {
		try {

			Alert alert = obj.driver.switchTo().alert();
			String alertMessage = alert.getText();
			System.out.println(alertMessage);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to fetch alert box text");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void acceptFromAlert(DriverMembers obj) {
		try {

			Alert alert = obj.driver.switchTo().alert();
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to accept from alert box");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void setDataVariableContext(DriverMembers obj) {

		try {
			obj.sLocalDataVariable = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to set data variable context");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void resetDataVariableContext(DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = ("Unable to reset data variable context");
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void updateDataVariableValue(DriverMembers obj) {

		String value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageData, obj);
		obj.xlObj.setDataVariable(Constants.Sheet_DataVariables, obj.sPageData, value, obj);
	}

	public static synchronized void clickLinkInDynamicRow(DriverMembers obj) {
		String xpath = null;
		try {
			String lookupText = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, obj.sPageObject, obj);
			xpath = ("//*[normalize-space(text())='" + lookupText + "']//parent::*//following-sibling::td["
					+ obj.sPageData + "]//*[text()]");
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (ElementNotInteractableException ei) {
			setScroll(xpath, obj);
			obj.driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = (UNCLICKABLE);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void getDataFromDynamicRow(DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
			String containerRow = (obj.sPageObject + "//tr[./td[normalize-space(text())='" + obj.sLocalDataVariable
					+ "']]//td[" + obj.sPageData + "]//a[text()]");
			obj.sLocalDataVariable = obj.driver.findElement(By.xpath(containerRow)).getText();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = (UNCLICKABLE);

			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void validateDataVariable(DriverMembers obj) {
		try {
			obj.sLocalDataVariable = null;
			String containerRow = (obj.sPageObject + "//tr[./td[normalize-space(text())='" + obj.sLocalDataVariable
					+ "']]//td[" + obj.sPageData + "]//a[text()]");
			obj.sLocalDataVariable = obj.driver.findElement(By.xpath(containerRow)).getText();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = (UNCLICKABLE);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void waitUntilClickable(DriverMembers obj) {
		try {
			WebDriverWait wait = new WebDriverWait(obj.driver, Constants.Global_Timeout);
			By item = By.xpath(obj.sPageObject);
			WebElement expected = wait.until(ExpectedConditions.presenceOfElementLocated(item));
			wait.until(ExpectedConditions.visibilityOf(expected));
			wait.until(ExpectedConditions.elementToBeClickable(expected));
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = ("Unable to wait for element to be clickable - trying next step"
					+ e.getMessage());
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public static synchronized void clearField(DriverMembers obj) {
		WebElement toClear = obj.driver.findElement(By.xpath(obj.sPageObject));
		toClear.sendKeys(Keys.CONTROL + "a");
		toClear.sendKeys(Keys.DELETE);
	}

	public synchronized void launchRunConfig(DriverMembers obj) {
		try {
			String launchURL = obj.xlObj.getRunConfig(obj.sPageData);
			if (launchURL != null) {
				obj.driver.get(launchURL);
			} else {
				obj.sTestStepFailureDetail = (NO_RUN_CONFIG + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println(NO_RUN_CONFIG + obj.sPageData);
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public synchronized void inputRunConfig(DriverMembers obj) {
		String inputText = null;
		try {
			inputText = obj.xlObj.getRunConfig(obj.sPageData);
			if (inputText != null) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(inputText);
			} else {
				obj.sTestStepFailureDetail = (NO_RUN_CONFIG + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println(NO_RUN_CONFIG + obj.sPageData);
			}
		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(inputText);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public static synchronized void inputRandomEmail(DriverMembers obj) {
		try {
			String userPrefix = obj.xlObj.getRunConfig("NewGCPEmailPrefix");
			if (userPrefix.equals("")) {
				userPrefix = "test";
			}

			String randomEmail;
			String domain = obj.xlObj.getRunConfig(obj.sPageData);
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
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(randomEmail);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public static synchronized void inputRandomUsername(DriverMembers obj) {
		try {
			String userPrefix = obj.xlObj.getRunConfig(obj.sPageData);
			if (obj.generatedRandomString == null) {
				obj.generatedRandomString = String.valueOf(obj.xlObj.randomNumber(4));
			}
			String randomUsername = (userPrefix + obj.generatedRandomString);
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(randomUsername);
			obj.xlObj.updateRunConfig("NewGCPUsername", randomUsername, obj);
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public static synchronized void validateElementPresent(DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
				System.out.println("Expected element displayed on UI");
			} else {
				obj.sTestStepFailureDetail = (NO_DISPLAY);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println(NO_DISPLAY);
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public static synchronized void validateElementNotPresent(DriverMembers obj) {
		try {
			if (!isElementPresent(By.xpath(obj.sPageObject), obj)) {
				obj.sTestStepFailureDetail = ("Expected element not displayed on UI...");
				obj.sTestStepStatus = Constants.Key_Pass_Result;
				obj.sTestCaseStatus = Constants.Key_Pass_Result;
				System.out.println(NO_DISPLAY);
			} else {
				obj.sTestStepFailureDetail = ("Element displayed on UI...");
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println("Element displayed on UI...");
			}
		} catch (NoSuchElementException ne) {
			obj.sTestStepFailureDetail = (NO_DISPLAY + ne.getMessage());
			obj.sTestStepStatus = Constants.Key_Pass_Result;
			obj.sTestCaseStatus = Constants.Key_Pass_Result;
			System.out.println(NO_DISPLAY + ne.getMessage());
		}

		catch (Exception e) {
			setFailResult(e, obj, "");
		}

	}

	public synchronized void inputRunConfigValue(DriverMembers obj) {
		try {
			String inputText = (obj.xlObj.getRunConfig(obj.sPageData));
			if (inputText != null) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(String.valueOf(inputText));
			} else {
				obj.sTestStepFailureDetail = (NO_RUN_CONFIG + obj.sPageData);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				System.out.println(NO_RUN_CONFIG + obj.sPageData);
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized void updateRunConfig(DriverMembers obj) {
		try {
			String runValue = obj.driver.findElement(By.xpath(obj.sPageObject)).getText();
			obj.xlObj.updateRunConfig(obj.sPageData, runValue, obj);
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public synchronized void inputDataFeeder(DriverMembers obj) {
		String inputDataFeed = null;
		try {
			obj.xlObj.setExcelFile(obj.sDataFeeder, "DataFeeder");
			inputDataFeed = obj.xlObj.getSpecificCellData(obj.sCurrentIteration, Integer.parseInt(obj.sPageData),
					"DataFeeder", obj.sDataFeeder);
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(inputDataFeed);
			obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
		} catch (ElementNotInteractableException ei) {
			try {
				setScroll(obj.sPageObject, obj);
				obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(inputDataFeed);
				obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
			} catch (Exception e) {
				setFailResult(e, obj, "");
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}

	}

	public static synchronized void setScroll(String object, DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(object));
		je.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static synchronized void highlightElement(DriverMembers obj) {
		try {
			JavascriptExecutor je = (JavascriptExecutor) obj.driver;
			WebElement element = obj.driver.findElement(By.xpath(obj.sPageObject));
			je.executeScript("arguments[0].setAttribute('style', 'border: 2px solid red;');", element);
			Thread.sleep(1000);
			je.executeScript("arguments[0].setAttribute('style', '');", element);
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void assertFontHexColor(DriverMembers obj) {
		try {
			String expectedColor = obj.xlObj.getRunConfig(obj.sPageData);

			highlightElement(obj);

			String color = obj.driver.findElement(By.xpath(obj.sPageObject)).getCssValue("color");
			String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

			int hexValue1 = Integer.parseInt(hexValue[0]);
			hexValue[1] = hexValue[1].trim();
			int hexValue2 = Integer.parseInt(hexValue[1]);
			hexValue[2] = hexValue[2].trim();
			int hexValue3 = Integer.parseInt(hexValue[2]);

			String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
			if (!actualColor.equalsIgnoreCase(expectedColor)) {
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualColor + " not matches Expected color - "
						+ expectedColor);
				obj.sTestStepStatus = Constants.Key_Fail_Result;
			} else {
				obj.sTestStepFailureDetail = (ACTUAL_DATA + actualColor + " matches Expected color - "
						+ expectedColor);
			}
		} catch (Exception e) {
			setFailResult(e, obj, "");
		}
	}

	public static synchronized void assertBackgroundHexColor(DriverMembers obj) {
		try {
			String expectedColor = obj.xlObj.getRunConfig(obj.sPageData);

			highlightElement(obj);
			Thread.sleep(1000);
			String color = obj.driver.findElement(By.xpath(obj.sPageObject)).getCssValue("background-color");
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
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void setFailResult(Exception e, DriverMembers obj, String customFailureMessage) {
		if (customFailureMessage.isEmpty()) {
			obj.sTestStepFailureDetail = e.getMessage();
		} else {
			obj.sTestStepFailureDetail = customFailureMessage + " - " + e.getMessage();
		}
		obj.sTestStepStatus = Constants.Key_Fail_Result;
		System.out.println(e.getMessage());
	}

	public static synchronized void writeMDMFile(DriverMembers obj) {
		try {
			readMDMConfig.writeFile();
		} catch (Exception e) {
			setFailResult(e, obj, e.getMessage());
		}
	}

	public static synchronized void validateToggleSetting(DriverMembers obj) {
		obj.dbObj.validateToggleSetting(obj.sPageData, obj);
	}

	public static synchronized void validateRMSLOVLoad(DriverMembers obj) {
		try {
			obj.dbObj.validateLOVLoad(obj);
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public static synchronized void verifyEmailTriggered(DriverMembers obj) {
		obj.emlObj.getMail(obj);
	}

	public static synchronized void mouseHover(DriverMembers obj) {

		try {
			Actions actions = new Actions(obj.driver);
			WebElement target = obj.driver.findElement(By.xpath(obj.sPageObject));

			actions.moveToElement(target).perform();
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public static synchronized void performDragAndDrop(DriverMembers obj) {

		WebElement from = obj.driver.findElement(By.xpath(obj.sPageData));
		WebElement to = obj.driver.findElement(By.xpath(obj.sPageObject));

		Actions actions = new Actions(obj.driver);

		try {
			actions.dragAndDrop(from, to).build().perform();

		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
			Thread.currentThread().interrupt();
		}

	}

	public static synchronized void performDragAndDropJS(DriverMembers obj) {

		WebElement from = obj.driver.findElement(By.xpath(obj.sPageData));
		WebElement to = obj.driver.findElement(By.xpath(obj.sPageObject));

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
			((JavascriptExecutor) obj.driver).executeScript(java_script, from, to);
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void acceptAlert(DriverMembers obj) {
		try {
			Alert alert = obj.driver.switchTo().alert(); // switch to alert
			alert.accept();
		} catch (Exception e) {
			e.printStackTrace();
			setFailResult(e, obj, "");
		}
	}

	public static void clickButtonSalesforce(DriverMembers obj) {
		try {
			Thread.sleep(1000);

			WebElement ele = obj.driver.findElement(By.xpath(obj.sPageObject));
			JavascriptExecutor executor = (JavascriptExecutor) obj.driver;
			executor.executeScript("arguments[0].click();", ele);

		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void switchToFrame(DriverMembers obj) {
		try {
			WebDriverWait wait = new WebDriverWait(obj.driver, 10);
			WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(obj.sPageObject)));

			obj.driver.switchTo().frame(iframe);
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public static synchronized void validateDropDownOptions(DriverMembers obj) {
		String[] dataOptions = obj.sPageData.split(",");
		WebElement dropDown = obj.driver.findElement(By.xpath(obj.sPageObject));
		Select check = new Select(dropDown);
		List<WebElement> allOptions = check.getOptions();
		List<String> options = new ArrayList<>();

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
						System.out.println(dataOptions[j] + " " + "- option matches..");
					} else {
						obj.sTestStepStatus = Constants.Key_Fail_Result;
						obj.sTestCaseStatus = Constants.Key_Fail_Result;
						obj.sTestStepFailureDetail = (dataOptions[j] + " " + NO_MATCH);
						Assert.fail(dataOptions[j] + " " + NO_MATCH);
						System.out.println(dataOptions[j] + " " + NO_MATCH);
					}
				}
			} else {
				Assert.fail("Expected & Actual # of options do not match ...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void validateElementSelected(DriverMembers obj) {

		try {
			if (isElementSelected(By.xpath(obj.sPageObject), obj)) {
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

	public static synchronized void navigateBack(DriverMembers obj) {
		obj.driver.navigate().back();
	}

	// Get The Current Day
	public static synchronized String getCurrentDay() {
		// Create a Calendar Object
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		// Get Current Day as a number
		int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
		return Integer.toString(todayInt);
	}

	// Selects date in date picker
	public static synchronized void selectCurrentDate(DriverMembers obj) {

		// Get Today's number
		String today = getCurrentDay();
		// date picker table
		WebElement dateWidgetFrom = obj.driver.findElement(By.xpath(obj.sPageObject));
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
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void selectAnyDate(DriverMembers obj) {

		// date picker table
		WebElement dateWidgetFrom = obj.driver.findElement(By.xpath(obj.sPageObject));
		// columns from date picker table
		List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));

		try {
			for (WebElement cell : columns) {
				if (cell.getText().equals(obj.sPageData)) {
					System.out.println(cell.getText());
					cell.click();
					break;
				}
			}
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized String getCurrentMonth() {
		YearMonth thisMonth = YearMonth.now();
		DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

		return thisMonth.format(monthYearFormatter);
	}

	public static synchronized void validateOptionsOrder(DriverMembers obj) {

		List<String> tabOptions = new ArrayList<>(Arrays.asList(obj.sPageData.split(",")));

		List<WebElement> originalList = new ArrayList<>(obj.driver.findElements(By.xpath(obj.sPageObject)));

		List<String> options = new ArrayList<>();
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

	public synchronized void validateElementEnabled(DriverMembers obj) {

		try {
			if (isElementEnabled(By.xpath(obj.sPageObject), obj)) {
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

	public synchronized void handleLastCallAccountOption(DriverMembers obj) {

		try {
			if (isElementPresent(By.xpath(obj.sPageData), obj)) {
				if (new WebDriverWait(obj.driver, 20)
						.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageData)))
						.getAttribute("innerHTML").contains("Current call is the most recent one for")) {
					System.out.println("Message displayed on UI - "
							+ "Current call is the latest call for this account and no last call exists");

				} else {
					Assert.fail("Message not displayed on UI");
				}

			} else {
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<>(obj.driver.getWindowHandles());
				Thread.sleep(5000);
				newTab.remove(oldTab);
				obj.driver.switchTo().window(newTab.get(0));

				obj.driver.switchTo().frame(0);

				if (obj.driver.findElement(By.xpath(obj.sPageObject)).isDisplayed()) {
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
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void handleNewOrderOption(DriverMembers obj) {

		try {
			if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).click();
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<>(obj.driver.getWindowHandles());
				Thread.sleep(5000);
				newTab.remove(oldTab);
				obj.driver.switchTo().window(newTab.get(0));
			} else {
				System.out.println("User is navigated to a new tab");
				Thread.sleep(30000);
				String oldTab = obj.driver.getWindowHandle();
				ArrayList<String> newTab = new ArrayList<>(obj.driver.getWindowHandles());
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

	public static synchronized void switchToPreviousTab(DriverMembers obj) {
		try {

			ArrayList<String> tabs = new ArrayList<>(obj.driver.getWindowHandles());
			System.out.println(tabs.size());
			// Use the list of window handles to switch between windows
			obj.driver.switchTo().window(tabs.get(0));

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public synchronized void handleCallSaveAndSubmit(DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(obj.sPageData), obj)) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).click();
				Thread.sleep(40000);

				// Verify call is saved and user is navigated to Account page
				if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
					System.out.println("Call saved successfully & user navigated to account page");
				} else {
					Assert.fail("Call save not successful");
				}

			} else {
				Thread.sleep(40000);

				// Verify call is saved and user is navigated to Account page
				if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
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
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void enterQuantity(DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
			Thread.sleep(10000);

			obj.driver
					.findElement(By.xpath(
							"//div[contains(@class,'number-item backspace')]//button[contains(@class,'btn btn-link')]"))
					.click();
			Thread.sleep(5000);
			obj.driver.findElement(By.xpath("//button[contains(text(),'" + obj.sPageData + "')]")).click();
			Thread.sleep(5000);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void handleSampleLimitFeature(DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath("//button[contains(text(),'Confirm')]"), obj)) {
				obj.driver.findElement(By.xpath("//button[contains(text(),'Confirm')]")).click();
				Thread.sleep(20000);

				if (isElementPresent(By.xpath(obj.sPageData), obj)) {
					if (new WebDriverWait(obj.driver, 20)
							.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageData)))
							.getAttribute("innerHTML").contains("Quantity exceeds sample limits")) {
						System.out.println(
								"Message displayed on UI - " + "Sample Limit has reached and Call can not be saved");

					} else {
						System.out.println("Message not displayed on UI");
					}

				} else {
					Thread.sleep(40000);
					// Verify call is saved and user is navigated to Account page
					if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
						System.out.println("Sample Limit is allowed and call saved successfully");
					} else {
						Assert.fail("Call save not successful");
					}
				}

			} else {
				if (isElementPresent(By.xpath(obj.sPageData), obj)) {
					if (new WebDriverWait(obj.driver, 20)
							.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageData)))
							.getAttribute("innerHTML").contains("Quantity exceeds sample limits")) {
						System.out.println(
								"Message displayed on UI - " + "Sample Limit has reached and Call can not be saved");

					} else {
						System.out.println("Message not displayed on UI");
					}

				} else {
					Thread.sleep(40000);
					// Verify call is saved and user is navigated to Account page
					if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
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
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void handleAddDocumentID(DriverMembers obj) {
		try {
			obj.driver.findElement(By.xpath(obj.sPageData)).click();
			Thread.sleep(10000);

			if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).click();
				Thread.sleep(40000);
			}
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void validateErrorMessage(DriverMembers obj) {
		try {
			if (isElementPresent(By.xpath(obj.sPageObject), obj)) {
				if (new WebDriverWait(obj.driver, 20)
						.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(obj.sPageObject)))
						.getAttribute("innerHTML").contains(obj.sPageData)) {
					System.out.println("Error Message Displayed - " + obj.sPageData);

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

	public synchronized void assertColorLegendPlanner(DriverMembers obj) {
		try {
			Thread.sleep(10000);

			String scheduledTime = "//span[contains(text(),'" + obj.sPageData + "')]//following::div[1]";
			String xpath = obj.sPageObject + scheduledTime;
			obj.driver.findElement(By.xpath(xpath)).click();
			;
		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void inputTextRandomString(DriverMembers obj) {
		try {
			String inputText = obj.sPageData;
			if (obj.generatedRandomString == null) {
				obj.generatedRandomString = String.valueOf(obj.xlObj.randomNumber(1));
			}
			String randomString = (inputText + " " + obj.generatedRandomString);
			obj.driver.findElement(By.xpath(obj.sPageObject)).sendKeys(randomString);

		} catch (Exception e) {
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
	}

	public static synchronized void selectAnyDatePlanner(DriverMembers obj) {

		try {
			Thread.sleep(1000);

			String listitem = "/a[text()='" + obj.sPageData + "']";
			String xpath = obj.sPageObject + listitem;
			obj.driver.findElement(By.xpath(xpath)).getLocation();
			obj.driver.findElement(By.xpath(xpath)).click();

		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(
					Thread.currentThread().getName() + " - Undable to select from date picker - " + obj.sPageObject);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}
	}

	public static synchronized void selectPMCall(DriverMembers obj) {

		try {
			if (obj.driver.findElement(By.xpath(obj.sPageObject)).getText().equalsIgnoreCase("AM")) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(
					Thread.currentThread().getName() + " - Undable to select from drop down - " + obj.sPageObject);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public static synchronized void selectAMCall(DriverMembers obj) {

		try {
			if (obj.driver.findElement(By.xpath(obj.sPageObject)).getText().equalsIgnoreCase("PM")) {
				obj.driver.findElement(By.xpath(obj.sPageObject)).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.println(
					Thread.currentThread().getName() + " - Undable to select from drop down - " + obj.sPageObject);
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}
	}

	public static synchronized void setScrolltoPage(DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(obj.sPageObject));
		je.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static void clickButtonAction(DriverMembers obj) {
		try {
			Thread.sleep(1000);
			Actions actions = new Actions(obj.driver);
			WebElement ele = obj.driver.findElement(By.xpath(obj.sPageObject));
			actions.clickAndHold(ele).perform();
			Thread.sleep(1000);
			actions.release().perform();

		} catch (ElementNotInteractableException ei) {
			setScroll(obj.sPageObject, obj);
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void openBrowserWithoutSecurity(DriverMembers obj) {

		try {

			String service = System.getProperty("user.dir") + "\\chrome2\\chromedriver.exe";
			System.setProperty("webobj.driver.chrome.driver", service);
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("useAutomationExtension", false);
			options.addArguments("start-maximized");
			options.addArguments("no-sandbox");
			options.addArguments("disable-extensions");
			options.addArguments("disable-popup-blocking");
			options.addArguments("--disable-web-security");
			obj.driver = new ChromeDriver(options);

			obj.driver.manage().deleteAllCookies();
			obj.driver.manage().window().maximize();
			obj.driver.manage().timeouts().pageLoadTimeout(Constants.Global_Timeout, TimeUnit.SECONDS);

		}

		catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepFailureDetail = e.getMessage();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public static synchronized void scrollwithinElement(DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		WebElement element = obj.driver.findElement(By.xpath(obj.sPageObject));
		je.executeScript("arguments[0].scrollTo(0, arguments[0].scrollHeight)", element);

	}

	public static synchronized void clickAndSwitchTab(DriverMembers obj) {
		try {
			String oldTab = obj.driver.getWindowHandle();
			System.out.println("Current window handle saved successfully.");
			obj.driver.findElement(By.xpath(obj.sPageObject)).getLocation();
			obj.driver.findElement(By.xpath(obj.sPageObject)).click();
			System.out.println("Clicked on element successfully");
			ArrayList<String> newTab = null;

			Thread.sleep(5000);
			newTab = new ArrayList<>(obj.driver.getWindowHandles());

			newTab.remove(oldTab);
			// change focus to new tab
			System.out.println("Trying to switch");
			obj.driver.switchTo().window(newTab.get(0));
			System.out.println("Moved to new window handle successfully");

		} catch (InterruptedException e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			e.printStackTrace();
			obj.sTestStepStatus = Constants.Key_Fail_Result;
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
			System.out.print(Thread.currentThread().getName() + Thread.currentThread().isAlive());
		}

	}

	public static synchronized void scrollTillPageEnd(DriverMembers obj) {
		JavascriptExecutor je = (JavascriptExecutor) obj.driver;
		je.executeScript(
				"window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
	}

	public static synchronized void placeMDMTestData(DriverMembers obj) {
		try {
			obj.mdm.prepareAndTransferMDMFile(obj);
		} catch (Exception e) {
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
		}
	}

	public static synchronized void checkMDMExecution(DriverMembers obj) {
		try {
			obj.dbObj.checkMDMStatus(obj);
		} catch (Exception e) {
			obj.sTestCaseStatus = Constants.Key_Fail_Result;
			obj.sTestStepFailureDetail = e.getMessage();
		}
	}
}
