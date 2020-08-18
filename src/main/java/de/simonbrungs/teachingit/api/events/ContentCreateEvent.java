package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.TempUser;

public class ContentCreateEvent extends Event {
	private final TempUser user;
	private boolean canceld = false;
	private String content = null;
	private String title = null;

	public ContentCreateEvent(TempUser pUser) {
		user = pUser;
	}

	@Override
	public String getEventName() {
		return "ContentCreateEvent";
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}

	public String getContent() {
		return content;

	}

	public void setContent(String pContent) {
		content = pContent;
	}

	public TempUser getUser() {
		return user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String pTitle) {
		title = pTitle;
	}
}
