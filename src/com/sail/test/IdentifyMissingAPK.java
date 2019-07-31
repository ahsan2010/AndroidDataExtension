package com.sail.test;

import java.io.File;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;

public class IdentifyMissingAPK {

	ArrayList<String> updateLink = new ArrayList<String>();
	
	public void readAppUpdateData() throws Exception{
		CsvReader reader = new CsvReader(ProjectConstants.APP_UPDATES_SHORT_FILE);
		reader.readHeaders();
		while(reader.readRecord()){
			String packageName = reader.get("PACKAGE_NAME");
			String versionCode = reader.get("VERSION_CODE");
			String releaseDate = reader.get("RELEASE_DATE");				
			releaseDate = releaseDate.replace("-","_");
			
			String updateString = packageName +"-" + versionCode + "-" + releaseDate;
			updateLink.add(updateString);
		}
	}
	
	public void investigateAPK() throws Exception{
		ArrayList<String> missingAPKList = new ArrayList<String>();
		ArrayList<String> existingAPKList = new ArrayList<String>();
		readAppUpdateData();
		for(int i = 0 ; i < updateLink.size() ; i ++){
			String fileLocation = ProjectConstants.APK_LOCATION + updateLink.get(i)+".apk";
			System.out.println(fileLocation);
			File f = new File(fileLocation);
			if(f.exists() && !f.isDirectory()) { 
				existingAPKList.add(updateLink.get(i));
			}else{
				missingAPKList.add(updateLink.get(i));
			}
		}
		System.out.println("Total APK ["+updateLink.size()+"]");
		System.out.println("Missing APK ["+missingAPKList.size()+"]");
		System.out.println("Existing APK ["+existingAPKList.size()+"]");
		
		//writeMissingAPKList(missingAPKList);
	}
	
	public void writeMissingAPKList(ArrayList<String> missingAPKList ) throws Exception{
		CsvWriter writer = new CsvWriter(ProjectConstants.JAR_CONVERSION_LOG+"MissingAPK.csv");
		writer.write("Package_Name");
		writer.write("Version_Code");
		writer.write("Release_Date");
		writer.endRecord();
		
		for(String missingAPK : missingAPKList){
			String terms[] = missingAPK.split("-");
			writer.write(terms[0].trim());
			writer.write(terms[1].trim());
			writer.write(terms[2].replace("_", "-").trim());
			writer.endRecord();
		}
		
		writer.close();

	}
	
	public static void main(String[] args) throws Exception{
		IdentifyMissingAPK ob = new IdentifyMissingAPK();
		ob.investigateAPK();
		System.out.println("Program finishes successfully");
	}
}
