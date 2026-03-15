package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import cn.linmoyu.bedwarsitem.utils.Utils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class TNT implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Config.tnt_enable) return;
        Player player = e.getPlayer();
        ItemStack handItem = e.getItemInHand();
        if (handItem == null || handItem.getType() != Material.TNT) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        if (game.isSpectator(player)) {
            return;
        }
//        TNTPrimed tnt = e.getBlock().getLocation().getWorld().spawn(e.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
        e.setBuild(true);
        e.setCancelled(false);
        e.getBlock().setType(Material.AIR);
        TNTPrimed tnt;
        if (Config.tnt_offsetfix) {
            tnt = (TNTPrimed) player.getWorld().spawnEntity(e.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        } else {
            tnt = (TNTPrimed) player.getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
        }
        if (tnt == null) return;
        tnt.setYield(Config.tnt_yield);
        tnt.setIsIncendiary(false);
        tnt.setFuseTicks(Config.tnt_fuse_ticks);
        tnt.setMetadata("LightTNT", new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + "." + player.getName()));
        TakeItemUtil.TakeItem(player, handItem);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (!(damager instanceof TNTPrimed) || !damager.hasMetadata("LightTNT")) {
            return;
        }
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        if (player.isDead()) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isOverSet()) {
            return;
        }

        if (game.isSpectator(player) || !game.getPlayers().contains(player)) {
            return;
        }

        Player placedPlayer = Utils.getPlacedPlayer(damager, "LightTNT");
        if (placedPlayer != null) {
            Team playerTeam = game.getPlayerTeam(player);
            Team placedPlayerTeam = game.getPlayerTeam(placedPlayer);
            if (placedPlayerTeam != null && playerTeam == placedPlayerTeam) {
                e.setCancelled(true);
                return;
            } else {
                game.setPlayerDamager(player, placedPlayer);
            }
        }

        double distance = player.getLocation().distance(damager.getLocation());
        Utils.debug(player.getName() + " 距离TNT: " + distance);
        Utils.debug("秒杀启用: " + Config.tnt_killable_enabled + " 设定距离: " + Config.tnt_killable_distance);

        if (Config.tnt_killable_enabled && distance <= Config.tnt_killable_distance) {
            e.setCancelled(true);
            player.setHealth(0);
        } else {
            e.setDamage(Config.tnt_damage);
        }
    }

}