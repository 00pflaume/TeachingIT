package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class HeaderCreateEvent extends Event {
	private boolean canceld = false;
	private String header = "";
	private TempUser user;

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

	public void setHeader(String pHeader) {
		header = pHeader;
	}

	public String getHeader() {
		return header;
	}

	@Override
	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}
}
