package de.simonbrungs.teachingit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.XMLFormatter;

import de.simonbrungs.teachingit.api.Console;
import de.simonbrungs.teachingit.api.events.EventExecuter;
import de.simonbrungs.teachingit.api.groups.GroupManager;
import de.simonbrungs.teachingit.api.plugin.PluginManager;
import de.simonbrungs.teachingit.api.users.AccountManager;
import de.simonbrungs.teachingit.commands.ShutDown;
import de.simonbrungs.teachingit.connectors.MySQLConnector;
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
	private MySQLConnector connector;
	private GroupManager groupManager;
	private AccountManager accountManager;
	private FileHandler fh;
	private Logger logger;

	public static void main(String[] args) {
		new TeachingIt();
	}

	public String getIncludeContentURL() {
		return getHomeDirectory() + "include/";
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public Logger getLogger() {
		return logger;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public TeachingIt() {
		main = this;
		try {
			LogManager lm = LogManager.getLogManager();
			File folder = new File("logs");
			if (!folder.exists())
				folder.mkdirs();
			String minute = Calendar.getInstance().get(Calendar.MINUTE) + "";
			if (minute.length() < 2) {
				minute = "0" + minute;
			}
			File file = new File("./logs/" + Calendar.getInstance().get(Calendar.YEAR) + "-"
					+ Calendar.getInstance().get(Calendar.MONTH) + "-"
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-"
					+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "-" + minute + ".xml");
			int count = 0;
			while (file.exists()) {
				count++;
				file = new File(
						"./logs/" + file.getName().substring(0, file.getName().length() - 4) + "-" + count + ".xml");
			}
			fh = new FileHandler(file.getAbsolutePath());
			logger = Logger.getLogger("log");
			lm.addLogger(logger);
			logger.setLevel(Level.INFO);
			fh.setFormatter(new XMLFormatter());
			logger.addHandler(fh);
		} catch (SecurityException | IOException e) {
			System.out.println(PREFIX + "Fatal Error!");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			shutDown(1);
		}
		getLogger().log(Level.INFO, PREFIX + "Server is starting");
		if (createConfig()) {
			File folder = new File("plugins");
			if (!folder.exists()) {
				folder.mkdirs();
			}
			folder = new File("theme");
			if (!folder.exists()) {
				folder.mkdirs();
			}
			getLogger().log(Level.WARNING,
					PREFIX + "The Config was created. Please input your data into the config file.");
			return;
		}
		config = initConfig();
		registerCommands();
		connector = new MySQLConnector(config.getProperty("MySQLUser"), config.getProperty("MySQLPassword"),
				Integer.parseInt(config.getProperty("MySQLPort")), config.getProperty("MySQLHost"),
				config.getProperty("MySQLTablePrefix"), config.getProperty("MySQLDatabase"));
		eventExecuter = new EventExecuter();
		groupManager = new GroupManager();
		accountManager = new AccountManager();
		getLogger().log(Level.INFO, PREFIX + "Now going to load plugins.");
		loadPlugins();
		getLogger().log(Level.INFO, PREFIX + "Plugins loaded.");
		getLogger().log(Level.INFO, PREFIX + "Now going to load theme");
		if (loadTheme()) {
			getLogger().log(Level.INFO, PREFIX + "The server is started");
			webserver = new Webserver(config.getProperty("WebServerPath"),
					Integer.parseInt(config.getProperty("WebServerPort")));
			registerIncludes();
			console.commandsReader();
		} else {
			getLogger().log(Level.INFO, PREFIX + "The server is now going to hold.");
			shutDown(0);
		}
	}

	private void registerIncludes() {
		File folder = new File("include");
		if (!folder.exists()) {
			folder.mkdirs();
		}
		for (final File fileEntry : folder.listFiles()) {
			getWebserver().registerFile(fileEntry, fileEntry.getName());
		}
	}

	public Webserver getWebserver() {
		return webserver;
	}

	public MySQLConnector getConnector() {
		return connector;
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
			getLogger().log(Level.WARNING, PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		File theme = new File("theme/theme.jar");
		if (!theme.exists()) {
			getLogger().log(Level.WARNING, PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		if (theme.isDirectory()) {
			getLogger().log(Level.WARNING, PREFIX + "Put a theme which is named theme.jar into the theme folder");
			return false;
		}
		try {
			if (!pluginManager.registerTheme(theme)) {
				getLogger().log(Level.WARNING, PREFIX + "An error occurred while loading the theme");
				return false;
			}
		} catch (ThemeAlreadyRegisterdException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		return true;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void shutDown(int pExitState) {
		try {
			getLogger().log(Level.INFO, PREFIX + "Server is going to ShutDown");
			shouldClose = true;
			if (webserver != null) {
				webserver.stop();
			}
			fh.close();
			getPluginManager().unregisterAllPlugins();
			getLogger().log(Level.INFO, PREFIX + "GoodBye");
		} catch (Throwable e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			getLogger().log(Level.WARNING, PREFIX + "Error while shuttingdown");
		} finally {
			Runtime.getRuntime().exit(pExitState);
		}
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
				prop.setProperty("SiteName", "TeachingIt");
				prop.setProperty("MySQLHost", "localhost");
				prop.setProperty("MySQLPort", "3306");
				prop.setProperty("MySQLUser", "root");
				prop.setProperty("MySQLPassword", "password");
				prop.setProperty("MySQLDatabase", "TeachingIt");
				prop.setProperty("MySQLTablePrefix", "TIt_");
				prop.setProperty("MySQLUseSSL", "false");
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
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));

					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
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
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));

					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
				}
			}
		}
	}
}