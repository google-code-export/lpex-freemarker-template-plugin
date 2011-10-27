package com.freemarker.lpex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.freemarker.lpex.preferences.PreferenceConstants;
import com.freemarker.lpex.utils.PluginLogger;
import com.ibm.lpex.alef.LpexPreload;
import com.ibm.lpex.core.LpexView;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class Preload implements LpexPreload {

	public void preload() {
		initPreferences();
		PluginLogger.logger.info("Preferences initialized");
		LpexView.doGlobalCommand("set default.updateProfile.userProfile com.freemarker.lpex.CustomUserProfile");
		PluginLogger.logger.info("Set default profile to com.freemarker.lpex.CustomUserProfile");
	}

	private void initPreferences() {
		Activator.preferenceStore = Activator.getDefault().getPreferenceStore();
		
		//Setup the debug logger
		/*if (Activator.preferenceStore.getString(PreferenceConstants.P_LOG_PATH) != "") {
			try {
				PluginLogger.setup(Activator.preferenceStore.getString(PreferenceConstants.P_LOG_PATH), Activator.preferenceStore.getString(PreferenceConstants.P_LOG_LEVEL));
			} catch (IOException e) {}
		}else{
			try {
				PluginLogger.setup("c:/com.freemarker.lpex.log", "all");
			} catch (IOException e) {}
		}*/
		
		try {
			PluginLogger.setup(Activator.preferenceStore.getString(PreferenceConstants.P_LOG_PATH), Activator.preferenceStore.getString(PreferenceConstants.P_LOG_LEVEL));
		} catch (IOException e) {}
		
		// Add change listener to the preference store so that we are notified
		// in case of changes
		Activator.preferenceStore
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if (event.getProperty() == PreferenceConstants.P_LOG_PATH) {
							try {
								PluginLogger.setPath(event.getNewValue().toString());
							} catch (IOException e) {}
						}else if (event.getProperty() == PreferenceConstants.P_LOG_LEVEL) {
							try {
								PluginLogger.setLevel(event.getNewValue().toString());
							} catch (Exception e) {}
						}else if (event.getProperty() == PreferenceConstants.P_AUTHOR) {
							try {
								Activator.freemarkerConfig.setSharedVariable("author", event.getNewValue().toString());
							} catch (Exception e) {}
						}else if (event.getProperty() == PreferenceConstants.P_USE_CURRENT_DATE) {
							try {
								if (event.getNewValue().toString() == "true") {
									Calendar cal = Calendar.getInstance();
								    SimpleDateFormat sdf = new SimpleDateFormat(Activator.preferenceStore.getString(PreferenceConstants.P_DATE_FORMAT));
								    String today = sdf.format(cal.getTime());
									Activator.freemarkerConfig.setSharedVariable("date", today);
								}else{
									Activator.freemarkerConfig.setSharedVariable("date", "");
								}
								Activator.freemarkerConfig.setSharedVariable("author", event.getNewValue().toString());
							} catch (Exception e) {}
						}
					}
				});

		//Setup freemarker configurations
		Activator.freemarkerConfig = new Configuration();
		Activator.freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
		
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(Activator.preferenceStore.getString(PreferenceConstants.P_DATE_FORMAT));
	    String today = sdf.format(cal.getTime());
		
		//Setup plugin configured constants
	    try {
			Activator.freemarkerConfig.setSharedVariable("author", Activator.preferenceStore.getString(PreferenceConstants.P_AUTHOR));
			if (Activator.preferenceStore.getString(PreferenceConstants.P_USE_CURRENT_DATE) == "true") {
				Activator.freemarkerConfig.setSharedVariable("date", today);
			}else{
				Activator.freemarkerConfig.setSharedVariable("date", "");
			}
			Activator.freemarkerConfig.setSharedVariable("prefixedLineWrap", new com.freemarker.lpex.freemarker.PrefixedLineWrap());
		} catch (Exception e) {
			//
		}
	}
}