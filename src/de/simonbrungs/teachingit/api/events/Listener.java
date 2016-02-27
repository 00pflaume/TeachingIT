package de.simonbrungs.teachingit.api.events;

public interface Listener<T extends Event> {
	public void executeEvent(T pEvent);

}
