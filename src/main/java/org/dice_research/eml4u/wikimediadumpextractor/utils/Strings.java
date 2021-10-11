package org.dice_research.eml4u.wikimediadumpextractor.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public abstract class Strings {

	public static String stackTraceToString(Exception exception) {
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public static String getReadableTimpestamp(long timestamp) {
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((Long) timestamp));
	}

	public static String getFilenameTimpestamp(long timestamp) {
		return new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date((Long) timestamp));
	}
}