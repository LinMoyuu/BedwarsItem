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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SpiderSpawner implements Listener {

    public static String meta = "BwSpider";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player thrower = event.getPlayer();
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        if (handItem.getDurability() != 52) {
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
        spawnSpider(MonsterUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace()), thrower);
    }

    private void spawnSpider(Location location, Player thrower) {
        Spider spider = (Spider) location.getWorld().spawnEntity(location, EntityType.SPIDER);
        spider.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), thrower.getName()));

        // 设置自定义属性
        spider.setCustomName("§a§l[" + thrower.getDisplayName() + "§a§l] §b§l的宠物");
        spider.setRemoveWhenFarAway(false);

        new BukkitRunnable() {
            @Override
            public void run() {
                Player thrower = Bukkit.getPlayerExact(spider.getMetadata(meta).get(0).asString());
                if (spider.isDead() || !spider.isValid() || thrower == null || !thrower.isOnline()) {
                    spider.remove();
                    this.cancel();
                    return;
                }

                double distance = spider.getLocation().distance(thrower.getLocation());
                if (distance > 20) {
                    spider.teleport(thrower);
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 200L);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (spider.isDead() || !spider.isValid()) {
                    this.cancel();
                    return;
                }

                Player target = MonsterUtils.findNearestEnemy(spider, thrower);
                // 设置目标
                spider.setTarget(target);
//                if (target == null) {
//                    NoAIUtils.setAI(spider, false);
//                } else {
//                    spider.setTarget(target);
//                    NoAIUtils.setAI(spider, true);
//                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 20L);
    }

}
