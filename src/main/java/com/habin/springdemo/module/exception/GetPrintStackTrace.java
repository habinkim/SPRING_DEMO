package com.habin.springdemo.module.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GetPrintStackTrace {

	public static String GetException(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		return errors.toString();
	}

}

