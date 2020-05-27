package com.sail.adlibrayry.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.Constants;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.analysis.util.DateUtil;
import com.sail.mobile.analysis.util.FileUtil;
import com.sail.mobile.model.AdInformation;
import com.sail.mobile.model.AppTable;
import com.sail.mobile.model.UpdateTable;

public class MigrationAnalyzer {

	public final String UPDATE_PATH = ProjectConstants.STUDIED_APP_UPDATES;
	public final String AD_LIBRARY_MAP = ProjectConstants.VARIFIED_AD_PACKAGE_LIST;
	
	public final String AD_MIGRATION_TABLE = ProjectConstants.AD_MIGRATION_TABLE;
	public final String AD_MIGRATION_HEATMAP = ProjectConstants.AD_MIGRATION_HEATMAP;
	public final String AD_MIGRATION_PER_APP_UPDATE = ProjectConstants.AD_MIGRATION_PER_APP_UPDATE;
	public final String AD_SURVIVAL_TIME = ProjectConstants.AD_SURVIVAL_TIME;
	public final String AD_ROLL_BACK_TIME = ProjectConstants.AD_ROLL_BACK_TIME;
	public final String AD_ADD_TIME = ProjectConstants.AD_ADD_TIME;
	public final String AD_REMOVE_TIME = ProjectConstants.AD_REMOVE_TIME;

	int adMigration[][] = new int[70][70];
	ArrayList<String> listOfAllAdLibraries = new ArrayList<String>();
	Map<String, AdInformation> appUseAd = FileUtil.readAdLibraryInformationII();
	HashMap<String, ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(UPDATE_PATH);
	Map<String, String> varifiedAdPackageMapGroup = FileUtil.readVerfiedAdList(AD_LIBRARY_MAP);

	
	public static Map<String, AppTable> appsData = FileUtil.readAppData();

	public static Set<String> selectedCategories = new HashSet<String>(
			Arrays.asList("BOOKS_AND_REFERENCE", "BUSINESS", "COMICS", "COMMUNICATION", "EDUCATION", "ENTERTAINMENT",
					"GAME", "HEALTH_AND_FITNESS", "LIBRARIES_AND_DEMO", "LIFESTYLE", "MAPS_AND_NAVIGATION", "MEDICAL",
					"MUSIC_AND_AUDIO", "NEWS_AND_MAGAZINES", "PERSONALIZATION", "PHOTOGRAPHY", "PRODUCTIVITY",
					"SHOPPING", "SOCIAL", "SPORTS", "TOOLS", "TRAVEL_AND_LOCAL", "VIDEO_PLAYERS", "WEATHER"));
	
	public void initializeListofAds() {
		Set<String> adsList = new HashSet<String>();
		adsList.addAll(varifiedAdPackageMapGroup.values());
		listOfAllAdLibraries.addAll(adsList);
		System.out.println("Number of analyzed ad libraries [" + listOfAllAdLibraries.size() + "]");
	}

	public boolean isUpdateHasAdLibraries(String adKey, Map<String, AdInformation> appUseAd) {
		
		if (appUseAd.containsKey(adKey)){
			return true;
		}		
		return false;
	}

	public void writeAdMigrationHeatMap() throws Exception {
		CsvWriter writerHeatMap = new CsvWriter(AD_MIGRATION_HEATMAP);
		CsvWriter writerTable = new CsvWriter(AD_MIGRATION_TABLE);

		writerHeatMap.write("From");
		writerHeatMap.write("To");
		writerHeatMap.write("Count");
		writerHeatMap.endRecord();

		writerTable.write("AdNames");
		for (int i = 0; i < listOfAllAdLibraries.size(); i++) {
			writerTable.write(listOfAllAdLibraries.get(i));
		}
		writerTable.endRecord();

		for (int i = 0; i < listOfAllAdLibraries.size(); i++) {
			writerTable.write(listOfAllAdLibraries.get(i));
			for (int j = 0; j < listOfAllAdLibraries.size(); j++) {
				writerTable.write(Integer.toString(adMigration[i][j]));
				writerHeatMap.write(listOfAllAdLibraries.get(i));
				writerHeatMap.write(listOfAllAdLibraries.get(j));
				writerHeatMap.write(Integer.toString(adMigration[i][j]));
				writerHeatMap.endRecord();
			}
			writerTable.endRecord();
		}
		writerHeatMap.close();
		writerTable.close();
	}
	
