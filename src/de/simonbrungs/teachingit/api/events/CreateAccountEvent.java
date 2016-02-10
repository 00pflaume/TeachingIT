package de.simonbrungs.teachingit.api.events;

public class CreateAccountEvent extends Event {
	private boolean cancel = false;
	private String username;
	private String email;
	private boolean active;

	public CreateAccountEvent(String pUserName, String pEmail, boolean pActive) {
		username = pUserName;
		email = pEmail;
		active = pActive;
	}

	@Override
	public String getEventName() {
		return "CreateAccountEvent";
	}

	public void setCanceld(boolean pCancel) {
		cancel = pCancel;
	}

	@Override
	public boolean isCanceld() {
		return cancel;
	}

}
