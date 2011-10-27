package com.freemarker.lpex.utils;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PluginLogger {
	public final static Logger logger = Logger.getLogger(PluginLogger.class.getName());
	static private FileHandler fileTxt;
	static private Formatter formatterTxt;

	static public void setup() throws IOException {
		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler(
				"C:/Documents and Settings/RNewton/IBM/rationalsdp/workspace/com.freemarker.lpex/bin/eclipse/plugins/com.freemarker.lpex.log",  
				true);
		
		//Relative paths start at C:\Program Files\IBM\SDP (location of the eclipse.exe)
		//Plugin directory is C:\Program Files\IBM\SDP\dropins\com.freemarker.lpex\eclipse\plugins

		// Create text Formatter
		formatterTxt = new TextFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);
	}
}

class TextFormatter extends Formatter {
	// This method is called for every log records
	public String format(LogRecord rec) {
		StringBuffer buf = new StringBuffer(1000);
		buf.append(rec.getLevel());
		buf.append(' ');
		buf.append(calcDate(rec.getMillis()));
		buf.append(' ');
		buf.append(formatMessage(rec));
		buf.append('\n');
		return buf.toString();
	}

	private String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	// This method is called just after the handler using this
	// formatter is created
	public String getHead(Handler h) {
		SimpleDateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		Date resultdate = new Date();
		return "New session " + date_format.format(resultdate) + "\n=================================\n";
	}

	// This method is called just after the handler using this
	// formatter is closed
	public String getTail(Handler h) {
		return "=================================\n";
	}
}