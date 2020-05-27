package com.sail.mobile.analysis.util;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class TextUtil {
	
	static DecimalFormat INTEGER_FORMATTER = new DecimalFormat("#,###");
	static DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,###.#");
	
	public static final String ANALYTICS_CLASS = "analytic";
	static Pattern patternAnalyticsLibrary = Pattern.compile(ANALYTICS_CLASS);
	
	public static boolean isAnalyticsLibrary(String packageName, String className)
	{
		// Generate the matcher with pattern to filter
		boolean result;
		//result = pattern1.matcher(packageName).find() || pattern2.matcher(packageName).find() || pattern3.matcher(className).find() || pattern4.matcher(className).find();
		result = patternAnalyticsLibrary.matcher(packageName.toLowerCase()).find() || patternAnalyticsLibrary.matcher(className.toLowerCase()).find();
		return result;
	}
	
	
	public static boolean isEmptyText(String text)
	{
		return (text == null) || text.isEmpty();
	}

	public static String getReadableIntegerNumber(long number)
	{
		return INTEGER_FORMATTER.format(number);
	}

	public static String getReadableFloatNumber(double number)
	{
		return INTEGER_FORMATTER.format(number);
	}
	
	public static void printSetsDiff(Set<String> setA, Set<String> setB, String setAName, String setBName)
	{
		boolean isSetsMatched = true;
		int diffCount = 0;
		for(String element : setA)
		{
			if(!setB.contains(element))
			{
				System.out.println(setBName + " missing element [" + element + "]");
				isSetsMatched=false;
				diffCount++;
			}
		}
		
		for(String element : setB)
		{
			if(!setA.contains(element))
			{
				System.out.println(setAName + " missing element [" + element + "]");
				isSetsMatched=false;
				diffCount++;
			}
		}
		
		System.out.println("The two sets [" + setAName + "] and [" + setBName + "] are matched result = [" + isSetsMatched + "] with [" + diffCount + "] differences.");

	}
	
	
	/**
	 * 
	 * 
	 * @param info
	 * @return
	 */
	public static String setToString(Set<String>info){
		if(info == null){
			return "";
		}
		int totalSize = info.size();
		String infoString = "";
		for(String s : info){
			totalSize --;
			infoString += s;
			if(totalSize > 0 ){
				infoString += "-";
			}
		}
		return infoString;
	}
	
	/**
	 * 
	 * @param info
	 * @return
	 */
	public static Set<String> convertStringToSet(String info){
		Set<String> infoList = new HashSet<String>();
		for (String w : info.split("-")){
			w = w.trim();
			infoList.add(w);
		}
		return infoList;
	}
	
	public static void main(String[] args)
	{
		HashSet<String> setA = new HashSet<String> ();
		HashSet<String> setB = new HashSet<String> ();
		setA.add("A");
		setA.add("B");
		setB.add("B");
		setB.add("C");
		
		printSetsDiff(setA, setB, "setAName", "setBName");
	}
}
