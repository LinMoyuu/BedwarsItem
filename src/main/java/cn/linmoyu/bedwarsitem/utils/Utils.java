package cn.linmoyu.bedwarsitem.utils;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Utils {

    public static boolean isCanPlace(Game game, Location location) {
        if (isProtectionLocation(game, location)) {
            return false;
        }
        for (Entity entity : location.getWorld().getNearbyEntities(location.clone().add(0.5, 1, 0.5), 0.5, 1, 0.5)) {
            if (entity instanceof Player) {
                if (!BedwarsUtil.isSpectator(game, ((Player) entity).getPlayer())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isProtectionLocation(Game game, Location location) {
        if (!game.getRegion().isInRegion(location)) {
            return true;
        }
        Block block = location.getBlock();
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            if (me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_spawn_enabled) {
                for (Team team : game.getTeams().values()) {
                    if (team.getSpawnLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_spawn_range, 2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Sound getSound(String modernSound, String legacySound) {
        try {
            return Sound.valueOf(modernSound);
        } catch (IllegalArgumentException e) {
            try {
                return Sound.valueOf(legacySound);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    public static boolean debug(String string) {
        if (!Config.debug) return false;
        BedwarsItem.getInstance().getLogger().info("[DEBUG] " + string);
        return true;
    }

    public static Player getPlacedPlayer(Entity entity, String metaData) {
        String metadata = entity.getMetadata(metaData).get(0).asString();
        String[] data = metadata.split("\\.");
        if (data.length > 2) return null;
        String shooterName = data[1];

        return Bukkit.getPlayer(shooterName);
    }
}
