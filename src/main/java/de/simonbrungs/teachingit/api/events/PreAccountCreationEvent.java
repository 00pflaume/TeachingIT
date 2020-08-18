package de.simonbrungs.teachingit.api.events;

public class PreAccountCreationEvent extends Event {
	private boolean cancel = false;
	private final String username;
	private final String email;
	private byte activated;

	public PreAccountCreationEvent(String pUserName, String pEmail, byte pActive) {
		username = pUserName;
		email = pEmail;
		activated = pActive;
	}

	@Override
	public String getEventName() {
		return "CreateAccountEvent";
	}

	@Override
	public boolean isCanceld() {
		return cancel;
	}

	public void setCanceld(boolean pCancel) {
		cancel = pCancel;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public byte getActivated() {
		return activated;
	}

	public void setActivated(byte pActivated) {
		activated = pActivated;
	}
}
