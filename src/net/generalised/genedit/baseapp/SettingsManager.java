package net.generalised.genedit.baseapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {

	private static final SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	private SettingsManager() {
		this.properties = new Properties();
		this.defaultProperties = new Properties();
	}

	private static final String CONFIG_FILE_PATH = System.getProperty("user.home")
			+ File.separator + ".genedit.properties";
	public static final File CONFIG_FILE = new File(CONFIG_FILE_PATH);
	
	private final Properties properties;
	private final Properties defaultProperties;

	public void setDefaultProperty(String key, String value) {
		this.defaultProperties.put(key, value);
		syncActualProperties();
	}

	public void reset() {
		this.properties.clear();
		syncActualProperties();
		
		CONFIG_FILE.delete();
	}
	
	// call this after every modification of this.properties
	private void syncActualProperties() {
		for (Object key : this.defaultProperties.keySet()) {
			if (this.properties.get(key) == null) {
				this.properties.put(key, this.defaultProperties.get(key));
			}
		}
	}

	public String getProperty(String key) {
		return (String) this.properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		this.properties.put(key, value);
		syncActualProperties();
	}

	public void store() throws IOException {
		this.properties.store(new FileWriter(CONFIG_FILE), null);
	}
	
	public void load() throws IOException {
		if (CONFIG_FILE.exists()) {
			this.properties.load(new FileInputStream(CONFIG_FILE));
		} else {
			reset();
		}
	}
}	
