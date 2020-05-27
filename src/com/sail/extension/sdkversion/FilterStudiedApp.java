package com.sail.extension.sdkversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.model.UpdateTable;

public class FilterStudiedApp {

	String UPDATE_DATA = ProjectConstants.STUDIED_APP_UPDATES;
	Map<String,ArrayList<UpdateTable>> appUpdatePerAppList = AdsInputDataLoader.readUpdateData(UPDATE_DATA);
	Map<String,UpdateTable> appUpdatePerUpdateList = AdsInputDataLoader.readUpdateDataPerUpdate(UPDATE_DATA);
	
	String SDK_VERSION_GENERATED_DATA = "/home/local/SAIL/ahsan/Documents/Sdk_version/Safwat_Approach/SDK_Version_Folder/SDK_Versions_Report.csv";
	String FINAL_SDK_VERSION_DATA = "/home/local/SAIL/ahsan/Documents/Sdk_version/Safwat_Approach/SDK_Version_Folder/Final_SDK_Version_Data.csv";
	
	Set<String> takenUpdateList = new HashSet<String>();
	public void readGenearatedRawSdkInformation() throws Exception{
		CsvReader reader = new CsvReader(SDK_VERSION_GENERATED_DATA);
		reader.readHeaders();
		
		CsvWriter writer = new CsvWriter(FINAL_SDK_VERSION_DATA);
		writer.write("Package_Name");
		writer.write("Version_Code");
		writer.write("Release_Date");
		writer.write("Min_SDK_Version");
		writer.write("Target_SDK_Version");
		writer.write("Max_SDK_Version");
		writer.endRecord();
		
		int totalUpdatesHasSdkInfo = 0;
		
		while(reader.readRecord()){
			//Release_Name,Application,Date,Version_Name,Version_Code,Min_SDK_Version,Target_SDK_Version,Max_SDK_Version

			String releaseName = reader.get("Release_Name");
			String applicationName = reader.get("Application");
			String releaseDate = reader.get("Date");
			String versionName = reader.get("Version_Name");
			String versionCode = reader.get("Version_Code");
			String minimumSdk = reader.get("Min_SDK_Version");
			String targetSdk = reader.get("Target_SDK_Version");
			String maxSdk = reader.get("Max_SDK_Version");
			
			
			String packageName = applicationName.substring(applicationName.lastIndexOf("/") + 1);
			
			
			String key =  packageName + "-" + versionCode;
			
			//System.out.println(releaseName);
			//System.out.println(key);
			
			if(!appUpdatePerUpdateList.containsKey(key)){
				continue;
			}
			
			if(takenUpdateList.contains(key)){
				System.out.println("Repeat: " + key +" " + appUpdatePerUpdateList.get(key).getRELEASE_DATE() );
				continue;
			}
			
			writer.write(packageName);
			writer.write(appUpdatePerUpdateList.get(key).getVERSION_CODE());
			writer.write(appUpdatePerUpdateList.get(key).getRELEASE_DATE());
			writer.write(minimumSdk);
			writer.write(targetSdk);
			writer.write(maxSdk);
			writer.endRecord();
			++totalUpdatesHasSdkInfo;
			
			
				
			takenUpdateList.add(key);
		}
		System.out.println("Total studied updates = " + appUpdatePerUpdateList.size());
		System.out.println("Total updates with Sdk info = " + totalUpdatesHasSdkInfo);
		writer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws Exception{
		FilterStudiedApp ob = new FilterStudiedApp();
		ob.readGenearatedRawSdkInformation();
		System.out.println("Program finishes successfully");
	}
}
