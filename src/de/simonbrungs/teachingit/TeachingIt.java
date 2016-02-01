package de.simonbrungs.teachingit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import de.simonbrungs.teachingit.api.Console;
import de.simonbrungs.teachingit.api.events.EventExecuter;
import de.simonbrungs.teachingit.api.plugin.PluginManager;
import de.simonbrungs.teachingit.api.theme.Theme;
import de.simonbrungs.teachingit.commands.ShutDown;
import de.simonbrungs.teachingit.webserver.Webserver;

public class TeachingIt {
	private static TeachingIt main;
	private Webserver webserver;
	private Properties config;
	private boolean shouldClose = false;
	private Console console = new Console();
	private PluginManager pluginManager = new PluginManager();
	public final String PREFIX = "[TeachingIt] ";
	private EventExecuter eventExecuter;
	private Theme theme;

	public static void main(String[] args) {
		new TeachingIt();
	}

	public String getIncludeContentURL() {
		return getHomeDirectory() + "include/";
	}

	public TeachingIt() {
		main = this;
		if (createConfig()) {
			System.out.println(PREFIX + "The Config was created. Please input your data into the config file.");
			return;
		}
		config = getConfig();
		loadPlugins();
		webserver = new Webserver(config.getProperty("WebServerPath"),
				Integer.parseInt(config.getProperty("WebServerPort")));
		registerCommands();
		System.out.println(PREFIX + "Now going to load plugins.");
		System.out.println(PREFIX + "Plugins loaded.");
		System.out.println(PREFIX + "The server is started");
		console.commandsReader();
	}

	private void loadPlugins() {
		final File folder = new File("plugins");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				pluginManager.registerPlugin(fileEntry);
			}
		}

	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void shutDown() {
		shouldClose = true;
		if (webserver != null)
			webserver.stop();
		System.out.println(PREFIX + "The server is now going to hold. Goodbye");
	}

	public boolean getShouldClose() {
		return shouldClose;
	}

	public static TeachingIt getInstance() {
		return main;
	}

	public Console getConsole() {
		return console;
	}

	private void registerCommands() {
		getConsole().registerCommand(new ShutDown(), "stop");
	}

	public EventExecuter getEventExecuter() {
		return eventExecuter;
	}

	public Theme getTheme() {
		return theme;
	}

	public String getHomeDirectory() {
		return config.getProperty("WebServerDomain") + config.getProperty("WebServerPath");
	}

	private boolean createConfig() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			File file = new File("config.properties");
			if (!file.exists()) {
				output = new FileOutputStream(file);
				prop.setProperty("WebServerDomain", "localhost");
				prop.setProperty("WebServerPath", "/");
				prop.setProperty("WebServerPort", "80");
				prop.setProperty("MySQLHost", "localhost");
				prop.setProperty("MySQLPort", "3306");
				prop.setProperty("MySQLUser", "root");
				prop.setProperty("MySQLPassword", "password");
				prop.setProperty("MySQLDatabase", "TeachingIt");
				prop.setProperty("MySQLTablePrefixes", "TIt_");
				prop.store(output, null);
				return true;
			}
			return false;
		} catch (IOException io) {
			io.printStackTrace();
			return false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Properties getConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
			return prop;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}