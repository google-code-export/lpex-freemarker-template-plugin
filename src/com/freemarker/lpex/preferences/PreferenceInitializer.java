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
		store.setDefault(PreferenceConstants.P_PARSER_MAPPINGS, "cl=cl;cle=cl;clle=cl;clp=cl;cbl=cobol;c++=cpp;h=cpp;dds=dds;dspf=dds;lf=dds;pf=dds;prtf=dds;css=html;htm=html;html=html;html-ss=html;xhtml=html;jardesc=java;jav=java;java=java;jhtml=java;jpage=java;jsp=java;pl=perl;py=python;rex=rexx;rexx=rexx;ilerpg=rpg;ilerpgsql=rpg;mbr=rpg;rpg=rpg;rpg36=rpg;rpg38=rpg;rpgle=rpg;rpgleinc=rpg;sqlrpg=rpg;sqlrpgle=rpg;sqlrple=rpg;pftbl=sql;sql=sql;sqlc=sql;ini=unknown;inl=unknown;dtd=xml;schxmi=xml;xmi=xml;xml=xml;xsd=xml;xsl=xml;");
		store.setDefault(PreferenceConstants.P_TEMPLATES_SYNC, true);
		store.setDefault(PreferenceConstants.P_TEMPLATES_SYNC_DIR, "\\\\199.5.86.73\\develop\\LPEXTemplates");
	}

}
