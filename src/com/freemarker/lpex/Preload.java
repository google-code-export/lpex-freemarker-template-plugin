package com.freemarker.lpex;

import com.ibm.lpex.alef.LpexPreload;
import com.ibm.lpex.core.LpexView;

public class Preload implements LpexPreload {

	public void preload() {
		LpexView.doGlobalCommand("set default.updateProfile.userProfile com.freemarker.lpex.CustomUserProfile");
	}
}