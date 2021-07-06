package automation.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import automation.config.Constants;
import automation.core.DriverMembers;
import net.sf.json.JSONObject;;

@SuppressWarnings("static-access")
public class DBUtils {

  static String dbServer = null;
  static String dbServerInstance = null;
  static String dbName = null;
  static String dbUser = null;
  static String dbPassword = null;
  static Connection connection;

  static Map<String, String> expectedToggleSetting = new HashMap<String, String>();
  static Map<String, String> actualToggleSetting = new HashMap<String, String>();
  static Map<String, ArrayList<String>> actualLOVLoad = new HashMap<String, ArrayList<String>>();
  static Map<String, String> backendNameLOV = new HashMap<String, String>();
  static Map<String, String> expectedLOVList = new HashMap<String, String>();
  static Map<String, ArrayList<String>> expectedLOVLoad = new HashMap<String, ArrayList<String>>();

  public static void validateToggleSetting(String toggleName, DriverMembers obj) {
    try {
      getExpectedToggleSetting(obj);
      getActualToggleSetting();
      compareToggleSetting(obj);
      System.out.println("Printing failure string");
      System.out.println(obj.sTestStepFailureDetail);

    } catch (Exception err) {
      System.out.println(err.getMessage());
      System.err.println("Error loading JDBC driver");
      err.printStackTrace(System.err);
      System.exit(0);
    }
  }

  private static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

