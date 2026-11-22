package com.cosmiclogic.logiclib.paper;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * LogicLib for Paper: shared code for CosmicLogic's Paper plugins. Adds no gameplay on its own.
 *
 * <p>Plugins that use it add {@code depend: [LogicLib]} to their plugin.yml (only if they really
 * use it). Modules:
 * <ul>
 *   <li>{@link LogicVersion} - which Minecraft version the server runs (1.12.2 ... 26.x)</li>
 *   <li>{@link LogicConfigs} - config.yml with new keys added automatically; broken files are kept, not overwritten (0.2.0)</li>
 *   <li>{@link LogicText} - colored chat messages with a prefix</li>
 *   <li>{@link LogicParticles} - particle lines, boxes and circles that work on every version; {@code FAR} for big shapes (0.2.0)</li>
 * </ul>
 */
public final class LogicLibPlugin extends JavaPlugin {

    private static LogicLibPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("LogicLib for Paper " + getDescription().getVersion() + " running on Minecraft " + LogicVersion.current());
        if (new File(getDataFolder(), "selftest").exists()) {
            // Only used by LogicLib's automatic tests (the file is never created on a real server).
            getServer().getScheduler().runTaskLater(this, this::selfTest, 20L);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /** The running LogicLib plugin, or null if it isn't enabled. */
    public static LogicLibPlugin get() {
        return instance;
    }

    private void selfTest() {
        List<String> bad = new ArrayList<>();
        // LR-014: long-distance particles
        String far;
        List<World> worlds = getServer().getWorlds();
        if (!LogicParticles.farSupported()) {
            far = "fallback";
            if (LogicVersion.current().isAtLeast(1, 13, 0)) bad.add("far particles not found on a 1.13+ server");
        } else if (!worlds.isEmpty() && LogicParticles.selfTestFar(worlds.get(0))) {
            far = "forced";
        } else {
            far = "broken";
            bad.add("far particle call threw");
        }
        // LR-015: a broken config.yml is kept, not overwritten
        try {
            File dir = new File(getDataFolder(), "selftest-config");
            dir.mkdirs();
            File[] old = dir.listFiles();
            if (old != null) for (File f : old) f.delete();
            File cfg = new File(dir, "config.yml");
            String broken = "range: 16\nenabled: [true\n  oops: : :\n";
            Files.write(cfg.toPath(), broken.getBytes(StandardCharsets.UTF_8));
            Logger quiet = Logger.getAnonymousLogger();
            quiet.setUseParentHandlers(false);
            File backup = LogicConfigs.keepBrokenFile(cfg, quiet);
            if (backup == null || !backup.isFile()) bad.add("broken config was not set aside");
            else if (!new String(Files.readAllBytes(backup.toPath()), StandardCharsets.UTF_8).equals(broken)) bad.add("backup content differs");
            if (cfg.exists()) bad.add("broken config.yml still in place");
            Files.write(cfg.toPath(), "range: 16\n".getBytes(StandardCharsets.UTF_8));
            if (LogicConfigs.keepBrokenFile(cfg, quiet) != null) bad.add("a good config was moved");
            YamlConfiguration typed = new YamlConfiguration();
            typed.setDefaults(YamlConfiguration.loadConfiguration(new StringReader("range: 16\nenabled: true\nname: x\n")));
            typed.loadFromString("range: banana\nenabled: maybe\nname: 5\n");
            int n = LogicConfigs.problems(typed).size();
            if (n != 2) bad.add("type check found " + n + " problems, expected 2");
        } catch (Throwable t) {
            bad.add("config check threw " + t);
        }
        if (bad.isEmpty()) {
            getLogger().info("LogicLib self-test passed on Minecraft " + LogicVersion.current() + " (far particles: " + far + ", broken config kept: yes, type check: yes)");
        } else {
            getLogger().warning("LogicLib self-test FAILED: " + String.join("; ", bad));
        }
    }
}
