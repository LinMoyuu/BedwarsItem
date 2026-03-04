package cn.linmoyu.bedwarsitem.utils;

import org.bukkit.Location;

public class LocationUtil {

    public static Location getLocation(Location location, int x, int y, int z) {
        Location loc = location.getBlock().getLocation();
        loc.add(x, y, z);
        return loc;
    }
}