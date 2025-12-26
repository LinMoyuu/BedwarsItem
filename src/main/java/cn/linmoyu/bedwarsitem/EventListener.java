package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.utils.MonsterUtils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import io.github.bedwarsrel.game.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
//        Entity entity = event.getEntity();
//        EntityType entityType = event.getEntityType();
//        if (event.isCancelled() && MonsterUtils.isGameMonsters(entity, entityType)) {
//            event.setCancelled(false);
//        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        EntityType victimType = victim.getType();
        if (MonsterUtils.isGameMonsters(victim) &&
                (victimType == EntityType.ZOMBIE || victimType == EntityType.PIG_ZOMBIE)) {
            event.setCancelled(false);
            return;
        }

        Player attackerOwner = MonsterUtils.getPlayer(damager);
        Player victimOwner = MonsterUtils.getPlayer(victim);

        if (attackerOwner == null || victimOwner == null) {
            return;
        } else if (victim.getType() == EntityType.ZOMBIE || victim.getType() == EntityType.PIG_ZOMBIE) {
            event.setCancelled(false);
            return;
        }

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(attackerOwner);
        if (game == null) {
            game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(victimOwner);
            if (game == null) {
                return;
            }
        }
        if (game.getState() != GameState.RUNNING) return;

        if (attackerOwner.equals(victimOwner)) {
            event.setCancelled(true);
            return;
        }

        if ((victim instanceof Player && (game.isSpectator((Player) victim) || ((Player) victim).getGameMode() == GameMode.SPECTATOR)) ||
                (game.isSpectator(attackerOwner) || attackerOwner.getGameMode() == GameMode.SPECTATOR)) {
            event.setCancelled(true);
            return;
        }

        Team attackerTeam = game.getPlayerTeam(attackerOwner);
        Team victimTeam = game.getPlayerTeam(victimOwner);

        event.setCancelled(attackerTeam != null && attackerTeam.equals(victimTeam));
    }

    // 不掉落经验 和指定物品之外的掉落物
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (MonsterUtils.isGameMonsters(entity)) {
            event.setDroppedExp(0);
        }
    }

    // 怪物死亡消息 随便生草一个类似的就行了 原版死亡消息被设置为空了
    @EventHandler(ignoreCancelled = true)
    public void onMonsterDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();
        if (!MonsterUtils.isGameMonsters(entity)) return;
        String monsterMeta = MonsterUtils.getMonsterMeta(entityType);
        if (monsterMeta.isEmpty()) return;
        // 获取死亡消息
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
        Player thrower = MonsterUtils.getThrower(entity, monsterMeta);
        String deathMessage = entity.getCustomName() + "§f" + deathCause;
        if (killer != null) {
            deathMessage = entity.getCustomName() + "§f" + killer.getDisplayName() + deathCause;
        }
        if (thrower == null) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGame(MonsterUtils.getGameName(entity, monsterMeta));
        if (game != null) {
            thrower.sendMessage(deathMessage);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("yukiend") || player.getName().equalsIgnoreCase("linmoyu_") || player.getName().toLowerCase().startsWith("lmy_")) {
            player.sendMessage(BedwarsItem.getInstance().getAboutMessage());
        }
    }
}
