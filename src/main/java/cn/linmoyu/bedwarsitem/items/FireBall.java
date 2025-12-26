package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

// 没能仿明白
public class FireBall implements Listener {

    @EventHandler
    public void onInteractFireball(PlayerInteractEvent e) {
        if (!Config.fireball_enabled) {
            return;
        }
        ItemStack handItem = e.getItem();
        if (handItem == null || handItem.getType() != Material.FIREBALL) {
            return;
        }
        e.setCancelled(true);
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = e.getPlayer();
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null || game.getState() != GameState.RUNNING || game.isOverSet()) {
            return;
        }
        if (game.isSpectator(player)) {
            return;
        }

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setBounce(false);
        fireball.setShooter(player);
        fireball.setYield(Config.fireball_yield);
        fireball.setVelocity(fireball.getDirection().multiply(Config.fireball_velocity));
        fireball.setMetadata("FireBall", new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + "." + player.getName()));
        TakeItemUtil.TakeItem(player, handItem);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent e) {
        Entity entity = e.getIgnitingEntity();
        if (entity instanceof Fireball && entity.hasMetadata("FireBall")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireballDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (!damager.hasMetadata("FireBall")) {
            return;
        }
        if (!(entity instanceof Player && damager instanceof Fireball)) {
            return;
        }
        Player player = (Player) entity;
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        if (game.getState() != GameState.RUNNING) {
            return;
        }
        e.setDamage(Config.fireball_damage);
    }
}