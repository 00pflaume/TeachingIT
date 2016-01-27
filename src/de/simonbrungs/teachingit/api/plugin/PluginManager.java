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

public class PluginManager {
	ArrayList<Plugin> plugins = new ArrayList<>();
	private String pluginManagerPrefix = "[PluginManager] ";

	public void registerPlugin(File pPluginJar) {
		Properties propertieFile = getPropertieFile(pPluginJar);
		if (propertieFile == null)
			return;
		if (!checkPropertieFile(propertieFile)) {
			System.out.println(
					pluginManagerPrefix + "The plugin propertie file is not correct. Needed information are missing");
			return;
		}
		URLClassLoader loader = null;
		try {
			Plugin pluginInstance = null;
			loader = new URLClassLoader(new URL[] { pPluginJar.toURI().toURL() });
			Class<?> cl = Class.forName(propertieFile.getProperty("main"));
			if (Plugin.class.isAssignableFrom(cl)) {
				pluginInstance = (Plugin) cl.newInstance();
			}
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

	private Properties getPropertieFile(File pPluginJar) {
		ZipFile zipFile = null;
		Properties prop = new Properties();
		try {
			zipFile = new ZipFile(pPluginJar.getAbsolutePath());
			ZipEntry propertieFileEntry = zipFile.getEntry("plugin.yml");
			if (propertieFileEntry == null) {
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
