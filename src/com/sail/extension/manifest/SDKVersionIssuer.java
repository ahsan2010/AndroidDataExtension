package com.sail.extension.manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.model.UpdateTable;

public class SDKVersionIssuer {
	
	Map<String,String> mapActualPackageVersionList = new HashMap<String,String>();
	ArrayList<String> problemPackageNameList = new ArrayList<String>();
	
	String updatedSdkPath = "/home/local/SAIL/ahsan/Documents/Sdk_version/SDK_Versions_Updated/";
	List<String> codeString = Arrays.asList("0_10000","10001_15000","15001_20000","20001_30000","30001_40000","40001_50000","50001_60000","60001_Last");
	
	Set<String> finalUpdaetLinkSet = new HashSet<String>();
	
	
	public void readSDKVersionInfo(String path) throws Exception{
		CsvReader reader = new CsvReader(path);
		reader.readHeaders();
		while(reader.readRecord()){
			String packageName = reader.get("Package_Name");
			problemPackageNameList.add(packageName);
		}
		System.out.println("Successfully Read SDK Information");
	}
	
	public ArrayList<String> getAppUpdatesLink() {
		ArrayList<String> updateLink = new ArrayList<String>();
		try {
			CsvReader reader = new CsvReader(ProjectConstants.STUDIED_APP_UPDATES);
			reader.readHeaders();
			while (reader.readRecord()) {
				String packageName = reader.get("PACKAGE_NAME");
				String versionCode = reader.get("VERSION_CODE");
				String releaseDate = reader.get("RELEASE_DATE");
				releaseDate = releaseDate.replace("-", "_");

				String updateString = packageName + versionCode + "-" + packageName + "-" + versionCode;
				updateLink.add(updateString);

			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Total uppdate read ["+updateLink.size()+"]");
		return updateLink;
	}

	
	public ArrayList<String> getAppUpdatesLinkII() {
		ArrayList<String> updateLink = new ArrayList<String>();
		try {
			CsvReader reader = new CsvReader(ProjectConstants.STUDIED_APP_UPDATES);
			reader.readHeaders();
			while (reader.readRecord()) {
				String packageName = reader.get("PACKAGE_NAME");
				String versionCode = reader.get("VERSION_CODE");
				String releaseDate = reader.get("RELEASE_DATE");
				releaseDate = releaseDate.replace("-", "_");

				String updateString = packageName +"-" +  versionCode;
				updateLink.add(updateString);

			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Total uppdate read ["+updateLink.size()+"]");
		return updateLink;
	}
	
	
	public void fixingSDKIssue() throws Exception{
		String path = "/home/local/SAIL/ahsan/Documents/Sdk_version/SDK_Versions/SDK_version_20001_30000.csv";
		readSDKVersionInfo(path);
		Map<String,ArrayList<UpdateTable>> updateList = new HashMap<String,ArrayList<UpdateTable>>();
		ArrayList<String> updateLink = getAppUpdatesLink();
		
		int problem = 0;
		
		
		CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/Documents/Sdk_version/SDK_Versions_Updated/sdk_updated_20001_30000.csv");
		writer.write("Package_Name");
		writer.write("Version_Code");
		writer.write("Minimum_SDK_Version");
		writer.write("Target_SDK_Version");
		writer.endRecord();
		
		for(String updateKeyString : updateLink){
			String key = updateKeyString.split("-")[0];
			String packageName = updateKeyString.split("-")[1];
			String versionCode = updateKeyString.split("-")[2];
			
			for(String sdkInfo : problemPackageNameList){
				if(sdkInfo.contains(key)){
					String sdkVersionNumbers="";
					String targetSdk = "";
					String minimumSdk = "";
					if(sdkInfo.length() <= key.length() + 1){
						System.out.println("PROBLEM ");
						++problem;
					}else{
						sdkVersionNumbers = sdkInfo.substring(key.length());
					}
					if(sdkVersionNumbers.length() == 4){
						targetSdk += sdkVersionNumbers.charAt(2);targetSdk += sdkVersionNumbers.charAt(3);
						minimumSdk += sdkVersionNumbers.charAt(0);minimumSdk += sdkVersionNumbers.charAt(1);
						
					}
					if(sdkVersionNumbers.length() == 3){
						targetSdk += sdkVersionNumbers.charAt(1);targetSdk += sdkVersionNumbers.charAt(2);
						minimumSdk += sdkVersionNumbers.charAt(0);
					}
					if(sdkVersionNumbers.length() == 2){
						targetSdk += sdkVersionNumbers.charAt(1);
						minimumSdk += sdkVersionNumbers.charAt(0);
					}
					
					
					writer.write(packageName);
					writer.write(versionCode);
					writer.write(targetSdk);
					writer.write(minimumSdk);
					writer.endRecord();
					
					System.out.println(sdkVersionNumbers.length() + " " +sdkInfo +" "+ key + " " + targetSdk + " " + minimumSdk);
					//System.out.println(sdkVersionNumbers);
					break;
				}
			}

		}
		writer.close();
		System.out.println("Total Update: " + problemPackageNameList.size());
		System.out.println("Total problems ["+problem+"]");
	}
	
	public int checkSDKInfoCoveragePerFile(Set<String> updateLinkSet, int index, CsvWriter writer) throws Exception{
		
		int missing = 0;
		String path = updatedSdkPath +  "sdk_updated_" + codeString.get(index) + ".csv";
		CsvReader reader = new CsvReader(path);
		reader.readHeaders();
		int total = 0;
		while(reader.readRecord()){
			String packageName = reader.get("Package_Name");
			String versionCode = reader.get("Version_Code");
			String targetSdk = reader.get("Minimum_SDK_Version");
			String minimumSdk = reader.get("Target_SDK_Version");
			String key = packageName + "-" + versionCode;
			
			
			if(!finalUpdaetLinkSet.contains(key)){
				finalUpdaetLinkSet.add(key);
				writer.write(packageName);
				writer.write(versionCode);
				writer.write(minimumSdk);
				writer.write(targetSdk);
				writer.write("F");
				writer.endRecord();
			}
			++total;
			if(!updateLinkSet.contains(key)){
				++missing;
			}
		}
		
		System.out.println(codeString.get(index) + " Total = " + total + " Missing = " + missing);
		
		return missing;
		
	}
	
	public void checkSdkCoverageInfo() throws Exception{
		
		CsvWriter writer = new CsvWriter(updatedSdkPath + "Sdk_Final_List.csv");
		writer.write("Package_Name");
		writer.write("Version_Code");
		writer.write("Minimum_SDK_Version");
		writer.write("Target_SDK_Version");
		writer.write("Missing");
		writer.endRecord();
		
		ArrayList<String> updateLink = getAppUpdatesLinkII();
		Set<String> updateLinkSet = new HashSet<String>();
		for(String update : updateLink){
			//System.out.println(update);
			updateLinkSet.add(update);
		}
		for(int i = 0 ; i < codeString.size() ; i ++ ){
			checkSDKInfoCoveragePerFile(updateLinkSet, i, writer);
		}
		
		for(String key : updateLinkSet){
			if(!finalUpdaetLinkSet.contains(key)){
				writer.write(key.split("-")[0]);
				writer.write(key.split("-")[1]);
				writer.write("");
				writer.write("");
				writer.write("T");
				writer.endRecord();
			}
		}
		
		System.out.println("Total in our data = " + updateLinkSet.size());
		System.out.println("Total we generate SDK = " + finalUpdaetLinkSet.size());
		
		
		writer.close();
	}
	
	
	public static void main(String[] args) throws Exception{
		SDKVersionIssuer ob = new SDKVersionIssuer();
		ob.checkSdkCoverageInfo();
		System.out.println("Program finishes successfully");
	}

}
