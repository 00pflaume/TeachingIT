package de.simonbrungs.teachingit.api.events;

public abstract class Event {
	public abstract String getEventName();

	public abstract boolean isCanceled();

	public abstract void setCanceled(boolean pCanceled);
}
