package com.sail.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ShellCommandTester {

	String apkLocation = "/safwatscratch/shassan/Input_Apks/";
	List<String> testingAPKList = Arrays.asList("a2dp.Vol-145-2018_11_09.apk","a2dp.Vol-148-2018_11_30.apk");
	String copyLocation = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Temp_Folder/";
	String dex2jarTool = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Conversion_Tools/dex-tools-2.1-20171001-lanchon/d2j-jar2dex.sh";
	String jarOutputPath = "/home/local/SAIL/ahsan/Documents/SAIL_Projects/Android_Data_Extension/Output_Data/";
	
	public void testCommands() throws Exception{
		
		for(int i = 0 ; i < testingAPKList.size() ; i ++ ){
			String apkName = testingAPKList.get(i);
			String commandCopy[] = {"/bin/sh","-c", "cp "+ apkLocation+apkName+" "+copyLocation};
			ProcessBuilder pb = new ProcessBuilder(commandCopy);
			Process processCopy = pb.start();
			processCopy.waitFor();
			
			//
			String jarOutputFileName = apkName.substring(0,apkName.lastIndexOf(".apk"))+"-dex2jar.jar";
			String commandJarConversion[] = {"/bin/sh","-c", "sh "+dex2jarTool+" "+copyLocation+apkName+" "+"-f --output "+jarOutputPath+jarOutputFileName};
			ProcessBuilder pbJarConversion = new ProcessBuilder(commandJarConversion);
			Process processJarConversion = pbJarConversion.start();
			processJarConversion.waitFor();
			//
			
			BufferedReader stdError = new BufferedReader(new InputStreamReader(processJarConversion.getErrorStream())); //getInputStream() for normal output
			System.out.println("Here is the standard error of the command (if any):\n");
			String s = null;
			while ((s = stdError.readLine()) != null) {
			    System.out.println(s);
			}
			
			String commandRemove[] = {"/bin/sh","-c", "rm -f "+copyLocation+apkName};
			ProcessBuilder pbRemove = new ProcessBuilder(commandRemove);
			Process processRemove = pbRemove.start();
			processRemove.waitFor();
			
			
			
			System.out.println("Finish the Copy & Conversion Process");
		}
		
		
	}

	
	
	public static void main(String[] args) throws Exception{
		ShellCommandTester ob = new ShellCommandTester();
		ob.testCommands();
		System.out.println("Program finishes successfully");
	}
}
