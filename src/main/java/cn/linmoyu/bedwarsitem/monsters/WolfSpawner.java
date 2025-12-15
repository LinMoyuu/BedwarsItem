package cn.linmoyu.bedwarsitem.monsters;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.utils.MonsterUtils;
import cn.linmoyu.bedwarsitem.utils.NoAIUtils;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class WolfSpawner implements Listener {

    public static String meta = "BwWolf";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player thrower = event.getPlayer();
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        if (handItem.getDurability() != 95) {
            return;
        }
        event.setCancelled(true);
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isOverSet()) {
            return;
        }
        if (game.isSpectator(player) || !game.getPlayers().contains(player)) {
            return;
        }

        TakeItemUtil.TakeItem(player, handItem);
        spawnWolf(MonsterUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace()), thrower);
    }

    private void spawnWolf(Location location, Player thrower) {
        Wolf wolf = (Wolf) location.getWorld().spawnEntity(location, EntityType.WOLF);
        wolf.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), thrower.getName()));

        // 设置自定义属性
        wolf.setCustomName("§a§l[" + thrower.getDisplayName() + "§a§l] §b§l的宠物");
        wolf.setRemoveWhenFarAway(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(wolf.getMetadata(meta).get(0).asString());
                if (wolf.isDead() || !wolf.isValid() || thrower == null || !thrower.isOnline()) {
                    this.cancel();
                    return;
                }

                double distance = wolf.getLocation().distance(thrower.getLocation());
                if (distance > 20) {
                    wolf.teleport(thrower);
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 200L);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (wolf.isDead() || !wolf.isValid()) {
                    this.cancel();
                    return;
                }

                Player target = MonsterUtils.findNearestEnemy(wolf, thrower);
                // 设置目标
                if (target == null) {
                    NoAIUtils.setAI(wolf, false);
                } else {
                    wolf.setTarget(target);
                    NoAIUtils.setAI(wolf, true);
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 20L);
    }

}
