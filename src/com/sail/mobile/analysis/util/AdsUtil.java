package com.sail.mobile.analysis.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import com.sail.mobile.model.UpdateTable;

public class AdsUtil
{
	/*
	 * Adds keywords
	 */
	public static final String ADS_PACKAGE_PATTERN1 = "\\.ads\\.";
	public static final String ADS_PACKAGE_PATTERN2 = "advertise";

	public static final String ADS_CLASS_PATTERN1 = "^Ads?[A-Z]"; // Starts with Ad[A-Z]
	public static final String ADS_CLASS_PATTERN2 = "Ads?$"; // Ends with Ad
	// public static final String ADS_CLASS_PATTERN3 = "^Ads[A-Z]"; // Starts with Ad[A-Z]
	// public static final String ADS_CLASS_PATTERN4 = "Ads$"; // Ends with Ad
	
	public static final String ADS_CLASS_ISRAEL = "[aA][dD]";

	static Pattern pattern1 = Pattern.compile(ADS_PACKAGE_PATTERN1);
	static Pattern pattern2 = Pattern.compile(ADS_PACKAGE_PATTERN2);
	static Pattern pattern3 = Pattern.compile(ADS_CLASS_PATTERN1);
	static Pattern pattern4 = Pattern.compile(ADS_CLASS_PATTERN2);
	static Pattern patternIsrael = Pattern.compile(ADS_CLASS_ISRAEL);
	/**
	 * Checks if the class is an ADs or not
	 * 
	 * @param libraryName
	 *            The full package identifier for the library (package name and class name)
	 * @return
	 */
	public static boolean isAdsLibrary(String packageName, String className)
	{
		// Generate the matcher with pattern to filter
		boolean result;
		//result = pattern1.matcher(packageName).find() || pattern2.matcher(packageName).find() || pattern3.matcher(className).find() || pattern4.matcher(className).find();
		result = patternIsrael.matcher(packageName).find() || patternIsrael.matcher(className).find();
		return result;
	}

	public static boolean isStudiedAdLibrary(String packageName,Map<String,String> varifiedAdPackageMapGroup){
	
		for(String pkName : varifiedAdPackageMapGroup.keySet()){
			if(packageName.startsWith(pkName)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Obfuscated classes are classes start with lower case letter.
	 * @param className
	 * @return
	 */
	public static boolean isObfuscatedClass(String className)
	{
		try
		{
			if(TextUtil.isEmptyText(className))
			{
				return true;
			}
			return (className.charAt(0) >= 'a' && className.charAt(0) <= 'z');
		}
		catch (Exception e) {
			System.out.println("Error occured with text [" + className + "]");
			throw e;
		}		

	}
	
	/**
	 * Returns the package name till the package ads or advertise.
	 * Otherwise returns the full name (include the class name).
	 * @param fullName
	 * @return
	 */
	public static String summarizedPackageName(String fullName)
	{
		String result = "";
		try{
			if (fullName.contains("ads."))
			{
				result = fullName.substring(0, fullName.lastIndexOf("ads.") + 3);
			}
			else if (fullName.contains("advertise"))
			{
				result =  fullName.substring(0, fullName.lastIndexOf("advertise") + 9);
			}else{
				if(fullName.contains("."))
					result = fullName.substring(0, fullName.lastIndexOf("."));
				else result = fullName;
			} 
			
		}catch(Exception e){
			System.out.println("Error occured with text [" + fullName + "]");
			//throw e;
		}
		result = result.trim();
		return result;
		
	}

	/**
	 * Vlaidates if this updates should be analyzed if it exists in the data set of interestign apps and updates.
	 * @param updates
	 * @param packageName
	 * @param versionCode
	 * @return
	 */
	public static boolean isUpdateIncluded(Map<String,ArrayList<UpdateTable>>updates, String packageName, String  versionCode){
		
		if(!updates.containsKey(packageName))
			return false;
		
		for(UpdateTable update : updates.get(packageName)){
			if(update.getPACKAGE_NAME().equals(packageName) && update.getVERSION_CODE().equals(versionCode))
				return true;
		}
		
		return false;
	}
	
	
	public static void main(String[] args)
	{
		System.out.println("com.ads.safwat.Helper" + " -> " + isAdsLibrary("com.ads.safwat", "Helper"));
		System.out.println("com.advertise.safwat.Helper" + " -> " + isAdsLibrary("com.advertise.safwat", "Helper"));
		System.out.println("com.weadvertise.safwat.Helper" + " -> " + isAdsLibrary("com.weadvertise.safwat", "Helper"));
		System.out.println("com.d.safwat.Ads" + " -> " + isAdsLibrary("com.d.safwat", "Ads"));
		System.out.println("com.d.safwat.AdsHelper" + " -> " + isAdsLibrary("com.d.safwat", "AdsHelper"));
		System.out.println("com.d.safwat.Ad" + " -> " + isAdsLibrary("com.d.safwat", "Ad"));
		System.out.println("com.d.safwat.AdHelper" + " -> " + isAdsLibrary("com.d.safwat", "AdHelper"));

		System.out.println("com.d.safwat.MeAdjustdHelper" + " -> " + isAdsLibrary("com.d.safwat", "MeAdjustdHelper"));
		System.out.println("com.d.safwat.AdjustdHelper" + " -> " + isAdsLibrary("com.d.safwat", "AdjustdHelper"));		
	}

	public static boolean isSubclass( String fullClassName ){
		if(fullClassName.contains("$")){
			return true;
		}
		return false;
	}
	
}