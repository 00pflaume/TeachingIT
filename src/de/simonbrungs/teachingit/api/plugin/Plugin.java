package de.simonbrungs.teachingit.api.plugin;

import java.util.Properties;

public abstract class Plugin {
	private Properties propertieFile;

	public Plugin(Properties pPropertieFile) {
		propertieFile = pPropertieFile;
	}

	public Properties getProperties() {
		return propertieFile;
	}

	public abstract void onEnable();

	public abstract void onDisable();
}
