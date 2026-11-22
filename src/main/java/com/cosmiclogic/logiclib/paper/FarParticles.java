package com.cosmiclogic.logiclib.paper;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Long-distance ("force") particles for ONE player (LR-014).
 *
 * <p>Normal particles are thrown away by the player's game when they are more than 32 blocks from the camera.
 * Paper's {@code World.spawnParticle(particle, receivers, source, x, y, z, count, ox, oy, oz, extra, data, force)}
 * with {@code force = true} sends them as long-distance particles, which the game draws much farther away.
 * That method is looked up by its parameter types, so the same jar runs from Paper 1.12.2 to 26.x; when a server
 * doesn't have it, {@link #spawn} returns false and the caller falls back to a normal particle.
 */
final class FarParticles {

    private static volatile boolean looked;
    private static Method method;

    private FarParticles() {
    }

    static Method method() {
        if (!looked) {
            synchronized (FarParticles.class) {
                if (!looked) {
                    method = find();
                    looked = true;
                }
            }
        }
        return method;
    }

    private static Method find() {
        for (Method m : World.class.getMethods()) {
            if (!m.getName().equals("spawnParticle")) continue;
            Class<?>[] p = m.getParameterTypes();
            if (p.length == 13 && p[0] == Particle.class && p[1] == List.class && p[2] == Player.class
                    && p[3] == double.class && p[4] == double.class && p[5] == double.class && p[6] == int.class
                    && p[7] == double.class && p[8] == double.class && p[9] == double.class && p[10] == double.class
                    && p[11] == Object.class && p[12] == boolean.class) {
                return m;
            }
        }
        return null;
    }

    /** True if this server can send long-distance particles to one player. */
    static boolean supported() {
        return method() != null;
    }

    /** Sends one long-distance particle to {@code receivers}. Returns false if the server can't (caller falls back). */
    static boolean spawn(World world, List<Player> receivers, Particle particle, double x, double y, double z, int count,
                         double ox, double oy, double oz, double extra, Object data) {
        Method m = method();
        if (m == null || world == null) {
            return false;
        }
        try {
            m.invoke(world, particle, receivers, null, x, y, z, count, ox, oy, oz, extra, data, true);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    static boolean spawn(Player player, Particle particle, double x, double y, double z, int count,
                         double ox, double oy, double oz, double extra, Object data) {
        return spawn(player.getWorld(), Collections.singletonList(player), particle, x, y, z, count, ox, oy, oz, extra, data);
    }
}
