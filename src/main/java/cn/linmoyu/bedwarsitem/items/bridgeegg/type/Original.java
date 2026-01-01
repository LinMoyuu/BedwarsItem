package cn.linmoyu.bedwarsitem.items.bridgeegg.type;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.items.bridgeegg.BridgeEgg;
import cn.linmoyu.bedwarsitem.items.bridgeegg.SetBlock;
import cn.linmoyu.bedwarsitem.utils.LocationUtil;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Original implements SetBlock {

    public void start(Game game, Egg egg, Player player) {
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (!egg.isDead()) {
                    new BukkitRunnable() {
                        final Location location = egg.getLocation().add(0, -1, 0);

                        @Override
                        public void run() {
                            if (game.isOverSet() || game.getState() != GameState.RUNNING) {
                                this.cancel();
                                return;
                            }
                            location.setX((int) location.getX());
                            location.setY((int) location.getY());
                            location.setZ((int) location.getZ());
                            List<Location> blocklocation = new ArrayList<>();
                            blocklocation.add(location);
                            Vector vector = egg.getVelocity();
                            double x = vector.getX() > 0 ? vector.getX() : -vector.getX();
                            double y = vector.getY() > 0 ? vector.getY() : -vector.getY();
                            double z = vector.getZ() > 0 ? vector.getZ() : -vector.getZ();
                            if (y < x || y < z) {
                                blocklocation.add(LocationUtil.getLocation(location, -1, 0, -1));
                                blocklocation.add(LocationUtil.getLocation(location, -1, 0, 0));
                                blocklocation.add(LocationUtil.getLocation(location, 0, 0, -1));
                            } else {
                                blocklocation.add(LocationUtil.getLocation(location, 0, 1, 0));
                                blocklocation.add(LocationUtil.getLocation(location, -1, 1, -1));
                                blocklocation.add(LocationUtil.getLocation(location, -1, 1, 0));
                                blocklocation.add(LocationUtil.getLocation(location, 0, 1, -1));
                                blocklocation.add(LocationUtil.getLocation(location, -1, 0, -1));
                                blocklocation.add(LocationUtil.getLocation(location, -1, 0, 0));
                                blocklocation.add(LocationUtil.getLocation(location, 0, 0, -1));
                            }
                            for (Location loc : blocklocation) {
                                Block block = loc.getBlock();
                                if (BridgeEgg.canPlace(block, player, game, loc) && i <= Config.bridge_egg_max_blocks) {
                                    i++;
                                    loc.getBlock().setType(Material.SANDSTONE);
                                    game.getRegion().addPlacedBlock(loc.getBlock(), null);
                                    if (BridgeEgg.sound != null) {
                                        player.playSound(loc, BridgeEgg.sound, 1.0f, 1.0f);
                                    }
                                }
                            }
                        }
                    }.runTaskLater(BedwarsItem.getInstance(), 5L);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 0L);
    }
}
