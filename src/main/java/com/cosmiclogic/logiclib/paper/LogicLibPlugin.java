package com.cosmiclogic.logiclib.paper;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * LogicLib for Paper: shared code for CosmicLogic's Paper plugins. Adds no gameplay on its own.
 *
 * <p>Plugins that use it add {@code depend: [LogicLib]} to their plugin.yml (only if they really
 * use it). Modules (v0.1.0):
 * <ul>
 *   <li>{@link LogicVersion} - which Minecraft version the server runs (1.12.2 ... 26.x)</li>
 *   <li>{@link LogicConfigs} - config.yml with new keys added automatically</li>
 *   <li>{@link LogicText} - colored chat messages with a prefix</li>
 *   <li>{@link LogicParticles} - particle lines, boxes and circles that work on every version</li>
 * </ul>
 */
public final class LogicLibPlugin extends JavaPlugin {

    private static LogicLibPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("LogicLib for Paper " + getDescription().getVersion() + " running on Minecraft " + LogicVersion.current());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /** The running LogicLib plugin, or null if it isn't enabled. */
    public static LogicLibPlugin get() {
        return instance;
    }
}
