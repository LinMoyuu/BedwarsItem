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
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class WolfSpawner implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (!Config.wolf_spawner_enabled) return;
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
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        spawnWolf(game, spawnLocation, thrower);
    }

    private void spawnWolf(Game game, Location location, Player thrower) {
        String meta = Entities.PETS_WOLF.getMeta();
        Wolf wolf = (Wolf) location.getWorld().spawnEntity(location, EntityType.WOLF);
        wolf.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + thrower.getName()));

        // 设置自定义属性
        wolf.setCustomName("§a§l[" + thrower.getDisplayName() + "§a§l] §b§l的宠物");
        wolf.setRemoveWhenFarAway(false);
        wolf.setOwner(thrower);
        wolf.setTamed(true);

        EntityTaskManager.addPet(wolf);

    }

}
