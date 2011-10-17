package com.freemarker.lpex;

import com.freemarker.lpex.FormDialogs.LPEXTemplate;
import com.freemarker.lpex.Utils.PluginLogger;
import com.freemarker.lpex.Utils.StackTraceUtil;
import com.ibm.lpex.core.LpexAction;
import com.ibm.lpex.core.LpexView;

import freemarker.template.TemplateException;

import java.io.*;

public class Actions {

	public static class insertTemplate implements LpexAction {
		
		public void doAction(LpexView view) {
			try {
				LPEXTemplate lpexTemplate = new LPEXTemplate(new File("c:/ftl/test.ftl"));
				
				//Parse the form configuration that will dictate the form dialog structure
				lpexTemplate.buildForm();
				
				//Get the form data container ready to receive data
				lpexTemplate.initializeFormData();
				
				//Present the dialogs for the user to fill out
				lpexTemplate.getForm().open();
				
				//Merge the collected data with the template
				lpexTemplate.merge();
				
				//Insert the merged template into the cursor position of the current LPEX document
				//TODO Replace this log write with the insert to the LPEX editor
				PluginLogger.logger.info(lpexTemplate.getResult());
				PluginLogger.logger.info(lpexTemplate.getFormData().toString());
				
			} catch (TemplateException e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			} catch (IOException e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			} catch (Exception e) {
				PluginLogger.logger.info(StackTraceUtil.getStackTrace(e));
			}

			return;
		}

		public boolean available(LpexView view) {
			return true;
		}
	}
}