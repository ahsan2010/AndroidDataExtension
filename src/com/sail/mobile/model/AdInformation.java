package com.sail.mobile.model;

import java.util.HashSet;
import java.util.Set;

public class AdInformation {

	public String usedAdLibraries;
	public String versionCode;
	public String packageName;
	public String releaseDate;

	public AdInformation(String packageName, String versionCode, String usedAdLibraries, String releaseDate) {
		this.packageName = packageName;
		this.versionCode = versionCode;
		this.usedAdLibraries = usedAdLibraries;
		this.releaseDate = releaseDate;
	}

	public  Set<String> getAdsNames() {
		Set<String> libraryList = new HashSet<String>();
		for (String libraryName : usedAdLibraries.split("-")) {
			libraryList.add(libraryName);
		}
		return libraryList;
	}

	public String getUsedAdLibraries() {
		return usedAdLibraries;
	}

	public void setUsedAdLibraries(String usedAdLibraries) {
		this.usedAdLibraries = usedAdLibraries;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	

}
