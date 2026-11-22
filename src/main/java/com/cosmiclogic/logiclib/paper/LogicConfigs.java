package com.cosmiclogic.logiclib.paper;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads a plugin's config.yml and writes any keys that are new in an update, keeping the
 * server owner's other values.
 *
 * <pre>{@code
 * FileConfiguration config = LogicConfigs.load(this);   // in onEnable()
 * int range = config.getInt("range", 16);
 * }</pre>
 * The plugin must ship a default config.yml in its jar.
 *
 * <p><b>Safe with broken files (0.2.0+, LR-015):</b> if the owner's config.yml has a YAML mistake, it is NOT
 * overwritten. It is moved to {@code config.yml.broken-<date>}, a fresh default config.yml is written, and one
 * clear line is logged saying where the mistake is and where the file was kept.
 *
 * <p><b>Wrong types:</b> a value of the wrong kind (e.g. {@code range: banana} where a number is expected) is
 * reported with one log line per key; Bukkit then uses the default value for it. {@link #problems} returns the
 * same list if a plugin wants to show it somewhere else (e.g. in a reload command).
 */
public final class LogicConfigs {

    private static final Pattern LINE = Pattern.compile("line (\\d+)");

    private LogicConfigs() {
    }

    public static FileConfiguration load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        keepBrokenFile(file, plugin.getLogger());
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
        for (String problem : problems(config)) {
            plugin.getLogger().warning(problem);
        }
        return config;
    }

    /**
     * If {@code file} exists but isn't valid YAML, moves it to {@code <name>.broken-<date>} and logs where the
     * mistake is. Returns the backup file, or null if the file was fine (or missing).
     */
    public static File keepBrokenFile(File file, Logger log) {
        if (!file.isFile()) {
            return null;
        }
        String problem;
        try {
            new YamlConfiguration().load(file);
            return null;
        } catch (InvalidConfigurationException e) {
            problem = describe(e);
        } catch (IOException e) {
            return null; // can't read it at all; leave it alone
        }
        String stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        File backup = new File(file.getParentFile(), file.getName() + ".broken-" + stamp);
        try {
            Files.move(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warning(file.getName() + " has a mistake (" + problem + ") and could not be set aside (" + e.getMessage()
                    + "). Using the default settings for now.");
            return null;
        }
        log.warning(file.getName() + " has a mistake (" + problem + "). Using the default settings for now. Your file was kept as "
                + backup.getName() + ": fix it and rename it back to " + file.getName() + ".");
        return backup;
    }

    /** One line per key whose value has the wrong type compared with the plugin's default config.yml. */
    public static List<String> problems(FileConfiguration config) {
        List<String> out = new ArrayList<>();
        ConfigurationSection defaults = config.getDefaults();
        if (defaults == null) {
            return out;
        }
        for (String key : defaults.getKeys(true)) {
            Object def = defaults.get(key);
            if (def instanceof ConfigurationSection || !config.isSet(key)) {
                continue;
            }
            Object value = config.get(key);
            String expected = null;
            if (def instanceof Boolean && !(value instanceof Boolean)) {
                expected = "true or false";
            } else if ((def instanceof Integer || def instanceof Long) && !(value instanceof Integer || value instanceof Long)) {
                expected = "a whole number";
            } else if (def instanceof Number && !(value instanceof Number)) {
                expected = "a number";
            } else if (def instanceof List && !(value instanceof List)) {
                expected = "a list";
            }
            if (expected != null) {
                out.add("config.yml: '" + key + "' should be " + expected + " but is '" + value + "'. Using the default (" + def + ").");
            }
        }
        return out;
    }

    private static String describe(InvalidConfigurationException e) {
        String msg = String.valueOf(e.getMessage());
        Matcher m = LINE.matcher(msg);
        String first = msg.split("\\R", 2)[0].trim();
        if (first.length() > 120) {
            first = first.substring(0, 120) + "...";
        }
        return m.find() ? "around line " + m.group(1) + ": " + first : first;
    }
}
