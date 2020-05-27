package com.sail.app.update.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.DateUtil;
import com.sail.mobile.model.UpdateTable;

public class AppUpdateFilter {

	Set<String> candidateAppSelection = new HashSet<String>();
	Set<String> missingAPKList = new HashSet<String>();
	Map<String,ArrayList<UpdateTable>> appUpdates = new HashMap<String,ArrayList<UpdateTable>>();
	Map<String,ArrayList<UpdateTable>> selectedAppUpdates = new HashMap<String,ArrayList<UpdateTable>>();
	
	public void filterAppUpdates() throws Exception{
		int totalSelectedAppUpdates = 0;
		int missingAPKInStudiedApps = 0;
		for(String appName : appUpdates.keySet()){
			if(!candidateAppSelection.contains(appName)){
				continue;
			}
			/*if(!appName.equals("com.google.android.gm")){
				continue;
			}
			*/
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			//System.out.println(appName + " Updates ["+updates.size()+"]");
			for(int i = updates.size() - 1 ; i >=0 ; i--){
				UpdateTable update = updates.get(i);
				String updateKey = update.getPACKAGE_NAME() + "-" + update.getVERSION_CODE();
				//System.out.println(i + " " + update.getRELEASE_DATE());
				if(missingAPKList.contains(updateKey)){
					++missingAPKInStudiedApps;
					continue;
				}
				DateTime releaseDateTime = DateUtil.formatterWithHyphen.parseDateTime(update.getRELEASE_DATE());
				
				if(!selectedAppUpdates.containsKey(appName)){
					selectedAppUpdates.put(appName, new ArrayList<UpdateTable>());
				}
				selectedAppUpdates.get(appName).add(update);
				++totalSelectedAppUpdates;
				if(releaseDateTime.isBefore(ProjectConstants.EPERIMENT_START_TIME)){	
					break;
				}
			}
			
		}
		for(String appName : selectedAppUpdates.keySet()){
			selectedAppUpdates.get(appName).sort(new Comparator<UpdateTable>() {

				@Override
				public int compare(UpdateTable o1, UpdateTable o2) {
					// TODO Auto-generated method stub
					DateTime firstDate = DateUtil.formatterWithHyphen.parseDateTime(o1.getRELEASE_DATE());
					DateTime secondDate = DateUtil.formatterWithHyphen.parseDateTime(o2.getRELEASE_DATE());
					
					return firstDate.compareTo(secondDate);
				}
			});
		}
		int totalInitialUpdates = 0;
		for(String appName : appUpdates.keySet()){
			totalInitialUpdates += appUpdates.get(appName).size();
		}
		
		
		for(String appName : selectedAppUpdates.keySet()){
			System.out.println(appName +" "+ appUpdates.get(appName).size() + " "+ selectedAppUpdates.get(appName).size()+ " "+ selectedAppUpdates.get(appName).get(0).getRELEASE_DATE() + " " + selectedAppUpdates.get(appName).get(selectedAppUpdates.get(appName).size()-1).getRELEASE_DATE());
		}
		System.out.println("Total available updtes ["+totalInitialUpdates+"]");
		System.out.println("Total missing APK List ["+missingAPKList.size()+"}");
		System.out.println("Total selected app updates ["+totalSelectedAppUpdates+"]");
		System.out.println("Missing APK List in the Studied Apps ["+missingAPKInStudiedApps+"]");
	}
	
	public void readAvailableUpdateOfApps () throws Exception{
		CsvReader reader = new CsvReader("/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/scripts/Total_Updates_Apps_Top_2016_UpTo_April_2019.csv");
		reader.readHeaders();
		while(reader.readRecord()){
			String appId = reader.get("APP_ID");
			String packageName = reader.get("PACKAGE_NAME");
			String versionCode = reader.get("VERSION_CODE");
			String releaseDate = reader.get("RELEASE_DATE");
			String apkSize = reader.get("APK_FILE_SIZE");
			String appUpdateId = reader.get("APP_UPDATE_ID");
			
			UpdateTable update = new UpdateTable();
			update.setPACKAGE_NAME(packageName);
			update.setAPP_ID(appId);
			update.setAPP_UPDATE_ID(appUpdateId);
			update.setVERSION_CODE(versionCode);
			update.setRELEASE_DATE(releaseDate);
			update.setAPK_SIZE(apkSize);
			
			if(!appUpdates.containsKey(packageName)){
				appUpdates.put(packageName, new ArrayList<UpdateTable>());
			}
			appUpdates.get(packageName).add(update);			
		}
		
		for(String appName : appUpdates.keySet()){
			appUpdates.get(appName).sort(new Comparator<UpdateTable>() {

				@Override
				public int compare(UpdateTable o1, UpdateTable o2) {
					// TODO Auto-generated method stub
					DateTime firstDate = DateUtil.formatterWithHyphen.parseDateTime(o1.getRELEASE_DATE());
					DateTime secondDate = DateUtil.formatterWithHyphen.parseDateTime(o2.getRELEASE_DATE());
					
					return firstDate.compareTo(secondDate);
				}
			});
		}
		
	}
	
	public void readMissingAPKList() throws Exception{
		CsvReader reader = new CsvReader(ProjectConstants.ROOT+"/scripts/"+"MissingAPK_Top_APP_2016_2019.csv");
		reader.readHeaders();
		while(reader.readRecord()){
			String packageName = reader.get("Package_Name");
			String versionCode = reader.get("Version_Code");
			String updateKey = packageName + "-" + versionCode;
			missingAPKList.add(updateKey);
		}
		
		reader.close();
	}
	
	public void readCandidateApps() throws Exception{
		CsvReader reader = new CsvReader("/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/scripts/studied_app_updates_final_2016.csv");
		reader.readHeaders();
		while(reader.readRecord()){
			String packageName = reader.get("PACKAGE_NAME");
			candidateAppSelection.add(packageName);
		}
		System.out.println("Total candidate App["+candidateAppSelection.size()+"]");
	}
	
	public void writeSelectedAppUpdates() throws Exception{
		CsvWriter writer = new CsvWriter(ProjectConstants.STUDIED_APP_UPDATES);
		writer.write("APP_ID");
		writer.write("APP_UPDATE_ID");
		writer.write("PACKAGE_NAME");
		writer.write("VERSION_CODE");
		writer.write("RELEASE_DATE");
		writer.write("APK_FILE_SIZE");
		writer.endRecord();
		
		for(String appName : selectedAppUpdates.keySet()){
			ArrayList<UpdateTable> updates = selectedAppUpdates.get(appName);
			for(int i = 0 ; i < updates.size() ; i ++){
				UpdateTable update = updates.get(i);
				writer.write(update.getAPP_ID());
				writer.write(update.getAPP_UPDATE_ID());
				writer.write(update.getPACKAGE_NAME());
				writer.write(update.getVERSION_CODE());
				writer.write(update.getRELEASE_DATE());
				writer.write(update.getAPK_SIZE());
				writer.endRecord();
			}
		}
		writer.close();
	}
	/*
	 * 
	 * Total available updtes [80559]
Total missing APK List [11931}
Total selected app updates [61494]
Missing APK List in the Studied Apps [707]
	 */
	public static void main(String[] args) throws Exception{
		AppUpdateFilter ob = new AppUpdateFilter();
		ob.readMissingAPKList();
		ob.readCandidateApps();
		ob.readAvailableUpdateOfApps();
		ob.filterAppUpdates();
		ob.writeSelectedAppUpdates();
		System.out.println("Program Finish Successfully");
	}
}
