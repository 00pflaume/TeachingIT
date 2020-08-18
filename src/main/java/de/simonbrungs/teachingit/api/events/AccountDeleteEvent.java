package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.api.users.Account;

public class AccountDeleteEvent extends Event {
	private final Account account;
	private boolean canceled = false;
	private boolean shouldBeDeleted = true;

	public AccountDeleteEvent(Account pAccount) {
		account = pAccount;
	}

	public Account getAccount() {
		return account;
	}

	public boolean getShouldBeDeleted() {
		return shouldBeDeleted;
	}

	public void setShouldBeDeleted(boolean pShouldBeDeleted) {
		shouldBeDeleted = pShouldBeDeleted;
	}

	@Override
	public String getEventName() {
		return "AccountDeleteEvent";
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean pCanceled) {
		canceled = pCanceled;
	}

}
