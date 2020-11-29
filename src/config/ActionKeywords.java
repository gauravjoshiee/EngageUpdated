package config;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import executionEngine.DriverMembers;
import executionEngine.DriverScript;
import utility.ExcelUtils;
import utility.Log;
import utility.readMDMConfig;

//import org.openqa.selenium.ie.InternetExplorerDriver;
@SuppressWarnings("static-access")
public class ActionKeywords {
	
	
	public static WebElement form;
		
		/**
		 * This function used to click on a button/ input/ link
		 * Provide object in test case sheet
		 * @param object
		 * @param data
		 */
		public static  void click_button(String object, String data, DriverMembers obj){
			try{
			//this.obj.driver.findElement(By.xpath(OR.getProperty(object))).click();
				Thread.sleep(1000);
				obj.driver.findElement(By.xpath(object)).getLocation();
				obj.driver.findElement(By.xpath(object)).click();
			
			}
			catch (ElementNotInteractableException ei){
				setScroll(object,obj);
				obj.driver.findElement(By.xpath(object)).getLocation();
				obj.driver.findElement(By.xpath(object)).click();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public void waitUntilDisplayed(String object, String data, DriverMembers obj){
			
			boolean initiate=false;
			
			for (int i=0;i<=5;i++){
				initiate = isElementPresent(By.xpath(object),obj);
				if(initiate){
					try{
						Thread.sleep(1000);
						int counter=0;
						while(counter<=Constants.Global_Timeout){
							if(obj.driver.findElement(By.xpath(object)).isDisplayed()){
								Thread.sleep(1000);
								counter++;
							}
						}
					}
					catch (Exception e){
						
							obj.sTestStepFailureDetail="Element to wait appeared and removed in";
							Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
							System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
					}
					break;
				}
				else{
					try{
						Thread.sleep(1000);
					}
					catch (Exception e){
						System.out.println(e.getLocalizedMessage());
						System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
					}
				}
			}
			if (!initiate){
				obj.sTestStepFailureDetail="Element to wait did not appear";
				Log.info("Element did not appear at - "+object);
			}
						
			}
			
		/**
		 * This function closes browser session
		 * @param object
		 * @param data
		 */
		public synchronized void closeBrowser(String object, String data, DriverMembers obj){
			try{
				obj.driver.close();
			obj.driver.quit();
			
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void captureContent(String object, String data, DriverMembers obj){
			try{
			String capturedText = obj.driver.findElement(By.xpath(object)).getText();
			ExcelUtils.insertDataVariable(obj.sTestCase, obj.sTestStepName, data, capturedText, Constants.Sheet_DataVariables, obj);
			
			}
			catch (Exception e){
				
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		/**
		 * This function is to assert DataVariable value from excel sheet to value of target xpath
		 * */
				
		public synchronized void assertDataVariable(String object, String data, DriverMembers obj){
				String expectedData="";
				String actualData = obj.driver.findElement(By.xpath(object)).getText();
				try{
					expectedData= obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
					if (actualData.equals(expectedData)){
						System.out.println("Actual value - "+actualData+" matches with expected value - "+expectedData+"");
					}
					else{
						obj.sTestStepFailureDetail=("Actual value - "+actualData+" NOT matches with expected value - "+expectedData+"");
						obj.sTestStepStatus=Constants.Key_Fail_Result;
						obj.sTestCaseStatus=Constants.Key_Fail_Result;
					}
				}
				
				catch(Exception e){
					obj.sTestStepFailureDetail=e.getMessage();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				}
				
			
			
		}
		
		public synchronized static void inputDataVariable(String object, String data, DriverMembers obj){
			
			String value="";
			try{
				
				value=obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
				
				if (value!=null){
					obj.driver.findElement(By.xpath(object)).sendKeys(value);
				}
				else{
					obj.sTestStepFailureDetail=("Unable to fetch data variable - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					
				}
			}
			
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
			
		
		
	}
		
		/**
		 * This function selects from multi select drop down on Charitable request form.
		 * Provide object and data in test case sheet
		 * @param object
		 * @param data
		 */
		public synchronized static void drpdwnSelect(String object, String data, DriverMembers obj){
			try{
			//WebElement element = obj.driver.findElement(By.id(OR.getProperty(object)));
			//Select drpSelect = new Select(element);
			//drpSelect.selectByVisibleText(data);
				Thread.sleep(1000);
			
			String listitem="//*[normalize-space(text())='"+data+"']";
			String xpath=object+listitem;
			obj.driver.findElement(By.xpath(xpath)).getLocation();
			obj.driver.findElementByXPath(xpath).click();
			
			
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(Thread.currentThread().getName()+" - Undable to select from drop down - "+object);
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		/**
		 * This function used to print stack trace if waiting for specific element fails
		 * @param x
		 */
		private void fail(String x){
			System.out.println(x);
		}
		
		/**
		 * This function takes screenshot and saves at defined location
		 * Currently only appends time stamp. Can be modified to append test step ID/ Description
		 * @param object
		 * @param data
		 * @throws Exception
		 */
		public synchronized void getscreenshot(String object, String data, DriverMembers obj) throws Exception
		{
			try{
			File scrnsht = obj.driver.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrnsht, new File ("D:\\Automation POC/BVT Automation/RMSDefault_May2017/src/screenshots"+System.currentTimeMillis()+".png"));
			
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		/**
		 * This function used to send text value to input field. 
		 * Provide object and data in test case sheet
		 * @param object
		 * @param data
		 */
		public void input_text(String object, String data, DriverMembers obj){
			try{
				obj.driver.findElement(By.xpath(object)).getLocation();
				obj.driver.findElement(By.xpath(object)).clear();
				obj.driver.findElement(By.xpath(object)).sendKeys(data);
			}
			catch (ElementNotInteractableException ei){
				setScroll(object,obj);
				obj.driver.findElement(By.xpath(object)).getLocation();
				obj.driver.findElement(By.xpath(object)).clear();
				obj.driver.findElement(By.xpath(object)).sendKeys(data);
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		/**
		 * This function used for waiting for specific element to be visible
		 * @param by
		 * @return
		 */
		private boolean isElementPresent(By by, DriverMembers obj) {
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
		
		/**
		 * This function hits given URL on browser. Provide URL in Constants class. Will be driven through configuration sheet later
		 * @param object
		 * @param data
		 */
		public synchronized void launchApp(String object, String data, DriverMembers obj){
			try{
				
				obj.driver.get(data);
			
			}
			catch (Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Launching application failed - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		/**
		 * This function sets system property for browser driver exe and instantiates browser session. 
		 * Currently hard coded, will be driven by configuration sheet later
		 * @param object
		 * @param data
		 */
		public synchronized void openBrowser(String object, String data, DriverMembers obj){
			
			try{
			
			
			/*
			ChromeOptions chromeOptions = new ChromeOptions();
		    chromeOptions.addArguments("--headless");
		    */
//			DesiredCapabilities cap = DesiredCapabilities.chrome();
//			cap.setCapability("disable-restore-session-state", true);
//			obj.remotedriver = new RemoteWebDriver (new URL("http://192.168.225.113:5555/wd/hub"),cap);	
				
			String Service = System.getProperty("user.dir")+"\\ChromeDriver\\chromeobj.driver.exe";
			System.setProperty("webobj.driver.chrome.driver", Service);

			obj.driver = new ChromeDriver();
			obj.driver.manage().deleteAllCookies();
			obj.driver.manage().window().maximize();
			obj.driver.manage().timeouts().pageLoadTimeout(Constants.Global_Timeout, TimeUnit. SECONDS);
			
			}
			
			catch(Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
			
		}
		
		/**
		 * 
		 * This function uploads a file. Provide upload element object in test case sheet.
		 * @param object
		 * @param data
		 */
		public synchronized static void uploadByRobot(String object, String data, DriverMembers obj){
			try {
				Thread.holdsLock(DriverScript.threadList);
			obj.driver.findElement(By.xpath(object)).click();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}		
		    //put path to your image in a clipboard
//		    StringSelection ss = new StringSelection(Constants.txtUploadPath);
		    StringSelection ss = new StringSelection(data);
		    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

		    //imitate mouse events like ENTER, CTRL+C, CTRL+V
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
				// TODO Auto-generated catch block
				e.printStackTrace();
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
			catch (Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
			}
		    
		}
		
		public synchronized static void uploadRunConfig(String object, String data, DriverMembers obj){
			try{
				String inputPath=obj.xlObj.getRunConfig(data);
				uploadByRobot(object, inputPath, obj);
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		/**
		 * This function waits for specific seconds. Can be used where page reloads for dependent fields.
		 * Provide time in second to wait in test case sheet
		 * @param object
		 * @param data
		 */
		public synchronized void waitForSeconds(String object, String data, DriverMembers obj){
			
			for (int second=0;second<Integer.parseInt(data);second++){
				
				try {
					
					Thread.sleep(1000);					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					Log.info("Wait got intercepted - "+e.getMessage());
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
					
				}
			}
			
		}
		
		/**
		 * This function waits for specific element to be visible on page. Timeouts afters 60 seconds.
		 * Provide object in test case sheet for which to wait
		 * @param object
		 * @param data
		 */
		public synchronized void waitForVisible(String object, String data, DriverMembers obj){
			for (int second = 0;; second++) {
		    	if (second >= Constants.Global_Timeout) {
		    		obj.sTestStepFailureDetail=("Unable to load in "+Constants.Global_Timeout+" seconds");
		    		obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					Log.info("Element not visible within global timeout limit");
		    		break;
		    		
		    		
		    	}
		    	try { if (isElementPresent(By.xpath(object),obj)){
		    		Log.info("Element is now visible at - "+object);
		    		break;
		    	}
		    	else{
					Thread.sleep(1000);
		    	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					obj.sTestStepFailureDetail=e.getMessage();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;	
					Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
		    }
		}
		
		public synchronized void waitForEnabled(String object, String data, DriverMembers obj){
			for (int second = 0;; second++) {
		    	if (second >= Constants.Global_Timeout) {
		    		fail("timeout");
		    		obj.sTestStepStatus=Constants.Key_Fail_Result;
		    		Log.info("Element not enabled within global timeout limit.");
		    		break;
		    		
		    		
		    	}
		    	try { if (isElementEnabled(By.xpath(object),obj)){
		    		Log.info("Element is now enabled at - "+object);
		    		break;
		    	}
		    	else{
					Thread.sleep(1000);
		    	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					obj.sTestStepFailureDetail=e.getMessage();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					Log.info("Event unsuccessful at - "+object+" \\n Error description - "+e.getMessage());
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
		    }
		}
		
		public synchronized static void clickAndSwitchTab(String object, String data, DriverMembers obj){
			try{
//				Thread.holdsLock(Thread.currentThread());
				String oldTab=obj.driver.getWindowHandle();
				System.out.println("Current window handle saved successfully.");
				obj.driver.findElement(By.xpath(object)).click();
				System.out.println("Clicked on element successfully");
				ArrayList<String> newTab=null;
				
						Thread.sleep(5000);
						newTab = new ArrayList<String>(obj.driver.getWindowHandles());
				
					newTab.remove(oldTab);
					// change focus to new tab
					System.out.println("Trying to switch");
					obj.driver.switchTo().window(newTab.get(0));
					System.out.println("Moved to new window handle successfully");
			
				
				}
				catch (Exception e){
					e.printStackTrace();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=e.getMessage();
					Log.info("Unable to complete window switch");
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
			
		}
		
		public synchronized void setForm(String object, String data, DriverMembers obj){
			try{
				form = obj.driver.findElement(By.xpath(object));
//				form.sendKeys(Keys.ARROW_DOWN);
				}
				catch (Exception e){
					e.printStackTrace();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=e.getMessage();
					Log.info("Unable to complete window switch");
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
			
		}
		
		public synchronized static void clickOnFrame(String object, String data, DriverMembers obj){
			try{
				obj.driver.switchTo().frame(data);				
				obj.driver.findElement(By.xpath(object)).click();
				//form.findElement(By.xpath(object)).click();
				}
				catch (Exception e){
					e.printStackTrace();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=e.getMessage();
					Log.info("Unable to complete window switch");
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
			
		}
		
		public synchronized void resetForm(String object, String data, DriverMembers obj){
			try{
				form = null;
				}
				catch (Exception e){
					e.printStackTrace();
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=e.getMessage();
					Log.info("Unable to complete window switch");
					System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
				}
			
		}
		
		public synchronized static void clickLinkHavingText(String object, String data, DriverMembers obj){
			String xpath = null;
			try{
				xpath = (object+"//a[contains(normalize-space(text()),'"+data+"'");
				obj.driver.findElement(By.xpath(xpath)).click();
			}
			catch (ElementNotInteractableException ei){
				setScroll(xpath,obj);
				obj.driver.findElement(By.xpath(xpath)).click();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to click on link having text - "+data);
				Log.info("Unable to click on link having text - "+data);
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized static void clickDataVariableText(String object, String data, DriverMembers obj){
			String value="";
			String xpath = null;
			try{
				value= obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
				if (value==""){
					System.out.println("No value found for data variable - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=("No value found for data variable - "+data);
				}
				else{
					
					if(object!=null){
						xpath = (object+"//a[contains(normalize-space(text()),'"+value+"')]");
					}
					else{
						xpath = ("//a[contains(normalize-space(text()),'"+value+"')]");
					}
					obj.driver.findElement(By.xpath(xpath)).click();
				}
			}
			catch (ElementNotInteractableException ei){
				setScroll(xpath,obj);
				obj.driver.findElement(By.xpath(xpath)).click();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to click on link having data variable text - "+value);
				obj.sLocalDataVariable=null;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void inputDataVariableText(String object, String data, DriverMembers obj){
			String value="";
			try{
				value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
				if (value==null){
					System.out.println("No value found for data variable - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=("No value found for data variable - "+data);
				}
				else{
					obj.driver.findElement(By.xpath(object)).sendKeys(value);
				}
			}
			catch (ElementNotInteractableException ei){
				setScroll(object,obj);
				obj.driver.findElement(By.xpath(object)).sendKeys(value);
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to input data variable text - "+value);
				obj.sLocalDataVariable=null;
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized static void input_randomNumber(String object, String data, DriverMembers obj){
			String inputStream;
			try{
				inputStream = String.valueOf(obj.xlObj.randomNumber(Integer.parseInt(data)));
				obj.driver.findElement(By.xpath(object)).getLocation();
				obj.driver.findElement(By.xpath(object)).sendKeys(inputStream);
			}
			catch (ElementNotInteractableException ei){
				setScroll(object,obj);
				obj.driver.findElement(By.xpath(object)).getLocation();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to input data");
			}
		}
		
		public synchronized void assertText(String object, String data, DriverMembers obj){
			try{
				String actualText=null;
				highlightElement(obj);
				actualText=obj.driver.findElement(By.xpath(object)).getText();
				if(actualText.equals(data)){
					obj.sTestStepFailureDetail=("Actual data - "+actualText+" matches with Expected data - "+data);
				}
				else{
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sScreenshotPath=obj.extObj.addScreencast(obj);
					obj.sTestStepFailureDetail=("Actual data - "+actualText+" does not match with Expected data - "+data+"<br>"+obj.sScreenshotPath);
					
				}
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to assert text");
				Log.info("Unable to assert text");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void assertPartialText(String object, String data, DriverMembers obj){
			try{
			String actualText=null;				
			actualText=obj.driver.findElement(By.xpath(object)).getText();
			if(actualText.contains(data)){
				obj.sTestStepFailureDetail=("Actual data - "+actualText+" contains Expected data - "+data);
				obj.sLocalDataVariable=null;
			}
			else{
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sScreenshotPath=obj.extObj.addScreencast(obj);
				obj.sTestStepFailureDetail=("Actual data - "+actualText+" does not contain Expected data - "+data+"\n"+obj.sScreenshotPath);
				Log.info("Unable to assert text");
				obj.sLocalDataVariable=null;
			}
			}
			catch (Exception e)
			{
				setFailResult(e,obj,"Unablet to assert due to exception");
			}
			
		}
		
		public synchronized void assertPartialDataVariable(String object, String data, DriverMembers obj){
			String value="";
			try{
				value=obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
				
				if (value==null){
					System.out.println("No value found for data variable - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					obj.sTestStepFailureDetail=("No value found for data variable - "+data);
				}
				else{
					String actualText=null;				
					actualText=obj.driver.findElement(By.xpath(object)).getText();
					if(actualText.contains(value)){
						obj.sTestStepFailureDetail=("Actual data - "+actualText+" contains Expected data - "+data);
					}
					else{
						obj.sTestStepStatus=Constants.Key_Fail_Result;
						obj.sTestCaseStatus=Constants.Key_Fail_Result;
						obj.sScreenshotPath=obj.extObj.addScreencast(obj);
						obj.sTestStepFailureDetail=("Actual data - "+actualText+" does not contain with Expected data - "+value+"\n"+obj.sScreenshotPath);
						Log.info("Unable to assert text");
					}
				}
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to assert text");
				Log.info("Unable to assert text");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void getTextFromAlert(String object, String data, DriverMembers obj){
			try{
				
				Alert alert = obj.driver.switchTo().alert();
				String alertMessage = alert.getText();
				System.out.println(alertMessage);
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to fetch alert box text");
				Log.info("Unable to fetch alert box text");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void acceptFromAlert(String object, String data, DriverMembers obj){
			try{
				
				Alert alert = obj.driver.switchTo().alert();
				alert.accept();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to accept from alert box");
				Log.info("Unable to accept from alert box");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void setDataVariableContext(String object, String data, DriverMembers obj){
			
			try{
				obj.sLocalDataVariable=obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to set data variable context");
				Log.info("Unable to set data variable context");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void resetDataVariableContext (String object, String data, DriverMembers obj){
			try{
				obj.sLocalDataVariable=null;
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepFailureDetail=("Unable to reset data variable context");
				Log.info("Unable to reset data variable context");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void updateDataVariableValue(String object, String data, DriverMembers obj){
			
			String value = obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, data,obj);
			obj.xlObj.setDataVariable(Constants.Sheet_DataVariables, object,value,obj);
		}
		
		public synchronized static void clickLinkInDynamicRow(String object, String data, DriverMembers obj){
			String xpath = null;
			try{
				String lookupText=obj.xlObj.getDataVariable(Constants.Sheet_DataVariables, object, obj);
				xpath=("//*[normalize-space(text())='"+lookupText+"']//parent::*//following-sibling::td["+data+"]//*[text()]");
				obj.driver.findElement(By.xpath(xpath)).click();
			}
			catch (ElementNotInteractableException ei){
				setScroll(xpath,obj);
				obj.driver.findElement(By.xpath(xpath)).click();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to click on expected link");
				Log.info("Unable to accept from alert box");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void getDataFromDynamicRow(String object, String data,DriverMembers obj){
			try{
				obj.sLocalDataVariable=null;
				String containerRow = (object+"//tr[./td[normalize-space(text())='"+obj.sLocalDataVariable+"']]//td["+data+"]//a[text()]");
				obj.sLocalDataVariable=obj.driver.findElement(By.xpath(containerRow)).getText();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to click on expected link");
				
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void validateDataVariable(String object, String data, DriverMembers obj){
			try{
				obj.sLocalDataVariable=null;
				String containerRow = (object+"//tr[./td[normalize-space(text())='"+obj.sLocalDataVariable+"']]//td["+data+"]//a[text()]");
				obj.sLocalDataVariable=obj.driver.findElement(By.xpath(containerRow)).getText();
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to click on expected link");
				Log.info("Unable to accept from alert box");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
		}
		
		public synchronized void waitUntilClickable(String object, String data, DriverMembers obj){
			try{
				WebDriverWait wait = new WebDriverWait(obj.driver, Constants.Global_Timeout);
				By item = By.xpath(object);
				WebElement expected = wait.until(ExpectedConditions.presenceOfElementLocated(item));;
				wait.until(ExpectedConditions.visibilityOf(expected)); 
				wait.until(ExpectedConditions.elementToBeClickable(expected));
				
				wait=null;
				expected=null;
				item=null;
			}
			catch (Exception e){
				e.printStackTrace();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail=("Unable to wait for element to be clickable - trying next step"+e.getMessage());
				Log.info("Unable to wait for element to be clickable");
				System.out.print(Thread.currentThread().getName()+Thread.currentThread().isAlive());
			}
			
		}
		
		public synchronized static void clearField(String object, String data, DriverMembers obj){
			WebElement toClear = obj.driver.findElement(By.xpath(object));
			toClear.sendKeys(Keys.CONTROL + "a");
			toClear.sendKeys(Keys.DELETE);
		}

		public synchronized void launchRunConfig(String object, String data, DriverMembers obj){
			try{
				String launchURL=obj.xlObj.getRunConfig(data);
				if(launchURL!=null){
					obj.driver.get(launchURL);
				}
				else{
					obj.sTestStepFailureDetail=("No value for run config - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					System.out.println("No value for run config - "+data);
				}
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		
		public synchronized void inputRunConfig(String object, String data, DriverMembers obj){
			String inputText = null;
			try{
				inputText=obj.xlObj.getRunConfig(data);
				if(inputText!=null){
					obj.driver.findElement(By.xpath(object)).sendKeys(inputText);
				}
				else{
					obj.sTestStepFailureDetail=("No value for run config - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					System.out.println("No value for run config - "+data);
				}
			}
			catch (ElementNotInteractableException ei){
				setScroll(object,obj);
				obj.driver.findElement(By.xpath(object)).sendKeys(inputText);
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		
		public synchronized static void inputRandomEmail(String object, String data, DriverMembers obj){
			try{
				String userPrefix = obj.xlObj.getRunConfig("NewGCPEmailPrefix");
				if(userPrefix.equals("")){
					userPrefix="test";
				}
				
				String randomEmail;
				String domain = obj.xlObj.getRunConfig(data);
				String regex = "^@[a-zA-Z0-9]*+[.]+[a-zA-Z]*$";
			 
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(domain);
				
				if(obj.generatedRandomString==null){
					obj.generatedRandomString=String.valueOf(obj.xlObj.randomNumber(4));
				}
				
				if (matcher.find()){
					randomEmail=(userPrefix+obj.generatedRandomString+domain);
				}
				else{
					randomEmail=(userPrefix+obj.generatedRandomString+"@yopmail.com");
				}
				System.out.println(randomEmail); 
				obj.xlObj.updateRunConfig("NewGCPEmail", randomEmail, obj);
				obj.driver.findElement(By.xpath(object)).sendKeys(randomEmail);
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		
		public synchronized static void inputRandomUsername(String object, String data, DriverMembers obj){
			try{
				String userPrefix = obj.xlObj.getRunConfig(data);
				if(obj.generatedRandomString==null){
					obj.generatedRandomString=String.valueOf(obj.xlObj.randomNumber(4));
				}
				String randomUsername =(userPrefix+obj.generatedRandomString);
				obj.driver.findElement(By.xpath(object)).sendKeys(randomUsername);;
				obj.xlObj.updateRunConfig("NewGCPUsername", randomUsername, obj);
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		
		public synchronized static void validateElementPresent(String object, String data, DriverMembers obj){
			try{
				boolean isPresent=obj.driver.findElement(By.xpath(object)).isDisplayed();
				if(isPresent){
					System.out.println("Expected element displayed on UI");
				}
				else{
					obj.sTestStepFailureDetail=("Expected element not displayed on UI");
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					System.out.println("Expected element not displayed on UI");
				}
			}
			catch(Exception e){
				obj.sTestStepFailureDetail=e.getMessage();
				obj.sTestStepStatus=Constants.Key_Fail_Result;
				obj.sTestCaseStatus=Constants.Key_Fail_Result;
				System.out.println(e.getMessage());
			}
		}
		
		public synchronized static void validateElementNotPresent(String object, String data, DriverMembers obj){
			try{
				boolean isPresent=obj.driver.findElement(By.xpath(object)).isDisplayed();
				if(isPresent){
					obj.sTestStepFailureDetail=("Element displayed on UI");
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					System.out.println("Expected element displayed on UI");
				}
				else{
					System.out.println("Element not displayed");
					
				}
			}
			catch(Exception e){
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized void inputRunConfigValue(String object, String data, DriverMembers obj){
			try{
				String inputText=(obj.xlObj.getRunConfig(data));
				if(inputText!=null){
					obj.driver.findElement(By.xpath(object)).sendKeys(String.valueOf(inputText));;
				}
				else{
					obj.sTestStepFailureDetail=("No value for run config - "+data);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
					obj.sTestCaseStatus=Constants.Key_Fail_Result;
					System.out.println("No value for run config - "+data);
				}
			}
			catch(Exception e){
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized void updateRunConfig(String object, String data, DriverMembers obj){
			try{
				String runValue = obj.driver.findElement(By.xpath(object)).getText();
				obj.xlObj.updateRunConfig(data, runValue, obj);
			}
			catch (Exception e){
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized void executeFunctionalBlock(String object, String data, DriverMembers obj){
			try {
				DriverScript.execute_Block(data,obj);
			} catch (Exception e) {
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized void inputDataFeeder(String object, String data, DriverMembers obj){
			String inputDataFeed = null;
			try{
				obj.xlObj.setExcelFile(obj.sDataFeeder, "DataFeeder");
				inputDataFeed = obj.xlObj.getSpecificCellData(obj.sCurrentIteration,Integer.parseInt(data),"DataFeeder",obj.sDataFeeder);
				obj.driver.findElement(By.xpath(object)).sendKeys(inputDataFeed);
				obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
			}
			catch (ElementNotInteractableException ei){
				try {
					setScroll(object,obj);
					obj.driver.findElement(By.xpath(object)).sendKeys(inputDataFeed);
					obj.xlObj.setExcelFile(DriverScript.Path_Executable, obj.sTestCase);
				} catch (Exception e) {
					setFailResult(e,obj,"");
				}
			}
			catch(Exception e){
				setFailResult(e,obj,"");
			}
			
		}
		
		public synchronized static void setScroll(String object, DriverMembers obj){
			JavascriptExecutor je = (JavascriptExecutor) obj.driver;
			WebElement element = obj.driver.findElement(By.xpath(object));
			je.executeScript("arguments[0].scrollIntoView(true);",element);
		}
		
		public synchronized static void highlightElement(DriverMembers obj){
			try{
			JavascriptExecutor je = (JavascriptExecutor) obj.driver;
			WebElement element = obj.driver.findElement(By.xpath(obj.sPageObject));
			je.executeScript("arguments[0].setAttribute('style', 'border: 2px solid red;');", element);
			Thread.sleep(1000);
			je.executeScript("arguments[0].setAttribute('style', '');", element);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
		public synchronized static void assertFontHexColor(String object, String data, DriverMembers obj){
			try{
				String expectedColor=obj.xlObj.getRunConfig(data);
				
				highlightElement(obj);
				
				String color=obj.driver.findElement(By.xpath(object)).getCssValue("color");
				String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

				int hexValue1=Integer.parseInt(hexValue[0]);
				hexValue[1] = hexValue[1].trim();
				int hexValue2=Integer.parseInt(hexValue[1]);
				hexValue[2] = hexValue[2].trim();
				int hexValue3=Integer.parseInt(hexValue[2]);

				String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
				if(!actualColor.equalsIgnoreCase(expectedColor)){
					obj.sTestStepFailureDetail=("Actual color -"+actualColor+" not matches Expected color - "+expectedColor);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
				}
				else{
					obj.sTestStepFailureDetail=("Actual color -"+actualColor+" matches Expected color - "+expectedColor);
				}
			}
			catch(Exception e){
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized static void assertBackgroundHexColor(String object, String data, DriverMembers obj){
			try{
				String expectedColor=obj.xlObj.getRunConfig(data);
				
				highlightElement(obj);
				Thread.sleep(1000);
				String color=obj.driver.findElement(By.xpath(object)).getCssValue("background-color");
				String[] hexValue = color.replace("rgba(", "").replace(")", "").split(",");

				int hexValue1=Integer.parseInt(hexValue[0]);
				hexValue[1] = hexValue[1].trim();
				int hexValue2=Integer.parseInt(hexValue[1]);
				hexValue[2] = hexValue[2].trim();
				int hexValue3=Integer.parseInt(hexValue[2]);

				String actualColor = String.format("#%02x%02x%02x", hexValue1, hexValue2, hexValue3);
				if(!actualColor.equalsIgnoreCase(expectedColor)){
					obj.sTestStepFailureDetail=("Actual color -"+actualColor+" not matches Expected color - "+expectedColor);
					obj.sTestStepStatus=Constants.Key_Fail_Result;
				}
				else{
					obj.sTestStepFailureDetail=("Actual color -"+actualColor+" matches Expected color - "+expectedColor);
				}
			}
			catch(Exception e){
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized static void mouseHover(String object, String data, DriverMembers obj){
			Actions actions = new Actions(obj.driver);
			WebElement target = obj.driver.findElement(By.xpath(object));
			
			actions.moveToElement(target).perform();
		}
		
		public synchronized static void setFailResult(Exception e, DriverMembers obj, String customFailureMessage){
			if (customFailureMessage.isEmpty()){
			obj.sTestStepFailureDetail=e.getMessage();
			}
			else{
				obj.sTestStepFailureDetail=customFailureMessage+" - "+e.getMessage();	
			}
			obj.sTestStepStatus=Constants.Key_Fail_Result;
//			obj.sTestCaseStatus=Constants.Key_Fail_Result;
			System.out.println(e.getMessage());
		}
		
		public synchronized static void writeMDMFile (String object, String data, DriverMembers obj){
			try {
				readMDMConfig.writeFile();
			} catch (Exception e) {
				setFailResult(e,obj,e.getMessage());
			}
		}
		
		public synchronized static void validateToggleSetting(String object, String data, DriverMembers obj){
			obj.dbObj.validateToggleSetting(data,obj);
		}
		
		
		public synchronized static void validateRMSLOVLoad (String object, String data, DriverMembers obj){
			try {
				obj.dbObj.validateLOVLoad(obj);
			} catch (Exception e) {
				e.printStackTrace();
				setFailResult(e,obj,"");
			}
		}
		
		public synchronized static void verifyEmailTriggered(String object, String data, DriverMembers obj){
			obj.emlObj.getMail(obj);
		}
		
		public synchronized static void placeMDMTestData(String object, String data, DriverMembers obj){
			try{
				obj.mdm.prepareAndTransferMDMFile(obj);
				}
			catch(Exception e){
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = e.getMessage();
			}
		}
		
		public synchronized static void checkMDMExecution(String object, String data, DriverMembers obj){
			try{
				obj.dbObj.checkMDMStatus(obj);
				}
			catch(Exception e){
				obj.sTestCaseStatus = Constants.Key_Fail_Result;
				obj.sTestStepFailureDetail = e.getMessage();
			}
		}
}
