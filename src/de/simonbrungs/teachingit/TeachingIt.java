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
import de.simonbrungs.teachingit.api.groups.GroupManager;
import de.simonbrungs.teachingit.api.plugin.PluginManager;
import de.simonbrungs.teachingit.api.users.AccountManager;
import de.simonbrungs.teachingit.commands.ShutDown;
import de.simonbrungs.teachingit.connection.MySQLConnection;
import de.simonbrungs.teachingit.exceptions.ThemeAlreadyRegisterdException;
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
	private MySQLConnection con;
	private GroupManager groupManager;
	private AccountManager accountManager;

	public static void main(String[] args) {
		new TeachingIt();
	}

	public String getIncludeContentURL() {
		return getHomeDirectory() + "include/";
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public TeachingIt() {
		main = this;
		if (createConfig()) {
			System.out.println(PREFIX + "The Config was created. Please input your data into the config file.");
			return;
		}
		config = initConfig();
		registerCommands();
		con = new MySQLConnection(config.getProperty("MySQLUser"), config.getProperty("MySQLPassword"),
				Integer.parseInt(config.getProperty("MySQLPort")), config.getProperty("MySQLHost"),
				config.getProperty("MySQLTablePrefix"), config.getProperty("MySQLDatabase"));
		eventExecuter = new EventExecuter();
		groupManager = new GroupManager();
		accountManager = new AccountManager();
		System.out.println(PREFIX + "Now going to load plugins.");
		loadPlugins();
		System.out.println(PREFIX + "Plugins loaded.");
		System.out.println(PREFIX + "Now going to load theme");
		if (loadTheme()) {
			System.out.println(PREFIX + "The server is started");
			webserver = new Webserver(config.getProperty("WebServerPath"),
					Integer.parseInt(config.getProperty("WebServerPort")));
			registerIncludes();
			console.commandsReader();
		} else {
			System.out.println(PREFIX + "The server is now going to hold.");
			shutDown(0);
		}
	}

	private void registerIncludes() {
		File folder = new File("include");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		for (final File fileEntry : folder.listFiles()) {
			System.out.println(fileEntry.getName());
			getWebserver().registerFile(fileEntry, fileEntry.getName());
		}
	}

	public Webserver getWebserver() {
		return webserver;
	}

	public MySQLConnection getConnection() {
		return con;
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

	private boolean loadTheme() {
		final File folder = new File("theme");
		if (!folder.exists()) {
			folder.mkdirs();
			System.out.println(PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		File theme = new File("theme/theme.jar");
		if (!theme.exists()) {
			System.out.println(PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		if (theme.isDirectory()) {
			System.out.println(PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		try {
			if (!pluginManager.registerTheme(theme)) {
				System.out.println(PREFIX + "An error happend while loading the theme");
				return false;
			}
		} catch (ThemeAlreadyRegisterdException e) {
			e.printStackTrace();
		}
		return true;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void shutDown(int pExitState) {
		shouldClose = true;
		if (webserver != null) {
			webserver.stop();
		}
		System.out.println(PREFIX + "The server is now going to hold. Goodbye");
		getPluginManager().unregisterAllPlugins();
		Runtime.getRuntime().exit(pExitState);
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
		ShutDown shutdownCommand = new ShutDown();
		getConsole().registerCommand(shutdownCommand, "stop");
		getConsole().registerCommand(shutdownCommand, "end");
		getConsole().registerCommand(shutdownCommand, "hold");
		getConsole().registerCommand(shutdownCommand, "shutdown");
	}

	public EventExecuter getEventExecuter() {
		return eventExecuter;
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
				prop.setProperty("MySQLTablePrefix", "TIt_");
				prop.setProperty("MySQLUseSSL", "true");
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

	public Properties getConfig() {
		return config;
	}

	private Properties initConfig() {
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