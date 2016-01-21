package de.simonbrungs.teachingit;

import java.io.File;
import java.util.Properties;

import de.simonbrungs.teachingit.api.Console;
import de.simonbrungs.teachingit.api.events.EventExecuter;
import de.simonbrungs.teachingit.api.plugin.PluginManager;
import de.simonbrungs.teachingit.api.theme.Theme;
import de.simonbrungs.teachingit.commands.ShutDown;
import de.simonbrungs.teachingit.utilities.config.ConfigLoader;
import de.simonbrungs.teachingit.webserver.Webserver;

public class TeachingIt {
	private static TeachingIt main;
	private Webserver webserver;
	private Properties config;
	private boolean shouldClose = false;
	private Console console = new Console();
	private PluginManager pluginManager = new PluginManager();
	private String prefix = "[TeachingIt] ";
	private EventExecuter eventExecuter;
	private Theme theme;

	public static void main(String[] args) {
		new TeachingIt();
	}

	public String getPrefix() {
		return prefix;
	}

	public TeachingIt() {
		main = this;
		ConfigLoader configLoader = new ConfigLoader();
		if (configLoader.createConfig()) {
			System.out.println(getPrefix() + "The Config was created. Please input your data into the config file.");
			return;
		}
		config = configLoader.getConfig();
		loadPlugins();
		webserver = new Webserver(config.getProperty("WebServerPath"),
				Integer.parseInt(config.getProperty("WebServerPort")));
		registerCommands();
		System.out.println(getPrefix() + "Now going to load plugins.");
		System.out.println(getPrefix() + "Plugins loaded.");
		System.out.println(getPrefix() + "The server is started");
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
		System.out.println(getPrefix() + "The server is now going to hold. Goodbye");
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
}