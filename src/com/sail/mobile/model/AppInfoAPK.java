package com.sail.mobile.model;

public class AppInfoAPK {
	public String appName;
	public String versionCode;
	public String releaseDate;

	public AppInfoAPK(String appId,String packageName, String versionCode, String releaseDate){
		this.appName = packageName;
		this.versionCode = versionCode;
		this.releaseDate = releaseDate;
	}
	
	public AppInfoAPK(String packageName, String versionCode, String releaseDate){
		this.appName = packageName;
		this.versionCode = versionCode;
		this.releaseDate = releaseDate;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String packageName) {
		this.appName = packageName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

}
