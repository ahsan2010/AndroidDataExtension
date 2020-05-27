package com.sail.mobile.model;

public class AdRollbackModel {
	public String packageName;
	public String versionRollbackFrom;
	public String versionRollbackTo;
	public String releaseDateRollbackFrom;
	public String releaseDateRollbackTo;

	public String rollbackedLibrary;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionRollbackFrom() {
		return versionRollbackFrom;
	}

	public void setVersionRollbackFrom(String versionRollbackFrom) {
		this.versionRollbackFrom = versionRollbackFrom;
	}

	public String getVersionRollbackTo() {
		return versionRollbackTo;
	}

	public void setVersionRollbackTo(String versionRollbackTo) {
		this.versionRollbackTo = versionRollbackTo;
	}

	public String getReleaseDateRollbackFrom() {
		return releaseDateRollbackFrom;
	}

	public void setReleaseDateRollbackFrom(String releaseDateRollbackFrom) {
		this.releaseDateRollbackFrom = releaseDateRollbackFrom;
	}

	public String getReleaseDateRollbackTo() {
		return releaseDateRollbackTo;
	}

	public void setReleaseDateRollbackTo(String releaseDateRollbackTo) {
		this.releaseDateRollbackTo = releaseDateRollbackTo;
	}

	public String getRollbackedLibrary() {
		return rollbackedLibrary;
	}

	public void setRollbackedLibrary(String rollbackedLibrary) {
		this.rollbackedLibrary = rollbackedLibrary;
	}
}
