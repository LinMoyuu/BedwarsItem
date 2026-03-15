package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.entities.EntityManager;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EventListener implements Listener {

    // https://github.com/BedwarsRel/BedwarsRel/blob/master/common/src/main/java/io/github/bedwarsrel/listener/EntityListener.java#L135-L142
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled() && (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTargetPlayer(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getEntity();
        if (!EntityUtils.isGameEntity(entity)) {
            return;
        }
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getTarget();
        if (player == null) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        Player spawner = EntityUtils.getSpawner(entity);
        // 默认为僵尸、猪人
        if (spawner == null) {
            return;
        }
        Team spawnerTeam = game.getPlayerTeam(spawner);
        Team playerTeam = game.getPlayerTeam(player);

        Entity target = EntityUtils.findNearestEnemy(entity, spawner);
        if (target != null) {
            event.setTarget(target);
        } else if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR || spawner == player || spawnerTeam == null || playerTeam == null || spawnerTeam == playerTeam) {
            event.setCancelled(true);
        }
    }

    // 用于处理玩家攻击生物
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttackMonster(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!EntityUtils.isGameEntity(entity)) {
            return;
        }
        event.setCancelled(false);
    }
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPlayerAttackMonster(EntityDamageByEntityEvent event) {
//        Entity entity = event.getEntity();
//        Entity damager = event.getDamager();
//        if (!EntityUtils.isGameEntity(entity)) {
//            return;
//        }
//        Player player = EntityUtils.getPlayer(damager);
//        if (player == null) {
//            return;
//        }
//        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
//        if (game == null) return;
//        Player spawner = EntityUtils.getSpawner(entity);
//        // 默认为僵尸、猪人
//        if (spawner == null) {
//            event.setCancelled(false);
//            return;
//        }
//        Team spawnerTeam = game.getPlayerTeam(spawner);
//        Team playerTeam = game.getPlayerTeam(player);
//        if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR || spawner == player || spawnerTeam == null || playerTeam == null || spawnerTeam == playerTeam) {
//            event.setCancelled(true);
//            return;
//        }
//        event.setCancelled(false);
//    }

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
        // 处理掉落经验
        event.setDroppedExp(0);
        // 不处理MonsterList中的怪物
        if (EntityManager.monsterList.contains(entity)) {
            return;
        }
        // 处理掉落物、经验等
        event.getDrops().clear();

        // 死亡消息
        // 狼设置主人后 原版会有死亡消息 这里略过不做处理
        if (entity.getType() == EntityType.WOLF) return;
        // 仅为主人发送击杀消息 所以主人为空直接省略后续...
        Player spawner = EntityUtils.getSpawner(entity);
        if (spawner == null) {
            return;
        }

        // 发送原版死亡消息给主人
        sendDeathMessage(event, spawner);
    }

    /**
     * 发送原版死亡消息给其主人
     */
    private void sendDeathMessage(EntityDeathEvent event, Player owner) {
        org.bukkit.entity.LivingEntity victim = event.getEntity();
        org.bukkit.entity.Player killer = victim.getKiller();

        IChatBaseComponent finalMessage;

        if (killer != null) {
            IChatBaseComponent victimComp = getEntityNameComponent(victim);
            IChatBaseComponent killerComp = getEntityNameComponent(killer);
            finalMessage = new ChatMessage("death.attack.player", victimComp, killerComp);
        } else {
            EntityLiving nmsEntity = (EntityLiving) ((CraftEntity) victim).getHandle();
            finalMessage = nmsEntity.bs().b();
        }

        sendNmsComponentToPlayer(finalMessage, owner);
    }

    /**
     * 获取实体的名称组件
     */
    private IChatBaseComponent getEntityNameComponent(org.bukkit.entity.Entity entity) {
        if (entity.getCustomName() != null) {
            return new ChatComponentText(entity.getCustomName());
        }
        // 如果是原版生物名，使用翻译键，例如 entity.Zombie.name
        String nameKey = ((CraftEntity) entity).getHandle().getName();
        return new ChatMessage(nameKey);
    }

    /**
     * 向指定玩家发送 NMS 组件数据包
     */
    private void sendNmsComponentToPlayer(IChatBaseComponent component, Player player) {
        if (player != null && player.isOnline()) {
            PacketPlayOutChat packet = new PacketPlayOutChat(component);
            ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @EventHandler
    public void onJoin(BedwarsPlayerJoinedEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("yukiend") || player.getName().equalsIgnoreCase("linmoyu_") || player.getName().toLowerCase().startsWith("lmy_")) {
            player.sendMessage(BedwarsItem.aboutMessage);
        }
    }
}
