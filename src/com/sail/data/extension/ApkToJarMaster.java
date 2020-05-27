package com.sail.data.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;

public class ApkToJarMaster {

	int numberOfThreads = 1;
	public Set<String> existingJarConersion = new HashSet<String>();
	public ArrayList<String> apkNeedsToCompile = new ArrayList<String>();
	
	public ArrayList<String> getAppUpdatesLink(){
		ArrayList<String> updateLink = new ArrayList<String>();
		try{
			CsvReader reader = new CsvReader(ProjectConstants.STUDIED_APP_UPDATES);
			reader.readHeaders();
			while(reader.readRecord()){
				String packageName = reader.get("PACKAGE_NAME");
				String versionCode = reader.get("VERSION_CODE");
				String releaseDate = reader.get("RELEASE_DATE");				
				releaseDate = releaseDate.replace("-","_");
				
				String updateString = packageName +"-" + versionCode + "-" + releaseDate;
				updateLink.add(updateString);
				
			}
		reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return updateLink;
	}
	
	public void checkExistingJar(){
		int totalExistingJars = 0;
		ArrayList<String> updateLink = getAppUpdatesLink();
		for(int i = 0 ; i < updateLink.size() ; i ++){
			
			String updateString = updateLink.get(i);
			String fileLocation = ProjectConstants.jarOutputPath + updateString + "-dex2jar.jar";
			File f = new File(fileLocation);
			if(f.exists() && !f.isDirectory()) { 
				existingJarConersion.add(updateString);
				++totalExistingJars;
			}else{
				apkNeedsToCompile.add(updateString);
			}
			
			System.out.println("Check complete " + (i+1) + " " + fileLocation + " " + totalExistingJars);
		}
		System.out.println("Total existing Jars = " + totalExistingJars +" " + existingJarConersion.size());
	}
	
	public void runTheWorker(){
		ArrayList<String> updateLink = apkNeedsToCompile;
		//ArrayList<String> updateLink = getAppUpdatesLink();
		int start = 0;
		int end = apkNeedsToCompile.size();
		int updatesNeedToAnalyze = end - start + 1;
		
		/*if((start + updatesNeedToAnalyze) > updateLink.size()){	
			updatesNeedToAnalyze = updateLink.size() - start;
		}*/
		
		int difference = (int)((updatesNeedToAnalyze)/numberOfThreads);
		
		int END_INDEX = end;
		
		System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Jars ["+updateLink.size()+"]");
		System.out.println("Difference ["+difference+"]");
		System.out.println("Update needs to analyze ["+updatesNeedToAnalyze+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
		long startTime = System.currentTimeMillis();
		for(int i = 1 ; i <= numberOfThreads ; i ++ ){
			
			if( i == numberOfThreads){
				Runnable worker = new ApkToJarWorkers(updateLink,start, END_INDEX,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(END_INDEX)+"]");
				executor.execute(worker);
	
			}else{
				Runnable worker = new ApkToJarWorkers(updateLink, start, start+difference,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(start+difference)+"]");
				start+= difference;
				executor.execute(worker);
				
			}
		}
		executor.shutdown();
		try {
			  executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			}
		catch(Exception e){
			e.printStackTrace();
		}
        long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time ["+totalTime/(1000)+"] seconds");	
		System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
        System.out.println("Finished all threads");
	}	
	
	
	public static void main(String[] args) throws Exception{
		ApkToJarMaster ob = new ApkToJarMaster();
		ob.checkExistingJar();
		ob.runTheWorker();
		System.out.println("Program finishes successfully");
	}
	
}
