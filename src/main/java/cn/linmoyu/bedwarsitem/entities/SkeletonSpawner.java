package cn.linmoyu.bedwarsitem.entities;

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
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SkeletonSpawner implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (!Config.skeleton_spawner_enabled) return;
        Player spawner = event.getPlayer();
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
        Location spawnLocation = EntityUtils.getSpawnLocation(event.getClickedBlock().getLocation(), event.getBlockFace());
        spawnSkeleton(game, spawnLocation, spawner);
    }

    private void spawnSkeleton(Game game, Location location, Player spawner) {
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.getEquipment().setItemInHandDropChance(0.0f); // 不掉落

        // 设置自定义属性
        skeleton.setCustomName("§a§l[" + spawner.getDisplayName() + "§a§l] §b§l的宠物");
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setFireTicks(0);

        EntityManager.addPet(skeleton, game, spawner);
    }

}
