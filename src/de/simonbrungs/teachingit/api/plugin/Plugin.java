package de.simonbrungs.teachingit.api.plugin;

import java.util.Properties;

public abstract class Plugin {
	Properties propertieFile;

	public Plugin(Properties pPropertieFile) {
		propertieFile = pPropertieFile;
	}

	public abstract void onEnable();

	public abstract void onDisable();
}
