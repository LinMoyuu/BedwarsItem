package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.LocationUtil;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import cn.linmoyu.bedwarsitem.utils.Utils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BridgeEgg implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Config.bridge_egg_enabled) return;
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack handItem = e.getItem();
        if (handItem == null || handItem.getType() != Material.EGG) {
            return;
        }
        ItemMeta itemMeta = handItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String displayName = itemMeta.hasDisplayName() ?
                ColorUtil.removeColor(itemMeta.getDisplayName()) : "";
        if (!(Config.bridge_egg_name.equals(displayName) || displayName.isEmpty() && Config.bridge_egg_name.isEmpty())) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || !game.getPlayers().contains(player) || game.isOverSet()) {
            return;
        }
        e.setCancelled(true);

        Egg egg = player.launchProjectile(Egg.class);
        egg.setBounce(false);
        egg.setShooter(player);
        this.setblock(game, egg, player);
        TakeItemUtil.TakeItem(player, handItem);
    }

    public void setblock(Game game, Egg egg, Player player) {
        Sound sound = Utils.getSound("ENTITY_PLAYER_LEVELUP", "DIG_STONE");
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
                            if (!Utils.isCanPlace(game, location)) {
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
                                if (block.getType() == new ItemStack(Material.AIR).getType() && !block.equals(player.getLocation().getBlock()) && !block.equals(player.getLocation().clone().add(0, 1, 0).getBlock()) && game.getRegion().isInRegion(loc) && i < Config.bridge_egg_max_blocks) {
                                    loc.getBlock().setType(Material.SANDSTONE);
                                    i++;
                                    game.getRegion().addPlacedBlock(loc.getBlock(), null);
                                    if (sound != null) player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
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