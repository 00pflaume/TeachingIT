package de.simonbrungs.teachingit.api.events;

public class SocketAcceptedEvent extends Event {
	private final String ip;
	private boolean canceled = false;

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
	public boolean isCanceled() {
		return canceled;
	}

	@Override
	public void setCanceled(boolean pCanceled) {
		canceled = pCanceled;
	}

}
