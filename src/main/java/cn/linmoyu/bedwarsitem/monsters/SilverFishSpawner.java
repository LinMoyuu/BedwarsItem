package cn.linmoyu.bedwarsitem.monsters;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.utils.MonsterUtils;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SilverFishSpawner implements Listener {

    public static String meta = "BwSilverFish";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player thrower = event.getPlayer();
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        if (handItem.getDurability() != 97) {
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
        spawnSilverfish(MonsterUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace()), thrower);
    }

    private void spawnSilverfish(Location location, Player thrower) {
        // 生成蠹虫
        Silverfish silverfish = (Silverfish) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        silverfish.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), thrower.getName()));

        // 设置自定义属性
        silverfish.setCustomName("§a§l[" + thrower.getDisplayName() + "§a§l] §b§l的宠物");
//        silverfish.setCustomNameVisible(true);
        silverfish.setRemoveWhenFarAway(false);

        // 在10秒后没跟过来自动传送
        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(silverfish.getMetadata(meta).get(0).asString());
                if (silverfish.isDead() || !silverfish.isValid() || thrower == null || !thrower.isOnline()) {
                    this.cancel();
                    return;
                }

                double distance = silverfish.getLocation().distance(thrower.getLocation());
                if (distance > 20) {
                    silverfish.teleport(thrower);
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 200L);
        // 指定玩家
        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(silverfish.getMetadata(meta).get(0).asString());
                if (silverfish.isDead() || !silverfish.isValid() || thrower == null || !thrower.isOnline()) {
                    this.cancel();
                    return;
                }

                Player target = MonsterUtils.findNearestEnemy(silverfish, thrower);
                // 设置蠹虫目标
                silverfish.setTarget(target);
//                if (target == null) {
//                    NoAIUtils.setAI(silverfish, false);
//                } else {
//                    silverfish.setTarget(target);
//                    NoAIUtils.setAI(silverfish, true);
//                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSilverfishChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SILVERFISH && entity.hasMetadata(SilverFishSpawner.meta)) {
            event.setCancelled(true);
        }
    }
}
