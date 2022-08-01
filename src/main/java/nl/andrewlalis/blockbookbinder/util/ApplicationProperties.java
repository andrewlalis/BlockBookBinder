package nl.andrewlalis.blockbookbinder.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton class which handles the properties defined in an external
 * properties file.
 */
public class ApplicationProperties {
	private static ApplicationProperties instance;
	private final Properties properties;

	private final Map<String, Integer> intPropCache;

	public static ApplicationProperties getInstance() {
		if (instance == null) {
			try {
				instance = new ApplicationProperties();
			} catch (IOException e) {
				System.err.println("Could not load properties!");
				System.exit(1);
			}
		}
		return instance;
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * Shortcut for getting a property.
	 * @param key The property's key.
	 * @return The value for that property.
	 */
	public static String getProp(String key) {
		return getInstance().getProperties().getProperty(key);
	}

	public static Integer getIntProp(String key) {
		Integer value = getInstance().intPropCache.get(key);
		if (value == null) {
			value = Integer.parseInt(getProp(key));
			getInstance().intPropCache.put(key, value);
		}
		return value;
	}

	private ApplicationProperties() throws IOException {
		this.properties = new Properties();
		this.properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
		this.intPropCache = new HashMap<>();
	}
}
