package de.simonbrungs.teachingit.api.events;

public class PreAccountCreationEvent extends Event {
	private final String username;
	private final String email;
	private boolean cancel = false;
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
	public boolean isCanceled() {
		return cancel;
	}

	public void setCanceled(boolean pCanceled) {
		cancel = pCanceled;
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
