package de.simonbrungs.teachingit.api.events;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;

import de.simonbrungs.teachingit.TeachingIt;

public class EventExecuter {
	private static EventExecuter eventExecuter;

	public EventExecuter() {
		eventExecuter = this;
	}

	public static EventExecuter getInstance() {
		return eventExecuter;
	}

	ArrayList<ListenerEntry> registerdEvents = new ArrayList<>();

	public void registerListener(Listener<?> pListener, Class<?> pClass, int pPriority) {
		ListenerEntry entry = new ListenerEntry(pListener, pClass, pPriority);
		registerdEvents.add(entry);
		sortByListenerPriority(registerdEvents);
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
		for (ListenerEntry listenerEntry : registerdEvents) {
			if (pEvent.getClass().isAssignableFrom(listenerEntry.getToExecuteEventType())) {
				try {
					listenerEntry.getExecutiveListener().executeEvent(pEvent);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();e.printStackTrace(new PrintWriter(sw));TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
				}
				if (pEvent.isCanceld()) {
					return;
				}
			}
		}
	}

	private class ListenerEntry {
		private int priority;
		private Listener<?> listener;
		private Class<?> eventClass;

		public ListenerEntry(Listener<?> pListener, Class<?> pEvent, int pPriority) {
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
