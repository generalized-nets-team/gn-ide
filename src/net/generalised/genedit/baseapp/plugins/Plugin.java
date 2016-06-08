package net.generalised.genedit.baseapp.plugins;

import net.generalised.genedit.baseapp.SettingsManager;

public abstract class Plugin {

	public abstract void initialize();
	
	/**
	 * Utility function
	 */
	protected void setDefaultProperty(String key, String value) {
		SettingsManager.getInstance().setDefaultProperty(key, value);
	}
}
