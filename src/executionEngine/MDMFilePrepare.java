package executionEngine;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

//import net.sf.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import utility.ExcelUtils;

import org.json.simple.JSONObject;

public class MDMFilePrepare {
	
	static XSSFSheet sheet;
	static String configPath = "";
	static String delimiter="|";
	static Map <String, Map<Integer,String>> fieldMap = new HashMap<String, Map<Integer,String>>();
	static Map <String, Integer> pipeCount = new HashMap<String, Integer>();
	static ArrayList<String> processingFileList=new ArrayList<String>();
	static Map <String, String> preparedFileList = new HashMap<String, String>();
	static int outputRecordCount;
	
	static int currentSequence = 1;
	static int fieldMapStartColumn=2;
	
	public static Map <String,String> prepareMDMTestData(DriverMembers obj){
		configPath=obj.sPageObject;
		outputRecordCount=Integer.valueOf(obj.sPageData);
		processSource(obj);
		return preparedFileList;
	}
	
	public static void processSource(DriverMembers obj){
		try {
			//Setting sheet to Source tab
			sheet = obj.xlObj.setExcelFile(configPath,"Sources");
			int sourceLength = sheet.getLastRowNum();
			
			//Identifying which record has source sequence equal to current sequence (current sequence auto-increment after successful writing of file)
			for (int i=1;i<=sourceLength;i++){
				int processingSequence = Integer.parseInt(obj.xlObj.getSpecificCellData(i, 4,"Sources",configPath));
				if(processingSequence==currentSequence){
					//Setting a list of files to be created for source having current sequence
//					processingFileList.clear();
					String currentSourceName = obj.xlObj.getSpecificCellData(i, 0, "Sources", configPath);
					getFileListForSource(currentSourceName,obj);
					processFieldMap();
					writeFlatFile(obj);
					currentSequence++;
				}
//				System.out.println(processingFileList.toString());
					
				System.out.println(fieldMap.toString());	
				}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Config file not loaded");
		}
	}
	
	public static void getFileListForSource(String sourceName,DriverMembers obj){
		try{
		sheet = obj.xlObj.setExcelFile(configPath, "Source files");
		int sourceFileLength = sheet.getLastRowNum();
		for(int j=1;j<=sourceFileLength;j++){
			String processingSourceName = obj.xlObj.getSpecificCellData(j, 0, "Source files", configPath);
			if(processingSourceName.equals(sourceName)){
				String processingFileName = obj.xlObj.getSpecificCellData(j, 1, "Source files", configPath);
				int filePipeCount = Integer.parseInt(obj.xlObj.getSpecificCellData(j, 6, "Source files", configPath));
				processingFileList.add(processingFileName);
				pipeCount.put(processingFileName, filePipeCount);
				obj.jsonTable1.put(processingFileName, "");
			}
		}
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("Could not get fielname at in Source files tab");
		}

	}
	
