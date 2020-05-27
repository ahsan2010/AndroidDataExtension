package com.sail.test;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.DateUtil;

public class BasicInfoStudiedApps {

	Set<String> appHasUpdatesBeforeStartDate = new HashSet<String>();
	Set<String> studiedAppList = new HashSet<String>();
	public void readAppUpdates() throws Exception{
		try{
			CsvReader reader = new CsvReader(ProjectConstants.STUDIED_APP_UPDATES);
			reader.readHeaders();
			while(reader.readRecord()){
				String packageName = reader.get("PACKAGE_NAME");
				String versionCode = reader.get("VERSION_CODE");
				String releaseDate = reader.get("RELEASE_DATE");				
				//releaseDate = releaseDate.replace("-","_");
				
				DateTime releaseDateTime = DateUtil.formatterWithHyphen.parseDateTime(releaseDate);
				
				if(releaseDateTime.isBefore(ProjectConstants.EPERIMENT_START_TIME) || releaseDateTime.equals(ProjectConstants.EPERIMENT_START_TIME)){
					appHasUpdatesBeforeStartDate.add(packageName);
				}
				studiedAppList.add(packageName);
			}
		System.out.println("Total Studied Apps ["+studiedAppList.size()+"]");
		System.out.println("Apps has atleast one update at the start of the study period ["+appHasUpdatesBeforeStartDate.size()+"]");
		reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		BasicInfoStudiedApps ob = new BasicInfoStudiedApps();
		ob.readAppUpdates();
		System.out.println("Program finishes successfully");
	}
}
