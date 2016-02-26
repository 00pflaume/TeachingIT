package de.simonbrungs.teachingit.api.events;

import java.net.SocketAddress;

public class SocketAcceptedEvent extends Event {
	private boolean canceld = false;
	private SocketAddress socketAddress;

	public SocketAcceptedEvent(SocketAddress remoteSocketAddress) {
		socketAddress = remoteSocketAddress;
	}

	public SocketAddress getRemoteSocketAddress() {
		return socketAddress;
	}

	@Override
	public String getEventName() {
		return "SocketAcceptEvent";
	}

	@Override
	public boolean isCanceld() {
		return canceld;
	}

	@Override
	public void setCanceld(boolean pCanceld) {
		canceld = pCanceld;
	}

}