	public static void processFieldMap(){
		try {
			ExcelUtils xlObj = new ExcelUtils();
			String fieldMapSheetName = "Source files field mappings";
			sheet = xlObj.setExcelFile(configPath, fieldMapSheetName);
			int fieldMapLength = sheet.getLastRowNum();
			for (String currentFileName:processingFileList){
				Map<Integer,String> currentFileHash=new HashMap<Integer,String>();
				for (int k=1;k<=fieldMapLength;k++){
					String processingFileName = xlObj.getSpecificCellData(k, 1, fieldMapSheetName, configPath);
					if (processingFileName.equals(currentFileName)){
						int fieldMappingLength = getColumnsCount(sheet);
						for (int l=fieldMapStartColumn;l<=fieldMappingLength;l++){
							String columnValue = xlObj.getSpecificCellData(k, l, fieldMapSheetName, configPath);
							if(!columnValue.isEmpty()){
								int columnPlace = Integer.valueOf(columnValue);
								String columnHeader = xlObj.getSpecificCellData(sheet.getTopRow(), l, fieldMapSheetName, configPath);
								if(!currentFileHash.containsKey(columnPlace)){
									currentFileHash.put(columnPlace, columnHeader);
								}
							}
						}
//						System.out.println(currentFileHash.toString());
					}
				}
				fieldMap.put(currentFileName, currentFileHash);
				System.out.println("Added mapping for - "+currentFileName);
				System.out.println(currentFileHash.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not read field mapping for file");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public static void writeFlatFile(DriverMembers dmObj){
		
		try {
			String filename = System.getProperty("user.dir")+"//FieldProperties.json";
			FileReader reader = new FileReader(filename);
			JSONParser jParser = new JSONParser();
			// parsing file "JSONExample.json" 
	        Object obj = jParser.parse(reader); 
	        
	        // typecasting obj to JSONObject 
	        JSONObject jo = (JSONObject) obj;
	        Iterator it = fieldMap.entrySet().iterator();
	        Map<Integer,String> identifier = new HashMap<Integer,String>();
	        
	        while(it.hasNext()){
	        	Map.Entry pair = (Map.Entry)it.next();
	        	String outputfilePrefix = (String) pair.getKey();
	        	String outputfileTime = getDate("yyyy-MM-dd HH:mm:ss");
	        	String timeStamp = getDate("yyyy-MM-dd_HH_mm_ss");
	        	String outputfileName = outputfilePrefix+timeStamp;
	        	String outPutFileLocation = System.getProperty("user.dir")+"//UploadPath//"+outputfileName+".txt";
	        	String format = "%s"+delimiter;
	        	System.out.println(outputfilePrefix);
	        	
//	        	File file = new File(outPutFileLocation);
//	        	FileWriter fw = new FileWriter(file);
	        	PrintWriter simFile = new PrintWriter(new FileWriter(outPutFileLocation,true));
	        	int expectedDelimiterCount = pipeCount.get(outputfilePrefix);
            	
	            for(int recordCountLoop=1;recordCountLoop<=outputRecordCount;recordCountLoop++){
	            	int actualDelimiterCount = 0;
	            	Map <Integer,String> currentFileMap = (Map<Integer, String>) pair.getValue();
	            	int mapLength = currentFileMap.size();
	            	String outputString;
	            	//updated condition from less than maplength to less than expected column count
	            	for (int i=1;i<=expectedDelimiterCount-1;i++){
	            		//Check and Record unique identifier for repeated use in all applicable files of source
	            		if(i==1){
	            			if(!identifier.containsKey(recordCountLoop)){
	            				outputString = getRandomString(6,"Number");
	            				identifier.put(recordCountLoop, outputString);
	            			}
	            			else{
	            				outputString = identifier.get(recordCountLoop);
	            			}
	            			simFile.printf(format, outputString);
	            		}
	            		else{
	        			String fieldName = currentFileMap.get(i);
	        			if(fieldName!=null){
	        				JSONObject field = (JSONObject) jo.get(fieldName);
	        				String IsRequired = (String) field.get("IsRequired");
	        				String InputType = (String) field.get("InputType");
	        				String InputLengthString = (String) field.get("InputLength");
	        				Float Length;
	        				int InputLength;
	        				if(InputLengthString.isEmpty()){
	        					InputLength = 10;
	        				}
	        				else{
	        					Length= Float.valueOf(InputLengthString);
	        					InputLength = (int) Math.round(Length);
	        				}
	        				String InputReference = (String) field.get("InputReference");
	        				if(IsRequired.equals("Yes")){
	        					if(!InputType.equals("List")){
	        						if(!InputType.equalsIgnoreCase("Value")){
	        							outputString = getRandomString(InputLength,InputType);
	        						}
	        						else{
	        							outputString = InputReference;
	        						}
	        					}
	        					else{
	        						outputString = getListItem(InputReference);
	        					}
	        					simFile.printf(format, outputString);
	        					System.out.println(i+"--"+outputString);
	        				}
	        				else{
	        					simFile.printf(format, "");
	        					System.out.println(i+"--"+"Blank");
	        				}
	        			}
	        			else {
	        				simFile.printf(format, "");
	        				System.out.println("Field is not mapped for order number **"+i+"** in file - "+outputfileName);
	        			}
	        			actualDelimiterCount++;
	        		}
	            		
	            	}
	            	if (expectedDelimiterCount>actualDelimiterCount){
	            		int remainingDelimiter = expectedDelimiterCount-actualDelimiterCount-1;
	            		for (int j=1;j<remainingDelimiter;j++){
	            			simFile.printf(format, "");
	            		}
	            	}
	            	
	            	simFile.printf("\n");
	            }
	        	simFile.flush();
	        	//write code for pushing entry in generated file list
	        	dmObj.jsonRow1.put("FileName", outputfileName);
	        	dmObj.jsonRow1.put("DateTime", outputfileTime);
	        	dmObj.jsonTable1.put(outputfilePrefix, dmObj.jsonRow1);
	        	dmObj.jsonRow1.clear();
	        	preparedFileList.put(outputfileName, outPutFileLocation);
	        }
	       
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
	}
		
    public static String getRandomString(int inputLength, String type) 
    { 
  
        // chose a Character random from this String 
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+"abcdefghijklmnopqrstuvxyz";
        
        String Number = "0123456789";
        
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(inputLength); 
  
         
        	int index;
        	if(type.equalsIgnoreCase("AlphaNumeric")){
        		for (int i = 0; i < inputLength; i++) {
        			index = (int)(alphaNumeric.length()* Math.random()); 
        			sb.append(alphaNumeric.charAt(index));
        			}
        	}
        		else{
        			if(type.equalsIgnoreCase("String")){
                		for (int j = 0; j < inputLength; j++) {
                			index = (int)(string.length()* Math.random()); 
                			sb.append(string.charAt(index));
                		}
                	}
        			else{
        				if(type.equalsIgnoreCase("Number")){
                    		for (int j = 0; j < inputLength; j++) {
                    			index = (int)(Number.length()* Math.random()); 
                    			sb.append(Number.charAt(index));
                    		}
                    	}
        				else{
        					if(type.equalsIgnoreCase("Flag")){
        						sb.append("TRUE");
                        	}
        					else{
        						if(type.equalsIgnoreCase("Email")){
        							sb.append("test@yopmail.com");
                            	}
        						else{
        							if(type.equalsIgnoreCase("Date")){
        								sb.append(getDate("mm/dd/yyyy"));
                                	}
        						}
        					}
        				}
        			}
        		}
  
        return sb.toString(); 
    } 
    
    public static String getListItem(String ListName){
    	int itemColumn = 0;
    	boolean listFound = false;
    	String item="";
    	try {
    		ExcelUtils xlObj = new ExcelUtils();
			sheet = xlObj.setExcelFile(configPath, "List of values");
			int columnCount = getColumnsCount(sheet);
			int topRowNumber = sheet.getFirstRowNum();
			Row topRow = sheet.getRow(topRowNumber);
			for(int i=0;i<=columnCount;i++){
				Cell cell = topRow.getCell(i);
				String cellValue = cell.getStringCellValue();
				if(cellValue.equalsIgnoreCase(ListName)){
					itemColumn = i;
					listFound=true;
					break;
				}
			}
			if(listFound){
				Row targetRow = sheet.getRow(topRowNumber+1);
				Cell cell2 = targetRow.getCell(itemColumn);
				item = cell2.getStringCellValue();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return item;
    }
    
    private static int getColumnsCount(XSSFSheet xssfSheet) {
	    int result = 0;
	    Iterator<Row> rowIterator = xssfSheet.iterator();
	    while (rowIterator.hasNext()) {
	        Row row = rowIterator.next();
	        List<Cell> cells = new ArrayList<>();
	        Iterator<Cell> cellIterator = row.cellIterator();
	        while (cellIterator.hasNext()) {
	            cells.add(cellIterator.next());
	        }
	        for (int i = cells.size(); i >= 0; i--) {
	            Cell cell = cells.get(i-1);
	            if (cell.toString().trim().isEmpty()) {
	                cells.remove(i-1);
	            } else {
	                result = cells.size() > result ? cells.size() : result;
	                break;
	            }
	        }
	    }
	    return result;
	}
    
    public static String getDate(String format){
  	   DateFormat dateFormat = new SimpleDateFormat(format);
 		Date date = new Date();
 		
 		String timeStamp = String.valueOf(dateFormat.format(date));
 		
 		return timeStamp;
     }
}
