package de.simonbrungs.teachingit.api.events;

public interface Listener<T> {
	public void executeEvent(T pEvent);

}
