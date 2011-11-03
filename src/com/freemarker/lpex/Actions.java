package com.freemarker.lpex;

import com.freemarker.lpex.formdialogs.LPEXTemplate;
import com.freemarker.lpex.preferences.PreferenceConstants;
import com.freemarker.lpex.utils.PluginLogger;
import com.freemarker.lpex.utils.StackTraceUtil;
import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

import freemarker.template.TemplateException;

import java.io.*;

public class Actions {

	public static class insertTemplate implements LpexAction {
		
		public void doAction(LpexView view) {
			try {
				String baseTemplateFolder = "";
				try {
					baseTemplateFolder = Activator.preferenceStore.getString(PreferenceConstants.P_TEMPLATES_DIR);
					if (!new File(baseTemplateFolder).exists())
					{
					   throw new FileNotFoundException("You must have a valid template directory set.");
					}
				} catch (Exception e) {
					PluginLogger.logger.warning(StackTraceUtil.getStackTrace(e));
					baseTemplateFolder = "c:/templates";
				}
				if (baseTemplateFolder == "") {
					PluginLogger.logger.warning("No template directory set");
			        view.doDefaultCommand("set messageText You must first set the templates directory in the settings");
			        return;
				}
				
				LPEXManipulator lpexManipulator = new LPEXManipulator(view);
				final String templateHint = lpexManipulator.getCursorWord();
				String templateFolderName = lpexManipulator.getTemplateFolderFromParser();
				
				File templateDirectory = new File(baseTemplateFolder + "/" + templateFolderName);
				FilenameFilter templateFilter = new FilenameFilter() { 
					public boolean accept(File dir, String name) {
						File file = new File(dir + "/" + name);
						if (file.isFile()) {
					        String extension = file.toString().substring(file.toString().lastIndexOf("."));
							if (extension.compareToIgnoreCase(".ftl") == 0) {
								return name.toUpperCase().startsWith(templateHint.toUpperCase());
							}
						}
						return false;
					}
				};
				
				PluginLogger.logger.info("Present popup list of templates");
				
				String[] templateFiles = templateDirectory.list(templateFilter);
				
				if (templateFiles == null) {
					PluginLogger.logger.warning("No templates found");
					view.doDefaultCommand("set messageText No templates found");
			        return;
				}
				
				String selectedTemplate = "";
				lpexManipulator.promptTemplateChooser(templateFiles);
				selectedTemplate = lpexManipulator.getSelectedTemplateNameNoExt();
				if (selectedTemplate == "") { 
					// no matches found
					return;
				} 
				
				PluginLogger.logger.info("Selected: " + selectedTemplate);
				
				File templateFile = new File(baseTemplateFolder + "/" + templateFolderName + "/" + selectedTemplate + ".ftl");
				
				LPEXTemplate lpexTemplate = new LPEXTemplate(templateFile);
				
				//Parse the form configuration that will dictate the form dialog structure
				lpexTemplate.buildForm();
				
				//Get the form data container ready to receive data
				lpexTemplate.initializeFormData();
				
				//Present the dialogs for the user to fill out
				if (lpexTemplate.getForm().open()) {
					PluginLogger.logger.info(lpexTemplate.getFormDataAsString());
					PluginLogger.logger.info(lpexTemplate.formData.toString());
					
					//Merge the collected data with the template
					lpexTemplate.merge();
					
					//Insert the merged template into the cursor position of the current LPEX document
					lpexManipulator.addBlockTextAtCursorPosition(lpexTemplate.getResult());
				}else{
					PluginLogger.logger.info("Escaped the form early");
				}
				
			} catch (TemplateException e) {
				PluginLogger.logger.severe(StackTraceUtil.getStackTrace(e));
			} catch (IOException e) {
				PluginLogger.logger.severe(StackTraceUtil.getStackTrace(e));
			} catch (Exception e) {
				PluginLogger.logger.severe(StackTraceUtil.getStackTrace(e));
			}

			return;
		}

		public boolean available(LpexView view)
		{
			return true;
		}
	}
}