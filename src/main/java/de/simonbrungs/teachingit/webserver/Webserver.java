package de.simonbrungs.teachingit.webserver;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.events.*;
import de.simonbrungs.teachingit.api.users.Account;
import de.simonbrungs.teachingit.api.users.TempUser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Webserver {
	public final String PREFIX = "[Webserver] ";
	private final Thread webserverThread;
	private boolean shouldStop = false;

	public Webserver(final long maxPOSTSize, final int pPort) {
		webserverThread = new Thread(new Runnable() {
			public void run() {
				runWebserver(maxPOSTSize, pPort);
			}
		});
		webserverThread.start();
	}

	private void runWebserver(long pMaxPOSTSize, int pPort) {
		try {
			try (ServerSocket serverSocket = new ServerSocket(pPort)) {
				while (!shouldStop) {
					try {
						try (Socket socket = serverSocket.accept();
						     InputStream input = socket.getInputStream();
						     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						     OutputStream output = socket.getOutputStream();
						     PrintWriter writer = new PrintWriter(new OutputStreamWriter(output))) {
							SocketAcceptedEvent sae = new SocketAcceptedEvent(
									(new StringTokenizer(socket.getRemoteSocketAddress().toString(), ":")).nextToken());
							EventExecuter.getInstance().executeEvent(sae);
							if (!sae.isCanceld()) {
								InputProcessor inputprocessor = new InputProcessor(reader, pMaxPOSTSize);
								String path = inputprocessor.getPath();
								String ipAddress = (new StringTokenizer(socket.getRemoteSocketAddress().toString(),
										":")).nextToken();
								TeachingIt.getInstance().getLogger().log(Level.INFO,
										PREFIX + "Request from " + socket.getInetAddress() + " to path " + path);

								Account account = null;
								if (TeachingIt.getInstance().getAccountManager().getSessionKey(ipAddress,
										"username") != null) {
									account = TeachingIt.getInstance().getAccountManager()
											.getAccount((String) TeachingIt.getInstance().getAccountManager()
													.getSessionKey(ipAddress, "username"));
								}
								TempUser user = new TempUser(path, account, ipAddress, inputprocessor.getPostContent());
								if (!inputprocessor.wasPostAccepted())
									user.setUserVar("postaccepted", "false");
								else
									user.setUserVar("postaccepted", "true");
								WebsiteCallEvent websiteCallEvent = new WebsiteCallEvent(user);
								TeachingIt.getInstance().getEventExecuter().executeEvent(websiteCallEvent);
								if (!websiteCallEvent.isCanceld()) {
									{
										String response = "<html><head>";
										HeaderCreateEvent headerCreateEvent = new HeaderCreateEvent(user);
										TeachingIt.getInstance().getEventExecuter().executeEvent(headerCreateEvent);
										if (headerCreateEvent.getHeader() != null) {
											response += headerCreateEvent.getHeader();
										}
										response = response
												+ TeachingIt.getInstance().getPluginManager().getTheme().getHeader();
										ContentCreateEvent contentCreateEvent = new ContentCreateEvent(user);
										TeachingIt.getInstance().getEventExecuter().executeEvent(contentCreateEvent);
										if (contentCreateEvent.getTitle() == null)
											contentCreateEvent.setTitle(
													TeachingIt.getInstance().getConfig().getProperty("SiteName"));
										if (contentCreateEvent.getContent() == null)
											contentCreateEvent = TeachingIt.getInstance().getPluginManager().getTheme()
													.getErrorPageGenerator().getErrorPageNotFound(contentCreateEvent);
										response += "<title>" + contentCreateEvent.getTitle() + "</title>" + "</head>"
												+ TeachingIt.getInstance().getPluginManager().getTheme()
												.getBodyStart(user)
												+ contentCreateEvent.getContent() + TeachingIt.getInstance()
												.getPluginManager().getTheme().getBodyEnd(user)
												+ "</body></html>";
										writer.println("HTTP/1.0 200 OK");
										writer.println("Content-Type: text/html; charset=ISO-8859-1");
										writer.println("Server: HTTPServer");
										writer.println();
										writer.println(response);
									}
								}
							}
						} catch (IOException iox) {
						} catch (Exception e) {
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw));
							TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
						}
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						System.gc();
						runWebserver(pMaxPOSTSize, pPort);
					}
				}
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		shouldStop = true;
		webserverThread.stop();
	}

	private class InputProcessor {
		private final ArrayList<String> input = new ArrayList<>();
		private boolean postAccepted = true;
		private HashMap<String, Object> postContent = new HashMap<>();

		public InputProcessor(BufferedReader reader, long pMaxPOSTSize) {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			input.add(line);
			String raw = "";
			raw += line;
			boolean isPost = false;
			if (line != null)
				isPost = line.startsWith("POST");
			int contentLength = 0;
			postAccepted = true;
			boolean shouldRun = true;
			while (shouldRun) {
				try {
					if ((line = reader.readLine()) != null)
						if (line.equals("")) {
							shouldRun = false;
							break;
						}
					input.add(line);
					raw += ('\n' + line);
					if (isPost && postAccepted) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line.substring(contentHeader.length()));
							postAccepted = contentLength <= pMaxPOSTSize;
						}
					}
				} catch (IOException e) {
					postAccepted = false;
					e.printStackTrace();
				}
			}
			try {
				String body = "";
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = reader.read();
						postAccepted = false;
						body += ((char) c);
					}
				}
				raw += (body);
				postContent = parseQuery(raw);
			} catch (IOException e) {
				postAccepted = false;
				e.printStackTrace();
			}
		}

		private HashMap<String, Object> parseQuery(String query) throws UnsupportedEncodingException {
			HashMap<String, Object> parameters = new HashMap<>();
			if (query != null) {
				String[] pairs = query.split("[&]");
				for (String pair : pairs) {
					String[] param = pair.split("[=]");
					String key = null;
					String value = null;
					if (param.length > 0) {
						try {
							key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
						} catch (IllegalArgumentException e) {
							return parameters;
						}
					}
					if (param.length > 1) {
						try {
							value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
						} catch (IllegalArgumentException e) {
							return parameters;
						}
					}
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

		public boolean wasPostAccepted() {
			return postAccepted;
		}

		public HashMap<String, Object> getPostContent() {
			return postContent;
		}

		public String getPath() {
			String path = "";
			Pattern getLinePattern = Pattern.compile("(?i)GET\\s+/(.*?)\\s+HTTP/1\\.[01]");
			for (String line : input) {
				Matcher matcher = getLinePattern.matcher(line);
				if (matcher.matches())
					path = matcher.group(1);
			}
			if (path.equals("")) {
				getLinePattern = Pattern.compile("(?i)POST\\s+/(.*?)\\s+HTTP/1\\.[01]");
				path = "";
				for (String line : input) {
					Matcher matcher = getLinePattern.matcher(line);
					if (matcher.matches())
						path = matcher.group(1);
				}
			}
			return path;
		}
	}
}
