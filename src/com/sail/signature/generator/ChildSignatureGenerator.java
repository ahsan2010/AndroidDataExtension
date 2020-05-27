package com.sail.signature.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.analysis.util.AdsUtil;
import com.sail.mobile.analysis.util.FileUtil;

public class ChildSignatureGenerator implements Runnable {

	public  String JAR_DIRECTORY = "/safwatscratch/shassan/Jars/";
	public String OUTPUT_PATH = "/archive/ahsan/ClassSignatureExtendedData/";
	Map<String, String> varifiedAdPackageMapGroup = FileUtil.readVerfiedAdList(ProjectConstants.VARIFIED_AD_PACKAGE_LIST);
		
	ArrayList<String> updateLink = new ArrayList<String>();
	int startPosition;
	int endPosition;
	int threadNumber;
	CsvWriter signatureWriter;
	
	List<String> javaKeyword = Arrays.asList("abstract", "continue", "for", "new", "switch", "goto", "package",
			"synchronized", "try", "catch", "boolean", "do", "if", "private", "this", "break", "double", "protected",
			"throw", "byte", "else", "public", "case", "int", "super", "instanceof", "return", "transient", "char",
			"final", "interface", "static", "finally", "long", "float", "native", "while");

	List<String> javaType = Arrays.asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
	
	
	List<String> sampleJarList = Arrays.asList("air.com.playtika.slotomania-238000043-2016_09_13","com.lookout-10000309-2016_10_27");
	
	int inputUpdatesCount = 0;
	int PROBLEM = 0;
	
	public ChildSignatureGenerator(ArrayList<String>updateLink,int startPosition, int endPosition,int threadNumber){		
		this.threadNumber = threadNumber;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.updateLink = updateLink;		
		try{
			signatureWriter = new CsvWriter(OUTPUT_PATH + "Signature_"+ threadNumber+".csv");
		}catch(Exception e){
			
		}
	}

	/**
	 * This method extract all the classInformation from a jar file
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
			if (!entry.getName().endsWith(".class")) {
				continue;
			}
			ClassParser parser = new ClassParser(jarPath, entry.getName());
			JavaClass javaClass = parser.parse();
			javaClasses.put(javaClass.getClassName(), javaClass);
		}
		return javaClasses;
	}

	/**
	 * Combine all the methodName and parameters
	 * 
	 * @param javaClass
	 * @return
	 * @throws ClassNotFoundException
	 */
	private String findMethodIII(JavaClass javaClass) throws ClassNotFoundException {

		String methodInfoString = "";
		int totalMethods = javaClass.getMethods().length;
		String className = FileUtil.getClassName(javaClass.getClassName());
		ArrayList<String> methodStringSet = new ArrayList<String>();
		
		if (AdsUtil.isObfuscatedClass(className) || className.contains("$") || className.equals("R")) {			
			return methodInfoString;
		}
		
		for (Method method : javaClass.getMethods()) {
			String methodName = method.getName();
			if (methodName.contains("<init>")) {
				methodName = javaClass.getClassName();
			}
			else if(methodName.contains("<clinit>")){
				continue;
			}
			else if (methodName.length() == 1) {
				methodName = "X";
			}
			String arguments = "";
			for (int i = 0; i < method.getArgumentTypes().length; i++) {
				String argument = method.getArgumentTypes()[i].toString();
				if (argument.contains(".")) {
					String lastValue = argument.substring(argument.lastIndexOf(".") + 1);
					if (argument.startsWith("android.support")) {
						argument = "Y";
					} else if (lastValue.length() > 0) {
						if (lastValue.charAt(0) >= 'a' && lastValue.charAt(0) <= 'z') {
							argument = "Y";
						}
					}

				} else {
					if ((argument.charAt(0) >= 'a' && argument.charAt(0) <= 'z')
							&& !(javaType.contains(argument))) {
						argument = "Y";
					}
				}

				if (argument.contains("$")) {
					argument = argument.substring(0, argument.indexOf("$"));
				}
				arguments += argument;
				if (i < method.getArgumentTypes().length - 1) {
					arguments += "-";
				}
			}
			String methodString = methodName + "(" + arguments + ")";
			methodStringSet.add(methodString);			
		}
		
		Collections.sort(methodStringSet);
		int totalElement = methodStringSet.size();
		for(String mString : methodStringSet){
			totalElement --;
			methodInfoString += mString;
			if(totalElement > 0){
				methodInfoString += "_";
			}
		}
		return methodInfoString;
	}
	
	
	

