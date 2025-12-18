package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.utils.MonsterUtils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

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

    // 默认打不了生物
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (!MonsterUtils.isGameMonsters(entity)) return;
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            ProjectileSource shooter = arrow.getShooter();

            if (shooter instanceof Skeleton) {
                return;
            }
        }

        if (damager instanceof Player
                || damager instanceof Egg
                || damager instanceof EntityFishingHook
                || damager instanceof Fireball
                || damager instanceof TNTPrimed
                || damager instanceof Arrow) {
            event.setCancelled(false);
        }
    }

    // 不掉落经验 和指定物品之外的掉落物
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (MonsterUtils.isGameMonsters(entity)) {
            event.setDroppedExp(0);
            event.getDrops().removeIf(itemStack ->
                    itemStack.getType() != Material.EGG &&
                            itemStack.getType() != Material.OBSIDIAN
            );
        }
    }

    // 怪物攻击指定玩家时取消攻击
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMonsterAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();
        if (!MonsterUtils.isGameMonsters(damager) || !(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player == null) return;

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        String monsterMeta = MonsterUtils.getMonsterMeta(entityType);
        Player thrower = MonsterUtils.getThrower(damager, monsterMeta);
        if (thrower == null) return;
        Team throwerTeam = game.getPlayerTeam(thrower);
        Team playerTeam = game.getPlayerTeam(player);
        if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR || thrower == player || throwerTeam == null || playerTeam == null || throwerTeam == playerTeam) {
            event.setCancelled(true);
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
            for (Player player : game.getPlayers()) {
                player.sendMessage(deathMessage);
            }
        }
    }
}
