package cn.linmoyu.bedwarsitem.entities;

import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import cn.linmoyu.bedwarsitem.utils.Utils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
        if (game == null || game.getState() != GameState.RUNNING || game.isSpectator(player) || !game.getPlayers().contains(player)) {
            return;
        }
        EntityType entityType;
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        switch (handItem.getDurability()) {
            case 97:    // 蠹虫
                if (!Config.silverfish_spawner_enabled) return;
                entityType = EntityType.SILVERFISH;
                break;
            case 51:   // 骷髅
                if (!Config.skeleton_spawner_enabled) return;
                entityType = EntityType.SKELETON;
                break;
            case 52:   // 蜘蛛
                if (!Config.spider_spawner_enabled) return;
                entityType = EntityType.SPIDER;
                break;
            case 95:   // 狼
                if (!Config.wolf_spawner_enabled) return;
                entityType = EntityType.WOLF;
                break;
            default:
                return;
        }
        event.setCancelled(true);
        TakeItemUtil.TakeItem(player, handItem);
        Utils.debug(player.getName() + " 使用了刷怪蛋 " + handItem.getDurability() + " " + entityType);

        Entity entity = spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
        EntityManager.addPet(entity, game, spawner);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSilverfishChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SILVERFISH && EntityUtils.isGameEntity(entity)) {
            event.setCancelled(true);
        }
    }
}
