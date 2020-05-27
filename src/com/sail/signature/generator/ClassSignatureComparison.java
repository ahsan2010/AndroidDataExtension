package com.sail.signature.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.synth.SynthScrollBarUI;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsInputDataLoader;
import com.sail.mobile.analysis.util.DateUtil;
import com.sail.mobile.analysis.util.FileUtil;
import com.sail.mobile.analysis.util.TextUtil;
import com.sail.mobile.model.AdInformation;
import com.sail.mobile.model.UpdateTable;

public class ClassSignatureComparison {

	String SIGNATURE_PATH = "/archive/ahsan/ClassSignaturePerAppUpdate";
	HashMap<String, ArrayList<UpdateTable>> appUpdates = AdsInputDataLoader
			.readUpdateData(ProjectConstants.STUDIED_APP_UPDATES);
	Map<String, AdInformation> appUseAd = FileUtil.readAdLibraryInformationII();

	public static Map<String, String> varifiedAdPackageMapGroup = FileUtil
			.readVerfiedAdList(ProjectConstants.VARIFIED_AD_PACKAGE_LIST);

	Map<String, ArrayList<AdInformation>> appUseAdsPerApp = FileUtil.readAdLibraryInformationPerApp(ProjectConstants.ADS_UPDATE_DATA_PATH);

	public String hasAdLibrary(String packageName) {
		for (String adPackage : varifiedAdPackageMapGroup.keySet()) {
			if (packageName.contains(adPackage)) {
				String lib = varifiedAdPackageMapGroup.get(adPackage);
				return lib;
			}
		}
		return "";
	}

	public ClassSignature readSignature(String key) throws Exception {
		ClassSignature classSignature = new ClassSignature();
		String filePath = SIGNATURE_PATH + "/" + "Signature_" + key + ".csv";
		CsvReader reader = new CsvReader(filePath);
		reader.readHeaders();

		while (reader.readRecord()) {
			String appName = reader.get("App_Name");
			String versionCode = reader.get("Version_Code");
			String packageName = reader.get("Class_Package_Name");
			String className = reader.get("Class_Name");
			String signature = reader.get("Signature");
			String updateKey = appName + "-" + versionCode;

			String fullPackageName = packageName + "." + className;

			String adLib = hasAdLibrary(packageName);

			Set<String> useAds = new HashSet<String>();
			if (appUseAd.containsKey(updateKey)) {
				useAds = appUseAd.get(updateKey).getAdsNames();
			}

			if (adLib.length() > 0) {
				if (!classSignature.classSignatureByLibrary.containsKey(adLib)) {
					classSignature.classSignatureByLibrary.put(adLib, new ArrayList<String>());
				}
				classSignature.classSignatureByLibrary.get(adLib).add(fullPackageName + "-" + signature);
				continue;
			}

			if (packageName.contains(appName)) {
				classSignature.internalCodeHasAdImplementation.add(signature);
				continue;
			}

			classSignature.appOtherCode.add(signature);
		}
		classSignature.versionCode = key.split("-")[1];
		classSignature.releaseDate = key.split("-")[2];

		// System.out.println("SIGNATURE: " + classSignature.versionCode + " " +
		// classSignature.releaseDate);

		return classSignature;
	}

	public Set<String> adLibraryUpdates(ClassSignature oldSignature, ClassSignature newSignature) {
		Set<String> adLibs = new HashSet<String>();

		for (String adLib : newSignature.classSignatureByLibrary.keySet()) {
			double total = 0;
			ArrayList<String> newAdLibSig = newSignature.classSignatureByLibrary.get(adLib);
			// System.out.println("Lib: " + adLib + " " + newAdLibSig.size());
			if (oldSignature.classSignatureByLibrary.containsKey(adLib)) {
				ArrayList<String> oldAdLibSig = oldSignature.classSignatureByLibrary.get(adLib);
				for (String sig : newAdLibSig) {
					if (!oldAdLibSig.contains(sig)) {
						total++;
					}
				}
			}
			double ratio = total / newAdLibSig.size() * 100;

			if (ratio > 15) {
				adLibs.add(adLib);
				// System.out.println(adLib + " " + ratio);
			}
		}

		return adLibs;
	}

