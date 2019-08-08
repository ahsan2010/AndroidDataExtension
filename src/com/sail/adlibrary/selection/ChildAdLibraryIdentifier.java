package com.sail.adlibrary.selection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsUtil;
import com.sail.mobile.analysis.util.FileUtil;

public class ChildAdLibraryIdentifier implements Runnable{

	public  String JAR_DIRECTORY = "/safwatscratch/shassan/Jars/";
	public String OUTPUT_LOCATION =  ProjectConstants.ROOT + "Ad_Library_Identification_Result/";
	public String AD_LIBRARY_MAP = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Data/verified_ads_updated.csv";
	ArrayList<String>updateLink = new ArrayList<String>();
	Map<String,String> varifiedAdPackageMapGroup = FileUtil.readVerfiedAdList(AD_LIBRARY_MAP);
	Set<String> adLibraryImportSet = new HashSet<String>();
	
	int startPosition;
	int endPosition;
	int THREAD_NO;
	int error_files = 0;
	CsvWriter writer;
		
	
	/**
	 * 
	 * @param jarPath
	 * @param jarFile
	 * @return
	 * @throws ClassFormatException
	 * @throws IOException
	 */
	private static Map<String, JavaClass> collectJavaClasses(String jarPath, JarFile jarFile)
			throws ClassFormatException, IOException {
		Map<String, JavaClass> javaClasses = new LinkedHashMap<String, JavaClass>();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			System.out.println(entry.getName());
			if (!entry.getName().endsWith(".class")) {
				continue;
			}
			ClassParser parser = new ClassParser(jarPath, entry.getName());
			JavaClass javaClass = parser.parse();
			javaClasses.put(javaClass.getClassName(), javaClass);
			System.out.println("GOT >>>");
		}
		return javaClasses;
	}
	
	public String getVarifiedAdsPackage(String libraryName, Map<String,String> packageMapGroup){
		for(String pName : packageMapGroup.keySet()){
			if(libraryName.startsWith(pName))
				return packageMapGroup.get(pName);
		}
		return null;
	}
	
	public void incrementAdsCount(String addLibraryName, HashMap<String, Integer> adsUsageCountInfo)
	{
		if(adsUsageCountInfo.containsKey(addLibraryName))
		{
			adsUsageCountInfo.put(addLibraryName, (adsUsageCountInfo.get(addLibraryName)+1));
		}
		else
		{
			adsUsageCountInfo.put(addLibraryName, 1);
		}
	}
	
	public boolean checkAdSubPackage(String adPackage, String appPackage){
		String adPackageList[] = adPackage.split("\\.");
		String appPackageList[] = appPackage.split("\\.");
		int minimumSize = Math.min(adPackageList.length,appPackageList.length);
		int matching = 0;
		
		
		for(int i = 0 ; i < minimumSize ; i ++ ){
			if(!adPackageList[i].equals(appPackageList[i])){
				break;
			}else{
				matching ++;
			}
		}
		if(matching > 1) return true;
		return false;
	}
	
	public boolean checkAdPackage(Map<String, String> varifiedAdPackageMapGroup, String packageName){
		
		for(String adPackage : varifiedAdPackageMapGroup.keySet()){
			if(checkAdSubPackage(adPackage,packageName)){
				return true;
			}
			if(packageName.contains(adPackage)){				
				return true;
			}
		}		
		return false;
	}
	
	public Set<String> isAdLibraryImport(JavaClass investigationClass,Map<String, String> varifiedAdPackageMapGroup){
		ConstantPool cp = investigationClass.getConstantPool();
		boolean findAdPackageImportStatement = false;
		Set<String> adImport = new HashSet<String>();
		if (!checkAdPackage(varifiedAdPackageMapGroup,investigationClass.getPackageName())) {
			for (int i = 0; i < cp.getLength(); i++) {
				//System.out.println(investigationClass.getPackageName());
				if (cp.getConstant(i) != null) {
					if (cp.getConstant(i).getTag() == 1) {
						String ss = cp.getConstant(i).toString();
						ss = ss.substring(ss.indexOf("\"") + 1);
						ss = ss.substring(0, ss.lastIndexOf("\""));
						if (!ss.contains("/") || ss.contains(")") || ss.contains("(") || ss.contains(";")) {
							continue;
						}
						ss = ss.replace("/", ".");						
						for (String s : varifiedAdPackageMapGroup.keySet()) {
							if (ss.contains(s)) {
								adImport.add(varifiedAdPackageMapGroup.get(s));
								findAdPackageImportStatement = true;
							}
						}
					}
				}
			}
		}
		return adImport;
	}
	
	public HashMap<String,Integer> adLibraryDetection(String updateInfo){
		String jarLocation = JAR_DIRECTORY + updateInfo+"-dex2jar.jar";
		HashMap<String,Integer> adsUsageGroupCountInfo = new HashMap<String,Integer>();
		
		try{
			JarFile jarFile = new JarFile(new File(jarLocation));
			Map<String, JavaClass> javaClassesMap  = collectJavaClasses(jarLocation, jarFile);
			System.out.println("Total Java Classes ["+javaClassesMap.size()+"]");
			for (String key : javaClassesMap.keySet()) {
				JavaClass investigationClass = javaClassesMap.get(key);
				String fullClassIdentifier = investigationClass.getClassName();
				fullClassIdentifier = FileUtil.removeInnerClassName(fullClassIdentifier);
				String className = FileUtil.getClassName(fullClassIdentifier);
				String addLibraryName = AdsUtil.summarizedPackageName(fullClassIdentifier);
				if(addLibraryName.length() > 0){
					String groupName = getVarifiedAdsPackage(addLibraryName,varifiedAdPackageMapGroup);
					if(groupName != null){
						incrementAdsCount(groupName, adsUsageGroupCountInfo);
					}
				}				
				Set<String> adImport = isAdLibraryImport(investigationClass, varifiedAdPackageMapGroup);
				adLibraryImportSet.addAll(adImport);
			}			
		}catch(Exception e){
			e.printStackTrace();
			adsUsageGroupCountInfo = null;
			error_files ++;
		}		
		return adsUsageGroupCountInfo;		
	}
	
	public void initCsvWriter() throws Exception{
		String location = OUTPUT_LOCATION +"updates_with_ad_"+THREAD_NO+".csv";
		writer = new CsvWriter(location);
		writer.write("App_Name");
		writer.write("Version_Code");
		writer.write("Date");
		writer.write("Total_Ads");
		writer.write("List_Of_Ads");
		writer.write("Total_Ads_Import");
		writer.write("Total_Ads_Import");
		writer.write("Ads_Class_Count");
		writer.endRecord();
	}
	
	public void showInfo(String appAddress){
		String packageName = appAddress.substring(0,appAddress.indexOf("-"));
		String versionCode = appAddress.substring(appAddress.indexOf("-")+1);
		versionCode = versionCode.substring(0,versionCode.indexOf("-"));
		String releaseDate = appAddress.substring(appAddress.lastIndexOf("-")+1);
		releaseDate = releaseDate.replace("_", "-");
		
		System.out.println("Package Name: " + packageName);
		System.out.println("Version Code: " + versionCode);
		System.out.println("Release Date: " + releaseDate);
	}
	
	public void writeResult(HashMap<String,Integer> adsUsageGroupCountInfo, String appAddress) throws Exception{
		
		String packageName = appAddress.substring(0,appAddress.indexOf("-"));
		String versionCode = appAddress.substring(appAddress.indexOf("-")+1);
		versionCode = versionCode.substring(0,versionCode.indexOf("-"));
		String releaseDate = appAddress.substring(appAddress.lastIndexOf("-")+1);
		releaseDate = releaseDate.replace("_", "-");		
		
		String adsName="";
		String adsClass="";
		String adsImport = "";
		int totalGroup = adsUsageGroupCountInfo.size();
		int totalImport = adLibraryImportSet.size();
		for(String groupName : adsUsageGroupCountInfo.keySet()){
			adsName += groupName;
			adsClass += groupName + ">" + adsUsageGroupCountInfo.get(groupName);
			if(--totalGroup > 0){
				adsName = adsName+"-";
				adsClass = adsClass +"-";
			}
		}
		
		for(String ad : adLibraryImportSet){
			adsImport += ad;
			if(--totalImport > 0 ){
				adsImport+="-";
			}
		}
		writer.write(packageName);
		writer.write(versionCode);
		writer.write(releaseDate);
		writer.write(Integer.toString(adsUsageGroupCountInfo.size()));
		writer.write(adsName);
		writer.write(Integer.toString(adLibraryImportSet.size()));
		writer.write(adsImport);
		writer.write(adsClass);
		writer.endRecord();
	}
	
	public void run() {
		int total = 0;
		try{
			initCsvWriter(); // Csv Writer column names
			for(int i = startPosition ; i < endPosition ; i ++ ){				
				try{
					HashMap<String,Integer> adsUsageGroupCountInfo = adLibraryDetection(updateLink.get(i));
					if(adsUsageGroupCountInfo != null){
						writeResult(adsUsageGroupCountInfo,updateLink.get(i));
						adLibraryImportSet.clear();
					}
					System.out.println("["+THREAD_NO+"] Process ["+(++total)+"]"+" Error ["+error_files+"] ["+ (JAR_DIRECTORY + updateLink.get(i))+"]");
				}catch(Exception e){
					System.err.println("Missing File");
					System.out.println("PROBLEM ["+THREAD_NO+"] Process ["+(++total)+"]"+" Error ["+error_files+"] ["+ (JAR_DIRECTORY + updateLink.get(i))+"]");
				}
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public ChildAdLibraryIdentifier(){
		
	}
	public ChildAdLibraryIdentifier(ArrayList<String> updateLink, int start, int end, int threadNo){
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
	}
	public static void main(String[] args) {
		ChildAdLibraryIdentifier ob = new ChildAdLibraryIdentifier();
		ob.showInfo("com.digitalchemy.calculator.freedecimal-4950-2015_12_18");
	}

}
