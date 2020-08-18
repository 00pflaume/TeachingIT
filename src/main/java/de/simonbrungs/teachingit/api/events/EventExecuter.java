package de.simonbrungs.teachingit.api.events;

import de.simonbrungs.teachingit.TeachingIt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;

public class EventExecuter {
	private static EventExecuter eventExecuter;
	private final ArrayList<ListenerEntry> registredEvents = new ArrayList<>();

	public EventExecuter() {
		eventExecuter = this;
	}

	public static EventExecuter getInstance() {
		return eventExecuter;
	}

	public void registerListener(Listener<?> pListener, Class<? extends Event> pClass, int pPriority) {
		ListenerEntry entry = new ListenerEntry(pListener, pClass, pPriority);
		registredEvents.add(entry);
		sortByListenerPriority(registredEvents);
	}

	private void sortByListenerPriority(ArrayList<ListenerEntry> pToSort) {
		if (pToSort.size() > 1) {
			ArrayList<ListenerEntry> smaller = new ArrayList<>();
			ArrayList<ListenerEntry> bigger = new ArrayList<>();
			ListenerEntry piviot = pToSort.get(0);
			pToSort.remove(0);
			while (!pToSort.isEmpty()) {
				ListenerEntry actual = pToSort.get(0);
				if (actual.getPriority() < piviot.getPriority()) {
					smaller.add(actual);
				} else {
					bigger.add(actual);
				}
				pToSort.remove(0);
			}
			sortByListenerPriority(smaller);
			sortByListenerPriority(bigger);
			pToSort.addAll(smaller);
			pToSort.add(piviot);
			pToSort.addAll(bigger);
		}
	}

	public void executeEvent(Event pEvent) {
		for (ListenerEntry listenerEntry : registredEvents) {
			if (pEvent.getClass().isAssignableFrom(listenerEntry.getToExecuteEventType())) {
				try {
					listenerEntry.getExecutiveListener().executeEvent(pEvent);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
				}
				if (pEvent.isCanceld()) {
					return;
				}
			}
		}
	}

	private class ListenerEntry {
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
