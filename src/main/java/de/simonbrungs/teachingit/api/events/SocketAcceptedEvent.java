package de.simonbrungs.teachingit.api.events;

public class SocketAcceptedEvent extends Event {
	private final String ip;
	private boolean canceld = false;

	public SocketAcceptedEvent(String pIP) {
		ip = pIP;
	}

	public String getIP() {
		return ip;
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
