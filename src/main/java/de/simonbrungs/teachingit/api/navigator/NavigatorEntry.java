package de.simonbrungs.teachingit.api.navigator;

public class NavigatorEntry {
	private final NavigatorEntryInformation[] subEntries;
	private final NavigatorEntryInformation navBarEntry;

	public NavigatorEntry(NavigatorEntryInformation pNavBarEntry, NavigatorEntryInformation[] pNavBarSubEntries) {
		subEntries = pNavBarSubEntries;
		navBarEntry = pNavBarEntry;
	}

	public NavigatorEntryInformation[] getSubEntries() {
		return subEntries;
	}

	public NavigatorEntryInformation getNavBarEntry() {
		return navBarEntry;
	}
}
