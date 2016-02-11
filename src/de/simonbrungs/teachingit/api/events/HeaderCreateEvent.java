package de.simonbrungs.teachingit.api.events;

public class HeaderCreateEvent extends Event {
	private boolean canceld = false;
	private String header = "";

	public HeaderCreateEvent() {
	}

	@Override
	public String getEventName() {
		return "HeaderCreateEvent";
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	public void setHeader(String pHeader) {
		header = pHeader;
	}

	public String getHeader() {
		return header;
	}
}
