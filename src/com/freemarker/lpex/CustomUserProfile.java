package com.freemarker.lpex;

import com.freemarker.lpex.utils.PluginLogger;
import com.ibm.lpex.core.LpexView;

public class CustomUserProfile {

	public static void userProfile(LpexView view) {
		PluginLogger.logger.info("Configuring key binding...");
		view.defineAction("insertTemplate", new Actions.insertTemplate());
		view.doDefaultCommand("set keyAction.c-enter insertTemplate");
        view.doDefaultCommand("set messageText LPEX FreeMarker Actions available with CTRL+Enter");
		PluginLogger.logger.info("LPEX FreeMarker Actions available with CTRL+Enter");
	}

}
