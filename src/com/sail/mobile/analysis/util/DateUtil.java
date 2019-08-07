package com.sail.mobile.analysis.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.sail.common.ProjectConstants;


public class DateUtil {
	public static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy_MM_dd");
	public static DateTimeFormatter formatterWithHyphen = DateTimeFormat.forPattern("yyyy-MM-dd");
	public static DateTimeFormatter formatterWithHyphenII = DateTimeFormat.forPattern("dd-mm-yyyy");
	public static DateFormat javaUtilDateTimeformatter = new SimpleDateFormat("yyyy_MM_dd");
	
	
	public static String generateYearMonth(String dateString){
		return dateString.substring(0,dateString.lastIndexOf("_"));
	}

	public static String getDateTimeBetweenWeek(DateTime sampleDateTime){
		ArrayList<DateTime> weeks = getWeeksInBetween(ProjectConstants.EPERIMENT_START_TIME,ProjectConstants.EPERIMENT_END_TIME);
		for(DateTime d : weeks){
			if(sampleDateTime.isEqual(d)){
				return d.toString().substring(0,d.toString().lastIndexOf("T"));
			}
			if(sampleDateTime.isAfter(d) && sampleDateTime.isBefore(d.plusDays(7))){
				return d.toString().substring(0,d.toString().lastIndexOf("T"));
			}
		}
		return null;
	}
	
	public static String getDateTimeBetweenMonth(DateTime sampleDateTime){
		ArrayList<DateTime> months = getMonthsInBetween(ProjectConstants.EPERIMENT_START_TIME,ProjectConstants.EPERIMENT_END_TIME);
		for(DateTime d : months){
			if(sampleDateTime.isEqual(d)){
				return d.toString().substring(0,d.toString().lastIndexOf("T"));
			}
			if(sampleDateTime.isAfter(d) && sampleDateTime.isBefore(d.plusMonths(1))){
				return d.toString().substring(0,d.toString().lastIndexOf("T"));
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * The first day of the week and all weeks from start date till end date.
	 * 1/1/2017, 8/1/2017 .... 1/5/2018
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static ArrayList<DateTime> getWeeksInBetween(DateTime startDate, DateTime endDate)
	{		
		Weeks weeks = Weeks.weeksBetween(startDate, endDate);
		int numbOfWeek = weeks.getWeeks();
		DateTime tempIncrementDate = startDate;
		
		//System.out.println("Number of Weeks ["+numbOfWeek+"]");
		
		ArrayList<DateTime> firstDaysOfWeek = new ArrayList<DateTime>();
		firstDaysOfWeek.add(tempIncrementDate);
		for(int i = 0 ; i < numbOfWeek ; i ++){			
			tempIncrementDate = tempIncrementDate.plusDays(7);
			firstDaysOfWeek.add(tempIncrementDate);
		}
		return firstDaysOfWeek;
	}
	
	public static ArrayList<DateTime> getMonthsInBetween(DateTime startDate, DateTime endDate){
		ArrayList<DateTime> daysBetween = new ArrayList<DateTime>();
		int durationMonths = Months.monthsBetween(startDate, endDate).getMonths();
		DateTime tempIncrementDate = startDate;
		for(int i = 0 ; i < durationMonths ; i +=2 ){
			daysBetween.add(tempIncrementDate);
			tempIncrementDate = tempIncrementDate.plusMonths(2);
		}
		daysBetween.add(ProjectConstants.EPERIMENT_END_TIME.plusDays(3));
		return daysBetween;
	}
	
	public static ArrayList<DateTime> getMonthsInBetween(DateTime startDate, DateTime endDate, int noMonth){
		ArrayList<DateTime> daysBetween = new ArrayList<DateTime>();
		int durationMonths = Months.monthsBetween(startDate, endDate).getMonths();
		DateTime tempIncrementDate = startDate;
		for(int i = 0 ; i < durationMonths ; i +=noMonth ){
			daysBetween.add(tempIncrementDate);
			tempIncrementDate = tempIncrementDate.plusMonths(noMonth);
		}
		daysBetween.add(ProjectConstants.EPERIMENT_END_TIME.plusDays(3));
		return daysBetween;
	}
	
	public static ArrayList<DateTime> getDaysInBetween(DateTime startDate, DateTime endDate){
		ArrayList<DateTime> daysBetween = new ArrayList<DateTime>();
		int durationDays = Days.daysBetween(startDate, endDate).getDays();
		DateTime tempIncrementDate = startDate;
		for(int i = 0 ; i < durationDays ; i ++ ){
			daysBetween.add(tempIncrementDate);
			tempIncrementDate = tempIncrementDate.plusDays(1);
		}
		daysBetween.add(ProjectConstants.EPERIMENT_END_TIME.plusDays(3));
		return daysBetween;
	}
	
	
	public static ArrayList<DateTime> getQuarterInBetween(DateTime startDate, DateTime endDate){
		ArrayList<DateTime> daysBetween = new ArrayList<DateTime>();
		int durationMonths = Months.monthsBetween(startDate, endDate).getMonths();
		DateTime tempIncrementDate = startDate;
		for(int i = 0 ; i < durationMonths ; i +=4 ){
			daysBetween.add(tempIncrementDate);
			tempIncrementDate = tempIncrementDate.plusMonths(4);
		}
		daysBetween.add(ProjectConstants.EPERIMENT_END_TIME.plusDays(3));
		return daysBetween;
	}
	
	public static ArrayList<DateTime> getQuartersInBetween(DateTime startDate, DateTime endDate){
		ArrayList<DateTime> daysBetween = new ArrayList<DateTime>();
		int durationMonths = Days.daysBetween(startDate, endDate).getDays();
		DateTime tempIncrementDate = startDate;
		for(int i = 0 ; i < durationMonths ; i ++ ){
			daysBetween.add(tempIncrementDate);
			tempIncrementDate = tempIncrementDate.plusDays(15);
		}
		
		return daysBetween;
	}
	
	public static void main(String arg[])throws Exception{		
		DateUtil ob = new DateUtil();
		ArrayList<DateTime> myDateTime = ob.getMonthsInBetween(ProjectConstants.EPERIMENT_START_TIME,ProjectConstants.EPERIMENT_END_TIME);
		for(DateTime d : myDateTime){
			System.out.println(d.toString() +" " + d.toString());
		}	
	}
	
}
