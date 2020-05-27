package com.sail.signature.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.bcel.Constants;

import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.analysis.util.FileUtil;
import com.sail.mobile.model.UpdateTable;

public class ClassSignatureGeneratorMaster {
	
	//public final String UPDATE_PATH = Constants.root + "/scripts/studied_app_updates_final_short.csv";
	HashMap<String,ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(ProjectConstants.STUDIED_APP_UPDATES);
	public static Map<String,String> varifiedAdPackageMapGroup = FileUtil.readVerfiedAdList(ProjectConstants.VARIFIED_AD_PACKAGE_LIST);
	
	int numberOfThreads = 10;
	int start = 0;
	int totalUpdates = 0;
	
	
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
		System.out.println("Total updates ["+updateLink.size()+"]");
		return updateLink;
	}
	
	public void runTheWorker(){
		ArrayList<String> updateLink = getAppUpdatesLink();
		int start = 0;
		int difference = (int)(updateLink.size()/numberOfThreads);
		
		int END_INDEX = updateLink.size();
		
		System.out.println("Total Threads ["+numberOfThreads+"]");
		System.out.println("Total Number of Jars ["+updateLink.size()+"]");
		System.out.println("Difference ["+difference+"]");
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);		
		
		long startTime = System.currentTimeMillis();
		for(int i = 1 ; i <= numberOfThreads ; i ++ ){
			
			if( i == numberOfThreads){
				Runnable worker = new ChildSignatureGenerator(updateLink, start, END_INDEX, i);
				executor.execute(worker);
	
			}else{
				Runnable worker = new ChildSignatureGenerator(updateLink, start, start+difference, i);
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
		ClassSignatureGeneratorMaster ob = new ClassSignatureGeneratorMaster();
		/*ob.numberOfThreads = Integer.parseInt(args[0]);
		ob.start = Integer.parseInt(args[1]);
		ob.totalUpdates = Integer.parseInt(args[2]);*/
		
		ob.runTheWorker();
		System.out.println("Program finishes successfully");
	}
}
