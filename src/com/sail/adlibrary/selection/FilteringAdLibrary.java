package com.sail.adlibrary.selection;

import java.util.HashSet;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.TextUtil;

public class FilteringAdLibrary {

	public int THRESHOLD_CLASS = 20;
	
	public Set<String> filterAdLib(String libClassCountList){
		Set<String> selectedAdLibs = new HashSet<String>();
		if(libClassCountList.length() <=0 ){
			return selectedAdLibs;
		}
		for(String lib : libClassCountList.split("-")){
			String libInfo[] = lib.split(">");
			//System.out.println(lib);
			int totalClass = Integer.parseInt(libInfo[1]);
			if(totalClass > THRESHOLD_CLASS){
				selectedAdLibs.add(libInfo[0]);
			}
		}
		
		return selectedAdLibs;
	}
	
	public void filteringAdLibs() throws Exception{
		CsvReader reader = new CsvReader(ProjectConstants.ADS_UPDATE_DATA_PATH_OLD);
		reader.readHeaders();
		
		CsvWriter writer = new CsvWriter(ProjectConstants.ADS_UPDATE_DATA_PATH);
		writer.write("App_Name");
		writer.write("Version_Code");
		writer.write("Release_Date");
		writer.write("Total_Ads");
		writer.write("List_Of_Ads");
		writer.write("Total_Ads_Import");
		writer.write("Ads_Class_Count");
		writer.endRecord();
		
		while(reader.readRecord()){
			String appName = reader.get("App_Name");
			String versionCode = reader.get("Version_Code");
			String releaseDate = reader.get("Release_Date");
			String totalAds = reader.get("Total_Ads");
			String listOfAds = reader.get("List_Of_Ads");
			String totalAdsImport = reader.get("Total_Ads_Import");
			String totalAdsClass = reader.get("Ads_Class_Count");
			
			Set<String> filteredAdLib = filterAdLib(totalAdsClass);
			
			writer.write(appName);
			writer.write(versionCode);
			writer.write(releaseDate);
			writer.write(Integer.toString(filteredAdLib.size()));
			writer.write(TextUtil.setToString(filteredAdLib));
			writer.write(totalAdsImport);
			writer.write(totalAdsClass);
			writer.endRecord();
			
			if(Integer.parseInt(totalAds) != filteredAdLib.size()){
				System.out.println("Mismatch " + TextUtil.setToString(filteredAdLib) + " " + listOfAds);
			}
			
		}
		writer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws Exception{
		FilteringAdLibrary ob = new FilteringAdLibrary();
		ob.filteringAdLibs();
		System.out.println("Program finishes successfully");
	}
}
