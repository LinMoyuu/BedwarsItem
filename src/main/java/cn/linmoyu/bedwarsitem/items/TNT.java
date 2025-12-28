package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.Utils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class TNT implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!Config.tnt_enable) return;
        Player player = e.getPlayer();
        if (e.getBlock().getType() != Material.TNT) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        if (game.isSpectator(player)) {
            return;
        }

        if (!Utils.isCanPlace(game, e.getBlock().getLocation())) return;

        e.getBlock().setType(Material.AIR);
        TNTPrimed tnt = e.getBlock().getLocation().getWorld().spawn(e.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
        tnt.setYield(Config.tnt_yield);
        tnt.setIsIncendiary(false);
        tnt.setFuseTicks(Config.tnt_fuse_ticks);
        tnt.setMetadata("LightTNT", new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + "." + player.getName()));

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
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isOverSet()) {
            return;
        }
        if (game.isSpectator(player) || !game.getPlayers().contains(player)) {
            return;
        }
        e.setDamage(Config.tnt_damage);
    }
}