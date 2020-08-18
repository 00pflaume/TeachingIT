package de.simonbrungs.teachingit.api.plugin;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.plugin.theme.Theme;
import de.simonbrungs.teachingit.exceptions.ThemeAlreadyRegisteredException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginManager {
	private static final String PLUGIN_MANAGER_PREFIX = "[PluginManager] ";
	private static PluginManager instance = null;
	private final ArrayList<Plugin> plugins = new ArrayList<>();
	private Theme theme = null;

	public PluginManager() throws IllegalAccessException {
		if (instance != null)
			throw new IllegalAccessException();
		instance = this;
	}

	public static PluginManager getInstance() {
		return instance;
	}

	public Theme getTheme() {
		return theme;
	}

	public boolean registerTheme(File pThemeJar) throws ThemeAlreadyRegisteredException {
		if (this.theme != null)
			throw new ThemeAlreadyRegisteredException();
		Properties propertyFile = getPropertyFile(pThemeJar);
		if (propertyFile == null)
			return false;
		if (!checkPropertyFile(propertyFile)) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGIN_MANAGER_PREFIX + "The Theme property file of "
					+ pThemeJar.getName() + " is not correct. Needed information are missing");
			return false;
		}
		try {
			Theme theme;
			theme = (Theme) loadPlugin(pThemeJar, propertyFile, Theme.class);
			if (theme != null) {
				TeachingIt.getInstance().getLogger().log(Level.INFO,
						PLUGIN_MANAGER_PREFIX + "The Theme " + propertyFile.getProperty("name") + " (version "
								+ propertyFile.getProperty("version") + ") from " + propertyFile.getProperty("author")
								+ " was successfully loaded.");
				this.theme = theme;
				return true;
			} else {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGIN_MANAGER_PREFIX
								+ "The plugin could not be loaded. The given main class of the plugin does not "
								+ " extend the class \"Theme\".");
			}
		} catch (ClassNotFoundException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGIN_MANAGER_PREFIX + "The given main class \""
					+ propertyFile.getProperty("main") + "\" could not be found.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGIN_MANAGER_PREFIX + "An error occurred while loading the theme \"" + pThemeJar.getName() + "\".");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (SecurityException | IllegalArgumentException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		return false;
	}

	private Object loadPlugin(File pPluginJar, Properties propertyFile, Class<?> pSearchedSuperClass)
			throws InstantiationException, MalformedURLException, ClassNotFoundException, IllegalAccessException {
		URLClassLoader loader = null;
		try {
			loader = new URLClassLoader(new URL[]{pPluginJar.toURI().toURL()});
			Class<?> cl = loader.loadClass(propertyFile.getProperty("main"));
			if (pSearchedSuperClass.isAssignableFrom(cl)) {
				Plugin instance = (Plugin) cl.getDeclaredConstructor().newInstance();
				try {
					try {
						instance.onEnable();
					} catch (Throwable e) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
					}
				} catch (Throwable e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
					return null;
				}
				return instance;
			}
			return null;
		} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
			StringWriter sw = new StringWriter();
			e1.printStackTrace(new PrintWriter(sw));
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} finally {
			try {
				if (loader != null) {
					loader.close();
				}
			} catch (IOException e) {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGIN_MANAGER_PREFIX + "URLClassLoader could not be closed.");
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			}
		}
		return null;
	}

	public void registerPlugin(File pPluginJar) {
		Properties propertyFile = getPropertyFile(pPluginJar);
		if (propertyFile == null)
			return;
		if (!checkPropertyFile(propertyFile)) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGIN_MANAGER_PREFIX + "The plugin property file of " + pPluginJar.getName()
							+ " is not correct. Needed information are missing");
			return;
		}
		try {
			Plugin pluginInstance;
			pluginInstance = (Plugin) loadPlugin(pPluginJar, propertyFile, Plugin.class);
			if (pluginInstance != null) {
				TeachingIt.getInstance().getLogger().log(Level.INFO,
						PLUGIN_MANAGER_PREFIX + "The plugin " + propertyFile.getProperty("name") + " (version "
								+ propertyFile.getProperty("version") + ") from " + propertyFile.getProperty("author")
								+ " was successfully enabled.");
				plugins.add(pluginInstance);
			} else {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGIN_MANAGER_PREFIX + "Error while loading plugin \"" + pPluginJar.getName() + "\".");
			}
		} catch (ClassNotFoundException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGIN_MANAGER_PREFIX + "The given plugin \""
					+ propertyFile.getProperty("main") + "\" class could not be found.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (MalformedURLException | InstantiationException | IllegalAccessException | SecurityException
				| IllegalArgumentException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGIN_MANAGER_PREFIX + "An error occurred while loading a plugin.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
	}

	public boolean isThemeRegistered() {
		return theme != null;
	}

	private Properties getPropertyFile(File pPluginJar) {
		ZipFile zipFile = null;
		Properties prop = new Properties();
		try {
			zipFile = new ZipFile(pPluginJar.getAbsolutePath());
			ZipEntry propertyFileEntry = zipFile.getEntry("properties/plugin.properties");
			if (propertyFileEntry == null) {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGIN_MANAGER_PREFIX + "Property file missing in " + pPluginJar.getName() + ".");
				return null;
			}
			InputStream stream = zipFile.getInputStream(propertyFileEntry);
			prop.load(stream);
			return prop;
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));

			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			TeachingIt.getInstance().closeStream(zipFile);
		}
	}

	private boolean checkPropertyFile(Properties pProperties) {
		return pProperties.getProperty("main") != null && pProperties.getProperty("author") != null
				&& pProperties.getProperty("version") != null && pProperties.getProperty("name") != null;
	}

	public void unregisterPlugin(Plugin pPlugin) {
		if (plugins.contains(pPlugin)) {
			try {
				pPlugin.onDisable();
			} catch (Throwable e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			}
			plugins.remove(pPlugin);
		}
	}

	public void unregisterAllPlugins() {
		while (!plugins.isEmpty())
			unregisterPlugin(plugins.get(0));
	}

}
