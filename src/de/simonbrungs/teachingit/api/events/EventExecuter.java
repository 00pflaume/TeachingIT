package de.simonbrungs.teachingit.api.events;

import java.util.ArrayList;

public class EventExecuter {
	ArrayList<ListenerEntry> registerdEvents = new ArrayList<>();

	public void registerEventExecuter(Listener<?> pListener, Class<?> pClass, int pPriority) {
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
			if (pEvent.getClass().equals(listenerEntry.getToExecuteEventType())) {
				listenerEntry.getExecutiveListener().executeEvent(pEvent);
				if (pEvent.isCanceld()) {
					return;
				}
			}
		}
	}

	private class ListenerEntry {
		private int priority;
		private Listener<Event> listener;
		private Class<Event> eventClass;

		public ListenerEntry(Listener<?> pListener, Class<?> pEvent, int pPriority) {
			priority = pPriority;
		}

		public int getPriority() {
			return priority;
		}

		public Listener<Event> getExecutiveListener() {
			return listener;
		}

		public Class<Event> getToExecuteEventType() {
			return eventClass;
		}
	}

}
