package com.sail.mobile.model;

public class AppTable {

	public int APP_ID;
	public String PACKAGE_NAME;
	public String APP_TITLE;
	public String IS_APP_TITLE_HAS_NON_PRINTABLE_CHAR;
	public String APP_DESCRIPTION;
	public String IS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR;
	public String APP_CATEGORY;
	public String APP_SUB_CATEGORY;
	public int ORGANIZATION_ID;
	public int CONTENT_RATING;
	public String CONTENT_RATING_TEXT;
	public String IS_TOP_DEVELOPER;
	public String IS_WEARABLE_APP;
	public int APP_TYPE;
	public int AVAILABILITY_RESTRICTION;
	public String MAIN_PAGE_URL;
	public String DETAILS_URL;
	public String REVIEWS_URL;
	public String PURCHASE_DETAILS_URL;
	public String DEVELOPER_EMAIL;
	public String DEVELOPER_WEBSITE;
	public String IS_2013_TOP_10K_APPS;
	public String IS_2015_TOP_500_APPS;
	public String IS_FDROID_APP;
	public String IS_2016_TOP_2500_APPS;
	public String IS_2016_FAMILY_APPS;
	public String IS_2016_TOP_2500_NON_FREE_APPS;
	public String IS_2016_FAMILY_NON_FREE_APPS;
	public int getAPP_ID() {
		return APP_ID;
	}
	public void setAPP_ID(int aPP_ID) {
		APP_ID = aPP_ID;
	}
	public String getPACKAGE_NAME() {
		return PACKAGE_NAME;
	}
	public void setPACKAGE_NAME(String pACKAGE_NAME) {
		PACKAGE_NAME = pACKAGE_NAME;
	}
	public String getAPP_TITLE() {
		return APP_TITLE;
	}
	public void setAPP_TITLE(String aPP_TITLE) {
		APP_TITLE = aPP_TITLE;
	}
	public String getIS_APP_TITLE_HAS_NON_PRINTABLE_CHAR() {
		return IS_APP_TITLE_HAS_NON_PRINTABLE_CHAR;
	}
	public void setIS_APP_TITLE_HAS_NON_PRINTABLE_CHAR(String iS_APP_TITLE_HAS_NON_PRINTABLE_CHAR) {
		IS_APP_TITLE_HAS_NON_PRINTABLE_CHAR = iS_APP_TITLE_HAS_NON_PRINTABLE_CHAR;
	}
	public String getAPP_DESCRIPTION() {
		return APP_DESCRIPTION;
	}
	public void setAPP_DESCRIPTION(String aPP_DESCRIPTION) {
		APP_DESCRIPTION = aPP_DESCRIPTION;
	}
	public String getIS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR() {
		return IS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR;
	}
	public void setIS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR(String iS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR) {
		IS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR = iS_APP_DESCRIPTION_HAS_NON_PRINTABLE_CHAR;
	}
	public String getAPP_CATEGORY() {
		return APP_CATEGORY;
	}
	public void setAPP_CATEGORY(String aPP_CATEGORY) {
		APP_CATEGORY = aPP_CATEGORY;
	}
	public String getAPP_SUB_CATEGORY() {
		return APP_SUB_CATEGORY;
	}
	public void setAPP_SUB_CATEGORY(String aPP_SUB_CATEGORY) {
		APP_SUB_CATEGORY = aPP_SUB_CATEGORY;
	}
	public int getORGANIZATION_ID() {
		return ORGANIZATION_ID;
	}
	public void setORGANIZATION_ID(String oRGANIZATION_ID) {
		if(oRGANIZATION_ID != null)
			ORGANIZATION_ID = Integer.parseInt(oRGANIZATION_ID);
		else ORGANIZATION_ID = -1;
	}
	public int getCONTENT_RATING() {
		return CONTENT_RATING;
	}
	public void setCONTENT_RATING(String cONTENT_RATING) {
		if(cONTENT_RATING == null){			
			CONTENT_RATING = -1;			
		}
		else if (cONTENT_RATING.length() <= 0){
			CONTENT_RATING = -1;
		}
			
		else CONTENT_RATING = Integer.parseInt(cONTENT_RATING);		
	}
	public String getCONTENT_RATING_TEXT() {
		return CONTENT_RATING_TEXT;
	}
	public void setCONTENT_RATING_TEXT(String cONTENT_RATING_TEXT) {
		CONTENT_RATING_TEXT = cONTENT_RATING_TEXT;
	}
	public String getIS_TOP_DEVELOPER() {
		return IS_TOP_DEVELOPER;
	}
	public void setIS_TOP_DEVELOPER(String iS_TOP_DEVELOPER) {
		IS_TOP_DEVELOPER = iS_TOP_DEVELOPER;
	}
	public String getIS_WEARABLE_APP() {
		return IS_WEARABLE_APP;
	}
	public void setIS_WEARABLE_APP(String iS_WEARABLE_APP) {
		IS_WEARABLE_APP = iS_WEARABLE_APP;
	}
	public int getAPP_TYPE() {
		return APP_TYPE;
	}
	public void setAPP_TYPE(String aPP_TYPE) {
		if(aPP_TYPE != null)
			APP_TYPE = Integer.parseInt(aPP_TYPE);
		else APP_TYPE = -1;
	}
	public int getAVAILABILITY_RESTRICTION() {
		return AVAILABILITY_RESTRICTION;
	}
	public void setAVAILABILITY_RESTRICTION(String aVAILABILITY_RESTRICTION) {
		if(aVAILABILITY_RESTRICTION != null){			
			AVAILABILITY_RESTRICTION = -1;		
		}
		else if (aVAILABILITY_RESTRICTION.length() <= 0){
			AVAILABILITY_RESTRICTION = -1;
		}
			
		else AVAILABILITY_RESTRICTION = Integer.parseInt(aVAILABILITY_RESTRICTION);	
		
	}
	public String getMAIN_PAGE_URL() {
		return MAIN_PAGE_URL;
	}
	public void setMAIN_PAGE_URL(String mAIN_PAGE_URL) {
		MAIN_PAGE_URL = mAIN_PAGE_URL;
	}
	public String getDETAILS_URL() {
		return DETAILS_URL;
	}
	public void setDETAILS_URL(String dETAILS_URL) {
		DETAILS_URL = dETAILS_URL;
	}
	public String getREVIEWS_URL() {
		return REVIEWS_URL;
	}
	public void setREVIEWS_URL(String rEVIEWS_URL) {
		REVIEWS_URL = rEVIEWS_URL;
	}
	public String getPURCHASE_DETAILS_URL() {
		return PURCHASE_DETAILS_URL;
	}
	public void setPURCHASE_DETAILS_URL(String pURCHASE_DETAILS_URL) {
		PURCHASE_DETAILS_URL = pURCHASE_DETAILS_URL;
	}
	public String getDEVELOPER_EMAIL() {
		return DEVELOPER_EMAIL;
	}
	public void setDEVELOPER_EMAIL(String dEVELOPER_EMAIL) {
		DEVELOPER_EMAIL = dEVELOPER_EMAIL;
	}
	public String getDEVELOPER_WEBSITE() {
		return DEVELOPER_WEBSITE;
	}
	public void setDEVELOPER_WEBSITE(String dEVELOPER_WEBSITE) {
		DEVELOPER_WEBSITE = dEVELOPER_WEBSITE;
	}
	public String getIS_2013_TOP_10K_APPS() {
		return IS_2013_TOP_10K_APPS;
	}
	public void setIS_2013_TOP_10K_APPS(String iS_2013_TOP_10K_APPS) {
		IS_2013_TOP_10K_APPS = iS_2013_TOP_10K_APPS;
	}
	public String getIS_2015_TOP_500_APPS() {
		return IS_2015_TOP_500_APPS;
	}
	public void setIS_2015_TOP_500_APPS(String iS_2015_TOP_500_APPS) {
		IS_2015_TOP_500_APPS = iS_2015_TOP_500_APPS;
	}
	public String getIS_FDROID_APP() {
		return IS_FDROID_APP;
	}
	public void setIS_FDROID_APP(String iS_FDROID_APP) {
		IS_FDROID_APP = iS_FDROID_APP;
	}
	public String getIS_2016_TOP_2500_APPS() {
		return IS_2016_TOP_2500_APPS;
	}
	public void setIS_2016_TOP_2500_APPS(String iS_2016_TOP_2500_APPS) {
		IS_2016_TOP_2500_APPS = iS_2016_TOP_2500_APPS;
	}
	public String getIS_2016_FAMILY_APPS() {
		return IS_2016_FAMILY_APPS;
	}
	public void setIS_2016_FAMILY_APPS(String iS_2016_FAMILY_APPS) {
		IS_2016_FAMILY_APPS = iS_2016_FAMILY_APPS;
	}
	public String getIS_2016_TOP_2500_NON_FREE_APPS() {
		return IS_2016_TOP_2500_NON_FREE_APPS;
	}
	public void setIS_2016_TOP_2500_NON_FREE_APPS(String iS_2016_TOP_2500_NON_FREE_APPS) {
		IS_2016_TOP_2500_NON_FREE_APPS = iS_2016_TOP_2500_NON_FREE_APPS;
	}
	public String getIS_2016_FAMILY_NON_FREE_APPS() {
		return IS_2016_FAMILY_NON_FREE_APPS;
	}
	public void setIS_2016_FAMILY_NON_FREE_APPS(String iS_2016_FAMILY_NON_FREE_APPS) {
		IS_2016_FAMILY_NON_FREE_APPS = iS_2016_FAMILY_NON_FREE_APPS;
	}
	
	
	
	
}
