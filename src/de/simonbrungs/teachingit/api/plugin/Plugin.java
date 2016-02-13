package de.simonbrungs.teachingit.api.plugin;

import java.util.Properties;

public abstract class Plugin {
	private Properties pluginProperties;

	public Properties getProperties() {
		return pluginProperties;
	}

	public void setProperties(Properties pProperties) {
		pluginProperties = pProperties;
	}

	public abstract void onEnable() throws Throwable;

	public abstract void onDisable() throws Throwable;
}
