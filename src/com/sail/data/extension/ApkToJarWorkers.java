package com.sail.data.extension;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;
import com.sail.mobile.model.StreamGobbler;

public class ApkToJarWorkers implements Runnable{

	
	String apkLocation = "/safwatscratch/shassan/Input_Apks/";
	//List<String> testingAPKList = Arrays.asList("a2dp.Vol-145-2018_11_09.apk","a2dp.Vol-148-2018_11_30.apk");
	String copyLocation = ProjectConstants.ROOT + "Temp_Folder_Jars/";
	String dex2jarTool = ProjectConstants.ROOT + "Conversion_Tools/dex-tools-2.1-20171001-lanchon/d2j-dex2jar.sh";
	String jarOutputPath = "/safwatscratch/shassan/Jars/";
	//String jarOutputPath = ProjectConstants.ROOT + "Output_Data/";
	
	int startPosition;
	int endPosition;
	int THREAD_NO;
	ArrayList<String> updateLink = new ArrayList<String>();
	BufferedWriter bw;
	int totalProcessed;
	
	// if we require add later.
	public void readConversionPropertiesFile() throws Exception{
		CsvReader reader = new CsvReader("");
		reader.readHeaders();
		while(reader.readRecord()){
			String apkLocation = reader.get("Apk_Location");
			String copyLocation = reader.get("Copy_Location");
			String dex2jarTool = reader.get("Dex2jar_Location");
			String jarOutputPath = reader.get("Jar_Output_Path");
		}
		reader.close();
	}
	
	public ApkToJarWorkers(ArrayList<String> updateLink, int start, int end, int threadNo) {
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
		try{
			bw = new BufferedWriter(new FileWriter(ProjectConstants.JAR_CONVERSION_LOG+"Log_"+threadNo+".txt"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void apkTojarConversion(String appName) throws Exception{
		String apkName = appName + ".apk";
		System.out.println(apkName);
		
		String commandCopy[] = {"/bin/sh","-c", "cp "+ apkLocation+apkName+" "+copyLocation};
		ProcessBuilder pb = new ProcessBuilder(commandCopy);
		pb = pb.redirectErrorStream(true);
		Process processCopy = pb.start();
		
		// Any error message?
	    Thread errorGobbler
	      = new Thread(new StreamGobbler(processCopy.getErrorStream(), System.err));
	  
	    // Any output?
	    Thread outputGobbler
	      = new Thread(new StreamGobbler(processCopy.getInputStream(), System.out));
		
	    errorGobbler.start();
	    outputGobbler.start();

		processCopy.waitFor();
		//System.out.println("CopyCommand: cp " + apkLocation+apkName+" "+copyLocation);
		errorGobbler.join();   // Handle condition where the
	    outputGobbler.join();  // process ends before the threads finish
		
		//
		String jarOutputFileName = apkName.substring(0,apkName.lastIndexOf(".apk"))+"-dex2jar.jar";
		String commandJarConversion[] = {"/bin/sh","-c", "sh "+dex2jarTool+" "+copyLocation+apkName+" "+"-f --output "+jarOutputPath+jarOutputFileName};
		ProcessBuilder pbJarConversion = new ProcessBuilder(commandJarConversion);
		pbJarConversion = pbJarConversion.redirectErrorStream(true);
		Process processJarConversion = pbJarConversion.start();
		
		// Any error message?
	    Thread errorGobblerJarConversion
	      = new Thread(new StreamGobbler(processJarConversion.getErrorStream(), System.err));
	  
	    // Any output?
	    Thread outputGobblerJarConversion
	      = new Thread(new StreamGobbler(processJarConversion.getInputStream(), System.out));
		
	    errorGobblerJarConversion.start();
	    outputGobblerJarConversion.start();

		
		processJarConversion.waitFor();
		errorGobblerJarConversion.join();
	    outputGobblerJarConversion.join();
		//
		
		
		String commandRemove[] = {"/bin/sh","-c", "rm -f "+copyLocation+apkName};
		ProcessBuilder pbRemove = new ProcessBuilder(commandRemove);
		pbRemove = pbRemove.redirectErrorStream(true);
		Process processRemove = pbRemove.start();
		
		// Any error message?
	    Thread errorGobblerRemove
	      = new Thread(new StreamGobbler(processRemove.getErrorStream(), System.err));
	  
	    // Any output?
	    Thread outputGobblerRemove
	      = new Thread(new StreamGobbler(processRemove.getInputStream(), System.out));
		
	    errorGobblerRemove.start();
	    outputGobblerRemove.start();
	    
		processRemove.waitFor();
		errorGobblerRemove.join();
		outputGobblerRemove.join();
		++totalProcessed;
		System.out.println("Thread ["+THREAD_NO+"] Processed Completed ["+totalProcessed+"] ["+apkName+"]");
	}
	public ApkToJarWorkers(){
		int threadNo = 0;
		try{
			bw = new BufferedWriter(new FileWriter(ProjectConstants.JAR_CONVERSION_LOG+"Log_"+threadNo+".txt"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = startPosition; i < endPosition; i++) {
			try {
				String appName = updateLink.get(i);
				apkTojarConversion(appName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try{
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		ApkToJarWorkers ob = new ApkToJarWorkers();
		ob.apkTojarConversion("com.zzkko-251-2019_01_18");
		System.out.println();
		
	}

}
