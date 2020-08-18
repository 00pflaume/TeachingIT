package de.simonbrungs.teachingit.api.navigator;

public class NavigatorEntry {
	private final NavigatorEntryInformation[] subEntrys;
	private final NavigatorEntryInformation navBarEntry;

	public NavigatorEntry(NavigatorEntryInformation pNavBarEntry, NavigatorEntryInformation[] pNavBarSubEntrys) {
		subEntrys = pNavBarSubEntrys;
		navBarEntry = pNavBarEntry;
	}

	public NavigatorEntryInformation[] getSubEntrys() {
		return subEntrys;
	}

	public NavigatorEntryInformation getNavBarEntry() {
		return navBarEntry;
	}
}
