package com.sail.extension.manifest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.sail.common.ProjectConstants;

public class CopierWorker implements Runnable{

	ArrayList<String>updateLink = new ArrayList<String>();
	int startPosition;
	int endPosition;
	
	String APK_LOCATION = "/safwatscratch/shassan/Input_Apks/";			
	String DESTINATION = ProjectConstants.ROOT + "Android_Manifests/";
	String APK_TOOL = ProjectConstants.ROOT + "Conversion_Tools/apktool_2.3.1.jar";
	String TEMP_COPY_LOCATION = ProjectConstants.ROOT + "Temp_Folder/";
	
	int THREAD_NO;
	int error_files = 0;
	BufferedWriter bw;
	//ah.creativecodeapps.tiempo-14-2017_04_19
	public void startCopyOperation(String apkName, String appAddress) {
		
		try {
			
			String commandCopy[] = {"/bin/sh","-c", "cp "+ APK_LOCATION+apkName +" "+TEMP_COPY_LOCATION};
			ProcessBuilder pb = new ProcessBuilder(commandCopy);
			Process processCopy = pb.start();
			processCopy.waitFor();
			
			String commandExtract[] = {"/bin/sh","-c", "java -jar "+ APK_TOOL +" -f d "+ TEMP_COPY_LOCATION+apkName  +" -o " + TEMP_COPY_LOCATION+appAddress};
			ProcessBuilder pbAPKExtraction = new ProcessBuilder(commandExtract);
			Process processAPKExtraction = pbAPKExtraction.start();
			processAPKExtraction.waitFor();
  
			BufferedReader stdError = new BufferedReader(new InputStreamReader(processAPKExtraction.getErrorStream()));
			String s = null;
			while ((s = stdError.readLine()) != null) {
			    bw.write(s);
			    bw.newLine();
			}
			
			
            //System.out.println(commandExtract[2]);			
			String[] cmdRename = { "/bin/sh","-c", "mv "+TEMP_COPY_LOCATION+appAddress+"/AndroidManifest.xml "+DESTINATION+ "AndroidManifest-"+appAddress+".xml"};
			ProcessBuilder pbRename = new ProcessBuilder(cmdRename);
			Process processRename = pbRename.start();
			processRename.waitFor();
			
			String commandRemove[] = {"/bin/sh","-c", "rm -r -f "+TEMP_COPY_LOCATION+appAddress};
			ProcessBuilder pbRemove = new ProcessBuilder(commandRemove);
			Process processRemove = pbRemove.start();
			processRemove.waitFor();
			
			
			String commandRemoveAPK[] = {"/bin/sh","-c", "rm -f "+ TEMP_COPY_LOCATION+apkName};
			ProcessBuilder pbRemoveAPK = new ProcessBuilder(commandRemoveAPK);
			Process processRemoveAPK = pbRemoveAPK.start();
			processRemoveAPK.waitFor();
			
			
		} catch (Exception e) {
			error_files++;
			e.printStackTrace();
		}

	}
	
	public void run() {
		int total = 0;
		for(int i = startPosition ; i < endPosition ; i ++ ){
			String appLoc = updateLink.get(i)+".apk";
			try{
				startCopyOperation(appLoc,updateLink.get(i));
				System.out.println("["+THREAD_NO+"] Process ["+(++total)+"]"+" Error ["+error_files+"] ["+appLoc+"]");
			}catch(Exception e){
				System.err.println("Missing File");
			}
			
		}
		
	}
	
	public CopierWorker(ArrayList<String>updateLink, int start, int end, int threadNo){
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
		try{
			bw = new BufferedWriter(new FileWriter(ProjectConstants.JAR_CONVERSION_LOG+"Manifest_Log_"+threadNo+".txt"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public CopierWorker(){
		
	}
	
	public static void main(String[] args) {
		new CopierWorker().startCopyOperation("/home/local/SAIL/ahsan/Documents/AdLibraryRoot/shortJars/air.com.playtika.slotomania-238000043-2016_09_13", "air.com.playtika.slotomania-238000043-2016_09_13-dex2jar.jar");
		System.out.println("Program Finishes Successfully");
	}

}