	public int helperGetValue(Map<String,Integer> m, String app){
		int v = 0;
		if(m.containsKey(app)){
			v = m.get(app);
		}
		return v;
	}
	
	
	public void writeAdSurvivalTime(Map<String,ArrayList<Integer>> adSurvivalTime, String path){
		try{
			
			CsvWriter writer = new CsvWriter(path);
			writer.write("Ad_Library");
			writer.write("Duration");
			writer.endRecord();
			
			for(String adLibraryName : adSurvivalTime.keySet()){
				for(Integer days : adSurvivalTime.get(adLibraryName)){
					writer.write(adLibraryName);
					writer.write(Integer.toString(days));
					writer.endRecord();
				}
			}
			writer.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void writeMigrationAnalysisPerUpdate(
			Map<String,Integer> appFrequenceyAddition,
			Map<String,Integer> appFrequenceyRemoval,
			Map<String,Integer> appFrequenceyMigration,
			Map<String,Integer> appFrequenceyRollback) throws Exception{
		
		Set<String> appInvolveMigrationAnalysis = new HashSet<String>();
		appInvolveMigrationAnalysis.addAll(appFrequenceyAddition.keySet());
		appInvolveMigrationAnalysis.addAll(appFrequenceyMigration.keySet());
		appInvolveMigrationAnalysis.addAll(appFrequenceyRollback.keySet());
		appInvolveMigrationAnalysis.addAll(appFrequenceyRemoval.keySet());
		
		Map<String,Integer> appUpdateHasAds = calculateAppUpdateHasAds();
		
		CsvWriter writer = new CsvWriter(AD_MIGRATION_PER_APP_UPDATE);
		writer.write("App_Name");
		writer.write("Category");
		writer.write("Total_Updates");
		writer.write("Total_Updates_Has_Ad");
		writer.write("Addition");
		writer.write("Removal");
		writer.write("Migration");
		writer.write("Rollback");
		writer.endRecord();
		for(String app : appInvolveMigrationAnalysis){			
			writer.write(app);
			String category = appsData.get(app).getAPP_SUB_CATEGORY();
			if (appsData.get(app).getAPP_SUB_CATEGORY().contains("GAME")) {
				category = "GAME";
			}
			writer.write(category);
			writer.write(Integer.toString(appUpdates.get(app).size()));
			writer.write(Integer.toString(appUpdateHasAds.get(app)));
			
			
			writer.write(Integer.toString(helperGetValue(appFrequenceyAddition, app)));
			writer.write(Integer.toString(helperGetValue(appFrequenceyRemoval, app)));
			writer.write(Integer.toString(helperGetValue(appFrequenceyMigration, app)));
			writer.write(Integer.toString(helperGetValue(appFrequenceyRollback, app)));
			writer.endRecord();
		}
		writer.close();
	}
	
	public void writeAdditionRemovalTime(Map<String,Integer> addTime,Map<String,Integer>removalTime) throws Exception{
		CsvWriter writer = new CsvWriter(AD_ADD_TIME);
		CsvWriter writer2 = new CsvWriter(AD_REMOVE_TIME);
		writer.write("App_Name");
		writer.write("Add_Time");
		writer.endRecord();
		writer2.write("App_Name");
		writer2.write("Remove_Time");
		writer2.endRecord();
		
		for(String appName : addTime.keySet()){
			writer.write(appName);
			writer.write(Integer.toString(addTime.get(appName)));
			writer.endRecord();
		}
		for(String appName : removalTime.keySet()){
			writer2.write(appName);
			writer2.write(Integer.toString(removalTime.get(appName)));
			writer2.endRecord();
		}
		
		writer.close();
		writer2.close();
	}

	public Map<String,Integer> calculateAppUpdateHasAds(){
		Map<String,Integer> appUpdateHasAds = new HashMap<String,Integer>();
		for (String appName : appUpdates.keySet()) {
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			appUpdateHasAds.put(appName, 0);
			for (int i = 0; i < updates.size(); i++) {
				String key = updates.get(i).getPACKAGE_NAME() + "-" + updates.get(i).getVERSION_CODE();
				if(isUpdateHasAdLibraries(key, appUseAd)){
					appUpdateHasAds.put(appName, appUpdateHasAds.get(appName) + 1 );
				}
			}
		}
		return appUpdateHasAds;
	}
	
	
	public UpdateTable getLastUpdateUsingAds(String appName){
		UpdateTable lastUpdate = null;
		ArrayList<UpdateTable> updates = appUpdates.get(appName);
		for(int i = updates.size() - 1 ; i >= 0 ; i -- ){
			String key = updates.get(i).getPACKAGE_NAME() + "-" + updates.get(i).getVERSION_CODE();
			if(isUpdateHasAdLibraries(key, appUseAd)){
				lastUpdate = updates.get(i);
				break;
			}
		}
		return lastUpdate;
	}
	
	
	public void migrationAnalysis() {

		Set<String> appHasAds = new HashSet<String>();
		
		Set<String> appUseStopsLibrary = new HashSet<String>();
		
		Map<String,Integer> appFrequenceyMigration = new HashMap<String,Integer>();
		Map<String,Integer> appFrequenceyAddition = new HashMap<String,Integer>();
		Map<String,Integer> appFrequenceyRemoval = new HashMap<String,Integer>();
		Map<String,Integer> appFrequenceyRollback = new HashMap<String,Integer>();
		
		Map<String,ArrayList<Integer>> adSurvivalTime = new HashMap<String,ArrayList<Integer>>();
		Map<String,ArrayList<Integer>> adRollBackTime = new HashMap<String,ArrayList<Integer>>();
		
		
		
		
		Map<String,Integer> addTime = new HashMap<String,Integer>();
		Map<String,Integer> removeTime = new HashMap<String,Integer>();
		
		
		for (String appName : appUpdates.keySet()) {
			Set<String> usedAds = new HashSet<String>();
			Set<String> stopUsingAds = new HashSet<String>();
			boolean change = false;
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			Map<String,DateTime> firstAdTime = new HashMap<String,DateTime>();
			Map<String,DateTime> stopAdTime = new HashMap<String,DateTime>();
			
			
			//Calclate first time when ad library is used
			for(int i = 0 ; i < updates.size() ; i ++ ){
				String key = updates.get(i).getPACKAGE_NAME() + "-" + updates.get(i).getVERSION_CODE();
				if(isUpdateHasAdLibraries(key, appUseAd)){
					//First update to use ads library
					DateTime releaseDate = DateUtil.formatterWithHyphen.parseDateTime(updates.get(i).getRELEASE_DATE());
					if(releaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
						continue;
					}
					for(String lib: appUseAd.get(key).getAdsNames()){
						firstAdTime.put(lib, releaseDate);
					}					
					break;
				}
			}
			
			UpdateTable previousUpdate = updates.get(0);
			DateTime additionTime = DateUtil.formatterWithHyphen.parseDateTime(previousUpdate.getRELEASE_DATE());
			DateTime removalTime = DateUtil.formatterWithHyphen.parseDateTime(previousUpdate.getRELEASE_DATE());
			
			
			ArrayList<Integer> addTempTime = new ArrayList<Integer>();
			ArrayList<Integer> removeTempTime = new ArrayList<Integer>();
			
			for (int i = 1; i < updates.size(); i++) {
				
				boolean migrationFlag = false;
				String previousKey = previousUpdate.getPACKAGE_NAME() + "-" + previousUpdate.getVERSION_CODE();
				String updateKey = updates.get(i).getPACKAGE_NAME() + "-" + updates.get(i).getVERSION_CODE();

				DateTime previousReleaseDate = DateUtil.formatterWithHyphen.parseDateTime(previousUpdate.getRELEASE_DATE());
				DateTime updateReleaseDate = DateUtil.formatterWithHyphen.parseDateTime(updates.get(i).getRELEASE_DATE());
				
				if (isUpdateHasAdLibraries(previousKey, appUseAd) || isUpdateHasAdLibraries(updateKey, appUseAd)) {
					appHasAds.add(appName);
				}
				
				if (!isUpdateHasAdLibraries(previousKey, appUseAd) || !isUpdateHasAdLibraries(updateKey, appUseAd)) {
					continue;
				}

				AdInformation previousAdsInfo = appUseAd.get(previousKey);
				AdInformation updateAdsInfo = appUseAd.get(updateKey);

				Set<String> oldUseAd = previousAdsInfo.getAdsNames();
				Set<String> presentUseAd = updateAdsInfo.getAdsNames();
				Set<String> adLibraries = new HashSet<String>();
				adLibraries.addAll(oldUseAd);
				adLibraries.addAll(presentUseAd);

				Set<String> stopingAd = new HashSet<String>();
				Set<String> newUsingAd = new HashSet<String>();
				usedAds.addAll(previousAdsInfo.getAdsNames());
				
				boolean rollbackFlag = false;
				boolean migration = false;
				
				for (String adName : adLibraries) {
					if (!presentUseAd.contains(adName)) {
						stopingAd.add(adName);
						
						if(!previousReleaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
							//System.out.println("GOT ISSUE " + appName);
							stopUsingAds.add(adName);
							stopAdTime.put(adName, updateReleaseDate);
						}
						
					}
					if (!oldUseAd.contains(adName)) {
						newUsingAd.add(adName);
						//stopUsingAds.remove(adName);
						if(!previousReleaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
							firstAdTime.put(adName, updateReleaseDate);
						}
						
					}
					
					//Rollback Condition
					if(!oldUseAd.contains(adName) && presentUseAd.contains(adName) && usedAds.contains(adName) && stopUsingAds.contains(adName)){
						rollbackFlag = true;
						change = true;
						if(!adRollBackTime.containsKey(adName)){
							adRollBackTime.put(adName, new ArrayList<Integer>());
						}
						adRollBackTime.get(adName).add(Days.daysBetween(stopAdTime.get(adName),updateReleaseDate).getDays());
					}
				}
				
				//Survival time analysis
				for(String stopAd : stopingAd){
					
					if(!previousReleaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
						if(!adSurvivalTime.containsKey(stopAd)){
							adSurvivalTime.put(stopAd, new ArrayList<Integer>());
						}
						if(!firstAdTime.containsKey(stopAd)){
							System.out.println("PROBLEM: " + stopAd);
						}else{
							adSurvivalTime.get(stopAd).add(Days.daysBetween(firstAdTime.get(stopAd),updateReleaseDate).getDays());
							firstAdTime.remove(stopAd);
						}
					}					
					
				}
				
				
				for (String stopAd : stopingAd) {
					for (String newAd : newUsingAd) {
						adMigration[listOfAllAdLibraries.indexOf(stopAd)][listOfAllAdLibraries.indexOf(newAd)]++;
						migrationFlag = true;
					}
				}
				if(migrationFlag){

					if(!appFrequenceyMigration.containsKey(appName)){
						appFrequenceyMigration.put(appName, 1);
					}else{
						appFrequenceyMigration.put(appName,appFrequenceyMigration.get(appName)+ 1);
					}
				}
				
				if(rollbackFlag){
					if(!appFrequenceyRollback.containsKey(appName)){
						appFrequenceyRollback.put(appName, 1);
					}else{
						appFrequenceyRollback.put(appName, appFrequenceyRollback.get(appName) + 1);
					}
				}
				
				//App adds ad library
				if(updateAdsInfo.getAdsNames().size() > previousAdsInfo.getAdsNames().size()){
					if(!appFrequenceyAddition.containsKey(appName)){
						appFrequenceyAddition.put(appName, 1);
					}else{
						appFrequenceyAddition.put(appName, appFrequenceyAddition.get(appName) + 1);
					}
					change = true;
					//Addition Time
					if(!previousReleaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
						addTempTime.add(Days.daysBetween(additionTime,updateReleaseDate).getDays());
						additionTime = updateReleaseDate;
					}
					additionTime = updateReleaseDate;
				}
				
				//App removes ad library
				if(updateAdsInfo.getAdsNames().size() < previousAdsInfo.getAdsNames().size()){
					if(!appFrequenceyRemoval.containsKey(appName)){
						appFrequenceyRemoval.put(appName,1);
					}else{
						appFrequenceyRemoval.put(appName, appFrequenceyRemoval.get(appName)+1);
					}
					change = true;
					//removal time
					if(!previousReleaseDate.isBefore(ProjectConstants.EPERIMENT_START_TIME)){
						removeTempTime.add(Days.daysBetween(removalTime,updateReleaseDate).getDays());
						removalTime = updateReleaseDate;
					}
					removalTime = updateReleaseDate;
				}
				previousUpdate = updates.get(i);
			}
			
			if(addTempTime.size() > 0 ){
				double avg = 0;
				for(Integer v : addTempTime){
					avg += v;
				}
				avg /= addTempTime.size();
				addTime.put(appName,(int)Math.ceil(avg));
			}
			
			if(removeTempTime.size() > 0 ){
				double avg = 0;
				for(Integer v : removeTempTime){
					avg += v;
				}
				avg /= removeTempTime.size();
				removeTime.put(appName,(int)Math.ceil(avg));
			}
			
			//Last update using ad libraries
			UpdateTable lastUpdateUsingAds = getLastUpdateUsingAds(appName);
			if(lastUpdateUsingAds != null){
				String updateKey =  lastUpdateUsingAds.getPACKAGE_NAME() + "-" + lastUpdateUsingAds.getVERSION_CODE();
				for(String ad : appUseAd.get(updateKey).getAdsNames()){
					if(!adSurvivalTime.containsKey(ad)){
						adSurvivalTime.put(ad, new ArrayList<Integer>());
					}
					if(!firstAdTime.containsKey(ad)){
						System.out.println("PROBLEM: " + ad);
					}else{
						adSurvivalTime.get(ad).add(Days.daysBetween(firstAdTime.get(ad),ProjectConstants.EPERIMENT_END_TIME).getDays());
					}
					
				}
			}
			
			
		}
		try {
			// Writing migration information
			//writeAdMigrationHeatMap();
			//Write change information for per app per update
			//writeMigrationAnalysisPerUpdate(appFrequenceyAddition,appFrequenceyRemoval,appFrequenceyMigration,appFrequenceyRollback);			
			//Survival Time 
			//writeAdSurvivalTime(adSurvivalTime,AD_SURVIVAL_TIME);
			//Rollback Time
			//writeAdSurvivalTime(adRollBackTime,AD_ROLL_BACK_TIME);
			//Addition and removal time
			writeAdditionRemovalTime(addTime,removeTime);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		System.out.println("Summary");
		System.out.println("========================================");
		System.out.println("App has ads ["+appHasAds.size()+"] Percentage["+(appHasAds.size()*100.0/1840)+"]");
		System.out.println("App involve ad library migration ["+appFrequenceyMigration.size()+"] Percentage["+(appFrequenceyMigration.size()*100.0/appHasAds.size())+"]");
		System.out.println("App rollback ad library ["+appFrequenceyRollback.size()+"] Percentage ["+(appFrequenceyRollback.size()*100.0/appHasAds.size())+"]");
		System.out.println("App Add ad library ["+appFrequenceyAddition.size()+"] Percentage ["+(appFrequenceyAddition.size()*100.0/appHasAds.size())+"]");
		System.out.println("App Remove ad library ["+appFrequenceyRemoval.size()+"] Percentage ["+(appFrequenceyRemoval.size()*100.0/appHasAds.size())+"]");
		
	}

	/*
	 * App has ads [1453] Percentage[78.96739130434783]
	   App involve ad library migration [212] Percentage[14.59050240880936]
  	   App rollback ad library [204] Percentage [14.039917412250515]
	   App Add ad library [597] Percentage [41.08740536820372]
	   App Remove ad library [522] Percentage [35.92567102546455]	
	 */
	
	
	public static void main(String[] args) {
		MigrationAnalyzer ob = new MigrationAnalyzer();
		ob.initializeListofAds();
		ob.migrationAnalysis();
		System.out.println("Program finishes successfully");
	}
}
