package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class WebsiteCallEvent extends Event {
	private final TempUser user;
	private boolean isCanceled = false;

	public WebsiteCallEvent(TempUser user) {
		this.user = user;
	}

	@Override
	public String getEventName() {
		return "WebsiteCallEvent";
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean pCanceled) {
		isCanceled = pCanceled;
	}

	public TempUser getUser() {
		return user;
	}

}
