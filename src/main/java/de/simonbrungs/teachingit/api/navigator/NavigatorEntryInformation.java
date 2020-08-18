package de.simonbrungs.teachingit.api.navigator;

public class NavigatorEntryInformation {
	private final String displayName;
	private final String destination;

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
