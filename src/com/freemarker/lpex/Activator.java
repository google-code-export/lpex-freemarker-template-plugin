package com.freemarker.lpex;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.freemarker.lpex.Utils.PluginLogger;

//http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.freemarker.lpex";

	// The shared instance
	private static Activator plugin;
	
	public static Configuration cfg;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {	
		try {
			PluginLogger.setup();
		} catch (IOException e) {}
		
		cfg = new Configuration();
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	    String today = sdf.format(cal.getTime());
		
		//Setup plugin configured constants
		//TODO Get these values from the preferences
		cfg.setSharedVariable("author", "Rob Newton");
		cfg.setSharedVariable("date", today);
		cfg.setSharedVariable("prefixedLineWrap", new com.freemarker.lpex.FreeMarker.PrefixedLineWrap());
        
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

}
