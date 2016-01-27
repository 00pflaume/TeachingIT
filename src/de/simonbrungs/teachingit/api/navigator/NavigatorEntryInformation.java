package de.simonbrungs.teachingit.api.navigator;

public class NavigatorEntryInformation {
	private String displayName;
	private String destination = null;

	public NavigatorEntryInformation(String pDisplayName, String pDestination) {
		displayName = pDisplayName;
		destination = pDestination;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDestination() {
		return destination;
	}
}
