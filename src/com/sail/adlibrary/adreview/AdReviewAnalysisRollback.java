package com.sail.adlibrary.adreview;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.analysis.util.FileUtil;
import com.sail.mobile.model.AdInformation;
import com.sail.mobile.model.AdRollbackModel;
import com.sail.mobile.model.AppTable;
import com.sail.mobile.model.RatingModel;
import com.sail.mobile.model.Reviews;
import com.sail.mobile.model.UpdateTable;

public class AdReviewAnalysisRollback {

	Map<String,ArrayList<Reviews>> reviewUpdateList = new HashMap<String,ArrayList<Reviews>>();
			
	int MINIMUM_APP_USE_ADS_THRESHOLD = 30;
	public String AD_LIB_USAGE_APP_UPDATE = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Data/Updates_With_Ad_Library.csv";

	public final String UPDATE_PATH = ProjectConstants.STUDIED_APP_UPDATES;
	public final String AD_LIBRARY_MAP = ProjectConstants.VARIFIED_AD_PACKAGE_LIST;
	
	public final String AD_MIGRATION_TABLE = ProjectConstants.AD_MIGRATION_TABLE;
	public final String AD_MIGRATION_HEATMAP = ProjectConstants.AD_MIGRATION_HEATMAP;
	public final String AD_MIGRATION_PER_APP_UPDATE = ProjectConstants.AD_MIGRATION_PER_APP_UPDATE;
	public final String AD_SURVIVAL_TIME = ProjectConstants.AD_SURVIVAL_TIME;
	public final String AD_ROLL_BACK_TIME = ProjectConstants.AD_ROLL_BACK_TIME;
	public final String AD_ADD_TIME = ProjectConstants.AD_ADD_TIME;
	public final String AD_REMOVE_TIME = ProjectConstants.AD_REMOVE_TIME;
	
	Map<String, AdInformation> appUseAd = FileUtil.readAdLibraryInformationII();
	Map<String, Set<String>> libUsedByApp = new HashMap<String, Set<String>>();
	ArrayList<AdRollbackModel> rollbackData = new ArrayList<AdRollbackModel>();
	Map<String, ArrayList<AdInformation>> appUseAdsPerApp = FileUtil
			.readAdLibraryInformationPerApp(ProjectConstants.ADS_UPDATE_DATA_PATH);

	HashMap<String, ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(UPDATE_PATH);
	Map<String, String> varifiedAdPackageMapGroup = FileUtil.readVerfiedAdList(AD_LIBRARY_MAP);	
	public static Map<String, AppTable> appsData = FileUtil.readAppData();
	
