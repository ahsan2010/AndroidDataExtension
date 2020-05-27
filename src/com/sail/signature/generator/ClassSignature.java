package com.sail.signature.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassSignature {
	String versionCode;
	String releaseDate;
	Map<String,ArrayList<String>> classSignatureByLibrary = new HashMap<String,ArrayList<String>>();
	ArrayList<String> internalCodeHasAdImplementation = new ArrayList<String>();
	ArrayList<String> appOtherCode = new ArrayList<String>();
}
