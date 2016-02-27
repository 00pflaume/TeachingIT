package de.simonbrungs.teachingit.protection;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.simonbrungs.teachingit.api.events.Listener;
import de.simonbrungs.teachingit.api.events.SocketAcceptedEvent;

public class DosProtection implements Listener<SocketAcceptedEvent> {
	HashMap<String, Integer> counter = new HashMap<>();
	int connectedCounter = 0;
	int connectionsPerUserPerSecound;
	int connectionsPerSecoundGenerally;

	public DosProtection(int pConnectionsPerUserPerSecound, int pConnectionsPerSecoundGenerally) {
		connectionsPerSecoundGenerally = pConnectionsPerSecoundGenerally;
		connectionsPerUserPerSecound = pConnectionsPerUserPerSecound;
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
		String socketAddress = pEvent.getRemoteSocketAddress().toString();
		Integer counted = counter.get(socketAddress);
		connectedCounter++;
		if (counted == null)
			counted = 0;
		if (counted > connectionsPerUserPerSecound || connectedCounter > connectionsPerSecoundGenerally) {
			pEvent.setCanceld(true);
		} else {
			counter.put(socketAddress, counted + 1);
		}
	}
}