	public Map<String,ArrayList<Reviews>> loadReviewUpdateMap(String path){
		
		String file_path = path;

		long start = System.currentTimeMillis();

		FileInputStream input = null;
		try {

			input = new FileInputStream(file_path);
			System.out.println("FileReading...");

			ObjectInputStream in2 = new ObjectInputStream(input);
			System.out.println("Loading.....");

			reviewUpdateList = (Map<String,ArrayList<Reviews>>) in2.readObject();
			System.out.println("Finishing.. Reading..");
			System.out.println("Total Posts: " + reviewUpdateList.size());

		} catch (Exception e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();

		System.out.println("[Loading Posts takes: " + (end - start) + " ms]");
		System.out.println();
		
		return reviewUpdateList;
	}
	
	public void calculateLibUsedByApp() throws Exception {
		for (String appName : appUseAdsPerApp.keySet()) {
			ArrayList<AdInformation> appHasAds = appUseAdsPerApp.get(appName);
			for (int i = 0; i < appHasAds.size(); i++) {
				for (String lib : appHasAds.get(i).getAdsNames()) {
					if (!libUsedByApp.containsKey(lib)) {
						libUsedByApp.put(lib, new HashSet<String>());
					}
					libUsedByApp.get(lib).add(appName);
				}
			}
		}
		System.out.println("Load Lib used ["+libUsedByApp.size()+"]");
	}
	
	public RatingModel getReviewsOfUpdate(String updateKey){
		RatingModel ratModel = new RatingModel();
		if(!reviewUpdateList.containsKey(updateKey)){
			return null;
		}
		ArrayList<Reviews> reviews = reviewUpdateList.get(updateKey);
		double totalReviews = reviews.size();
		double negativeReviews = 0;
		double negAdsReviews = 0;
		double adReleatedReview = 0;
		for(Reviews r : reviews){
			if(r.getRating() <= 2){
				++negativeReviews;
				if(r.isAdRelated()){
					++negAdsReviews;
				}
			}
			if(r.isAdRelated){
				++adReleatedReview;
			}
		}
		
		ratModel.setTotalReviews(totalReviews);
		ratModel.setTotalNegativeReviews(negativeReviews);
		ratModel.setTotalAdReviews(adReleatedReview);
		ratModel.setTotalAdNegativeReviews(negAdsReviews);
		
		return ratModel;
	}
	
	public void readRollbackToAdLibraryVersion() throws Exception {	
		CsvReader reader = new CsvReader(
				"/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Data/modify_ad_lib_update_rollback.csv");
		reader.readHeaders();
		while (reader.readRecord()) {
			String appName = reader.get("App_Name");
			String fromVersionCode = reader.get("From_Version_Code");
			String toVersionCode = reader.get("To_Version_Code");
			String fromDate = reader.get("From_Release_Date");
			String toDate = reader.get("To_Release_Date");
			String noDays = reader.get("No_of_days");
			String lib = reader.get("Library");

			if (lib.equals("Supersonic")) {
				continue;
			}
			//System.out.println("Library: " + lib);
			if (libUsedByApp.get(lib).size() < MINIMUM_APP_USE_ADS_THRESHOLD) {
				continue;
			}
			
			AdRollbackModel rollbackOb = new AdRollbackModel();
			rollbackOb.setPackageName(appName);
			rollbackOb.setVersionRollbackFrom(fromVersionCode);
			rollbackOb.setVersionRollbackTo(toVersionCode);
			rollbackOb.setReleaseDateRollbackFrom(fromDate);
			rollbackOb.setReleaseDateRollbackTo(toDate);
			rollbackOb.setRollbackedLibrary(lib);
			
			rollbackData.add(rollbackOb);
			
			
		}		
		reader.close();
	}
	
	public void analyzeRollbackAdRating() throws Exception{
		
		loadReviewUpdateMap(ProjectConstants.AD_REVIEW_SERILIAZED_DATA_PER_APP_UPDATE);
		
		CsvWriter writer = new CsvWriter("/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Result/Rollback/review_ratio_rollback.csv");
		writer.write("App_Name");
		writer.write("Rollback_Version");
		writer.write("Lib");
		
		writer.write("Rollback_TotalReviews");
		writer.write("Rollback_TotalNegReviews");
		writer.write("Rollback_TotalAdReviews");
		writer.write("Rollback_TotalNegAdReviews");
		
		writer.write("Rollback_Previous_TotalReviews");
		writer.write("Rollback_Previous_TotalNegReviews");
		writer.write("Rollback_Previous_TotalAdReviews");
		writer.write("Rollback_Previous_TotalNegAdReviews");
		writer.write("Ratio");
		writer.endRecord();
		
		
		int missingRollbackPreviousUpdate = 0;
		for(AdRollbackModel rollback : rollbackData){
			String appName = rollback.getPackageName();
			String rollbackVersion = rollback.getVersionRollbackFrom();
			String rollbackLibrary = rollback.getRollbackedLibrary();
			
			String rollbackUpdateKey = appName + "-" + rollbackVersion;
			UpdateTable rollbackPreviousUpdate = null;

			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			for(int i = 0 ; i < updates.size() ; i ++ ){
				UpdateTable update = updates.get(i);
				String updateKey = update.getPACKAGE_NAME() + "-" + update.getVERSION_CODE();
				if(rollbackUpdateKey.equals(updateKey)){
					rollbackPreviousUpdate = updates.get(i-1);
					break;
				}
			}
			if(rollbackPreviousUpdate == null){
				++missingRollbackPreviousUpdate;
				continue;
			}
			String previousUpdateKey = appName + "-" + rollbackPreviousUpdate.getVERSION_CODE();
			
			RatingModel rollbackRatingModel = getReviewsOfUpdate(rollbackUpdateKey);
			RatingModel rollbackPreviousRatingModel = getReviewsOfUpdate(previousUpdateKey);
			
			if(rollbackPreviousRatingModel == null || rollbackRatingModel == null){
				continue;
			}
			
			double adNegativityRatioRollback = rollbackRatingModel.getTotalAdNegativeReviews()/rollbackRatingModel.getTotalReviews();
			double adNegativityRatioPreviousRollback = rollbackPreviousRatingModel.getTotalAdNegativeReviews()/rollbackPreviousRatingModel.getTotalReviews();
			
			double ratio = adNegativityRatioRollback/adNegativityRatioPreviousRollback;
			
			writer.write(appName);
			writer.write(rollbackVersion);
			writer.write(rollbackLibrary);
			
			writer.write(Double.toString(rollbackRatingModel.getTotalReviews()));
			writer.write(Double.toString(rollbackRatingModel.getTotalNegativeReviews()));
			writer.write(Double.toString(rollbackRatingModel.getTotalAdReviews()));
			writer.write(Double.toString(rollbackRatingModel.getTotalAdNegativeReviews()));
			writer.write(Double.toString(adNegativityRatioRollback));
			
			writer.write(Double.toString(rollbackPreviousRatingModel.getTotalReviews()));
			writer.write(Double.toString(rollbackPreviousRatingModel.getTotalNegativeReviews()));
			writer.write(Double.toString(rollbackPreviousRatingModel.getTotalAdReviews()));
			writer.write(Double.toString(rollbackPreviousRatingModel.getTotalAdNegativeReviews()));			
			writer.write(Double.toString(adNegativityRatioPreviousRollback));
			writer.write(Double.toString(adNegativityRatioPreviousRollback));
			writer.endRecord();
			
		}
		writer.close();
	}
	
	
	/*
	 * Finishing.. Reading..
Total Posts: 59600
[Loading Posts takes: 1777907 ms]
	 */
	public static void main(String[] args) throws Exception{
		AdReviewAnalysisRollback ob = new AdReviewAnalysisRollback();
		ob.calculateLibUsedByApp();
		ob.readRollbackToAdLibraryVersion();
		ob.analyzeRollbackAdRating();
		System.out.println("Program finishes successfully");
	}
}
