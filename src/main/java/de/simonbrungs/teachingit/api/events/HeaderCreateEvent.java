package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class HeaderCreateEvent extends Event {
	private boolean canceld = false;
	private String header = "";
	private final TempUser user;

	public HeaderCreateEvent(TempUser pUser) {
		user = pUser;
	}

	public TempUser getUser() {
		return user;
	}

	@Override
	public String getEventName() {
		return "HeaderCreateEvent";
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	@Override
	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String pHeader) {
		header = pHeader;
	}
}
