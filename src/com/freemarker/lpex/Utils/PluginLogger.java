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
	
	static public void setLevel(String level) {
		//Parse and set the level
		if (level.compareToIgnoreCase("all") == 0) {
			logger.setLevel(Level.ALL);
		}else if (level.compareToIgnoreCase("info") == 0) {
			logger.setLevel(Level.INFO);
		}else if (level.compareToIgnoreCase("warning") == 0) {
			logger.setLevel(Level.WARNING);
		}else if (level.compareToIgnoreCase("severe") == 0) {
			logger.setLevel(Level.SEVERE);
		}else if (level.compareToIgnoreCase("off") == 0) {
			logger.setLevel(Level.OFF);
		}else{
			logger.setLevel(Level.OFF);
		}
	}
	
	static public void setPath(String path) throws IOException {
		try {
			logger.removeHandler(fileTxt);
		} catch (Exception e) {}
		
		try {
			fileTxt = new FileHandler(path, true);
		} catch (Exception e) {
			path = "c:/com.freemarker.lpex.log";
		}

		// Create text Formatter
		formatterTxt = new TextFormatter();
		fileTxt.setFormatter(formatterTxt);
		
		logger.addHandler(fileTxt);
	}

	static public void setup(String path, String level) throws IOException {
		setLevel(level);
		setPath(path);
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