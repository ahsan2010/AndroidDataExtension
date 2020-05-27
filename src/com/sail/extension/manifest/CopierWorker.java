package com.sail.extension.manifest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import com.sail.common.ProjectConstants;
import com.sail.mobile.model.StreamGobbler;

public class CopierWorker implements Runnable{

	ArrayList<String>updateLink = new ArrayList<String>();
	int startPosition;
	int endPosition;
	
	String APK_LOCATION = "/safwatscratch/shassan/Input_Apks/";			
	String DESTINATION = "/archive/ahsan/Manifest_Remaining/";
	String APK_TOOL = ProjectConstants.ROOT + "Conversion_Tools/apktool_2.3.1.jar";
	String TEMP_COPY_LOCATION = ProjectConstants.ROOT + "Temp_Folder/";
	
	int THREAD_NO;
	int error_files = 0;
	BufferedWriter bw;
	//ah.creativecodeapps.tiempo-14-2017_04_19
	
	
	public void executeShellCommand(String command[]) throws Exception{
		//System.out.println("Command: " + Arrays.asList(command).toString());
		ProcessBuilder pb = new ProcessBuilder(command);			
		pb = pb.redirectErrorStream(true);
		Process process = pb.start();
	    Thread errorGobbler 	= new Thread(new StreamGobbler(process.getErrorStream(), System.err));
	    Thread outputGobbler    = new Thread(new StreamGobbler(process.getInputStream(), System.out));
	    errorGobbler.start();
	    outputGobbler.start();
	    process.waitFor();
	    errorGobbler.join();   // Handle condition where the
	    outputGobbler.join();  // process ends before the threads finish
	}
	
	public void readWriteAKPToolInfo(String path, String appAddress) throws Exception{

		BufferedReader reader = new BufferedReader(new FileReader(TEMP_COPY_LOCATION+appAddress+"/apktool.yml"));
		System.out.println("Location " + TEMP_COPY_LOCATION+appAddress+"/apktool.yml");
		String s = null;
		String minimumSDKVersion = "";
		String targetSDKVersion = "";
		
		while ((s = reader.readLine()) != null) {
		    if(s.contains("minSdkVersion")){
		    	minimumSDKVersion = s.substring(s.indexOf("'")+1,s.lastIndexOf("'")).trim();
		    }
		    if(s.contains("targetSdkVersion")){
		    	targetSDKVersion = s.substring(s.indexOf("'")+1,s.lastIndexOf("'")).trim();
		    }
		}
		reader.close();
		System.out.println("MinimumSDK: " + minimumSDKVersion + " " + "TargetSDK " + targetSDKVersion);
		bw.write(appAddress.split("-")[0]);bw.write(",");
		bw.write(appAddress.split("-")[1]);bw.write(",");
		bw.write(minimumSDKVersion);bw.write(",");
		bw.write(targetSDKVersion);
		bw.newLine();
		bw.close();
	}
	
	public void startCopyOperation(String apkName, String appAddress) {
		
		try {
			
			String commandCopy[] = {"/bin/sh","-c", "cp "+ APK_LOCATION+apkName +" "+TEMP_COPY_LOCATION};
			executeShellCommand(commandCopy);
		    
			String commandExtract[] = {"/bin/sh","-c", "java -jar "+ APK_TOOL +" -f d "+ TEMP_COPY_LOCATION+apkName  +" -o " + TEMP_COPY_LOCATION+appAddress};
			executeShellCommand(commandExtract);
			
			String[] cmdRename = { "/bin/sh","-c", "cp "+TEMP_COPY_LOCATION+appAddress+"/AndroidManifest.xml "+DESTINATION+ "AndroidManifest-"+appAddress+".xml"};
			executeShellCommand(cmdRename);
			
			//String ymlFilePath = TEMP_COPY_LOCATION+appAddress+"/apktool.yml";
			//readWriteAKPToolInfo(ymlFilePath,appAddress);
			
			String commandRemove[] = {"/bin/sh","-c", "rm -r -f "+TEMP_COPY_LOCATION+appAddress};
			executeShellCommand(commandRemove);
			
			String commandRemoveAPK[] = {"/bin/sh","-c", "rm -f "+ TEMP_COPY_LOCATION+apkName};
			executeShellCommand(commandRemoveAPK);		
			
			
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
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public CopierWorker(ArrayList<String>updateLink, int start, int end, int threadNo){
		this.updateLink = updateLink;
		this.startPosition = start;
		this.endPosition = end;
		this.THREAD_NO = threadNo;
		try{
			bw = new BufferedWriter(new FileWriter(ProjectConstants.JAR_CONVERSION_LOG+"SDK_Version_"+threadNo+".txt"));
			bw.write("Package_Name");bw.write(",");
			bw.write("Version_Code");bw.write(",");
			bw.write("Minimum_SDK_Version");bw.write(",");
			bw.write("Target_SDK_Version");
			bw.newLine();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public CopierWorker(){
		
	}
	
	public static void main(String[] args) {
		new CopierWorker().startCopyOperation("air.com.playtika.slotomania-238000043-2016_09_13", "air.com.playtika.slotomania-238000043-2016_09_13");
		System.out.println("Program Finishes Successfully");
	}

}