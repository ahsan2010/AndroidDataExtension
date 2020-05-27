package com.sail.mobile.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class GenerateClassSignaturePerAppUpdate {

	public String inputSignaturePath = "/archive/ahsan/ClassSignatureExtendedData/";
	public String outputSignaturePath = "/archive/ahsan/ClassSignaturePerAppUpdate/";

	Map<String, CsvWriter> csvWriterList = new HashMap<String, CsvWriter>();

	public void readGenarateSignaturePerUpdate() throws Exception {
		
		for (int i = 1; i <= 10; i++) {
			String csvReaderFile = inputSignaturePath + "Signature_"+i+".csv";
			CsvReader reader = new CsvReader(csvReaderFile);
			// reader.readHeaders();
			while (reader.readRecord()) {
				String appName = reader.get(0);
				String versionCode = reader.get(1);
				String releaseDate = reader.get(2);
				String classPackageName = reader.get(3);
				String className = reader.get(4);
				String signatureString = reader.get(5);

				String key = appName + "-" + versionCode + "-" + releaseDate;

				if (!csvWriterList.containsKey(key)) {
					csvWriterList.put(key, new CsvWriter(outputSignaturePath + "Signature_" + key + ".csv"));
					csvWriterList.get(key).write("App_Name");
					csvWriterList.get(key).write("Version_Code");
					csvWriterList.get(key).write("Release_Date");
					csvWriterList.get(key).write("Class_Package_Name");
					csvWriterList.get(key).write("Class_Name");
					csvWriterList.get(key).write("Signature");
					csvWriterList.get(key).endRecord();
					System.out.println("Working " + key);
				}
				CsvWriter writer = csvWriterList.get(key);
				writer.write(appName);
				writer.write(versionCode);
				writer.write(releaseDate);
				writer.write(classPackageName);
				writer.write(className);
				writer.write(signatureString);
				writer.endRecord();
			}
			System.out.println("Finish [ File "+i+" ]");
			reader.close();
		}
		for (String key : csvWriterList.keySet()) {
			csvWriterList.get(key).close();
		}
	}

	public static void main(String[] args) throws Exception{
		GenerateClassSignaturePerAppUpdate ob = new GenerateClassSignaturePerAppUpdate();
		ob.readGenarateSignaturePerUpdate();
		System.out.println("Program finishes successfully");
	}
}
 