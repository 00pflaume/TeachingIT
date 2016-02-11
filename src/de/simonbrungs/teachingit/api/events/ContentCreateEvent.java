package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.User;

public class ContentCreateEvent extends Event {
	private boolean canceld = false;
	private String content = null;
	private User user = null;
	private String title = null;

	public ContentCreateEvent(User pUser) {
		user = pUser;
	}

	@Override
	public String getEventName() {
		return "ContentCreateEvent";
	}

	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	public String getContent() {
		return content;

	}

	public void setContent(String pContent) {
		content = pContent;
	}

	public User getUser() {
		return user;
	}

	public void setTitle(String pTitle) {
		title = pTitle;
	}

	public String getTitle() {
		return title;
	}
}
