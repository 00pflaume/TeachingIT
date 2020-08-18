package de.simonbrungs.teachingit.api.events;

public interface Listener<T extends Event> {
	void executeEvent(T pEvent);

}