    String dbURL = ("jdbc:sqlserver://" + dbServer + "\\" + dbServerInstance + ":1433");
    Properties properties = new Properties();
    properties.put("database", dbName);
    properties.put("domain", "polaris");
    properties.put("user", dbUser);
    properties.put("password", dbPassword);
    // properties.put("integratedSecurity","true");
    Connection connection = DriverManager.getConnection(dbURL, properties);
    return connection;
  }

  private static void getExpectedToggleSetting(DriverMembers obj) throws Exception {
	  ExcelUtils xlObj = new ExcelUtils();
    String filePath = xlObj.getRunConfig("RMSSystemConfigPath");
    String sheetName = xlObj.getRunConfig("ToggleSettingSheetName");
    System.out.println("Filepath - " + filePath);
    System.out.println("Toggle sheet Name - " + sheetName);
    // int targetrow = ExcelUtils.getTargetRow(sheetName, toggleName, 1);
    int sourceLength = xlObj.setExcelFile(filePath, sheetName).getLastRowNum();
    expectedToggleSetting.clear();
    for (int i = 1; i <= sourceLength; i++) {
      String Key = obj.xlObj.getSpecificCellData(i, 1, sheetName, filePath);
      String Value = obj.xlObj.getSpecificCellData(i, 6, sheetName, filePath);
      if (Value.equals("N/A")) {
        Value = "OFF";
      }
      expectedToggleSetting.put(Key, Value);
    }
  }

  private static void getActualToggleSetting() throws SQLException, ClassNotFoundException {
    actualToggleSetting.clear();
    connection = getConnection();
    PreparedStatement prep = connection.prepareStatement("select Toggle_Name,active_flag from t_toggle");
    ResultSet resultset = prep.executeQuery();

    String activeFlag = null;
    if (resultset.isBeforeFirst()) {
      while (resultset.next()) {
        if (resultset.getString(2).equalsIgnoreCase("0")) {
          activeFlag = "OFF";
        } else {
          activeFlag = "ON";
        }
        actualToggleSetting.put(resultset.getString(1).trim(), activeFlag);
      }

    } else {
      System.out.println("No record found");
    }
    connection.close();
  }

  private static void compareToggleSetting(DriverMembers obj) throws Exception {
    // Iterator toggleIterator = actualToggleSetting.entrySet().iterator();
    // while (toggleIterator.hasNext()){
    // Map.Entry mapElement = (Map.Entry)toggleIterator.next();
    // System.out.println(mapElement.getKey()+" : "+mapElement.getValue());
    // }
    //
    // System.out.println("Expected Key - "+expectedToggleSetting.keySet());
    // System.out.println("Actual Key - "+actualToggleSetting.keySet());
    String failedSettings = "";
    try {

      for (String k : actualToggleSetting.keySet()) {
        if (expectedToggleSetting.containsKey(k)) {
          if (!expectedToggleSetting.get(k).equals(actualToggleSetting.get(k))) {
            obj.sTestStepStatus = Constants.Key_Fail_Result;
            obj.sTestCaseStatus = Constants.Key_Fail_Result;
            failedSettings = failedSettings.concat("<br>" + k + "toggle validation failed: " + "Expected - "
                + expectedToggleSetting.get(k) + " Actual - " + actualToggleSetting.get(k));
          }
        } else {
          failedSettings = failedSettings.concat("<br>" + k + " Key is not present in Expected setting");
        }
      }
      obj.sTestStepFailureDetail = failedSettings;
      // For checking missing setting
      // for (String y : expectedToggleSetting.keySet())
      // {
      // if (!actualToggleSetting.containsKey(y)) {
      // return false;
      // }
      // }
    } catch (NullPointerException np) {
      obj.sTestStepStatus = Constants.Key_Fail_Result;
      obj.sTestCaseStatus = Constants.Key_Fail_Result;
      obj.sTestStepFailureDetail = np.getMessage();
    }
  }

  private static void getlistItemValues(String listItem, ResultSet resultset) throws SQLException {

    if (resultset.isBeforeFirst()) {
      ArrayList<String> listValue = new ArrayList<String>();
      while (resultset.next()) {
        if (resultset.getString(1).equalsIgnoreCase(listItem)) {
          listValue.add(resultset.getString(2));
        }
      }
      actualLOVLoad.put(listItem, listValue);
      resultset.beforeFirst();
    } else {
      System.out.println("No record found");
    }
  }

  private static void getActualLOVLoad(DriverMembers obj) throws ClassNotFoundException, SQLException {
    actualLOVLoad.clear();
    backendNameLOV.clear();
    getListBackendName(obj);
    connection = getConnection();
    PreparedStatement prep = connection.prepareStatement(
        "Select a.list_name,b.list_description,a.gms_access from t_dropdown_lists as a JOIN t_dropdown_list_items as b on a.list_id=b.list_id where a.gms_access=? and b.active_ind=1 and b.language_id=1",
        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
    if (obj.sPageData.equalsIgnoreCase("Shared")) {
      prep.setString(1, "All");
    } else {
      prep.setString(1, obj.sPageData);
    }
    System.out.println(prep);
    ResultSet resultset = prep.executeQuery();

    for (String listItem : expectedLOVLoad.keySet()) {
      System.out.println("Loading Actual ative LOV for - " + listItem);
      getlistItemValues(backendNameLOV.get(listItem), resultset);
      System.out.println("Actual value for " + listItem + " - " + actualLOVLoad.get(listItem));
    }
    connection.close();
  }

  private static void getListBackendName(DriverMembers obj) {
    String FilePath = obj.xlObj.getRunConfig("LOVConfiguration");
    XSSFSheet sheet;
    try {
      sheet = obj.xlObj.setExcelFile(FilePath, "BackendMapping");
      int counter = sheet.getLastRowNum();
      for (int i = 1; 1 <= counter; i++) {
        String requirementKey = obj.xlObj.getSpecificCellData(i, 0, "BackendMapping", FilePath);
        String backendKey = obj.xlObj.getSpecificCellData(i, 1, "BackendMapping", FilePath);
        backendNameLOV.put(requirementKey, backendKey);
      }
    } catch (Exception e) {
      obj.sTestStepFailureDetail = "Unable to read backend name of list" + e.getMessage();
      obj.sTestStepStatus = Constants.Key_Fail_Result;
    }

  }

  public static void getListOfExpectedLOV(DriverMembers obj) {
    try {
      String FilePath = obj.xlObj.getRunConfig("LOVConfiguration");
      XSSFSheet sheet = obj.xlObj.setExcelFile(FilePath, "Summary");
      int counter = sheet.getLastRowNum();
      for (int i = 0; i < counter; i++) {
        if (obj.xlObj.getSpecificCellData(i, 4, "Summary", FilePath).equalsIgnoreCase("Y")) {
          if (obj.xlObj.getSpecificCellData(i, 5, "Summary", FilePath).equalsIgnoreCase(obj.sPageData)) {
            String referenceTab = obj.xlObj.getCellReferenceFromLOVSummary(i, 3, "Summary", FilePath);
            if (!(referenceTab.equals(""))) {
              String listName = obj.xlObj.getSpecificCellData(i, 3, "Summary", FilePath);
              expectedLOVList.put(listName, referenceTab);
              System.out.println(listName + " : " + referenceTab);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean getExpectedLOVLoad(DriverMembers obj) throws Exception {
    try {
      getListOfExpectedLOV(obj);
      String filePath = obj.xlObj.getRunConfig("LOVConfiguration");
      for (String key : expectedLOVList.keySet()) {
        String sheetName = expectedLOVList.get(key);
        System.out.println("Loading Expected active LOV for - " + sheetName);
        ArrayList<String> listValue = new ArrayList<String>();
        XSSFSheet sheet = obj.xlObj.setExcelFile(filePath, sheetName);
        int rowCounter = sheet.getLastRowNum();
        int activIndexColumn = 0;
        int listDescriptionColumn = 0;
        for (int j = 0; j <= 10; j++) {
          if (obj.xlObj.getSpecificCellData(0, j, sheetName, filePath).trim()
              .equalsIgnoreCase("Active (Y/N)")) {
            activIndexColumn = j;
            break;
          }
        }
        for (int j = 0; j <= 10; j++) {
          if (obj.xlObj.getSpecificCellData(0, j, sheetName, filePath).trim()
              .equalsIgnoreCase("List Description")) {
            listDescriptionColumn = j;
            break;
          }
        }
        if (activIndexColumn > 0 & listDescriptionColumn > 0) {
          for (int i = 0; i < rowCounter; i++) {
            String activeIndex = obj.xlObj.getSpecificCellData(i, activIndexColumn, sheetName, filePath);
            if (!activeIndex.isEmpty()) {
              if (activeIndex.equalsIgnoreCase("Y")) {
                String listItem = obj.xlObj.getSpecificCellData(i, listDescriptionColumn, sheetName,
                    filePath);
                listValue.add(listItem);
              }
            } else {
              break;
            }
          }
        } else {
          System.out.println("Unable to identify Active Index column, or List Description column");
        }
        // System.out.println(key+" : "+listValue.toString());
        expectedLOVLoad.put(key, listValue);
      }
      if (expectedLOVLoad.isEmpty()) {
        return false;
      }
      return true;
    } catch (Exception e) {
      obj.sTestStepFailureDetail = e.getMessage();
      obj.sTestStepStatus = Constants.Key_Fail_Result;
      e.printStackTrace();
      return false;
    }
  }

  private static void compareLOVLoad(DriverMembers obj) {

    for (String k : expectedLOVLoad.keySet()) {
      if (actualLOVLoad.containsKey(k)) {
        if (!expectedLOVLoad.get(k).equals(actualLOVLoad.get(k))) {
          obj.sTestStepStatus = Constants.Key_Fail_Result;
          obj.sTestCaseStatus = Constants.Key_Fail_Result;
          System.out.println(k + " - LOV Load is NOT as per expectation-------------------");
          System.out.println("Expected Load is - " + expectedLOVLoad.get(k).toString());
          System.out.println("Actual Load is - " + actualLOVLoad.get(k).toString());
          System.out.println("----------------------------------------------------------");
        } else {

          System.out.println(k + " - LOV Load is as per expectation");
        }

      } else {
        System.out.println("No Load found for list - " + k);
      }
    }
  }

  public static void validateLOVLoad(DriverMembers obj) throws Exception {
    if (getExpectedLOVLoad(obj)) {
      for (String k : expectedLOVLoad.keySet()) {
        System.out.println(k + " - " + expectedLOVLoad.get(k));
      }

      getActualLOVLoad(obj);
      // compareLOVLoad(obj);
    } else {

      obj.sTestStepFailureDetail = ("No active LOV found for given parameter - " + obj.sPageData);
      obj.sTestStepStatus = Constants.Key_Block_Result;
      System.out.println(obj.sTestStepFailureDetail);
    }
  }


  @SuppressWarnings("unchecked")
  public static void checkMDMStatus(DriverMembers obj){
    Iterator<String> keys = obj.jsonTable1.keys();
    String filePrefix;
    String date;
    String fileName;
    while(keys.hasNext()){
      filePrefix = keys.next();
      JSONObject json = new JSONObject();
      json = (JSONObject) obj.jsonTable1.get(filePrefix);
      date = json.getString("DateTime");
      fileName = json.getString("FileName")+".txt";
      System.out.println(fileName);
      try{
        connection = getConnection();
        PreparedStatement prep = connection.prepareStatement(
            "select FileName,ExecutionStatusId,ErrorMessage,TotalRecords,RecordsProcessed, RecordsFailed from IMONInterfaceExecutionDetails where InterfaceFileMappingID=8 and FileName=?",
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        prep.setString(1, fileName);

        ResultSet resultset = prep.executeQuery();
        if (resultset.isBeforeFirst()){
          resultset.next();
          if(resultset.getInt("ExecutionStatusId")==3){
            System.out.println("Execution completed for file "+fileName);
          }
          else{
            String error = resultset.getString("ErrorMessage");
            System.out.println("Execution for "+fileName+" failed. Error message "+error);
            obj.sTestCaseStatus=Constants.Key_Fail_Result;
            obj.sTestStepFailureDetail="Processing failed for file "+fileName+". Error Message: "+error;
          }
        }
        else{
          obj.sTestCaseStatus=Constants.Key_Fail_Result;
          obj.sTestStepFailureDetail="Processing not done for file "+fileName;
        }
        connection.close();
      }catch (ClassNotFoundException | SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        obj.sTestCaseStatus=Constants.Key_Fail_Result;
        obj.sTestStepFailureDetail="Exception while checking MDM job status";
      }
    }
  }

  public DBUtils() {
	  ExcelUtils xlObj = new ExcelUtils();
    dbServer = xlObj.getRunConfig("dbServer");
    dbServerInstance = xlObj.getRunConfig("dbServerInstance");
    dbName = xlObj.getRunConfig("dbName");
    dbUser = xlObj.getRunConfig("dbUser");
    dbPassword = xlObj.getRunConfig("dbPassword");
  }

}
