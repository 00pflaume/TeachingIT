package de.simonbrungs.teachingit.api.navigator;

public class NavigatorEntry {
	private NavigatorEntryInformation[] subEntrys;
	private NavigatorEntryInformation navBarEntry;

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
