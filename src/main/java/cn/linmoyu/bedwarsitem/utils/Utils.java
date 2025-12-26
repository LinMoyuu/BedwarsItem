package cn.linmoyu.bedwarsitem.utils;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Utils {

    public static boolean isCanPlace(Game game, Location location) {
        Block block = location.getBlock();
        if (!game.getRegion().isInRegion(location)) {
            return false;
        }
        for (Entity entity : location.getWorld().getNearbyEntities(location.clone().add(0.5, 1, 0.5), 0.5, 1, 0.5)) {
            if (!(entity instanceof Player)) {
                return false;
            }
            Player player = (Player) entity;
            if (!game.isInGame(player) || game.isSpectator(player)) {
                return false;
            }
        }
        if (Bukkit.getPluginManager().isPluginEnabled("BedwarsScoreBoardAddon")) {
            if (me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_spawn_enabled) {
                for (Team team : game.getTeams().values()) {
                    if (team.getSpawnLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_spawn_range, 2)) {
                        return false;
                    }
                }
            }
            if (me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_resource_enabled) {
                for (ResourceSpawner spawner : game.getResourceSpawners()) {
                    if (spawner.getLocation().distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_resource_range, 2)) {
                        return false;
                    }
                }
                if (me.ram.bedwarsscoreboardaddon.config.Config.game_team_spawner.containsKey(game.getName())) {
                    for (List<Location> locs : me.ram.bedwarsscoreboardaddon.config.Config.game_team_spawner.get(game.getName()).values()) {
                        for (Location loc : locs) {
                            if (loc.distanceSquared(block.getLocation().clone().add(0.5, 0, 0.5)) <= Math.pow(me.ram.bedwarsscoreboardaddon.config.Config.spawn_no_build_resource_range, 2)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
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
}
