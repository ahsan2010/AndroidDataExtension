package com.sail.extension.manifest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.model.UpdateTable;

public class SdkVersionDataCheckingAppAnalysis {

	String MISSING_SDK_INFO_PATH = "/home/local/SAIL/ahsan/Documents/Sdk_version/SDK_Versions_Updated/missingSdkInfo.csv";
	String SDKINFO_PATH = "/home/local/SAIL/ahsan/Documents/Sdk_version/SDK_Versions_Updated/Sdk_Final_List.csv";
	String UPDATE_PATH = ProjectConstants.STUDIED_APP_UPDATES;
	Map<String,ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(UPDATE_PATH);
	Map<String,String> sdkVersionInfo = new HashMap<String,String>();
	
	
	
	public void readFullSDKVersionInfo() throws Exception{
		CsvReader reader = new CsvReader(SDKINFO_PATH);
		reader.readHeaders();
		while(reader.readRecord()){
			String packageName = reader.get("Package_Name");
			String versionCode = reader.get("Version_Code");
			String minimumSdkVersion = reader.get("Minimum_SDK_Version");
			String targetSdkVersion = reader.get("Target_SDK_Version");
			String missingFlag = reader.get("Missing");
			
			String key = packageName + "-" + versionCode;
			
			sdkVersionInfo.put(key, minimumSdkVersion + "-" + targetSdkVersion + "-" + missingFlag);
			
		}
	}
	
	public void sdkInfoPerAppChecking() throws Exception{
		
		
		CsvWriter writer = new CsvWriter(MISSING_SDK_INFO_PATH);
		writer.write("Package_Name");
		writer.write("Version_Code");
		writer.write("Release_Date");
		writer.write("Minimum_SDK_Version");
		writer.write("Target_SDK_Version");
		writer.write("Missing");
		
		readFullSDKVersionInfo();
		int appUpdateHasSdkInfo = 0;
		int appUpdateMissingSdkInfo = 0;
		int totalApp = 0;
		for(String appName : appUpdates.keySet()){
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			boolean flagAllUpdatesCover = true;
			++totalApp;
			
			for(int i = 0 ; i < updates.size(); i ++){
				UpdateTable update = updates.get(i);
				
				String key = update.getPACKAGE_NAME() + "-" + update.getVERSION_CODE();
				
				if(!sdkVersionInfo.containsKey(key)){
					System.out.println("Missing update: " + key);
				}
				String value = sdkVersionInfo.get(key);
				String minSdk = value.split("-")[0];
				String targetSdk = value.split("-")[1];
				String missingFlag = value.split("-")[2];
				System.out.println(key + " " + value);
				if(missingFlag.equals("T") || minSdk.trim().length() <= 0 || targetSdk.trim().length() <= 0){
					flagAllUpdatesCover = false;
					
					writer.write(update.getPACKAGE_NAME());
					writer.write(update.getVERSION_CODE());
					writer.write(update.getRELEASE_DATE());
					writer.write(minSdk);
					writer.write(targetSdk);
					writer.write(missingFlag);
					writer.endRecord();
					//System.out.println("Missing "+key+ " " + (i+1) + " " + updates.size());
					//break;
				}else{
					//System.out.println("Success "+key+ " " + (i+1) + " " + updates.size());
				}
				
			}
			
			if(flagAllUpdatesCover){
				++ appUpdateHasSdkInfo;
				
			}else{
				++ appUpdateMissingSdkInfo;
			}
		}
		writer.close();
		System.out.println("Total studied apps = ["+totalApp+"]");
		System.out.println("App has Sdk Info for all its updates = ["+appUpdateHasSdkInfo+"]");
		System.out.println("App Miss Sdk Info for atleast one  update = ["+appUpdateMissingSdkInfo+"]");
	}
	
	
	public static void main(String[] args) throws Exception{
		SdkVersionDataCheckingAppAnalysis ob = new SdkVersionDataCheckingAppAnalysis();
		ob.sdkInfoPerAppChecking();
		System.out.println("Program finishess succssfully");
	}
}
