package com.cosmiclogic.logiclib.paper;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Colored particle shapes shown to ONE player (other players don't see them), on every version
 * from 1.12.2 to 26.x. Used for outlines and highlights (e.g. chests, ranges).
 *
 * <pre>{@code
 * LogicParticles.box(player, minX, minY, minZ, maxX, maxY, maxZ, Color.AQUA);
 * LogicParticles.circle(player, center, 20, Color.YELLOW);
 * }</pre>
 * Call it again every second or so to keep a shape visible (particles fade quickly).
 * Each call draws at most {@value #MAX_POINTS} particles to protect the server and the player.
 */
public final class LogicParticles {

    public static final int MAX_POINTS = 2000;

    private LogicParticles() {
    }

    /** A straight line from a to b, one particle every {@code step} blocks. */
    public static void line(Player player, Location a, Location b, double step, Color color) {
        line(player, a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), step, color, new int[]{0});
    }

    /** The 12 edges of a box (block corners, e.g. a chest from x..x+1). */
    public static void box(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color color) {
        double step = Math.max(0.25, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)) / 40.0);
        int[] budget = {0};
        double[][] bottom = {{minX, minZ}, {maxX, minZ}, {maxX, maxZ}, {minX, maxZ}};
        for (int i = 0; i < 4; i++) {
            double[] p = bottom[i];
            double[] q = bottom[(i + 1) % 4];
            line(player, p[0], minY, p[1], q[0], minY, q[1], step, color, budget);
            line(player, p[0], maxY, p[1], q[0], maxY, q[1], step, color, budget);
            line(player, p[0], minY, p[1], p[0], maxY, p[1], step, color, budget);
        }
    }

    /** A flat circle around a center (for ranges). */
    public static void circle(Player player, Location center, double radius, Color color) {
        int points = (int) Math.min(MAX_POINTS, Math.max(16, Math.round(2 * Math.PI * radius * 2)));
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            point(player, center.getX() + Math.cos(angle) * radius, center.getY(), center.getZ() + Math.sin(angle) * radius, color);
        }
    }

    private static void line(Player player, double x1, double y1, double z1, double x2, double y2, double z2, double step, Color color, int[] budget) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int count = Math.max(1, (int) Math.ceil(length / Math.max(0.05, step)));
        for (int i = 0; i <= count && budget[0] < MAX_POINTS; i++, budget[0]++) {
            double t = (double) i / count;
            point(player, x1 + dx * t, y1 + dy * t, z1 + dz * t, color);
        }
    }

    /** One colored dust particle for this player only. */
    public static void point(Player player, double x, double y, double z, Color color) {
        try {
            if (LogicVersion.current().isAtLeast(1, 13, 0)) {
                ModernDust.spawn(player, x, y, z, color);
            } else {
                LegacyDust.spawn(player, x, y, z, color);
            }
        } catch (Throwable ignored) {
            // Never break the calling plugin over a particle.
        }
    }
}
