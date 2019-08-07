package com.sail.adlibrary.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.model.UpdateTable;

public class MasterAdLibraryIdentifier {
	//public final String VALID_UPDATE_PATH ="/home/local/SAIL/ahsan/Documents/AdLibraryAnalysis/ROOT/scripts/studied_app_Short.csv" ;
	public final String VALID_UPDATE_PATH = "/home/local/SAIL/ahsan/Documents/AdLibraryAnalysis/ROOT/scripts/studied_app_updates_final_2016.csv";
	HashMap<String,ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(VALID_UPDATE_PATH);
	int numberOfThreads = 35;
	
	
	public ArrayList<String> getAppUpdatesLink(){
		ArrayList<String> updateLink = new ArrayList<String>();
		Set<String> appList = new HashSet<String>();
		appList.addAll(appUpdates.keySet());
		for(String appName : appList){
			for(UpdateTable update : appUpdates.get(appName)){
				String key = appName+"-"+update.getVERSION_CODE()+"-"+update.getRELEASE_DATE().replace("-", "_");
				updateLink.add(key);
			}
		}
		System.out.println("Total updates need to be decomipled ["+updateLink.size()+"]");
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
		System.out.println("Total Time ["+totalTime/(1000*60)+"] minutes");		
        System.out.println("Finished all threads");
	}	
	public static void main(String[] args) {
		MasterAdLibraryIdentifier ob = new MasterAdLibraryIdentifier();
		ob.runTheWorker();
	}
	
}
