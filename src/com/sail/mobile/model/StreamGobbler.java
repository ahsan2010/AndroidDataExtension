package com.sail.mobile.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class StreamGobbler implements Runnable{

	private final InputStream is;
	  private final PrintStream os;
	 
	  public StreamGobbler(InputStream is, PrintStream os) {
	    this.is = is;
	    this.os = os;
	  }
	 
	  public void run() {
	    try {
	      int c;
	      while ((c = is.read()) != -1){
	          //os.print((char) c);
	      }
	    } catch (IOException x) {
	      // Handle error
	    }
	  }
}
