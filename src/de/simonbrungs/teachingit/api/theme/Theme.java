package de.simonbrungs.teachingit.api.theme;

import java.util.ArrayList;

import de.simonbrungs.teachingit.api.navigator.NavigatorEntry;

public abstract class Theme {
	public abstract String getHeader();

	public abstract String getBodyStart(ArrayList<NavigatorEntry> pNavigatorEntrys);

	public abstract String getBodyEnd();
}
