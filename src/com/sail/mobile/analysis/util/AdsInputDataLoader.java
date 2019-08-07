package com.sail.mobile.analysis.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;

import com.sail.mobile.model.UpdateTable;

public class AdsInputDataLoader
{
	
	public static HashMap<String,ArrayList<UpdateTable>> readUpdateData(String fileName){
		HashMap<String,ArrayList<UpdateTable>> appUpdateRecords = new HashMap<String,ArrayList<UpdateTable>>();
		
		try{
			int inputUpdatesCount = 0;
			CsvReader reader = new CsvReader(fileName);
			reader.readHeaders();
			while (reader.readRecord())
			{
				inputUpdatesCount++;
				UpdateTable update = new UpdateTable();
				update.setAPP_ID(reader.get("APP_ID").trim());
				update.setAPP_UPDATE_ID(reader.get("APP_UPDATE_ID").trim());
				update.setPACKAGE_NAME(reader.get("PACKAGE_NAME").trim());
				update.setVERSION_CODE(reader.get("VERSION_CODE").trim());
				update.setRELEASE_DATE(reader.get("RELEASE_DATE").trim());
				update.setAPK_SIZE(reader.get("APK_FILE_SIZE").trim());
				
				if(appUpdateRecords.containsKey(update.getPACKAGE_NAME())){
					appUpdateRecords.get(update.getPACKAGE_NAME()).add(update);
				}else{
					ArrayList<UpdateTable> updaetList = new ArrayList<UpdateTable>();
					updaetList.add(update);
					appUpdateRecords.put(update.getPACKAGE_NAME(), updaetList);
				}
				
			}
			System.out.println("Initially loaded ["+appUpdateRecords.size()+"] apps with [" + inputUpdatesCount + "] updates.");
			SortUtil.sortUpdatesByReleaseDate(appUpdateRecords);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return appUpdateRecords;
	}
	
	public static HashMap<String,UpdateTable> readUpdateDataPerUpdate(String fileName){
		HashMap<String,UpdateTable> appUpdateRecords = new HashMap<String,UpdateTable>();
		
		try{
			int inputUpdatesCount = 0;
			CsvReader reader = new CsvReader(fileName);
			reader.readHeaders();
			while (reader.readRecord())
			{
				inputUpdatesCount++;
				UpdateTable update = new UpdateTable();
				update.setAPP_ID(reader.get("APP_ID").trim());
				update.setAPP_UPDATE_ID(reader.get("APP_UPDATE_ID").trim());
				update.setPACKAGE_NAME(reader.get("PACKAGE_NAME").trim());
				update.setVERSION_CODE(reader.get("VERSION_CODE").trim());
				update.setRELEASE_DATE(reader.get("RELEASE_DATE").trim());
				update.setAPK_SIZE(reader.get("APK_FILE_SIZE").trim());
				
				appUpdateRecords.put(update.getPACKAGE_NAME()+"-"+update.getVERSION_CODE(),update);
				
			}
			System.out.println("Initially loaded ["+appUpdateRecords.size()+"] apps with [" + inputUpdatesCount + "] updates.");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return appUpdateRecords;
	}

	public static Set<String> readErrorJarList(String path){
		Set<String> errorJars = new HashSet<String>();
		
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			while((line = br.readLine()) != null){
				String jarName = line.substring(line.lastIndexOf("/")+1,line.lastIndexOf(".")).trim();
				errorJars.add(jarName);
			}
			return errorJars;			
		}catch(Exception e){
			e.printStackTrace();
		}		
		return errorJars;
	}
	
	public static void main(String arg[])
	{
		AdsInputDataLoader adsInputDataLoader = new AdsInputDataLoader();
		//adsInputDataLoader.loadData();
		//adsInputDataLoader.readErrorJarList(Constants.ERROR_JAR_LIST);
		//adsInputDataLoader.loadInitialStudyAppsName();
		
	}

	
	
	
}
