package de.simonbrungs.teachingit.api.plugin.theme;

import java.util.ArrayList;

import de.simonbrungs.teachingit.api.navigator.NavigatorEntry;
import de.simonbrungs.teachingit.api.plugin.Plugin;

public abstract class Theme extends Plugin {

	public abstract String getHeader();

	public abstract String getBodyStart(ArrayList<NavigatorEntry> pNavigatorEntrys);

	public abstract String getBodyEnd();
}
