package com.sail.mobile.analysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.Constants;
import org.joda.time.DateTime;

import com.csvreader.CsvReader;
import com.sail.mobile.model.AppInfoAPK;
import com.sail.mobile.model.UpdateTable;

public class FileUtil {

	public static boolean isFileEndsWithCertainExtention(File file, String extention) {
		return (file.isFile() && file.getName().endsWith("." + extention));
	}

	/**
	 * Lists all files that have a certain extention. The script traver depth
	 * one layer (all files in the current directroy) and not ythe
	 * sub-directories.
	 * 
	 * @param dirName
	 * @return
	 */
	public static ArrayList<String> listFiles(String dirName, String fileExtention) {

		ArrayList<String> filesList = new ArrayList<String>();
		try {
			File directory = new File(dirName);

			File[] fList = directory.listFiles();
			// System.out.println(dirName +" " + fileExtention);
			for (File file : fList) {
				// System.out.println(file);
				if (isFileEndsWithCertainExtention(file, fileExtention)) {
					filesList.add(file.getAbsolutePath());
				}

			}
			// System.out.println("Finish reading file location [" +
			// fList.length + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filesList;

	}

	public static ArrayList<String> listFilesII(String dirName, String fileExtention, ArrayList<String> filesList) {

		try {
			File directory = new File(dirName);

			File[] fList = directory.listFiles();

			for (File file : fList) {
				if (file.isFile()) {
					if (isFileEndsWithCertainExtention(file, fileExtention)) {
						filesList.add(file.getAbsolutePath());
					}

				} else if (file.isDirectory()) {
					// System.out.println(file.getAbsolutePath());
					listFilesII(file.getAbsolutePath(), fileExtention, filesList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return filesList;

	}

	public static Set<String> getStudyUpdateList(HashMap<String, ArrayList<UpdateTable>> appUpdates) {
		Set<String> studyUpdateList = new HashSet<String>();
		for (String appName : appUpdates.keySet()) {
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			for (UpdateTable update : updates) {
				studyUpdateList.add(update.getPACKAGE_NAME() + "-" + update.getVERSION_CODE());
			}
		}

		return studyUpdateList;
	}

	public static String getVersionCode(String st) {
		String code = "";

		String temp = st.substring(st.indexOf("-") + 1);
		temp = temp.substring(0, temp.indexOf("-"));
		// System.out.println("Hello: " + temp);

		return code;
	}

	/**
	 * Removes the inner class name for example "com.google.MathUtil$1" will be
	 * "com.google.MathUtil"
	 * 
	 * @param fullClassIdentifier
	 * @return
	 */
	public static String removeInnerClassName(String fullClassIdentifier) {
		if (fullClassIdentifier.contains("$")) {
			String modifiedText;
			modifiedText = fullClassIdentifier.substring(0, fullClassIdentifier.indexOf("$"));
			return modifiedText;
		}
		return fullClassIdentifier;
	}

	public static boolean hasInnerClass(String fullClassIdentifier) {
		if (fullClassIdentifier.contains("$")) {
			return true;
		}
		return false;
	}

	/**
	 * Generate update key for the studied updates
	 * 
	 * @param updates
	 * @return
	 */
	public static Set<String> generateUpdateKey(HashMap<String, ArrayList<UpdateTable>> updates) {
		Set<String> updateKeySet = new HashSet<String>();
		for (String updateKey : updates.keySet()) {
			for (UpdateTable update : updates.get(updateKey)) {
				String key = update.getPACKAGE_NAME() + "," + update.getVERSION_CODE();
				updateKeySet.add(key);
			}
		}
		return updateKeySet;
	}

	public static ArrayList<String> generateJarFileLocation(HashMap<String, String> jarFilesMap) {
		ArrayList<String> jarLoacationList = new ArrayList<String>();
		jarLoacationList.addAll(jarFilesMap.keySet());
		return jarLoacationList;
	}

	/**
	 * This method extracts appName,ReleaseDate,VersionCode From the apk file
	 * Name.
	 * 
	 * @param filePath
	 * @return
	 */
	public static AppInfoAPK getAppPackageInfo(String filePath) {

		String info = filePath.substring(filePath.lastIndexOf("/") + 1);
		String appName = info.substring(0, info.indexOf("-")).trim();
		info = info.substring(info.indexOf("-") + 1);
		String versionCode = info.substring(0, info.indexOf("-")).trim();
		info = info.substring(info.indexOf("-") + 1);
		String dateString = info.substring(0, info.indexOf("-")).trim();

		AppInfoAPK appInfoAPK = new AppInfoAPK(appName, versionCode, dateString);
		return appInfoAPK;

	}

	public static String getClassName(String fullClassIdentifier) {
		String className = fullClassIdentifier.substring(fullClassIdentifier.lastIndexOf(".") + 1).trim();
		return className;
	}

	/**
	 * Reads the data in a file as a simple list of strings
	 */
	public static ArrayList<String> readFileAsList(String fileLocation, boolean skipHeader) throws Exception {
		ArrayList<String> fileRecords = new ArrayList<String>();
		String currentRecord;
		BufferedReader appsFileReader = new BufferedReader(new FileReader(fileLocation));
		if (skipHeader) {
			appsFileReader.readLine();
		}
		while ((currentRecord = appsFileReader.readLine()) != null) {
			fileRecords.add(currentRecord);
		}
		appsFileReader.close();
		return fileRecords;
	}

	
	public static Map<String, String> readVerfiedAdList(String path) {
		Map<String, String> adPackageMapGroup = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			while ((line = br.readLine()) != null) {
				String name[] = line.split(",");
				adPackageMapGroup.put(name[0].trim(), name[1].trim());

				System.out.println(name[0] + " " + name[1]);

			}
			System.out.println("Total Verified Ads: " + adPackageMapGroup.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return adPackageMapGroup;
	}

	public static boolean checkErrorJar(String fileName, Set<String> errorJarList) {

		String jarName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("-"));
		if (errorJarList.contains(jarName))
			return true;

		return false;
	}

	public static void main(String arg[]) {
		FileUtil futil = new FileUtil();
		
	}

}