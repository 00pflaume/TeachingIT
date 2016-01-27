package de.simonbrungs.teachingit.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.WebsiteCallEvent;

public class Webserver {
	HttpServer server;

	public Webserver(String pAdress, int pPort) {
		try {
			server = HttpServer.create(new InetSocketAddress(pPort), 0);
			server.createContext(pAdress, new MyHandler());
			server.setExecutor(null);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(String pResponse, HttpExchange pHttpExchange) throws IOException {
		pHttpExchange.sendResponseHeaders(200, pResponse.length());
		OutputStream os = pHttpExchange.getResponseBody();
		os.write(pResponse.getBytes());
		os.close();
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange pHttpExchange) throws IOException {
			WebsiteCallEvent websiteCallEvent = new WebsiteCallEvent(pHttpExchange);
			TeachingIt.getInstance().getEventExecuter().executeEvent(websiteCallEvent);
			String response = CreateWebsite.createHeader();
			response += CreateWebsite.createBody();
			if (!websiteCallEvent.isCanceld()) {
				pHttpExchange.sendResponseHeaders(200, response.length());
				OutputStream os = pHttpExchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}

	public void stop() {
		if (server != null)
			server.stop(0);
	}
}
