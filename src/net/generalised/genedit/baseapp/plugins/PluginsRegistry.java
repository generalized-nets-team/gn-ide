package net.generalised.genedit.baseapp.plugins;

import java.util.ArrayList;
import java.util.Collection;

public class PluginsRegistry {

	private static final Collection<Plugin> plugins = new ArrayList<Plugin>();
	
	public static void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
		plugin.initialize();
	}
}