	public Set<String> adLibraryUpdatesII(ClassSignature oldSignature, ClassSignature newSignature) {
		Set<String> adLibs = new HashSet<String>();
		System.out.println("[ENTER ZONE	]");
		for (String adLib : newSignature.classSignatureByLibrary.keySet()) {
			double total = 0;
			ArrayList<String> newAdLibSig = newSignature.classSignatureByLibrary.get(adLib);
			// System.out.println("Lib: " + adLib + " " + newAdLibSig.size());

			if (oldSignature.classSignatureByLibrary.containsKey(adLib)) {
				ArrayList<String> oldAdLibSig = oldSignature.classSignatureByLibrary.get(adLib);
				for (String sig : newAdLibSig) {
					String newPackage = sig.split("-")[0].trim();
					String newSigString = sig.split("-")[1].trim();
					for (String oldSig : oldSignature.classSignatureByLibrary.get(adLib)) {
						String oldPackage = oldSig.split("-")[0].trim();
						String oldSigString = oldSig.split("-")[1].trim();
						if (oldPackage.equals(newPackage)) {
							if (!oldSigString.equals(newSigString)) {
								// System.out.println(oldSigString +" " +
								// newSigString);
								System.out.println(sig.split("-")[0]);
								total++;
							}
						}

					}
				}
			}
			double ratio = total / newAdLibSig.size() * 100;

			if (ratio > 15) {
				adLibs.add(adLib);
				System.out.println(adLib + " " + ratio);
			}
		}

		return adLibs;
	}

	public int nonAdCodeHasAdUpdate(ClassSignature oldSignature, ClassSignature newSignature) {
		int total = 0;
		int addDelete = 0;

		if (newSignature.internalCodeHasAdImplementation.size() < oldSignature.internalCodeHasAdImplementation.size()) {
			// total +=
			// Math.abs(newSignature.internalCodeHasAdImplementation.size()
			// - oldSignature.internalCodeHasAdImplementation.size());
		}

		for (String newClassSignature : newSignature.internalCodeHasAdImplementation) {
			if (!oldSignature.internalCodeHasAdImplementation.contains(newClassSignature)) {
				// System.out.println("CHANGES");
				total++;
			}
		}

		// System.out.println(newSignature.internalCodeHasAdImplementation.size()
		// +" " + oldSignature.internalCodeHasAdImplementation.size());

		return total;
	}

