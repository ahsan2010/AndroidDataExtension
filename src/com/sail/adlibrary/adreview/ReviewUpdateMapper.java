package com.sail.adlibrary.adreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.analysis.util.DateUtil;
import com.sail.mobile.model.Reviews;
import com.sail.mobile.model.UpdateTable;

public class ReviewUpdateMapper {

	public final String UPDATE_PATH = ProjectConstants.STUDIED_APP_UPDATES;
	HashMap<String, ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader.readUpdateData(UPDATE_PATH);
	List<String> adKeyWords = Arrays.asList("ad", "ads", "advert", "advertise", "advertisement", "advertisement");

	public boolean isPresentAdKeyword(String text) {
		String words[] = text.split("\\s+");
		for (String word : words) {
			if (adKeyWords.contains(word)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAdRelatedReview(String reviewTitle, String reviewText) {

		reviewTitle = reviewTitle.toLowerCase();
		reviewText = reviewText.toLowerCase();

		if (isPresentAdKeyword(reviewTitle) || isPresentAdKeyword(reviewText)) {
			return true;
		}

		return false;
	}

	public String getAppUpdateOfThisReview(String appName, String reviewTime) {

		if (!appUpdates.containsKey(appName)) {
			return "MISSING APP";
		}
		// System.out.println(appUpdates.get(appName).size() + " " +
		// appUpdates.get(appName).get(0).getRELEASE_DATE() + " " + reviewTime);

		DateTime reviewJodaDateTime = DateUtil.formatterWithHyphen.parseDateTime(reviewTime);
		ArrayList<UpdateTable> updates = appUpdates.get(appName);
		for (int i = 1; i < updates.size(); i++) {
			UpdateTable oldUpdate = updates.get(i - 1);
			UpdateTable newUpdate = updates.get(i);
			DateTime oldUpdateRleaseDate = DateUtil.formatterWithHyphen.parseDateTime(oldUpdate.getRELEASE_DATE());
			DateTime newUpdateRleaseDate = DateUtil.formatterWithHyphen.parseDateTime(newUpdate.getRELEASE_DATE());

			// System.out.println(oldUpdate.getRELEASE_DATE() + " " +
			// newUpdate.getRELEASE_DATE());

			if (reviewJodaDateTime.isAfter(oldUpdateRleaseDate.minusDays(1))
					&& reviewJodaDateTime.isBefore(newUpdateRleaseDate)) {
				String oldUpdateKey = appName + "-" + oldUpdate.getVERSION_CODE();
				return oldUpdateKey;
			}

			if (i == updates.size() - 1) {
				if (reviewJodaDateTime.isAfter(newUpdateRleaseDate.minusDays(1))
						&& reviewJodaDateTime.isBefore(ProjectConstants.EPERIMENT_END_TIME.plusMonths(2))) {
					String newUpdateKey = appName + "-" + newUpdate.getVERSION_CODE();
					return newUpdateKey;
				}
			}
		}
		if (updates.size() == 1) {
			UpdateTable oldUpdate = updates.get(0);
			DateTime oldUpdateRleaseDate = DateUtil.formatterWithHyphen.parseDateTime(oldUpdate.getRELEASE_DATE());
			if (reviewJodaDateTime.isAfter(oldUpdateRleaseDate.minusDays(1))
					&& reviewJodaDateTime.isBefore(ProjectConstants.EPERIMENT_END_TIME.plusMonths(2))) {
				String oldUpdateKey = appName + "-" + oldUpdate.getVERSION_CODE();
				return oldUpdateKey;
			}

		}
		return "COULD NOT FIND MAPPING";
	}

	public void mapReviewToAppUpdate() throws Exception {

		Map<String, ArrayList<Reviews>> reviewUpdateList = new HashMap<String, ArrayList<Reviews>>();

		CsvReader reader = new CsvReader(ProjectConstants.REVIEW_DATA);
		reader.setSafetySwitch(false);
		reader.readHeaders();
		int missingReviewUpdateMaping = 0;
		int finishReviewReading = 0;
		int problemInRating = 0;
		int missingApp = 0;
		while (reader.readRecord()) {
			try {
				String reviewId = reader.get("REVIEW_ID");
				String appName = reader.get("PACKAGE_NAME");
				String reviewTitle = reader.get("REVIEW_TITLE");
				String reviewText = reader.get("REVIEW_TEXT");
				String reviewTime = reader.get("REVIEW_TIME").trim().split("\\s")[0];
				String reviewRating = reader.get("RATING");

				// System.out.println(reviewId + " " + appName + " " +
				// reviewRating + " " + reviewTime + " " +
				// reader.get("REVIEW_TIME"));

				if (reviewRating.equals("NULL") || reviewTime.length() <= 0) {
					++problemInRating;
					continue;
				}

				++finishReviewReading;

				String updateKey = getAppUpdateOfThisReview(appName, reviewTime);

				if (updateKey.equals("MISSING APP")) {
					++missingApp;
					// break;
					continue;
				}

				if (updateKey.equals("COULD NOT FIND MAPPING")) {
					++missingReviewUpdateMaping;

					/*
					 * DateTime reviewJodaDateTime =
					 * DateUtil.formatterWithHyphen.parseDateTime(reviewTime);
					 * if(reviewJodaDateTime.isBefore(Constants.
					 * EPERIMENT_START_TIME.plusDays(10))){ continue; }
					 */

					/*
					 * System.out.println(appName);
					 * System.out.println("["+reviewTime +"] Total Reviews ["
					 * +finishReviewReading+"] Missing App ["+missingApp+"] "
					 * +" Missing mapping reviews ["
					 * +missingReviewUpdateMaping+"] Missing Rating ["
					 * +problemInRating+"]"); break;
					 */
					continue;
				}
				// We find the update.
				double rating = Double.parseDouble(reviewRating);
				Reviews review = new Reviews();
				review.setAppName(appName);
				review.setReviewId(reviewId);
				review.setRating(rating);
				review.setReviewText(reviewText);
				review.setReviewTitle(reviewTitle);
				review.setReviewTime(reviewTime);
				if (isAdRelatedReview(reviewTitle, reviewText)) {
					review.setAdRelated(true);
				} else {
					review.setAdRelated(false);
				}

				if (!reviewUpdateList.containsKey(updateKey)) {
					reviewUpdateList.put(updateKey, new ArrayList<Reviews>());
				}
				reviewUpdateList.get(updateKey).add(review);
				System.out.println("F [" + reviewTime + "] Total Reviews [" + finishReviewReading + "] Missing App ["
						+ missingApp + "] " + " Missing mapping reviews [" + missingReviewUpdateMaping
						+ "] Missing Rating [" + problemInRating + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		System.out.println("Total [" + reviewUpdateList.size() + "]");

		FileOutputStream f = new FileOutputStream(new File(ProjectConstants.AD_REVIEW_SERILIAZED_DATA_PER_APP_UPDATE));
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(reviewUpdateList);
		o.close();
	}

	public static void main(String[] args) throws Exception {
		ReviewUpdateMapper ob = new ReviewUpdateMapper();
		ob.mapReviewToAppUpdate();
		System.out.println("[Program finishes successfully]");
	}
}
