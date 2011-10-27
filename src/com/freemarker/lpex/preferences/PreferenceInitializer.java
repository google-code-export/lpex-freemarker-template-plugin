package com.freemarker.lpex.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.freemarker.lpex.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_TEMPLATES_DIR, "c:/");
		store.setDefault(PreferenceConstants.P_LOG_PATH, "c:/com.freemarker.lpex.log");
		store.setDefault(PreferenceConstants.P_LOG_LEVEL, "severe");
		store.setDefault(PreferenceConstants.P_AUTHOR, "");
		store.setDefault(PreferenceConstants.P_USE_CURRENT_DATE, true);
		store.setDefault(PreferenceConstants.P_DATE_FORMAT, "MM/dd/yyyy");
	}

}
