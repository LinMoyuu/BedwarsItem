package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.monsters.SilverFishSpawner;
import cn.linmoyu.bedwarsitem.monsters.SkeletonSpawner;
import cn.linmoyu.bedwarsitem.monsters.SpiderSpawner;
import cn.linmoyu.bedwarsitem.monsters.WolfSpawner;
import cn.linmoyu.bedwarsitem.monsters.pig_zombie.PigZombieSpawnerTask;
import cn.linmoyu.bedwarsitem.monsters.zombie.ZombieSpawnerTask;
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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
//        if (event.isCancelled() && isGameMonsters(entity, entityType)) {
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
        if (isGameMonsters(entity, entityType)) {
            event.setCancelled(false);
        }
    }

    // 不掉落经验 和非鸡蛋之类的掉落物
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (isGameMonsters(entity, entity.getType())) {
            event.setDroppedExp(0);
            event.getDrops().removeIf(itemStack -> itemStack.getType() != Material.EGG);
        }
    }

    public boolean isGameMonsters(Entity entity, EntityType entityType) {
        if (entityType == EntityType.ZOMBIE && entity.hasMetadata(ZombieSpawnerTask.meta)) {
            return true;
        }
        if (entityType == EntityType.PIG_ZOMBIE && entity.hasMetadata(PigZombieSpawnerTask.meta)) {
            return true;
        }
        if (entityType == EntityType.SILVERFISH && entity.hasMetadata(SilverFishSpawner.meta)) {
            return true;
        }
        if (entityType == EntityType.SPIDER && entity.hasMetadata(SpiderSpawner.meta)) {
            return true;
        }
        if (entityType == EntityType.WOLF && entity.hasMetadata(WolfSpawner.meta)) {
            return true;
        }
        return entityType == EntityType.SKELETON && entity.hasMetadata(SkeletonSpawner.meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMonsterAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        EntityType entityType = damager.getType();
        if (!isGameMonsters(damager, entityType)) return;
        // 防止互博
        if (isGameMonsters(event.getEntity(),event.getEntityType())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) return;
        Player player = (Player) event.getEntity();
        if (player == null) return;

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) return;
        String meta = "";
        switch (damager.getType()) {
            case ZOMBIE:
                meta = ZombieSpawnerTask.meta;
                break;
            case PIG_ZOMBIE:
                meta = PigZombieSpawnerTask.meta;
                break;
            case SILVERFISH:
                meta = SilverFishSpawner.meta;
                break;
            case SKELETON:
                meta = SkeletonSpawner.meta;
                break;
            case SPIDER:
                meta = SpiderSpawner.meta;
                break;
            case WOLF:
                meta = WolfSpawner.meta;
                break;
            default:
                break;
        }
        if (meta.isEmpty()) return;
        Player thrower = Bukkit.getPlayerExact(damager.getMetadata(meta).get(0).asString());
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

    @EventHandler(ignoreCancelled = true)
    public void onMonsterDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        EntityType entityType = entity.getType();
        if (!isGameMonsters(entity, entityType)) return;
        EntityDamageEvent damageEvent = entity.getLastDamageCause();
        EntityDamageEvent.DamageCause cause = null;
        if (damageEvent != null) {
            cause = damageEvent.getCause();
        }

        switch (event.getEntity().getLastDamageCause()) {
            case cause
        }

    }



}
