package de.simonbrungs.teachingit.api.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import de.simonbrungs.teachingit.TeachingIt;

public class PluginManager {
	ArrayList<Plugin> plugins = new ArrayList<>();

	@SuppressWarnings("resource")
	public void registerPlugin(File pPluginJar) {
		URL[] urls = new URL[1];
		try {
			urls[0] = pPluginJar.toURI().toURL();
			ClassLoader loader = new URLClassLoader(urls);
			JarInputStream jaris = new JarInputStream(new FileInputStream(pPluginJar));
			JarEntry entry;
			while ((entry = jaris.getNextJarEntry()) != null) {
				if (entry.getName().toLowerCase().endsWith(".class")) {
					String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
					Class<?> cls = loader.loadClass(className);
					for (Class<?> i : cls.getInterfaces()) {
						if (i.equals(Plugin.class)) {
							plugins.add((Plugin) cls.newInstance());
							break;
						}
					}
				}
			}
			plugins.get(plugins.size() - 1).onEnable();
			System.out.println(
					TeachingIt.getInstance().getPrefix() + "The plugin " + plugins.get(plugins.size() - 1).getName()
							+ " (version " + plugins.get(plugins.size() - 1).getVersion() + ") from "
							+ plugins.get(plugins.size() - 1).getAuthor() + " was successfully enabled.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
