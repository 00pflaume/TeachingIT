package de.simonbrungs.teachingit.api.plugin.theme;

import java.util.ArrayList;
import java.util.Properties;

import de.simonbrungs.teachingit.api.navigator.NavigatorEntry;
import de.simonbrungs.teachingit.api.plugin.Plugin;

public abstract class Theme extends Plugin {

	public Theme(Properties pPropertieFile) {
		super(pPropertieFile);
	}

	public abstract String getHeader();

	public abstract String getBodyStart(ArrayList<NavigatorEntry> pNavigatorEntrys);

	public abstract String getBodyEnd();
}
