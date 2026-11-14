package com.cosmiclogic.logiclib.paper;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Colored dust for 1.12.2: the old trick where count = 0 and the offsets are the RGB color.
 * Only loaded on servers older than 1.13.
 */
final class LegacyDust {

    private static final Particle REDSTONE = find("REDSTONE");

    private LegacyDust() {
    }

    static void spawn(Player player, double x, double y, double z, Color color) {
        if (REDSTONE == null) {
            return;
        }
        double r = Math.max(0.001, color.getRed() / 255.0);
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;
        player.spawnParticle(REDSTONE, x, y, z, 0, r, g, b, 1);
    }

    private static Particle find(String name) {
        for (Particle particle : Particle.values()) {
            if (particle.name().equals(name)) {
                return particle;
            }
        }
        return null;
    }
}
