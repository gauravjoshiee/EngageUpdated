package utility;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;

import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import config.Constants;
import executionEngine.DriverMembers;
import executionEngine.DriverScript;

public class readMDMConfig {
	
	static String allRequiredFiles[];
	static String delimiter;
	static Map <String,String> columnNumbers = new HashMap<String, String>();
	static Map <String,String> fileDelimiters;
	static int recordCount;
	static String configFilePath;
	static String mdmFlatFile;
	static Map <String,String> sources;
	static Map <String,String> sourceFile;
	static Map <String,String> fieldSequence;
	
	public static void setMDMConfigFilePath() throws Exception{
		ExcelUtils xlObj = new ExcelUtils();
		xlObj.setExcelFile(DriverScript.Path_Executable, Constants.Sheet_RunConfig);
		configFilePath = xlObj.getRunConfig("MDMConfigPath");
	}
	
	
	
	public static void appendRecords(Map<String, String> record, String delimiter) throws IOException{
		try (PrintWriter simFile = new PrintWriter(new FileWriter(mdmFlatFile, true));){
			String format = "%s"+delimiter;
			int fieldcount=record.size();
			System.out.println(fieldcount);
			for (int i=0;i<=fieldcount;i++){
				if(i<fieldcount){
					simFile.printf(format, record.get(String.valueOf(i+1)));
				}
				else{
					simFile.printf("\n");
				}
			}
//			simFile.flush();
		}
		catch (FileNotFoundException e){
			
		}	
	}
	
	public static void writeHeader(String recordPosition, int recordCount)throws IOException{
		try (PrintWriter simFile = new PrintWriter(new FileWriter(mdmFlatFile, true));){
			
				if(recordPosition.equals("1")){
					simFile.printf("%s|\n", recordCount);
				}
				else{
					simFile.printf("|%s\n", recordCount);
				}
			
		}
		catch (FileNotFoundException e){
			
		}
	}
	
	public static void writeFile() throws Exception{
		ExcelUtils xlObj = new ExcelUtils();
		setMDMConfigFilePath();
		xlObj.setExcelFile(configFilePath, "Source files");
		allRequiredFiles();
		System.out.println("FoundAllFiles");
		knowColumnNumberInFiles();
		for(String s: allRequiredFiles){
			System.out.println(s+" : "+columnNumbers.get(s));
		}
		
		
		
		
//		writeHeader("2",recordCount);
//		for (int i=1;i<=recordCount;i++){
//			appendRecords(fieldSequence,delimiter);
//		}
	}
	
	public static void setRecord (Map <String, String> record){
		record.put("1", "ABC");
		record.put("2", "G002");
	}
	
//	public static void checkFileHeader(){
//		String headerPosition = ExcelUtils.getCellData(ExcelUtils.getTargetRow("Sources", file, 0), 4, "Sources");
//	}
	
	public static void knowColumnNumberInFiles() throws Exception{
		ExcelUtils xlObj = new ExcelUtils();
		int rowCount = xlObj.setExcelFile(configFilePath, "Source files").getLastRowNum();
		for (int i=1;i<=rowCount;i++){
			columnNumbers.put(xlObj.getCellData(i, 0, "Source files"), xlObj.getCellData(i, 5, "Source files"));
		}
	}
	
	public static void knowFileDelimiter(String fileName) throws Exception{
		ExcelUtils xlObj = new ExcelUtils();
				switch (xlObj.getCellData(xlObj.getTargetRow("Source files", fileName, 0), 4, "Source files")){
				case "Pipe":
					delimiter = "|";
				case "comma":
					delimiter = ",";
				};
	}
	
	public static void allRequiredFiles() throws Exception{
		ExcelUtils xlObj = new ExcelUtils();
		int rowCount = xlObj.setExcelFile(configFilePath, "Source files").getLastRowNum();
		allRequiredFiles = new String[rowCount];
		for (int i=1;i<=rowCount;i++){
			allRequiredFiles[i-1]= xlObj.getSpecificCellData(i, 0, "Source files",configFilePath);
		}
	}
}
