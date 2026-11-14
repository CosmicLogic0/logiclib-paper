package com.cosmiclogic.cosmiclib.paper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Loads a plugin's config.yml and writes any keys that are new in an update, keeping the
 * server owner's other values.
 *
 * <pre>{@code
 * FileConfiguration config = CosmicConfigs.load(this);   // in onEnable()
 * int range = config.getInt("range", 16);
 * }</pre>
 * The plugin must ship a default config.yml in its jar.
 */
public final class CosmicConfigs {

    private CosmicConfigs() {
    }

    public static FileConfiguration load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
        return config;
    }
}
