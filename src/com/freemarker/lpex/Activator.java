package com.freemarker.lpex;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

//http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin
import freemarker.template.Configuration;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.freemarker.lpex";

	// The shared instance
	private static Activator plugin;
	
	public static Configuration freemarkerConfig;
	
	public static IPreferenceStore preferenceStore;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {	
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
