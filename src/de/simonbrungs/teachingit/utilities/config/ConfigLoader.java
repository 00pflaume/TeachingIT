package de.simonbrungs.teachingit.utilities.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigLoader {

	public boolean createConfig() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			File file = new File("config.properties");
			if (!file.exists()) {
				output = new FileOutputStream(file);
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

	public Properties getConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			System.out.println(prop.getProperty("database"));
			System.out.println(prop.getProperty("dbuser"));
			System.out.println(prop.getProperty("dbpassword"));
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
