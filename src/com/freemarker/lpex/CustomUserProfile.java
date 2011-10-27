package com.freemarker.lpex;

import com.ibm.lpex.core.LpexView;

public class CustomUserProfile {

	public static void userProfile(LpexView view) {
		view.defineAction("insertTemplate", new Actions.insertTemplate());
		view.doDefaultCommand("set keyAction.c-enter insertTemplate");
        view.doDefaultCommand("set messageText LPEX FreeMarker Actions available with CTRL+Enter");
	}

}
