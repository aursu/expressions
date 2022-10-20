package com.github.aursu.expressions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorMessenger {
	// last error message stored here
	private String stacktrace = null, message = null;
	
	public ErrorMessenger() {}

	public void process(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		e.printStackTrace(pw);
		
		message = e.getMessage();
		stacktrace = sw.toString();
	}

	public String stack() {
		return stacktrace;
	}
	
	public String message() {
		return message;
	}
}
