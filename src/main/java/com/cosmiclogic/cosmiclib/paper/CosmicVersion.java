package com.cosmiclogic.cosmiclib.paper;

import org.bukkit.Bukkit;

/**
 * The server's Minecraft version, for code that must behave differently on old and new versions.
 * Works with both old numbers (1.12.2, 1.21.11) and year-based numbers (26.1, 26.3).
 *
 * <pre>{@code
 * if (CosmicVersion.current().isAtLeast(1, 20, 5)) { ... }
 * }</pre>
 */
public final class CosmicVersion implements Comparable<CosmicVersion> {

    private static CosmicVersion current;

    public final int major;
    public final int minor;
    public final int patch;

    public CosmicVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /** The version of the running server (cached). */
    public static CosmicVersion current() {
        if (current == null) {
            current = parse(Bukkit.getBukkitVersion());
        }
        return current;
    }

    /** Parses strings like "1.20.1-R0.1-SNAPSHOT" or "26.1.2". Unknown parts become 0. */
    public static CosmicVersion parse(String text) {
        String clean = text == null ? "" : text.trim();
        int dash = clean.indexOf('-');
        if (dash >= 0) {
            clean = clean.substring(0, dash);
        }
        String[] parts = clean.split("\\.");
        return new CosmicVersion(number(parts, 0), number(parts, 1), number(parts, 2));
    }

    private static int number(String[] parts, int index) {
        if (index >= parts.length) {
            return 0;
        }
        StringBuilder digits = new StringBuilder();
        for (char c : parts[index].toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                break;
            }
        }
        try {
            return digits.length() == 0 ? 0 : Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isAtLeast(int major, int minor, int patch) {
        return compareTo(new CosmicVersion(major, minor, patch)) >= 0;
    }

    public boolean isBelow(int major, int minor, int patch) {
        return !isAtLeast(major, minor, patch);
    }

    @Override
    public int compareTo(CosmicVersion other) {
        if (major != other.major) {
            return Integer.compare(major, other.major);
        }
        if (minor != other.minor) {
            return Integer.compare(minor, other.minor);
        }
        return Integer.compare(patch, other.patch);
    }

    @Override
    public String toString() {
        return patch == 0 ? major + "." + minor : major + "." + minor + "." + patch;
    }
}
