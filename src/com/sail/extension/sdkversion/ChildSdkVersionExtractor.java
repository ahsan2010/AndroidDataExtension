package com.sail.extension.sdkversion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvWriter;
import com.sail.common.ProjectConstants;
import com.sail.mobile.model.StreamGobbler;

public class ChildSdkVersionExtractor implements Runnable{

	ArrayList<String>updateLink = new ArrayList<String>();
	int startPosition;
	int endPosition;
	
	String APK_LOCATION = "/safwatscratch/shassan/Input_Apks/";			
	String DESTINATION = "/archive/ahsan/Manifest_Remaining/";
	String APK_TOOL = ProjectConstants.ROOT + "Conversion_Tools/apktool_2.3.1.jar";
	String TEMP_COPY_LOCATION = ProjectConstants.ROOT + "Temp_Folder/";
	
	
	String APT_TOOL = "/home/local/SAIL/ahsan/Android/Sdk/build-tools/29.0.2/aapt";
	
	String SDK_VERSION_GENERATED_PATH = "/home/local/SAIL/ahsan/Documents/Sdk_version/My_Thread_Approach/";
	int THREAD_NO;
	int error_files = 0;
	CsvWriter writer;
	//ah.creativecodeapps.tiempo-14-2017_04_19
	public void startCopyOperation(String apkName, String appAddress) {
		
		try {
			
			String minimumSDKVersion = "";
			String targetSDKVersion = "";
			String maxSDKVersion = "";
			String compileSDKVersion = "";
			String delimeter = "'";
			
			List<String> commands = new ArrayList<String>();
			commands.add("/bin/sh");
			commands.add("-c");
			commands.add(APK_TOOL);
			commands.add("d");
			commands.add("badging");
			commands.add(APK_LOCATION + apkName);
			/*commands.add("|");
			commands.add("grep \"sdkVersion:\"");
			commands.add("|");
			commands.add("cut");
			commands.add("-f2");
			commands.add("-d \"'\"");
			commands.add("|");
			commands.add("head -1");*/
			
			
			//, "|", "cut -f2 -d"+delimeter, "|", "head -1"
			String commandAPKBadgeInfo[] = {"/bin/sh","-c",APT_TOOL + " d badging "+ APK_LOCATION+apkName + "| grep \"sdkVersion:\" | cut -f2 -d \"'\" | head -1" };
			//String commandAPKBadgeInfo[] = {APT_TOOL, "d", "badging", APK_LOCATION+apkName};
			
			ProcessBuilder pb = new ProcessBuilder(commandAPKBadgeInfo);			
			pb = pb.inheritIO();
			Process processAPKBadging = pb.start();
			
			Thread errorGobbler 	= new Thread(new StreamGobbler(processAPKBadging.getErrorStream(), System.err));
		    processAPKBadging.waitFor();
		    errorGobbler.join();   // Handle condition where the
		    
			
			BufferedReader br = new BufferedReader(new InputStreamReader(processAPKBadging.getInputStream()));
		    
			
			String line = null;
		    while ((line = br.readLine()) != null) {
		     minimumSDKVersion = line;
		    }
			
		    if(minimumSDKVersion.trim().length()<= 0){       
		    	String commandApproachIIMin[] = {"/bin/sh","-c",APT_TOOL + " l -a "+ APK_LOCATION+apkName + "| grep \""+minimumSDKVersion+"\" | cut -f3 -d \")\" | head -1" };
		    	ProcessBuilder pbAppIIMin = new ProcessBuilder(commandApproachIIMin); 
		    }
		    
			
		    
					
			
		    writer.write(appAddress.split("-")[0]);
		    writer.write(appAddress.split("-")[1]);
		    writer.write(appAddress.split("-")[2]);
		    writer.write(minimumSDKVersion);
		    writer.write(targetSDKVersion);
		    writer.endRecord();
			
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
		try{
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public ChildSdkVersionExtractor(ArrayList<String>updateLink, int start, int end, int threadNo){
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
		try{
			writer = new CsvWriter(SDK_VERSION_GENERATED_PATH+"sdk_version.csv");
			writer.write("Package_Name");
			writer.write("Version_Code");
			writer.write("Release_Sate");
			writer.write("Minimum_SDK_Version");
			writer.write("Target_SDK_Version");
			writer.endRecord();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public ChildSdkVersionExtractor(){
		try{
			writer = new CsvWriter(SDK_VERSION_GENERATED_PATH + "sdk_version.csv");
			writer.write("Package_Name");
			writer.write("Version_Code");
			writer.write("Release_Sate");
			writer.write("Minimum_SDK_Version");
			writer.write("Target_SDK_Version");
			writer.endRecord();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		ChildSdkVersionExtractor ob = new ChildSdkVersionExtractor();
		ob.startCopyOperation("com.wantu.activity-180-2016_09_26.apk", "com.wantu.activity-180-2016_09_26");
		ob.writer.close();
		System.out.println("Program Finishes Successfully");
	}

}