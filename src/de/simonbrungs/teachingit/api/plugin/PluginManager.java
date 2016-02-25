package de.simonbrungs.teachingit.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.simonbrungs.teachingit.TeachingIt;
import de.simonbrungs.teachingit.api.plugin.theme.Theme;
import de.simonbrungs.teachingit.exceptions.ThemeAlreadyRegisterdException;

public class PluginManager {
	private ArrayList<Plugin> plugins = new ArrayList<>();
	private static final String PLUGINMANAGERPREFIX = "[PluginManager] ";
	private Theme theme = null;

	public Theme getTheme() {
		return theme;
	}

	public boolean registerTheme(File pThemeJar) throws ThemeAlreadyRegisterdException {
		if (this.theme != null)
			throw new ThemeAlreadyRegisterdException();
		Properties propertieFile = getPropertieFile(pThemeJar);
		if (propertieFile == null)
			return false;
		if (!checkPropertieFile(propertieFile)) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGINMANAGERPREFIX + "The Theme propertie file of "
					+ pThemeJar.getName() + " is not correct. Needed information are missing");
			return false;
		}
		try {
			Theme theme = null;
			theme = (Theme) loadPlugin(pThemeJar, propertieFile, Theme.class);
			if (theme != null) {
				TeachingIt.getInstance().getLogger().log(Level.INFO,
						PLUGINMANAGERPREFIX + "The Theme " + propertieFile.getProperty("name") + " (version "
								+ propertieFile.getProperty("version") + ") from " + propertieFile.getProperty("author")
								+ " was successfully loaded.");
				this.theme = theme;
				return true;
			} else {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGINMANAGERPREFIX
								+ "The plugin could not be loaded. The given main class of the plugin does not "
								+ " extend the class \"Theme\".");
			}
		} catch (ClassNotFoundException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGINMANAGERPREFIX + "The given main class \""
					+ propertieFile.getProperty("main") + "\" could not be found.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGINMANAGERPREFIX + "An error occurred while loading the theme \"" + pThemeJar.getName() + "\".");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (SecurityException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (IllegalArgumentException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
		return false;
	}

	private Object loadPlugin(File pPluginJar, Properties propertieFile, Class<?> pSearchedSuperClass)
			throws InstantiationException, MalformedURLException, ClassNotFoundException, IllegalAccessException {
		URLClassLoader loader = null;
		try {
			loader = new URLClassLoader(new URL[] { pPluginJar.toURI().toURL() });
			Class<?> cl = loader.loadClass(propertieFile.getProperty("main"));
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
						PLUGINMANAGERPREFIX + "URLClassLoader could not be closed.");
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			}
		}
		return null;
	}

	public void registerPlugin(File pPluginJar) {
		Properties propertieFile = getPropertieFile(pPluginJar);
		if (propertieFile == null)
			return;
		if (!checkPropertieFile(propertieFile)) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGINMANAGERPREFIX + "The plugin propertie file of " + pPluginJar.getName()
							+ " is not correct. Needed information are missing");
			return;
		}
		try {
			Plugin pluginInstance = null;
			pluginInstance = (Plugin) loadPlugin(pPluginJar, propertieFile, Plugin.class);
			if (pluginInstance != null) {
				TeachingIt.getInstance().getLogger().log(Level.INFO,
						PLUGINMANAGERPREFIX + "The plugin " + propertieFile.getProperty("name") + " (version "
								+ propertieFile.getProperty("version") + ") from " + propertieFile.getProperty("author")
								+ " was successfully enabled.");
				plugins.add(pluginInstance);
			} else {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGINMANAGERPREFIX + "Error while loading plugin \"" + pPluginJar.getName() + "\".");
			}
		} catch (ClassNotFoundException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING, PLUGINMANAGERPREFIX + "The given plugin \""
					+ propertieFile.getProperty("main") + "\" class could not be found.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		} catch (MalformedURLException | InstantiationException | IllegalAccessException | SecurityException
				| IllegalArgumentException e) {
			TeachingIt.getInstance().getLogger().log(Level.WARNING,
					PLUGINMANAGERPREFIX + "An error occurred while loading a plugin.");
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
		}
	}

	public boolean isThemeRegisterd() {
		return theme != null;
	}

	private Properties getPropertieFile(File pPluginJar) {
		ZipFile zipFile = null;
		Properties prop = new Properties();
		try {
			zipFile = new ZipFile(pPluginJar.getAbsolutePath());
			ZipEntry propertieFileEntry = zipFile.getEntry("properties/plugin.properties");
			if (propertieFileEntry == null) {
				TeachingIt.getInstance().getLogger().log(Level.WARNING,
						PLUGINMANAGERPREFIX + "Propertie file missing in " + pPluginJar.getName() + ".");
				return null;
			}
			InputStream stream = zipFile.getInputStream(propertieFileEntry);
			prop.load(stream);
			return prop;
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			
			TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
			return null;
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					TeachingIt.getInstance().getLogger().log(Level.WARNING, sw.toString());
				}
			}
		}
	}

	private boolean checkPropertieFile(Properties pProperties) {
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
