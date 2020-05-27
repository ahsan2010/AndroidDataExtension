package com.sail.appPackage.extraction;

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

public class ChildAdPackageIdentifier implements Runnable{

	public  String JAR_DIRECTORY = "/safwatscratch/shassan/Jars/";
	public String OUTPUT_LOCATION =  ProjectConstants.ROOT + "PackageNameExtraction/";
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
			//System.out.println(entry.getName());
			if (!entry.getName().endsWith(".class")) {
				continue;
			}
			ClassParser parser = new ClassParser(jarPath, entry.getName());
			JavaClass javaClass = parser.parse();
			javaClasses.put(javaClass.getClassName(), javaClass);
			//System.out.println("GOT >>>");
		}
		return javaClasses;
	}
	
	public HashMap<String,Integer> adLibraryDetection(String updateInfo){
		String jarLocation = JAR_DIRECTORY + updateInfo+"-dex2jar.jar";
		System.err.println("Jar Location " + jarLocation);
		HashMap<String,Integer> adsUsageGroupCountInfo = new HashMap<String,Integer>();
		
		try{
			JarFile jarFile = new JarFile(new File(jarLocation));
			Map<String, JavaClass> javaClassesMap  = collectJavaClasses(jarLocation, jarFile);
			System.out.println("Total Java Classes ["+javaClassesMap.size()+"]");
			for (String key : javaClassesMap.keySet()) {
				JavaClass investigationClass = javaClassesMap.get(key);
				String packageName = investigationClass.getPackageName();
				String fullClassIdentifier = investigationClass.getClassName();
				fullClassIdentifier = FileUtil.removeInnerClassName(fullClassIdentifier);
				String className = FileUtil.getClassName(fullClassIdentifier);
				if(FileUtil.hasInnerClass(fullClassIdentifier)){
					continue;
				}	
				if(AdsUtil.isObfuscatedClass(className)){
					continue;
				}
				
				if(AdsUtil.isAdsLibrary(packageName, className)){
					writer.write(updateInfo.split("-")[0]);
					writer.write(updateInfo.split("-")[1]);
					writer.write(fullClassIdentifier);
					writer.write(packageName);
					writer.write(className);
					writer.endRecord();
				}
				
			}			
		}catch(Exception e){
			e.printStackTrace();
			adsUsageGroupCountInfo = null;
			error_files ++;
		}		
		return adsUsageGroupCountInfo;		
	}
	
	public void run() {
		int total = 0;
		try{
			for(int i = startPosition ; i < endPosition ; i ++ ){				
				try{
					adLibraryDetection(updateLink.get(i));
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
	public ChildAdPackageIdentifier(){
		
	}
	public ChildAdPackageIdentifier(ArrayList<String> updateLink, int start, int end, int threadNo){
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
		try{
			writer = new CsvWriter(OUTPUT_LOCATION +"AdPackage_"+threadNo);
			writer.write("App_Name");
			writer.write("Version_Code");
			writer.write("Full_Package_Class");
			writer.write("Package_Name");
			writer.write("Class_Name");
			writer.endRecord();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ChildAdPackageIdentifier ob = new ChildAdPackageIdentifier();
		//ob.showInfo("com.digitalchemy.calculator.freedecimal-4950-2015_12_18");
	}

}
