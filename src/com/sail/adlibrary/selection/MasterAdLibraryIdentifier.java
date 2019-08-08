package com.sail.adlibrary.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.model.UpdateTable;

public class MasterAdLibraryIdentifier {
	
	int numberOfThreads = 10;
	
	
	public ArrayList<String> getAppUpdatesLink(){
		ArrayList<String> updateLink = new ArrayList<String>();
		try{
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
		reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return updateLink;
	}
	
	
	public void runTheWorker(){
		ArrayList<String> updateLink = getAppUpdatesLink();
		int start = 0;
		int difference = (int)((updateLink.size())/numberOfThreads);
		
		int END_INDEX = updateLink.size();
		
		System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Jars ["+updateLink.size()+"]");
		System.out.println("Difference ["+difference+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	
		long startTime = System.currentTimeMillis();
		for(int i = 1 ; i <= numberOfThreads ; i ++ ){
			
			if( i == numberOfThreads){
				Runnable worker = new ChildAdLibraryIdentifier(updateLink,start, END_INDEX,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(END_INDEX)+"]");
				executor.execute(worker);	
			}else{
				Runnable worker = new ChildAdLibraryIdentifier(updateLink, start, start+difference,i);
				System.out.println("*Thread Start Position ["+ start +"]" +" " + " End Position ["+(start+difference)+"]");
				start+= difference;
				executor.execute(worker);				
			}
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
        }
        long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time ["+totalTime/(1000)+"] seconds");	
		System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
        System.out.println("Finished all threads");
	}	
	public static void main(String[] args) {
		MasterAdLibraryIdentifier ob = new MasterAdLibraryIdentifier();
		ob.runTheWorker();
	}
	
}
