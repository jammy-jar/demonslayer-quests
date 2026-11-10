package me.jammy.quests.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class ParticleAPI {
    private static final double[][] sphereCoords = new double[11 * 10 * 2][];

    public static void drawSphere(Location location, double radius, Particle particle) {
        if (sphereCoords[0] == null || Arrays.equals(sphereCoords[0], new double[]{java.lang.Math.sin(java.lang.Math.PI / 10), java.lang.Math.cos(java.lang.Math.PI / 10)})) {
            int arrayLocation = 0;
            for (double i = 0; i <= java.lang.Math.PI; i += java.lang.Math.PI / 10) { // 10 being the amount of circles.
                double hradius = radius * java.lang.Math.sin(i); // we get the current radius
                double y = radius * java.lang.Math.cos(i); // we get the current y value.

                for (double a = 0; a < java.lang.Math.PI * 2; a += java.lang.Math.PI / 10) {
                    double x = java.lang.Math.cos(a) * hradius; // x-coordinate
                    double z = java.lang.Math.sin(a) * hradius; // z-coordinate
                    sphereCoords[arrayLocation++] = new double[]{x, y, z};
                }
            }
        }

        for (double[] coordinate : sphereCoords) {
            double x = coordinate[0];
            double y = coordinate[1];
            double z = coordinate[2];
            location.add(x, y, z);
            location.getWorld().spawnParticle(particle, location, 1);
            location.subtract(x, y, z);
        }
    }

    public static void drawLine(Location loc1, Location loc2, Particle particle) {
        Vector vector = loc2.toVector().clone().subtract(loc1.toVector());
        double distance = loc2.toVector().clone().distance(loc1.toVector());
        Location currentLoc = loc1.clone();
        Vector step = vector.normalize().multiply(0.1);
        // Loops for 10x particles per block between the 2 locations.
        for (int p = 0; p < (int) distance * 10; p++) {
            currentLoc.add(step);
            currentLoc.getWorld().spawnParticle(particle, currentLoc, 1);
        }
    }

    public static void drawFilledCircle(Location location, double radius, Particle particle) {
        for (double j = radius; j > 0; j -= 0.5) {
            for (double i = 0; i <= java.lang.Math.PI; i += java.lang.Math.PI / 10) {
                Location particleLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
                particleLoc.setX(location.getX() + java.lang.Math.cos(i) * j);
                particleLoc.setZ(location.getZ() + java.lang.Math.sin(i) * j);
                location.getWorld().spawnParticle(particle, particleLoc, 1);
            }
        }
    }

    public static void drawSpikes(Location location, double radius, Particle particle) {
        drawLine(location, location.clone().add(new Vector(0, radius, 0)), particle);
        drawLine(location, location.clone().add(new Vector(radius, 0, 0)), particle);
        drawLine(location, location.clone().add(new Vector(0, 0, radius)), particle);
        drawLine(location, location.clone().add(new Vector(-radius, 0, 0)), particle);
        drawLine(location, location.clone().add(new Vector(0, 0, -radius)), particle);
        drawLine(location, location.clone().add(new Vector(radius, radius, radius)), particle);
        drawLine(location, location.clone().add(new Vector(radius, radius, -radius)), particle);
        drawLine(location, location.clone().add(new Vector(-radius, radius, radius)), particle);
        drawLine(location, location.clone().add(new Vector(-radius, radius, -radius)), particle);
    }
}
