package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.Account;

public class AccountDeleteEvent extends Event {
	private final Account account;
	private boolean canceld = false;
	private boolean shouldBeDelted = true;

	public AccountDeleteEvent(Account pAccount) {
		account = pAccount;
	}

	public Account getAccount() {
		return account;
	}

	public boolean getShouldBeDeleted() {
		return shouldBeDelted;
	}

	public void setShouldBeDeleted(boolean pShouldBeDelted) {
		shouldBeDelted = pShouldBeDelted;
	}

	@Override
	public String getEventName() {
		return "AccountDeleteEvent";
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	@Override
	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}

}