	public void test() {

		List<String> investigatedFile = Arrays.asList("com.oki.phonenew-1006004-2016_01_27",
				"com.oki.phonenew-1006006-2016_05_13", "com.oki.phonenew-1006011-2016_11_17",
				"com.oki.phonenew-1006012-2016_11_17", "com.oki.phonenew-1006013-2016_12_21",
				"com.oki.phonenew-1006014-2016_12_21");

		for (int i = 1; i < investigatedFile.size(); i++) {
			System.out.println(investigatedFile.get(i));
			try {

				String oldVersion = investigatedFile.get(i - 1);
				String newVersion = investigatedFile.get(i);

				ClassSignature oldSignature = readSignature(oldVersion);
				ClassSignature newSignature = readSignature(newVersion);

				int nonAdCodeHasAdImplChange = nonAdCodeHasAdUpdate(oldSignature, newSignature);
				Set<String> adLibCodeChanges = adLibraryUpdates(oldSignature, newSignature);

				// System.out.println("TOTAL AD IMPLEMENTATION CLASSES: New: " +
				// newSignature.internalCodeHasAdImplementation.size() +" OLD: "
				// + oldSignature.internalCodeHasAdImplementation.size());
				// System.out.println("NON AD CODE HAS AD IMPLEMENTAITON
				// CHANGES: " + nonAdCodeHasAdImplChange);
				System.out.println("AD LIBRARY CHANGES: " + adLibCodeChanges.size() + " " + nonAdCodeHasAdImplChange);
				for (String adLib : adLibCodeChanges) {
					System.out.println("CHANGE: " + adLib);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// break;
		}

	}

	public void analyzeSignatureChagnes() throws Exception {

		int totalAppFinishAnalyze = 0;

		for (String appName : appUpdates.keySet()) {
			System.out.println("Finish analyinz app [" + (++totalAppFinishAnalyze) + "]");
			ArrayList<UpdateTable> updates = appUpdates.get(appName);
			System.out.println(appName);
			for (int i = 1; i < updates.size(); i++) {
				try {

					UpdateTable oldUpdate = updates.get(i - 1);
					UpdateTable newUpdate = updates.get(i);

					String oldVersion = oldUpdate.getPACKAGE_NAME() + "-" + oldUpdate.getVERSION_CODE() + "-"
							+ oldUpdate.getRELEASE_DATE().replace("-", "_");
					String newVersion = newUpdate.getPACKAGE_NAME() + "-" + newUpdate.getVERSION_CODE() + "-"
							+ newUpdate.getRELEASE_DATE().replace("-", "_");
					ClassSignature oldSignature = readSignature(oldVersion);
					ClassSignature newSignature = readSignature(newVersion);

					int nonAdCodeHasAdImplChange = nonAdCodeHasAdUpdate(oldSignature, newSignature);
					Set<String> adLibCodeChanges = adLibraryUpdates(oldSignature, newSignature);

				} catch (Exception e) {

				}
			}

		}
	}

	public void analyzeSignatureChagnesAdLibUpdates() throws Exception {

		Map<String, Set<String>> adLibUpdatesPerApp = new HashMap<String, Set<String>>();
		Map<String, Set<String>> adLibraryUsedByTotalApp = new HashMap<String, Set<String>>();

		Map<String, Set<String>> appUpdatesAdLib = new HashMap<String, Set<String>>();
		Map<String, Set<String>> revertBackAdLib = new HashMap<String, Set<String>>();

		int totalAppFinishAnalyze = 0;
		//
		for (String appName : appUseAdsPerApp.keySet()) {
			ArrayList<AdInformation> updateAdLibs = appUseAdsPerApp.get(appName);
			for (AdInformation adInfo : updateAdLibs) {
				for (String lib : adInfo.getAdsNames()) {
					if (!adLibraryUsedByTotalApp.containsKey(lib)) {
						adLibraryUsedByTotalApp.put(lib, new HashSet<String>());
					}
					adLibraryUsedByTotalApp.get(lib).add(appName);

				}
			}
		}
		int totalUpdate = 0;

		for (String appName : appUseAdsPerApp.keySet()) {

			ArrayList<ClassSignature> previousClassSignatures = new ArrayList<ClassSignature>();

			/*
			 * if(!appName.equals("wp.wattpad")){ continue; }
			 */
			System.out.println("Finish analyinz app [" + (++totalAppFinishAnalyze) + "] " + appName);
			ArrayList<AdInformation> updates = appUseAdsPerApp.get(appName);
			// System.out.println("SIZE : " + updates.size());
			for (int i = 1; i < updates.size(); i++) {
				try {
					// System.out.println(updates.get(i).getPackageName() + " "
					// + (++totalUpdate));
					AdInformation oldUpdate = updates.get(i - 1);
					AdInformation newUpdate = updates.get(i);

					String oldVersion = oldUpdate.getPackageName() + "-" + oldUpdate.getVersionCode() + "-"
							+ oldUpdate.getReleaseDate().replace("-", "_");
					String newVersion = newUpdate.getPackageName() + "-" + newUpdate.getVersionCode() + "-"
							+ newUpdate.getReleaseDate().replace("-", "_");

					// System.out.println(oldVersion + " " + newVersion);

					ClassSignature oldSignature = readSignature(oldVersion);
					ClassSignature newSignature = readSignature(newVersion);

					int nonAdCodeHasAdImplChange = nonAdCodeHasAdUpdate(oldSignature, newSignature);
					Set<String> adLibCodeChanges = adLibraryUpdates(oldSignature, newSignature);

					String presentAdKey = newUpdate.getPackageName() + "-" + newUpdate.getVersionCode() + "-"
							+ newUpdate.getReleaseDate().replace("-", "_");

					if (adLibCodeChanges.size() > 0) {
						appUpdatesAdLib.put(presentAdKey, adLibCodeChanges);
					}
					for (String lib : adLibCodeChanges) {
						if (!adLibUpdatesPerApp.containsKey(lib)) {
							adLibUpdatesPerApp.put(lib, new HashSet<String>());
						}
						adLibUpdatesPerApp.get(lib).add(appName);
					}

					// check Revertback to old ad library
					for (String lib : adLibCodeChanges) {
						for (ClassSignature clSignature : previousClassSignatures) {
							if (clSignature.classSignatureByLibrary.containsKey(lib)) {
								// System.out.println("CHECK");
								ArrayList<String> signatureOld = clSignature.classSignatureByLibrary.get(lib);
								ArrayList<String> signaturePresent = newSignature.classSignatureByLibrary.get(lib);
								boolean similar = true;
								int matching = 0;
								// System.out.println(lib + " Signature old " +
								// signatureOld.size() + " Signature present " +
								// signaturePresent.size());
								for (String signature : signaturePresent) {
									if (!signatureOld.contains(signature)) {
										similar = false;
										break;
									} else {
										++matching;
									}
								}
								double ratio = 100.0 * (double) matching / signaturePresent.size();
								/*
								 * if( ratio > 80.0 ){ System.out.println(lib +
								 * " Ratio " + ratio); }
								 */
								// System.out.println(lib + " Ratio " + ratio);
								// System.out.println(lib + " M: " + matching +
								// " T : " + signaturePresent.size());
								if (similar && (signatureOld.size() == signaturePresent.size())) {

									String keyLibRevert = appName + "," + newSignature.versionCode + ","
											+ newSignature.releaseDate.replace("_", "-");
									keyLibRevert = keyLibRevert + ">" + appName + "," + clSignature.versionCode + ","
											+ clSignature.releaseDate.replace("_", "-");

									
									System.out.println(keyLibRevert);
									
									if (!revertBackAdLib.containsKey(keyLibRevert)) {
										revertBackAdLib.put(keyLibRevert, new HashSet<String>());
									}
									revertBackAdLib.get(keyLibRevert).add(lib);
									System.out.println("ROLLBACK");
								}
							}
						}
					}

					previousClassSignatures.add(newSignature);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		//writeAdLibraryUpdate(adLibUpdatesPerApp, adLibraryUsedByTotalApp);
		//writeAdLibUpdatePerAppUpate(appUpdatesAdLib);
		writeAdLibraryRevertBack(revertBackAdLib);
	}

	public void writeAdLibraryRevertBack(Map<String, Set<String>> revertBackAdLib) throws Exception {
		CsvWriter writer = new CsvWriter(ProjectConstants.ROOT + "Result/AdLibrary_Update/ad_lib_update_rollback_File_NO_Similar.csv");
		writer.write("App_Name");
		writer.write("From_Version_Code");
		writer.write("To_Version_Code");
		writer.write("From_Release_Date");
		writer.write("To_Release_Date");
		writer.write("No_of_days");
		writer.write("Ad_Libraries_Revert_Back");
		writer.endRecord();

		for (String revertKey : revertBackAdLib.keySet()) {
			String appsInfo[] = revertKey.split(">");
			
			String fromAppInfo[] = appsInfo[0].split(",");
			String toAppInfo[] = appsInfo[1].split(",");

			writer.write(fromAppInfo[0]);
			writer.write(fromAppInfo[1]);
			writer.write(toAppInfo[1]);
			writer.write(fromAppInfo[2]);
			writer.write(toAppInfo[2]);

			DateTime fromDate = DateUtil.formatterWithHyphen.parseDateTime(fromAppInfo[2]);
			DateTime toDate = DateUtil.formatterWithHyphen.parseDateTime(toAppInfo[2]);
			int days = Days.daysBetween(toDate, fromDate).getDays();

			writer.write(Integer.toString(days));
			writer.write(TextUtil.setToString(revertBackAdLib.get(revertKey)));
			writer.endRecord();
		}

		writer.close();
	}

	public void writeAdLibUpdatePerAppUpate(Map<String, Set<String>> appUpdatesAdLib) throws Exception {
		CsvWriter writer = new CsvWriter(ProjectConstants.ROOT + "Result/AdLibrary_Update/app_update_ad_lib.csv");
		writer.write("App_Name");
		writer.write("Version_Code");
		writer.write("Release_Date");
		writer.write("Total_Ads");
		writer.write("Update_Ads");
		writer.write("List_Total_Ads");
		writer.write("List_of_Updates");
		writer.endRecord();

		for (String key : appUpdatesAdLib.keySet()) {
			String appInfo[] = key.split("-");
			String appKey = appInfo[0] + "-" + appInfo[1];
			if (!appUseAd.containsKey(appKey)) {
				continue;
			}

			writer.write(appInfo[0]);
			writer.write(appInfo[1]);
			writer.write(appInfo[2]);

			writer.write(Integer.toString(appUseAd.get(appKey).getAdsNames().size()));
			writer.write(Integer.toString(appUpdatesAdLib.get(key).size()));
			writer.write(TextUtil.setToString(appUseAd.get(appKey).getAdsNames()));
			writer.write(TextUtil.setToString(appUpdatesAdLib.get(key)));
			writer.endRecord();
		}
		writer.close();
	}

	public void writeAdLibraryUpdate(Map<String, Set<String>> adLibUpdatesPerApp,
			Map<String, Set<String>> adLibraryUsedByTotalApp) throws Exception {
		CsvWriter writer = new CsvWriter(ProjectConstants.ROOT + "Result/AdLibrary_Update/lib_update_data.csv");
		writer.write("Ad_Library");
		writer.write("#_of_app_updates");
		writer.write("#_of_app_use_ad_library");
		writer.endRecord();

		for (String lib : adLibUpdatesPerApp.keySet()) {
			writer.write(lib);
			writer.write(Integer.toString(adLibUpdatesPerApp.get(lib).size()));
			if (adLibraryUsedByTotalApp.containsKey(lib)) {
				writer.write(Integer.toString(adLibraryUsedByTotalApp.get(lib).size()));
			} else {
				writer.write("0");
			}

			writer.endRecord();
		}

		writer.close();

	}

	public static void main(String[] args) throws Exception {
		ClassSignatureComparison ob = new ClassSignatureComparison();
		long startTime = System.currentTimeMillis();
		ob.analyzeSignatureChagnesAdLibUpdates();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time [" + totalTime / (1000 * 60) + "] minutes");
		// ob.test();
		System.out.println("Program finishes successfully");
		/*String s[] = "com.bestweatherfor.bibleoffline_pt_ra,2770410,2018-05-22>com.bestweatherfor.bibleoffline_pt_ra,2770010,2018-04-27".split(">");
		for(String w : s){
			System.out.println(w);
		}*/
	}
}
