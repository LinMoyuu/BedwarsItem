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

public class SilverFishSpawner implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (!Config.silverfish_spawner_enabled) return;
        Player spawner = event.getPlayer();
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
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        spawnSilverfish(game, spawnLocation, spawner);
    }

    private void spawnSilverfish(Game game, Location location, Player spawner) {
        String meta = Entities.PETS_SILVERFISH.getMeta();
        Silverfish silverfish = (Silverfish) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        silverfish.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawner.getName()));

        // 设置自定义属性
        silverfish.setCustomName("§a§l[" + spawner.getDisplayName() + "§a§l] §b§l的宠物");
//        silverfish.setCustomNameVisible(true);
        silverfish.setRemoveWhenFarAway(false);

        EntityTaskManager.addPet(silverfish);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSilverfishChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SILVERFISH && entity.hasMetadata(Entities.PETS_SILVERFISH.getMeta())) {
            event.setCancelled(true);
        }
    }
}
