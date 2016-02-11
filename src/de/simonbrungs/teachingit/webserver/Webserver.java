package de.simonbrungs.teachingit.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.HeaderCreateEvent;
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

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange pHttpExchange) throws IOException {
			WebsiteCallEvent websiteCallEvent = new WebsiteCallEvent(pHttpExchange);
			TeachingIt.getInstance().getEventExecuter().executeEvent(websiteCallEvent);
			if (!websiteCallEvent.isCanceld()) {
				String header = "<html><head>";
				HeaderCreateEvent headerCreateEvent = new HeaderCreateEvent();
				TeachingIt.getInstance().getEventExecuter().executeEvent(headerCreateEvent);
				String response = header + headerCreateEvent.getHeader();
				response = response + TeachingIt.getInstance().getTheme().getHeader();
				response += "</body></html>";
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
