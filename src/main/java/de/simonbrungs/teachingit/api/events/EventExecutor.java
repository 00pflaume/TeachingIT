package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.TeachingIt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;

public class EventExecutor {
	private static EventExecutor eventExecutor;
	private final ArrayList<ListenerEntry> registeredEvents = new ArrayList<>();

	public EventExecutor() {
		eventExecutor = this;
	}

	public static EventExecutor getInstance() {
		return eventExecutor;
	}

	public void registerListener(Listener<?> pListener, Class<? extends Event> pClass, int pPriority) {
		ListenerEntry entry = new ListenerEntry(pListener, pClass, pPriority);
		registeredEvents.add(entry);
		sortByListenerPriority(registeredEvents);
	}

	private void sortByListenerPriority(ArrayList<ListenerEntry> pToSort) {
		if (pToSort.size() > 1) {
			ArrayList<ListenerEntry> smaller = new ArrayList<>();
			ArrayList<ListenerEntry> bigger = new ArrayList<>();
			ListenerEntry pivot = pToSort.get(0);
			pToSort.remove(0);
			while (!pToSort.isEmpty()) {
				ListenerEntry actual = pToSort.get(0);
				if (actual.getPriority() < pivot.getPriority()) {
					smaller.add(actual);
				} else {
					bigger.add(actual);
				}
				pToSort.remove(0);
			}
			sortByListenerPriority(smaller);
			sortByListenerPriority(bigger);
			pToSort.addAll(smaller);
			pToSort.add(pivot);
			pToSort.addAll(bigger);
		}
	}

	public void executeEvent(Event pEvent) {
		for (ListenerEntry listenerEntry : registeredEvents) {
			if (pEvent.getClass().isAssignableFrom(listenerEntry.getToExecuteEventType())) {
				try {
					listenerEntry.getExecutiveListener().executeEvent(pEvent);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
				}
				if (pEvent.isCanceled()) {
					return;
				}
			}
		}
	}

	private static class ListenerEntry {
		private final int priority;
		private final Listener<?> listener;
		private final Class<? extends Event> eventClass;

		public ListenerEntry(Listener<?> pListener, Class<? extends Event> pEvent, int pPriority) {
			priority = pPriority;
			listener = pListener;
			eventClass = pEvent;
		}

		public int getPriority() {
			return priority;
		}

		@SuppressWarnings("unchecked")
		public Listener<Event> getExecutiveListener() {
			return (Listener<Event>) listener;
		}

		public Class<?> getToExecuteEventType() {
			return eventClass;
		}
	}

}
