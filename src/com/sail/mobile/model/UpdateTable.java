package com.sail.mobile.model;

public class UpdateTable {

	public String APP_ID;
	public String APP_UPDATE_ID;
	public String PACKAGE_NAME;
	public String VERSION_CODE;
	public String RELEASE_DATE; 
	public String APK_SIZE;
	public String targetSdkVersion;
	public String minimumSdkVersion;
	public String missingFlag;
	
	
	
	public String getTargetSdkVersion() {
		return targetSdkVersion;
	}
	public void setTargetSdkVersion(String targetSdkVersion) {
		this.targetSdkVersion = targetSdkVersion;
	}
	public String getMinimumSdkVersion() {
		return minimumSdkVersion;
	}
	public void setMinimumSdkVersion(String minimumSdkVersion) {
		this.minimumSdkVersion = minimumSdkVersion;
	}
	public String getMissingFlag() {
		return missingFlag;
	}
	public void setMissingFlag(String missingFlag) {
		this.missingFlag = missingFlag;
	}
	public String getAPP_ID() {
		return APP_ID;
	}
	public void setAPP_ID(String aPP_ID) {
		APP_ID = aPP_ID;
	}
	public String getAPP_UPDATE_ID() {
		return APP_UPDATE_ID;
	}
	public void setAPP_UPDATE_ID(String aPP_UPDATE_ID) {
		APP_UPDATE_ID = aPP_UPDATE_ID;
	}
	public String getPACKAGE_NAME() {
		return PACKAGE_NAME;
	}
	public void setPACKAGE_NAME(String pACKAGE_NAME) {
		PACKAGE_NAME = pACKAGE_NAME;
	}
	public String getVERSION_CODE() {
		return VERSION_CODE;
	}
	public void setVERSION_CODE(String vERSION_CODE) {
		VERSION_CODE = vERSION_CODE;
	}
	//yyyy-mm-dd
	public String getRELEASE_DATE() {
		return RELEASE_DATE;
	}
	public void setRELEASE_DATE(String rELEASE_DATE) {
		RELEASE_DATE = rELEASE_DATE;
	}
	public String getAPK_SIZE() {
		return APK_SIZE;
	}
	public void setAPK_SIZE(String aPK_SIZE) {
		APK_SIZE = aPK_SIZE;
	}
	
	@Override
	public String toString()
	{
		return "APP_ID=" + APP_ID + ",APP_UPDATE_ID=" + APP_UPDATE_ID;
	}
	
}
