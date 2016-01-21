package de.simonbrungs.teachingit.api.events;

import com.sun.net.httpserver.HttpExchange;

public class WebsiteCallEvent extends Event {
	private boolean isCanceld = false;
	private HttpExchange httpExchange;

	public WebsiteCallEvent(HttpExchange pHttpExchange) {
		httpExchange = pHttpExchange;
	}

	@Override
	public String getEventName() {
		return "HttpExchangeEvent";
	}

	@Override
	public boolean isCanceld() {
		return isCanceld;
	}

	public void setCanceld(boolean pCanceld) {
		isCanceld = pCanceld;
	}

	public HttpExchange getHttpExchange() {
		return httpExchange;
	}

}
