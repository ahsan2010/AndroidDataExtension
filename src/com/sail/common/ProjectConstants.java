package com.sail.common;

import org.joda.time.DateTime;

import com.sail.mobile.analysis.util.DateUtil;

public class ProjectConstants {

	///home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/
	
	public static String ROOT = "/home/local/SAIL/ahsan/Documents/Pinky/Documents/SAIL_Projects/Android_Data_Extension/";
	public static String JAR_CONVERSION_PROPERTIES = ROOT + "";
	public static String JAR_CONVERSION_LOG = ROOT + "log/";
	public static String APP_UPDATES_FILE = ROOT + "Data/app_updates_top-2017_09_20-2019_04_20.csv";
	public static String APP_UPDATES_SHORT_FILE = ROOT + "Data/Final_Studied_App_Updates_2016_2019_Short.csv";
	public static String APK_LOCATION = "/safwatscratch/shassan/Input_Apks/";
	public static String JAR_LOCATION = "/safwatscratch/shassan/Jars/";
	public static String APP_UPDATES_FULL = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Data/app_updates_top_April_2016_April_2019.csv";
	public static String APP_UPDATES_JAN_16_SEP_19 = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Data/app_updates_top_Jan_2016_April_2019.csv";
	public static String STUDIED_APP_UPDATES = ROOT + "Data/Final_Studied_App_Updates_2016_2019.csv";
		
	public static String VARIFIED_AD_PACKAGE_LIST = ROOT + "Data/Updated_Varified_Ad_List.csv";
	
	public static String ADS_UPDATE_DATA_PATH_OLD = ROOT + "Data/Updates_With_Ad_Library_Old.csv";
	public static String ADS_UPDATE_DATA_PATH = ROOT + "Data/Updates_With_Ad_Library.csv";
	public static String APP_CSV_PATH = ROOT + "/Data/app_top_2016.csv";
	
	public static DateTime EPERIMENT_START_TIME = DateUtil.formatter.parseDateTime("2016_04_20");
	public static DateTime EPERIMENT_END_TIME = DateUtil.formatter.parseDateTime("2019_04_20");
	
	
	
	public static String AD_MIGRATION_TABLE = ROOT + "Result/Migration/ad_library_migration.csv";
	public static String AD_MIGRATION_HEATMAP = ROOT + "Result/Migration/ad_library_migration_heatmap.csv";
	public static String AD_MIGRATION_PER_APP_UPDATE = ROOT + "Result/Migration/ad_library_change_per_app.csv";
	public static String AD_SURVIVAL_TIME = ROOT + "Result/Migration/ad_suvival_time.csv";
	public static String AD_ROLL_BACK_TIME = ROOT + "Result/Rollbackad_rollback_time.csv";
	public static String AD_ADD_TIME = ROOT + "Result/Migration/add_time_ads.csv";
	public static String AD_REMOVE_TIME = ROOT + "Result/Migration/remove_time_ads.csv";

	public static String REVIEW_DATA = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/ReviewAnalysis/App_Reviews_2016_2019.csv";
	public static String AD_REVIEW_SERILIAZED_DATA_PER_APP_UPDATE = ROOT + "/Data/ad_review.ser";
	
	public static String  jarOutputPath = "/safwatscratch/shassan/Jars/";
}
