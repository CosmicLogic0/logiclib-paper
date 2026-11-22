package com.cosmiclogic.logiclib.paper;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/** Colored dust for 1.13+ (the particle is called REDSTONE before 1.20.5 and DUST after). Only loaded on 1.13+. */
final class ModernDust {

    private static final Particle DUST = find("DUST", "REDSTONE");

    private ModernDust() {
    }

    static void spawn(Player player, double x, double y, double z, Color color, boolean far) {
        if (DUST == null) {
            return;
        }
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);
        if (far && FarParticles.spawn(player, DUST, x, y, z, 1, 0, 0, 0, 0, dust)) {
            return;
        }
        player.spawnParticle(DUST, x, y, z, 1, 0, 0, 0, 0, dust);
    }

    /** Self-test: sends one far dust particle to nobody, to check the call works on this server. */
    static boolean selfTest(org.bukkit.World world) {
        return DUST != null && FarParticles.spawn(world, new java.util.ArrayList<Player>(), DUST, 0, 64, 0, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.RED, 1.0f));
    }

    static Particle find(String... names) {
        for (String name : names) {
            for (Particle particle : Particle.values()) {
                if (particle.name().equals(name)) {
                    return particle;
                }
            }
        }
        return null;
    }
}
