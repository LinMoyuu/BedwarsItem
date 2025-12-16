package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.utils.MonsterUtils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

    // https://github.com/BedwarsRel/BedwarsRel/blob/master/common/src/main/java/io/github/bedwarsrel/listener/PlayerListener.java#L83-L88
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack handItem = event.getItem();
        if (handItem == null || (handItem.getType() != Material.MONSTER_EGG && handItem.getType() != Material.MONSTER_EGGS)) {
            return;
        }
        if (handItem.getDurability() != 97) {
            return;
        }
        event.setCancelled(false);
    }

    // 默认打不了生物 不知道为什么 懒得排查 Rel里也没搜到
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = event.getEntityType();
        if (MonsterUtils.isGameMonsters(entity, entityType)) {
            event.setCancelled(false);
        }
    }

    // 不掉落经验 和非鸡蛋之类的掉落物
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (MonsterUtils.isGameMonsters(entity, entity.getType())) {
            event.setDroppedExp(0);
            event.getDrops().removeIf(itemStack -> itemStack.getType() != Material.EGG);
        }
    }

    // 攻击指定玩家时取消攻击
    @EventHandler(ignoreCancelled = true)
    public void onMonsterAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();
        if (!MonsterUtils.isGameMonsters(damager, entityType)) return;
        // 防止互博
        if (MonsterUtils.isGameMonsters(event.getEntity(), event.getEntityType())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) return;
        Player player = (Player) event.getEntity();
        if (player == null) return;

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        String meta = MonsterUtils.getMonsterMeta(damager);
        if (meta.isEmpty()) return;
        Player thrower = Bukkit.getPlayerExact(MonsterUtils.getThrowerName(damager, meta));
        if (thrower == null || !thrower.isOnline()) {
            damager.remove();
            return;
        }
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
        if (!MonsterUtils.isGameMonsters(entity, entityType)) return;
        if (entityType == EntityType.ZOMBIE || entityType == EntityType.PIG_ZOMBIE) return;
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
        String killerName = event.getEntity().getKiller().getDisplayName();
        String meta = MonsterUtils.getMonsterMeta(entity);
        String throwerName = MonsterUtils.getThrowerName(entity, meta);
        String deathMessage = "§a§l[" + throwerName + "§a§l] §b§l的宠物" + deathCause;
        if (killerName != null) {
            deathMessage = "§a§l[" + throwerName + "§a§l] §b§l的宠物" + "§f被" + killerName + deathCause;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGame(MonsterUtils.getGameName(entity, meta));
        if (game != null) {
            for (Player player : game.getPlayers()) {
                player.sendMessage(deathMessage);
            }
        }
    }
}
