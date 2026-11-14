package com.cosmiclogic.cosmiclib.paper;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * CosmicLib for Paper: shared code for CosmicLogic's Paper plugins. Adds no gameplay on its own.
 *
 * <p>Plugins that use it add {@code depend: [CosmicLib]} to their plugin.yml (only if they really
 * use it). Modules (v0.1.0):
 * <ul>
 *   <li>{@link CosmicVersion} - which Minecraft version the server runs (1.12.2 ... 26.x)</li>
 *   <li>{@link CosmicConfigs} - config.yml with new keys added automatically</li>
 *   <li>{@link CosmicText} - colored chat messages with a prefix</li>
 *   <li>{@link CosmicParticles} - particle lines, boxes and circles that work on every version</li>
 * </ul>
 */
public final class CosmicLibPlugin extends JavaPlugin {

    private static CosmicLibPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("CosmicLib for Paper " + getDescription().getVersion() + " running on Minecraft " + CosmicVersion.current());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /** The running CosmicLib plugin, or null if it isn't enabled. */
    public static CosmicLibPlugin get() {
        return instance;
    }
}
