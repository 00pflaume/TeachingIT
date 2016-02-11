package de.simonbrungs.teachingit.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.ContentCreateEvent;
import de.simonbrungs.teachingit.api.events.HeaderCreateEvent;
import de.simonbrungs.teachingit.api.events.WebsiteCallEvent;
import de.simonbrungs.teachingit.api.users.User;

public class Webserver {
	private boolean shouldStop = false;

	public Webserver(String pAdress, int pPort) {
		new Thread(new Runnable() {
			public void run() {
				webserver(pPort);
			}
		}).start();
	}

	public void webserver(int pPort) {
		try {
			try (ServerSocket serverSocket = new ServerSocket(pPort)) {
				while (!shouldStop)
					try (Socket socket = serverSocket.accept();
							InputStream input = socket.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(input));
							OutputStream output = socket.getOutputStream();
							PrintWriter writer = new PrintWriter(new OutputStreamWriter(output))) {
						for (String line = reader.readLine(); !line.isEmpty(); line = reader.readLine())
							;
						System.out.println("request from " + socket.getRemoteSocketAddress());
						User user = new User(null, null);
						WebsiteCallEvent websiteCallEvent = new WebsiteCallEvent(null);
						TeachingIt.getInstance().getEventExecuter().executeEvent(websiteCallEvent);
						if (!websiteCallEvent.isCanceld()) {
							String header = "<html><head>";
							HeaderCreateEvent headerCreateEvent = new HeaderCreateEvent();
							TeachingIt.getInstance().getEventExecuter().executeEvent(headerCreateEvent);
							String response = header + headerCreateEvent.getHeader();
							response = response + TeachingIt.getInstance().getPluginManager().getTheme().getHeader();
							ContentCreateEvent contentCreateEvent = new ContentCreateEvent(user);
							TeachingIt.getInstance().getEventExecuter().executeEvent(contentCreateEvent);
							if (contentCreateEvent.getTitle() != null) {
								contentCreateEvent.setTitle("Teaching IT");
							}
							if (contentCreateEvent.getContent() == null) {
								contentCreateEvent = TeachingIt.getInstance().getPluginManager().getTheme()
										.getErrorPageGenerator().getErrorPageNotFound(contentCreateEvent);
							}
							response += contentCreateEvent.getTitle() + "</head>"
									+ TeachingIt.getInstance().getPluginManager().getTheme().getBodyStart(user)
									+ contentCreateEvent.getContent()
									+ TeachingIt.getInstance().getPluginManager().getTheme().getBodyStart(user)
									+ "</body></html>";
							writer.println("HTTP/1.0 200 OK");
							writer.println("Content-Type: text/html; charset=ISO-8859-1");
							writer.println("Server: NanoHTTPServer");
							writer.println();
							writer.println(response);
						}
					} catch (IOException iox) {
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (shouldStop == false)
				webserver(pPort);
		}
	}

	public void stop() {
		shouldStop = true;
	}
}
