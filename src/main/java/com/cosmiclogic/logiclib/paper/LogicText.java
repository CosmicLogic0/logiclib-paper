package com.cosmiclogic.logiclib.paper;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Simple colored chat messages that work on every version (uses &-color codes).
 *
 * <pre>{@code
 * LogicText.send(player, "&8[&bStash Radar&8]", "&aFound &f3 &achests.");
 * }</pre>
 */
public final class LogicText {

    private LogicText() {
    }

    /** Turns &-codes (like &a, &l) into colors. */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    public static void send(CommandSender to, String message) {
        to.sendMessage(color(message));
    }

    public static void send(CommandSender to, String prefix, String message) {
        to.sendMessage(color(prefix + " " + message));
    }
}
