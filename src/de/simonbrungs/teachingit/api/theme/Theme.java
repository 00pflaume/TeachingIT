package de.simonbrungs.teachingit.api.theme;

import java.util.ArrayList;
import java.util.Properties;

import de.simonbrungs.teachingit.api.navigator.NavigatorEntry;

public abstract class Theme {
	private Properties properties;

	public Theme(Properties pProperties) {
		properties = pProperties;
	}

	public Properties getProperties() {
		return properties;
	}

	public abstract String getHeader();

	public abstract String getBodyStart(ArrayList<NavigatorEntry> pNavigatorEntrys);

	public abstract String getBodyEnd();
}
