package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

    // https://github.com/BedwarsRel/BedwarsRel/blob/master/common/src/main/java/io/github/bedwarsrel/listener/EntityListener.java#L135-L142
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled() && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
            event.setCancelled(false);
        }
    }

    // 用于处理玩家攻击生物
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttackMonster(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (!EntityUtils.isGameEntity(entity)) {
            return;
        }
        Player player = EntityUtils.getPlayer(damager);
        if (player == null) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        Player spawner = EntityUtils.getSpawner(entity);
        // 默认为僵尸、猪人
        if (spawner == null) {
            event.setCancelled(false);
            return;
        }
        Team spawnerTeam = game.getPlayerTeam(spawner);
        Team playerTeam = game.getPlayerTeam(player);
        if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR || spawner == player || spawnerTeam == null || playerTeam == null || spawnerTeam == playerTeam) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
    }

    // 用于生物攻击玩家
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMonsterAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!EntityUtils.isGameEntity(damager)) {
            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                Entity shooter = (Entity) projectile.getShooter();
                if (EntityUtils.isGameEntity(shooter)) {
                    damager = shooter;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player == null) return;

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        Player spawner = EntityUtils.getSpawner(damager);
        if (spawner == null) return;
        Team spawnerTeam = game.getPlayerTeam(spawner);
        Team playerTeam = game.getPlayerTeam(player);
        if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR || spawner == player || spawnerTeam == null || playerTeam == null || spawnerTeam == playerTeam) {
            event.setCancelled(true);
        }
    }

    // 怪物死亡处理
    @EventHandler
    public void onMonsterDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!EntityUtils.isGameEntity(entity)) return;
        // 不处理僵尸、猪人
        if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.PIG_ZOMBIE) {
            return;
        }
        // 处理掉落物、经验等
        event.setDroppedExp(0);
        event.getDrops().clear();

        // 死亡消息
        // 狼设置主人后 原版会有死亡消息 这里略过不做处理
        if (entity.getType() == EntityType.WOLF) return;
        EntityDamageEvent damageEvent = entity.getLastDamageCause();
        EntityDamageEvent.DamageCause cause = null;
        if (damageEvent != null) {
            cause = damageEvent.getCause();
        }
        String deathCause = "死了";
        if (cause != null) {
            switch (cause) {
                case ENTITY_EXPLOSION:
                case BLOCK_EXPLOSION:
                    deathCause = "炸死了";
                    break;
                case ENTITY_ATTACK:
                    deathCause = "杀死了";
                    break;
                case FALL:
                    deathCause = "摔死了";
                    break;
                case FALLING_BLOCK:
                    deathCause = "窒息了";
                    break;
                case PROJECTILE:
                    deathCause = "射死了";
                    break;
                case VOID:
                    deathCause = "掉出了世界";
                    break;
                default:
                    break;
            }
        }
        Player killer = event.getEntity().getKiller();
        Player spawner = EntityUtils.getSpawner(entity);
        String deathMessage = entity.getCustomName() + "§f" + deathCause;
        if (killer != null) {
            deathMessage = entity.getCustomName() + "§f" + killer.getDisplayName() + deathCause;
        }
        if (spawner == null) {
            return;
        }
        Game game = EntityUtils.getMonsterGame(entity);
        if (game != null) {
            spawner.sendMessage(deathMessage);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("yukiend") || player.getName().equalsIgnoreCase("linmoyu_") || player.getName().toLowerCase().startsWith("lmy_")) {
            player.sendMessage(BedwarsItem.aboutMessage);
        }
    }
}
