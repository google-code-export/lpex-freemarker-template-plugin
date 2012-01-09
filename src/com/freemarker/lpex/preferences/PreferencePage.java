package com.freemarker.lpex.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.freemarker.lpex.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(
				PreferenceConstants.P_TEMPLATES_DIR,
				"Templates directory:",
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_TEMPLATES_SYNC,
				"Sync templates with central directory:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(
				PreferenceConstants.P_TEMPLATES_SYNC_DIR,
				"Templates sync directory:",
				getFieldEditorParent()));
		addField(new FileFieldEditor(
				PreferenceConstants.P_LOG_PATH,
				"Debug log (relative to Eclipse root):", 
				getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_LOG_LEVEL,
				"Choose the logging level", 1,
				new String[][] {
					{ "All", "all" },
					{ "Info", "info" },
					{ "Warning", "warning" },
					{ "Severe", "severe" },
					{ "Off", "off" }
				}, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_AUTHOR,
				"Default author:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_DATE_FORMAT,
				"Date format (java.text.SimpleDateFormat):", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_USE_CURRENT_DATE,
				"Use current date for all dates:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Specify settings for LPEX FreeMarker templates.");
	}

}
