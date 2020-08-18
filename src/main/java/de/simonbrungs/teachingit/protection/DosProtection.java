package de.simonbrungs.teachingit.protection;

import de.simonbrungs.teachingit.api.events.Listener;
import de.simonbrungs.teachingit.api.events.SocketAcceptedEvent;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DosProtection implements Listener<SocketAcceptedEvent> {
	private final int connectionsPerUserPerSecond;
	private final int connectionsPerSecondGenerally;
	HashMap<String, Integer> counter = new HashMap<>();
	private int connectedCounter = 0;

	public DosProtection(int pConnectionsPerUserPerSecond, int pConnectionsPerSecondGenerally) {
		connectionsPerSecondGenerally = pConnectionsPerSecondGenerally;
		connectionsPerUserPerSecond = pConnectionsPerUserPerSecond;
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				counter = new HashMap<>();
				connectedCounter = 0;
			}
		}, 1000, 1000);
	}

	@Override
	public void executeEvent(SocketAcceptedEvent pEvent) {
		String ip = pEvent.getIP();
		Integer counted = counter.get(ip);
		connectedCounter++;
		if (counted == null)
			counted = 0;
		if (counted > connectionsPerUserPerSecond || connectedCounter > connectionsPerSecondGenerally) {
			pEvent.setCanceled(true);
		} else {
			counter.put(ip, counted + 1);
		}
	}
}
