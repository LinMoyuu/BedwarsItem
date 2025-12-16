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
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SkeletonSpawner implements Listener {

    public static String meta = "BwSkeleton";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player thrower = event.getPlayer();
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        if (handItem.getDurability() != 51) {
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
        Location spawnLocation = MonsterUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        spawnSkeleton(game, spawnLocation, thrower);
    }

    private void spawnSkeleton(Game game, Location location, Player thrower) {
        // 生成蠹虫
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + thrower.getName()));

        // 设置自定义属性
        skeleton.setCustomName("§a§l[" + thrower.getDisplayName() + "§a§l] §b§l的宠物");
        skeleton.setRemoveWhenFarAway(false);

        // 在10秒后没跟过来自动传送
        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(skeleton.getMetadata(meta).get(0).asString());
                if (skeleton.isDead() || !skeleton.isValid() || thrower == null || !thrower.isOnline()) {
                    this.cancel();
                    return;
                }

                double distance = skeleton.getLocation().distance(thrower.getLocation());
                if (distance > 20) {
                    skeleton.teleport(thrower);
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 200L);
        // 指定玩家
        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(skeleton.getMetadata(meta).get(0).asString());
                if (skeleton.isDead() || !skeleton.isValid() || thrower == null || !thrower.isOnline()) {
                    this.cancel();
                    return;
                }

                Player target = MonsterUtils.findNearestEnemy(skeleton, thrower);
                // 设置蠹虫目标
                skeleton.setTarget(target);
//                if (target == null) {
//                    NoAIUtils.setAI(skeleton, false);
//                } else {
//                    skeleton.setTarget(target);
//                    NoAIUtils.setAI(skeleton, true);
//                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 20L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSkeletonChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SKELETON && entity.hasMetadata(SkeletonSpawner.meta)) {
            event.setCancelled(true);
        }
    }

}
