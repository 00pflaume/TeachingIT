package de.simonbrungs.teachingit.api.events;

public abstract class Event {
	public abstract String getEventName();

	public abstract boolean isCanceld();

	public abstract void setCanceld(boolean pCanceld);
}
