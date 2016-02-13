package de.simonbrungs.teachingit.api.events;

public class PreAccountCreationEvent extends Event {
	private boolean cancel = false;
	private String username;
	private String email;
	private boolean activated;

	public PreAccountCreationEvent(String pUserName, String pEmail, boolean pActive) {
		username = pUserName;
		email = pEmail;
		activated = pActive;
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

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean pActivated) {
		activated = pActivated;
	}
}
