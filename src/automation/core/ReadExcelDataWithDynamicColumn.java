package automation.core;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.openqa.selenium.json.Json;
//
//import com.google.gson.Gson;
//import com.google.gson.stream.JsonReader;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import net.sf.json.JSONObject;



public class ReadExcelDataWithDynamicColumn {

	
	public static void prepareAndTransferMDMFile(DriverMembers obj) {
		//List to catch returned value of all prepared flat files and their locations
		Map <String, String> filesToPlace = new HashMap<String, String>();
		
		// You can specify your excel file path.
        String excelFilePath = obj.sPageObject;
        int outputRecordCount=5;

        // This method will read each sheet data from above excel file and create a JSON and a text file to save the sheet data.
        createJSONAndTextFileFromExcel(obj.sPageObject);
        
        //Reading Config File for Flat File generation purpose
        MDMFilePrepare mdmObj = new MDMFilePrepare();
        filesToPlace = mdmObj.prepareMDMTestData(obj);
        mdmObj = null;
        
        //Placing prepared flat files onto sftp location
        sftpTransfer sftp = new sftpTransfer();
        try {
			sftp.transferToRemote(filesToPlace);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Read data from an excel file and output each sheet data to a json file and a text file. 
     * filePath :  The excel file store path.
     * */
    private static void createJSONAndTextFileFromExcel(String filePath)
    {
        try{
         /* First need to open the file. */
            FileInputStream fInputStream = new FileInputStream(filePath.trim());
   
         /* Create the workbook object to access excel file. */
            //Workbook excelWookBook = new XSSFWorkbook(fInputStream)
         /* Because this example use .xls excel file format, so it should use HSSFWorkbook class. For .xlsx format excel file use XSSFWorkbook class.*/;
            Workbook excelWorkBook = new XSSFWorkbook(fInputStream);

            // Get all excel sheet count.
            int totalSheetNumber = excelWorkBook.getNumberOfSheets();

            // Loop in all excel sheet.
            for(int i=0;i<totalSheetNumber;i++)
            {
                // Get current sheet.
                Sheet sheet = excelWorkBook.getSheetAt(i);

                // Get sheet name.
                String sheetName = sheet.getSheetName();

                if(sheetName != null && sheetName.length() > 0)
                {
                    // Get current sheet data in a list table.
                    List<List<String>> sheetDataTable = getSheetDataList(sheet);
                    

//                    for (List<String> list: sheetDataTable){
//                    	System.out.println("This is new Record");
//                    	for (String item: list){
//                    		System.out.println(item+",");
//                    	}
//                    	
//                    }
                    
                    // Generate JSON format of above sheet data and write to a JSON file.
                    String jsonString = getJSONStringFromList(sheetDataTable);
                    String jsonFileName = sheet.getSheetName() + ".json";
                    writeStringToFile(jsonString, jsonFileName);

                    // Generate text table format of above sheet data and write to a text file.
//                    String textTableString = getTextTableStringFromList(sheetDataTable);
//                    String textTableFileName = sheet.getSheetName() + ".txt";
//                    writeStringToFile(textTableString, textTableFileName);
                    

                }
            }
            // Close excel work book object. 
            excelWorkBook.close();
        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }


    /* Return sheet data in a two dimensional list. 
     * Each element in the outer list is represent a row, 
     * each element in the inner list represent a column.
     * The first row is the column name row.*/
    private static List<List<String>> getSheetDataList(Sheet sheet)
    {
        List<List<String>> ret = new ArrayList<List<String>>();
        String sheetName = sheet.getSheetName();
        // Get the first and last sheet row number.
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        if(lastRowNum > 0)
        {
        	Row topRow = sheet.getRow(firstRowNum);
        	int firstCellNum = topRow.getFirstCellNum();
            int lastCellNum = topRow.getLastCellNum();
            // Loop in sheet rows.
            for(int k=firstRowNum; k<lastRowNum + 1; k++)
            {
                // Get current row object.
                Row row = sheet.getRow(k);

                // Get first and last cell number.
//                int firstCellNum = row.getFirstCellNum();
//                int lastCellNum = row.getLastCellNum();

                // Create a String list to save column data in a row.
                List<String> rowDataList = new ArrayList<String>();

                // Loop in the row cells.
                for(int j = firstCellNum; j < lastCellNum; j++)
                {
                    // Get current cell.
                    Cell cell = row.getCell(j);

                    // Get cell type.
                    try{
                    	CellType cellType = cell.getCellType();
                    	if (cellType!=null) {
    						if (cellType == CellType.NUMERIC) {
    							double numberValue = cell.getNumericCellValue();

    							// BigDecimal is used to avoid double value is counted use Scientific counting method.
    							// For example the original double variable value is 12345678, but jdk translated the value to 1.2345678E7.
    							String stringCellValue = BigDecimal.valueOf(numberValue).toPlainString();

    							rowDataList.add(stringCellValue);

    						} else if (cellType == CellType.STRING) {
    							String cellValue = cell.getStringCellValue();
    							rowDataList.add(cellValue);
    						} else if (cellType == CellType.BOOLEAN) {
    							boolean numberValue = cell.getBooleanCellValue();

    							String stringCellValue = String.valueOf(numberValue);

    							rowDataList.add(stringCellValue);

    						} else if (cellType == CellType.BLANK) {
    							rowDataList.add("");
    						} 
    					}
                        else{
                        	rowDataList.add("");
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException exception ){
                    	System.out.println("Within ArrayIndexOutOfBound");
                    	System.out.println(exception.getMessage());
                    }
                    catch(Exception e){
                    	System.out.println("At row# "+k);
                    	System.out.println("At column# "+j);
                    	System.out.println(rowDataList.size());
                    	
                    	System.out.println(e.getMessage());
                    }
                    
                }

                // Add current row data list in the return list.
                ret.add(rowDataList);
            }
        }
        return ret;
    }

    /* Return a JSON string from the string list. */
    private static String getJSONStringFromList(List<List<String>> dataTable)
    {
        String ret = "";

        if(dataTable != null)
        {
            int rowCount = dataTable.size();

            if(rowCount > 1)
            {
                // Create a JSONObject to store table data.
                JSONObject tableJsonObject = new JSONObject();

                // The first row is the header row, store each column name.
                List<String> headerRow = dataTable.get(0);

                int columnCount = headerRow.size();

                // Loop in the row data list.
                for(int i=1; i<rowCount; i++)
                {
                    // Get current row data.
                    List<String> dataRow = dataTable.get(i);
                    String keyName = dataRow.get(0);
                    // Create a JSONObject object to store row data.
                    JSONObject rowJsonObject = new JSONObject();

                    for(int j=1;j<columnCount;j++)
                    {
                        String columnName = headerRow.get(j);
                        String columnValue = dataRow.get(j);
                        if(!columnValue.isEmpty()){
                        	rowJsonObject.put(columnName, columnValue);
                        }
                        else{
                        	rowJsonObject.put(columnName, "0");
                        }
                    }

                    tableJsonObject.put(keyName, rowJsonObject);
                }

                // Return string format data of JSONObject object.
                ret = tableJsonObject.toString();

            }
        }
        return ret;
    }


    /* Return a text table string from the string list. */
    private static String getTextTableStringFromList(List<List<String>> dataTable)
    {
        StringBuffer strBuf = new StringBuffer();

        if(dataTable != null)
        {
        	for (List<String> list: dataTable){
            	for (String item: list){
            		strBuf.append(item+",");
            	}
            	strBuf.append("\n");
            }
            

        }
        return strBuf.toString();
    }

    /* Write string data to a file.*/
    private static void writeStringToFile(String data, String fileName)
    {
        try
        {
            // Get current executing class working directory.
            String currentWorkingFolder = System.getProperty("user.dir");

            // Get file path separator.
            String filePathSeperator = System.getProperty("file.separator");

            // Get the output file absolute path.
            String filePath = currentWorkingFolder + filePathSeperator + fileName;

            // Create File, FileWriter and BufferedWriter object.
            File file = new File(filePath);

            FileWriter fw = new FileWriter(file);

            BufferedWriter buffWriter = new BufferedWriter(fw);

         // Write string data to the output file, flush and close the buffered writer object.
            buffWriter.write(data);            

            buffWriter.flush();

            buffWriter.close();

            System.out.println(filePath + " has been created.");

        }catch(IOException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

}
