package de.simonbrungs.teachingit.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.simonbrungs.teachingit.api.plugin.theme.Theme;
import de.simonbrungs.teachingit.exceptions.ThemeAlreadyRegisterd;

public class PluginManager {
	private ArrayList<Plugin> plugins = new ArrayList<>();
	private String pluginManagerPrefix = "[PluginManager] ";
	private Theme theme = null;

	public Theme getTheme() {
		return theme;
	}

	public boolean registerTheme(File pThemeJar) throws ThemeAlreadyRegisterd {
		if (this.theme != null)
			throw new ThemeAlreadyRegisterd();
		Properties propertieFile = getPropertieFile(pThemeJar);
		if (propertieFile == null)
			return false;
		if (!checkPropertieFile(propertieFile)) {
			System.out.println(pluginManagerPrefix + "The Theme propertie file of " + pThemeJar.getName()
					+ " is not correct. Needed information are missing");
			return false;
		}
		try {
			Theme theme = null;
			theme = (Theme) loadPlugin(pThemeJar, propertieFile, Theme.class);
			if (theme != null) {
				System.out.println(pluginManagerPrefix + "The Theme " + propertieFile.getProperty("name") + " (version "
						+ propertieFile.getProperty("version") + ") from " + propertieFile.getProperty("author")
						+ " was successfully loaded.");
				this.theme = theme;
				return true;
			} else {
				System.out.println(pluginManagerPrefix
						+ "The plugin could not be loaded. The given main class of the plugin does not "
						+ " extends the class \"Theme\".");
			}
		} catch (ClassNotFoundException e) {
			System.out.println(pluginManagerPrefix + "The given plugin \"" + propertieFile.getProperty("main")
					+ "\" class could not be found.");
			e.printStackTrace();
		} catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
			System.out.println(pluginManagerPrefix + "An error occurred while loading a plugin.");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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
				return cl.newInstance();
			}
			return null;
		} finally {
			try {
				if (loader != null) {
					loader.close();
				}
			} catch (IOException e) {
				System.out.println(pluginManagerPrefix + "URLClassLoader could not be closed.");
				e.printStackTrace();
			}
		}
	}

	public void registerPlugin(File pPluginJar) {
		Properties propertieFile = getPropertieFile(pPluginJar);
		if (propertieFile == null)
			return;
		if (!checkPropertieFile(propertieFile)) {
			System.out.println(pluginManagerPrefix + "The plugin propertie file of " + pPluginJar.getName()
					+ " is not correct. Needed information are missing");
			return;
		}
		try {
			Plugin pluginInstance = null;
			pluginInstance = (Plugin) loadPlugin(pPluginJar, propertieFile, Plugin.class);
			if (pluginInstance != null) {
				pluginInstance.onEnable();
				System.out.println(pluginManagerPrefix + "The plugin " + propertieFile.getProperty("name")
						+ " (version " + propertieFile.getProperty("version") + ") from "
						+ propertieFile.getProperty("author") + " was successfully enabled.");
				plugins.add(pluginInstance);
			} else {
				System.out.println(pluginManagerPrefix
						+ "The plugin could not be loaded. The given main class of the plugin does not "
						+ " implement the interface \"Plugin\".");
			}
		} catch (ClassNotFoundException e) {
			System.out.println(pluginManagerPrefix + "The given plugin \"" + propertieFile.getProperty("main")
					+ "\" class could not be found.");
			e.printStackTrace();
		} catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
			System.out.println(pluginManagerPrefix + "An error occurred while loading a plugin.");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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
				System.out.println(pluginManagerPrefix + "Propertie file missing in " + pPluginJar.getName() + ".");
				return null;
			}
			InputStream stream = zipFile.getInputStream(propertieFileEntry);
			prop.load(stream);
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkPropertieFile(Properties pProperties) {
		return pProperties.getProperty("main") != null && pProperties.getProperty("author") != null
				&& pProperties.getProperty("version") != null && pProperties.getProperty("name") != null;
	}

}
