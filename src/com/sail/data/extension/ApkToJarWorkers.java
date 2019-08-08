package com.sail.data.extension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.csvreader.CsvReader;
import com.sail.common.ProjectConstants;

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
	
	public void apkTojarConversion(int position) throws Exception{
		String appName = updateLink.get(position);
		String apkName = appName + ".apk";
		System.out.println(apkName);
		
		String commandCopy[] = {"/bin/sh","-c", "cp "+ apkLocation+apkName+" "+copyLocation};
		ProcessBuilder pb = new ProcessBuilder(commandCopy);
		Process processCopy = pb.start();
		processCopy.waitFor();
		//System.out.println("CopyCommand: cp " + apkLocation+apkName+" "+copyLocation);
		//
		String jarOutputFileName = apkName.substring(0,apkName.lastIndexOf(".apk"))+"-dex2jar.jar";
		String commandJarConversion[] = {"/bin/sh","-c", "sh "+dex2jarTool+" "+copyLocation+apkName+" "+"-f --output "+jarOutputPath+jarOutputFileName};
		ProcessBuilder pbJarConversion = new ProcessBuilder(commandJarConversion);
		Process processJarConversion = pbJarConversion.start();
		processJarConversion.waitFor();
		//
		
		BufferedReader stdError = new BufferedReader(new InputStreamReader(processJarConversion.getErrorStream()));
		String s = null;
		while ((s = stdError.readLine()) != null) {
		    bw.write(s);
		    bw.newLine();
		}
		
		String commandRemove[] = {"/bin/sh","-c", "rm -f "+copyLocation+apkName};
		ProcessBuilder pbRemove = new ProcessBuilder(commandRemove);
		Process processRemove = pbRemove.start();
		processRemove.waitFor();
		++totalProcessed;
		System.out.println("Thread ["+THREAD_NO+"] Processed Completed ["+totalProcessed+"] ["+apkName+"]");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = startPosition; i < endPosition; i++) {
			try {
				apkTojarConversion(i);
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

}
