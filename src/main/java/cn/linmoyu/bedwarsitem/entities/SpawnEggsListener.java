package cn.linmoyu.bedwarsitem.entities;

import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnEggsListener implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Player spawner = event.getPlayer();
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING) {
            return;
        }
        if (game.isSpectator(player) || !game.getPlayers().contains(player)) {
            return;
        }
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        switch (handItem.getDurability()) {
            case 97:    // 蠹虫
                if (!Config.silverfish_spawner_enabled) return;
                spawnSilverfish(game, spawnLocation, spawner);
                break;
            case 51:   // 骷髅
                if (!Config.skeleton_spawner_enabled) return;
                spawnSkeleton(game, spawnLocation, spawner);
                break;
            case 52:   // 蜘蛛
                if (!Config.spider_spawner_enabled) return;
                spawnSpider(game, spawnLocation, spawner);
                break;
            case 95:   // 狼
                if (!Config.wolf_spawner_enabled) return;
                spawnWolf(game, spawnLocation, spawner);
                break;
            default:
                return;
        }
        event.setCancelled(true);
        TakeItemUtil.TakeItem(player, handItem);
    }

    private void spawnSilverfish(Game game, Location location, Player spawner) {
        Silverfish silverfish = (Silverfish) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        EntityManager.addPet(silverfish, game, spawner);
    }

    private void spawnSkeleton(Game game, Location location, Player spawner) {
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        // 设置自定义属性
        skeleton.setFireTicks(0);
        EntityManager.addPet(skeleton, game, spawner);
    }

    private void spawnSpider(Game game, Location location, Player spawner) {
        Spider spider = (Spider) location.getWorld().spawnEntity(location, EntityType.SPIDER);
        EntityManager.addPet(spider, game, spawner);
    }

    private void spawnWolf(Game game, Location location, Player spawner) {
        Wolf wolf = (Wolf) location.getWorld().spawnEntity(location, EntityType.WOLF);
        // 设置自定义属性
        wolf.setOwner(spawner);
        wolf.setTamed(true);
        EntityManager.addPet(wolf, game, spawner);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSilverfishChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SILVERFISH && EntityUtils.isGameEntity(entity)) {
            event.setCancelled(true);
        }
    }
}
