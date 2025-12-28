package cn.linmoyu.bedwarsitem.entities;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
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

public class SpiderSpawner implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (!Config.spider_spawner_enabled) return;
        Player spawner = event.getPlayer();
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
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        spawnSpider(game, spawnLocation, spawner);
    }

    private void spawnSpider(Game game, Location location, Player spawner) {
        String meta = Entities.PETS_SPIDER.getMeta();
        Spider spider = (Spider) location.getWorld().spawnEntity(location, EntityType.SPIDER);
        spider.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawner.getName()));

        // 设置自定义属性
        spider.setCustomName("§a§l[" + spawner.getDisplayName() + "§a§l] §b§l的宠物");
        spider.setRemoveWhenFarAway(false);

        EntityTaskManager.addPet(spider);
    }

}
