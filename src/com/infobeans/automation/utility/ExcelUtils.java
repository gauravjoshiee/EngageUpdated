package com.infobeans.automation.utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.infobeans.automation.config.Constants;
import com.infobeans.automation.core.DriverMembers;
import com.infobeans.automation.core.DriverScript;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

@SuppressWarnings("static-access")   
	public class ExcelUtils{
	   
	   private  static XSSFSheet ExcelWSheet;
       private  static XSSFWorkbook ExcelWBook;
       private  static XSSFCell Cell;
       
       /**
        * This method is to read the test data from the Excel cell
        * 
        * @param RowNum
        * @param ColNum
        * @return
        * @throws Exception
        */
       //This method is to read the test data from the Excel cell
       //In this we are passing parameters/arguments as Row Num and Col Num
       public synchronized String getCellData(int RowNum, int ColNum, String SheetName) throws Exception{
    	   String currentSheet = ExcelWSheet.getSheetName();
    	   if (currentSheet.equalsIgnoreCase(SheetName)){
    	   }
    	   else{
    		   this.setExcelFile(DriverScript.Path_Executable,SheetName);
    	   }
            DataFormatter formatter = new DataFormatter();
            Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
            String CellData="";
            if (Cell==null){
              return "";
            }
            else{
            	if (Cell.getCellType().equals(CellType.FORMULA)){
         		   if(Cell.getCachedFormulaResultType().equals(CellType.NUMERIC)){
         			  CellData = Double.toString(Cell.getNumericCellValue());
         		   }
            	}
            	else{
          		   CellData = formatter.formatCellValue(ExcelWSheet.getRow(RowNum).getCell(ColNum));
            	}
            }
            return CellData;
        }
       
       /**
        * 
        * @param SheetName
        * @param Variable
        */
       public synchronized String getDataVariable(String SheetName, String Variable, DriverMembers obj){
   			ExcelWSheet = ExcelWBook.getSheet(SheetName);
   			int targetRow;
   			String value="";
   				try{
   					targetRow = getTargetRow(Constants.Sheet_DataVariables, Variable, Constants.Col_DataVariableName);
   					value= this.getCellData(targetRow,Constants.Col_DataVariableValue,Constants.Sheet_DataVariables);
   					}
   				
   				catch(Exception e) {
   					System.out.println(e);
   				}
   			return value;
   		}
       
       public synchronized void setDataVariable(String SheetName, String Variable, String Value,DriverMembers obj){
  			ExcelWSheet = ExcelWBook.getSheet(SheetName);
  			try {
				int targetRow = getTargetRow(Constants.Sheet_DataVariables, Variable, Constants.Col_DataVariableName);
				this.setStepResult(Value,targetRow,Constants.Col_DataVariableValue,Constants.Sheet_DataVariables,obj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  		}
       
       /**
        * 
        * @param SheetName
        * @param Variable
        * @return
        * @throws Exception
        */
       public synchronized int getTargetRow(String SheetName, String Variable, int lookUpColumn) throws Exception{
    	   	ExcelWSheet = ExcelWBook.getSheet(SheetName);
   			int targetRow=0;
   			int RowNum = ExcelWSheet.getLastRowNum();
   			for (int i=0;i<=RowNum;i++){
   				String localVar = this.getCellData(i,lookUpColumn,SheetName);
   				if(localVar.equals(Variable)){
   					targetRow = i;	
   				}
   			}
			return targetRow;
   		}
       
       /**
        * This method is to set the File path and to open the Excel file
        * @param Path
        * @param SheetName
        * @throws Exception
        */
       public synchronized XSSFSheet setExcelFile(String path,String sheetName) throws Exception {
               
    	   try (FileInputStream excelFile = new FileInputStream(path)) {
				ExcelWBook = new XSSFWorkbook(excelFile);
				   ExcelWSheet = ExcelWBook.getSheet(sheetName);
				   
			} catch (IOException e) {
				e.printStackTrace();
			}
    	   return ExcelWSheet;
       }
       
       /**
        * This method to write test step result in the excel sheet
        * @param Result
        * @param RowNum
        * @param ColNum
        * @param SheetName
        * @throws Exception
        */

       public synchronized void setStepResult(String Result,  int RowNum, int ColNum, String SheetName, DriverMembers obj) throws Exception{
       	   try{
       		   this.setExcelFile(DriverScript.Path_Executable, SheetName);
       		   ExcelWSheet = ExcelWBook.getSheet(SheetName);
       		   
       		   Row R = ExcelWSheet.getRow(RowNum);
       		   Cell C = R.getCell(ColNum);
       		   if (C == null) {
       			   C = R.createCell(ColNum);
       			   C.setCellValue(Result);
       		   } else {
       				C.setCellValue(Result);
       				}
       			// Constant variables Test Data path and Test Data file name
       			FileOutputStream fileOut = new FileOutputStream(DriverScript.Path_Executable);
       			ExcelWBook.write(fileOut);
       			//fileOut.flush();
       			fileOut.close();
       			ExcelWBook = new XSSFWorkbook(new FileInputStream(DriverScript.Path_Executable));
       		}
       	   	catch(Exception e){
       			obj.sTestStepStatus = Constants.Key_Fail_Result;
       	   	}
       	}
       
       /**
        * 
        * @param TestCase
        * @param TestStep
        * @param Variable
        * @param Value
        * @param SheetName
        */
       public synchronized static void insertDataVariable(String TestCase, String TestStep, String Variable, String Value, String SheetName, DriverMembers obj ){
     	  try{
     		          		  
     		  ExcelWSheet = ExcelWBook.getSheet(SheetName);
     		  int RowNum=ExcelWSheet.getLastRowNum()+1;
     	  
     		  Row R = ExcelWSheet.getRow(RowNum);
     		  if (R==null){
     			  R=ExcelWSheet.createRow(RowNum);
     		  }
     		  
     		  Cell C0 = R.getCell(0);
     		  if (C0 == null) {
  			   C0 = R.createCell(0);
  			   C0.setCellValue(TestCase);
     		  } else {
  				C0.setCellValue(TestCase);
  				}
     		  
     		  Cell C1 = R.getCell(1);
     		  if (C1 == null) {
  			   C1 = R.createCell(1);
  			   C1.setCellValue(TestStep);
     		  } else {
  				C1.setCellValue(TestStep);
  				}
     		  
     		  Cell C2 = R.getCell(2);
     		  if (C2 == null) {
  			   C2 = R.createCell(2);
  			   C2.setCellValue(Variable);
     		  } else {
  				C2.setCellValue(Variable);
  				}
     		  
     		  Cell C3 = R.getCell(3);
     		  if (C3 == null) {
  			   C3 = R.createCell(3);
  			   C3.setCellValue(Value);
     		  } else {
  				C3.setCellValue(Value);
  				}
     		  
     		  // Constant variables Test Data path and Test Data file name
     		  FileOutputStream fileOut = new FileOutputStream(DriverScript.Path_Executable);
     		  ExcelWBook.write(fileOut);
     		  fileOut.close();
     		  ExcelWBook = new XSSFWorkbook(new FileInputStream(DriverScript.Path_Executable));
  		 } 
       	catch(Exception e){
  			obj.sTestStepStatus = Constants.Key_Fail_Result;
  			}
       } 
   
       public synchronized static String getDate(String format){
    	   DateFormat dateFormat = new SimpleDateFormat(format);
   		Date date = new Date();
   		
   		String timeStamp = String.valueOf(dateFormat.format(date));
   		
   		return timeStamp;
       }
       
       @SuppressWarnings("finally")
       public synchronized String getRunConfig(String Variable){
    	   
  			String runValue=null;
    	   try{
    		    this.setExcelFile(DriverScript.Path_Executable, Constants.Sheet_RunConfig);
  				int targetRow = getTargetRow(Constants.Sheet_RunConfig, Variable, Constants.Col_RunConfigName);
  				runValue = getCellData(targetRow,Constants.Col_RunConfigValue,Constants.Sheet_RunConfig);
  			}
    	  
  			catch(Exception e) {
  				System.out.println(e.getMessage());
  			}
    	   finally{
    		   if (runValue!=null){
    			   return runValue;
    		   }
    		   else{
    			   return "";
    		   }
    	   }
  		}
       
       public synchronized void updateRunConfig(String Variable, String Value, DriverMembers obj){
 			
   	   try{
 				int targetRow = this.getTargetRow(Constants.Sheet_RunConfig, Variable, Constants.Col_RunConfigName);
 				if(targetRow>0){
 					this.setStepResult(Value,targetRow,Constants.Col_RunConfigValue,Constants.Sheet_RunConfig,obj);
 				}
 				else{
 					obj.sTestStepFailureDetail="RunConfig Key "+Variable+" not found to store random email - "+Value;
 				}
 			}
   	  
 			catch(Exception e) {
 				System.out.println(e.getMessage());
 			}
   	   }
  	
       public synchronized static int randomNumber(int length){
//    	   Random rand = new Random();
//    	   int x = (int) ((Math.random()*((max-min)+1))+min);
//    	   return x;
    	   int max= (int) (Math.pow(10,length)-1);
    	     int min=(int) Math.pow(10,length-1);
    	     int range = max - min + 1;
    	     int randomNumber = (int) (Math.random() * range) + min;
    	   return randomNumber;
       }
       
       
       public synchronized String getSpecificCellData(int rowNum, int colNum, String sheetName, String filePath) throws Exception{
    	   String currentSheet = ExcelWSheet.getSheetName();
    	   if (currentSheet.equalsIgnoreCase(sheetName)){
    	   }
    	   else{
    		   setExcelFile(filePath, sheetName);
    	   }
            DataFormatter formatter = new DataFormatter();
            Cell = ExcelWSheet.getRow(rowNum).getCell(colNum);
            String cellData="";
            if (Cell==null){
              return "";
            }
            else{
            	if (Cell.getCellType().equals(CellType.FORMULA)){
         		   if(Cell.getCachedFormulaResultType().equals(CellType.NUMERIC)){
         			  cellData = Double.toString(Cell.getNumericCellValue());
         		   }
         		  if(Cell.getCachedFormulaResultType().equals(CellType.STRING)){
         			 cellData = Cell.getStringCellValue();
         		   }
            	}
            	else{
            		cellData = formatter.formatCellValue(ExcelWSheet.getRow(rowNum).getCell(colNum));
            	}
            }
            return cellData;
        }
 
       public synchronized String getCellReferenceFromLOVSummary(int rowNum, int colNum, String sheetName, String filePath) throws Exception{
    	   String currentSheet = ExcelWSheet.getSheetName();
    	   if (!currentSheet.equalsIgnoreCase(sheetName)){
    		   this.setExcelFile(filePath, sheetName);
    	   }
            DataFormatter formatter = new DataFormatter();
            Cell = ExcelWSheet.getRow(rowNum).getCell(colNum);
            String cellData="";
            XSSFHyperlink h = Cell.getHyperlink();
            if (h != null){
            
            	String location=(Cell.getHyperlink().getLocation());
            	
            	if (location.contains("'")){
            		cellData = location.substring(location.indexOf("'") + 1, location.indexOf("'!"));
            	}
            	else {
            		if(location.contains("!")){
            			cellData = location.substring(0, location.indexOf("!"));
            		}
            		else{
            			cellData = location;
            		}
            	}
            }
            return cellData;
        }
       
       
}



            
            
            
          
          
