package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.User;

public class HeaderCreateEvent extends Event {
	private boolean canceld = false;
	private String header = "";
	private User user;

	public HeaderCreateEvent(User pUser) {
		user = pUser;
	}

	public User getUser() {
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
