package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class HeaderCreateEvent extends Event {
	private final TempUser user;
	private boolean canceled = false;
	private String header = "";

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
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean pCanceled) {
		canceled = pCanceled;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String pHeader) {
		header = pHeader;
	}
}
