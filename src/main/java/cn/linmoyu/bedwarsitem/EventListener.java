package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.monsters.MonsterMeta;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EventListener implements Listener {

    // 为什么有这玩意 详见
    // https://github.com/BedwarsRel/BedwarsRel/blob/master/common/src/main/java/io/github/bedwarsrel/listener/EntityListener.java#L138
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled() && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(false);
        }
    }

    // 默认打不动 不知道为什么 懒得排查 Rel里也没搜到
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
        if (entityType == EntityType.ZOMBIE && entity.hasMetadata(MonsterMeta.BWZOMBIE.name())) {
            return true;
        }
        return entityType == EntityType.PIG_ZOMBIE && entity.hasMetadata(MonsterMeta.BWPIGZOMBIE.name());
    }

}
