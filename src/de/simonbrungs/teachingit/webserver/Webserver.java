package de.simonbrungs.teachingit.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.ContentCreateEvent;
import de.simonbrungs.teachingit.api.events.HeaderCreateEvent;
import de.simonbrungs.teachingit.api.events.WebsiteCallEvent;
import de.simonbrungs.teachingit.api.users.User;

public class Webserver {
	private boolean shouldStop = false;
	private Thread webserverThread;
	private HashMap<String, File> registerdFiles = new HashMap<>();
	public final String PREFIX = "[Webserver]";

	public Webserver(String pAdress, int pPort) {
		webserverThread = new Thread(new Runnable() {
			public void run() {
				starWebserver(pPort);
			}
		});
		webserverThread.start();
	}

	public void starWebserver(int pPort) {
		try {
			try (ServerSocket serverSocket = new ServerSocket(pPort)) {
				while (!shouldStop)
					try (Socket socket = serverSocket.accept();
							InputStream input = socket.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(input));
							OutputStream output = socket.getOutputStream();
							PrintWriter writer = new PrintWriter(new OutputStreamWriter(output))) {
						ArrayList<String> inputstring = new ArrayList<>();
						{
							for (String line = reader.readLine(); line != null; line = reader.readLine()) {
								if (line.isEmpty())
									break;
								inputstring.add(line);
							}
						}
						HashMap<String, Object> postRequests = new HashMap<String, Object>();
						postRequests = parseQuery(inputstring);
						String path = getPath(inputstring);
						System.out.println(PREFIX + "request from " + socket.getInetAddress() + " to path " + path);
						System.out.println("test1");
						User user = new User(path, null, socket.getRemoteSocketAddress(), postRequests);
						WebsiteCallEvent websiteCallEvent = new WebsiteCallEvent(null);
						TeachingIt.getInstance().getEventExecuter().executeEvent(websiteCallEvent);
						if (!websiteCallEvent.isCanceld()) {
							File file = registerdFiles.get(path);
							if (file != null) {
								List<String> lines = Files.readAllLines(Paths.get(path));
								writer.println("HTTP/1.0 200 OK");
								writer.println("Content-Type: text/html; charset=ISO-8859-1");
								writer.println("Server: NanoHTTPServer");
								writer.println();
								String response = lines.get(0);
								lines.remove(0);
								for (String line : lines)
									response += "\n" + line;
								writer.println(response);
							} else {
								String response = "<html><head>";
								HeaderCreateEvent headerCreateEvent = new HeaderCreateEvent();
								TeachingIt.getInstance().getEventExecuter().executeEvent(headerCreateEvent);
								if (headerCreateEvent.getHeader() != null) {
									response += headerCreateEvent.getHeader();
								}
								response = response
										+ TeachingIt.getInstance().getPluginManager().getTheme().getHeader();
								ContentCreateEvent contentCreateEvent = new ContentCreateEvent(user);
								TeachingIt.getInstance().getEventExecuter().executeEvent(contentCreateEvent);
								if (contentCreateEvent.getTitle() == null) {
									contentCreateEvent.setTitle("Teaching IT");
								}
								if (contentCreateEvent.getContent() == null) {
									contentCreateEvent = TeachingIt.getInstance().getPluginManager().getTheme()
											.getErrorPageGenerator().getErrorPageNotFound(contentCreateEvent);
								}
								response += "<title>" + contentCreateEvent.getTitle() + "</title>" + "</head>"
										+ TeachingIt.getInstance().getPluginManager().getTheme().getBodyStart(user)
										+ contentCreateEvent.getContent()
										+ TeachingIt.getInstance().getPluginManager().getTheme().getBodyEnd(user)
										+ "</body></html>";
								writer.println("HTTP/1.0 200 OK");
								writer.println("Content-Type: text/html; charset=ISO-8859-1");
								writer.println("Server: HTTPServer");
								writer.println();
								writer.println(response);
							}
						}
					} catch (IOException iox) {
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private HashMap<String, Object> parseQuery(ArrayList<String> list) throws UnsupportedEncodingException {
		HashMap<String, Object> parameters = new HashMap<>();
		String query = null;
		query = list.get(0);
		System.out.println("supertest");
		if (query != null) {
			System.out.println("test1");
			String[] pairs = query.split("[&]");
			System.out.println("test2");

			for (String pair : pairs) {
				System.out.println("test3");

				String[] param = pair.split("[=]");
				System.out.println("test4");

				String key = null;
				String value = null;
				System.out.println("test5");
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
					System.out.println("test1");

				}
				System.out.println("test6");

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
					System.out.println("test1");
				}
				System.out.println("test7");
				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
		return parameters;
	}

	private String getPath(ArrayList<String> list) throws IOException {
		final Pattern getLinePattern = Pattern.compile("(?i)GET\\s+/(.*?)\\s+HTTP/1\\.[01]");
		String resource = "";
		for (String line : list) {
			Matcher matcher = getLinePattern.matcher(line);
			if (matcher.matches())
				resource = matcher.group(1);
		}
		return resource;
	}

	public void registerFile(File pFile, String pURL) {
		registerdFiles.put(pURL, pFile);
	}

	public boolean isURLRegisterd(String pURL) {
		return registerdFiles.containsKey(pURL);
	}

	public boolean unregisterFile(String pURL) {
		return registerdFiles.remove(pURL) != null;
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		shouldStop = true;
		webserverThread.stop();
	}
}
