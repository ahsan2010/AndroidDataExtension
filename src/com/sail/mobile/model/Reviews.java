package com.sail.mobile.model;

import java.io.Serializable;

public class Reviews implements Serializable{

	public String reviewText;
	public String reviewTitle;
	public String deviceName;
	public double rating;
	public String reviewTime;
	public String appName;
	public String reviewId;
	public boolean isAdRelated;
		
	public boolean isAdRelated() {
		return isAdRelated;
	}
	public void setAdRelated(boolean isAdRelated) {
		this.isAdRelated = isAdRelated;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	public String getReviewTitle() {
		return reviewTitle;
	}
	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getReviewTime() {
		return reviewTime;
	}
	public void setReviewTime(String reviewTime) {
		this.reviewTime = reviewTime;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getReviewId() {
		return reviewId;
	}
	public void setReviewId(String reviewId) {
		this.reviewId = reviewId;
	} 
}
