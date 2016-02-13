package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.Account;

public class AccountDeleteEvent extends Event {
	private boolean canceld = false;
	private Account account;
	private boolean shouldBeDelted = true;

	public AccountDeleteEvent(Account pAccount) {
		account = pAccount;
	}

	public Account getAccount() {
		return account;
	}

	public void setShouldBeDeleted(boolean pShouldBeDelted) {
		shouldBeDelted = pShouldBeDelted;
	}

	public boolean getShouldBeDeleted() {
		return shouldBeDelted;
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
