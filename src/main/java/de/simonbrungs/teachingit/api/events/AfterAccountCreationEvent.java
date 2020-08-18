package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.Account;

public class AfterAccountCreationEvent extends Event {
	private boolean isCanceld = false;
	private final Account account;

	public AfterAccountCreationEvent(Account pAccount) {
		account = pAccount;
	}

	public Account getAccount() {
		return account;
	}

	@Override
	public String getEventName() {
		return "AfterAccountCreationEvent";
	}

	@Override
	public boolean isCanceld() {
		return isCanceld;
	}

	@Override
	public void setCanceld(boolean pCanceld) {
		isCanceld = pCanceld;
	}

}
