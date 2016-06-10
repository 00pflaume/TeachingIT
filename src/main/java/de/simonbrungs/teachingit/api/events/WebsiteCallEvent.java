package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class WebsiteCallEvent extends Event {
	private boolean isCanceld = false;
	private TempUser user;

	public WebsiteCallEvent(TempUser user) {
		this.user = user;
	}

	@Override
	public String getEventName() {
		return "WebsiteCallEvent";
	}

	@Override
	public boolean isCanceld() {
		return isCanceld;
	}

	public void setCanceld(boolean pCanceld) {
		isCanceld = pCanceld;
	}

	public TempUser getUser() {
		return user;
	}

}
