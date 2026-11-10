package me.jammy.quests.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Math {
    public static Vector vectorBetween(Location origin, Location destination) {
        return new Vector(destination.getX() - origin.getX(), destination.getY() - origin.getY(), destination.getZ() - origin.getZ())
                .normalize();
    }
}
