package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.User;

public class WebsiteCallEvent extends Event {
	private boolean isCanceld = false;
	private User user;

	public WebsiteCallEvent(User user) {
		this.user = user;
	}

	@Override
	public String getEventName() {
		return "HttpExchangeEvent";
	}

	@Override
	public boolean isCanceld() {
		return isCanceld;
	}

	public void setCanceld(boolean pCanceld) {
		isCanceld = pCanceld;
	}

	public User getUser() {
		return user;
	}

}