	/**
	 * Check whether this package is related to ad library or not
	 * 
	 * @param libraryName
	 * @param packageMapGroup
	 * @return
	 */
	public static String getVarifiedAdsPackage(String libraryName, Map<String, String> packageMapGroup) {
		for (String pName : packageMapGroup.keySet()) {
			if (libraryName.startsWith(pName))
				return packageMapGroup.get(pName);
		}
		return null;
	}

	
	
	/**
	 * We use the following procedure to find out the obfuscated code. From our observation we find that many obfuscated code has class name
	 * starts with small letter (.a/.b/.zz etc). Therefore, if we find that the class name starts with small letter we flag it as obfuscated
	 * code.
	 * @param className
	 * @return
	 */
	public boolean isObfuscated(String className){
		className = className.trim();
		if(className.length() > 0){
			if(className.charAt(0)>='a' && className.charAt(0) <= 'z')
				return true;
		}else if(className.length() == 0 ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * We generate our initial ad library package name. Therefore, we are considering this list. If we find the ad library package, then
	 * we flag it as a ad library code. [i.e. com.mopub is ad library package. So if we find the substring com.mopub from the package we
	 * flag it as Ad Library package]
	 * @param className
	 * @param packageName
	 * @return
	 */
	public boolean isAdLibrary(String className, String packageName){
		if(isObfuscated(className)){
			return false;
		}
			String addLibraryName = AdsUtil.summarizedPackageName(packageName);
			String libraryName = getVarifiedAdsPackage(addLibraryName,varifiedAdPackageMapGroup);
			if(libraryName != null){
				return true;
			}
		
		return false;
	}
	

	public boolean analyze(String updateKey) {		
		String appInformation[] = updateKey.split("-");
		int problemMethodName = 0;
		int totalClassAnalyzed = 0;
		
		String jarFileLocation = JAR_DIRECTORY + updateKey + "-dex2jar.jar";
		
		try {
			JarFile jarFile = new JarFile(new File(jarFileLocation));
			Map<String, JavaClass> javaClassesMap;
			try {
				javaClassesMap = collectJavaClasses(jarFileLocation, jarFile); 
			} catch (Exception e) {
				++PROBLEM;
				return false;
			}		
			
			
			for (Map.Entry<String, JavaClass> entity : javaClassesMap.entrySet()) {
				totalClassAnalyzed++;
						
				String classSignature = "";
				
				/** full class idenitifer is the package name + class name */
				String fullClassIdentifier = entity.getValue().getClassName().trim();
				String packageName = entity.getValue().getPackageName();
				if (FileUtil.hasInnerClass(fullClassIdentifier))
					continue;
				fullClassIdentifier = FileUtil.removeInnerClassName(fullClassIdentifier);
				
				// fullName contains (packageName+className) without the file
				// extension .java or .class
				String className = FileUtil.getClassName(fullClassIdentifier);
				String methodString = findMethodIII(entity.getValue());
				
				if (AdsUtil.isObfuscatedClass(className)) {
					continue;
				}
				
				if(methodString.length() <= 0){
					problemMethodName++;
					continue;
				}
				int depth = fullClassIdentifier.split("\\.").length - 1;
				classSignature += depth + "_" + methodString;
				
				
				
				String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(classSignature);
				
				//System.out.println(classSignature + " " + sha256hex);
				
				signatureWriter.write(appInformation[0]);
				signatureWriter.write(appInformation[1]);
				signatureWriter.write(appInformation[2]);
				signatureWriter.write(packageName);
				signatureWriter.write(className);
				signatureWriter.write(sha256hex);
				signatureWriter.endRecord();
			}
			System.out.println("System processed [" + (++inputUpdatesCount) + "] updates. [TH: "+threadNumber+"]" +" " +jarFileLocation +" Total Class Analyzed ["+totalClassAnalyzed+']' + " Problem ["+PROBLEM+"]" );
		} catch (Exception e) {
			++PROBLEM;
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		try {		
			for (int i = startPosition; i < endPosition; i++) {				
				boolean checkedJar = analyze(updateLink.get(i));
				if (checkedJar) {
					analyze(updateLink.get(i));
				}
			}			
			signatureWriter.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String toString() {
		return "Thread Number ["+this.threadNumber+"] " + "Ranges ["+startPosition+"-"+endPosition+"]";
	}
	
	public ChildSignatureGenerator(){
		for(String s : sampleJarList){
			this.updateLink.add(s);
		}
		try{
			signatureWriter = new CsvWriter(OUTPUT_PATH + "SAMPLE_"+ threadNumber+".csv");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChildSignatureGenerator ob = new ChildSignatureGenerator();
		//ob.startPosition = 0;
		//ob.endPosition = 1;
		//ob.an
		ob.analyze("com.lyrebirdstudio.colorme-144-2016_11_04");
		System.out.println("Program finishes successful");
	}

}