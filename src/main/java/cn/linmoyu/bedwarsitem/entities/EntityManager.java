package cn.linmoyu.bedwarsitem.entities;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import io.github.bedwarsrel.game.Game;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class EntityManager implements Listener {

    public static final List<Entity> monsterList = new ArrayList<>();
    private static final Map<Entity, PetData> pets = new HashMap<>();
    private static BukkitTask teleportPetsTask;
    private static BukkitTask targetPlayerTask;

    /**
     * 检查一个实体是否为当前管理的宠物。
     *
     * @param entity 要检查的实体
     * @return 如果是宠物则返回 true
     */
    public static boolean isPet(Entity entity) {
        return pets.containsKey(entity);
    }

    /**
     * 获取宠物的拥有者。
     *
     * @param pet 宠物实体
     * @return 拥有者 Player，如果不是宠物则返回 null
     */
    public static Player getSpawner(Entity pet) {
        PetData data = pets.get(pet);
        return (data != null) ? data.getSpawner() : null;
    }

    /**
     * 获取宠物所在的游戏实例。
     *
     * @param pet 宠物实体
     * @return 游戏 Game，如果不是宠物或没有游戏信息则返回 null
     */
    public static Game getGame(Entity pet) {
        PetData data = pets.get(pet);
        return (data != null) ? data.getGame() : null;
    }

    public static void addPet(Entity petEntity, Game game, Player spawner) {
        if (pets.containsKey(petEntity)) return;

        pets.put(petEntity, new PetData(spawner, game));
        startTasks();

        if (petEntity instanceof Skeleton) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player owner = getSpawner(petEntity);
                    if (isPetInvalid(petEntity, owner)) {
                        this.cancel();
                        return;
                    }
                    petEntity.setFireTicks(0);
                }
            }.runTaskTimer(BedwarsItem.getInstance(), 0L, 0L);
        }
    }

    public static void removeEntity(Entity entity) {
        pets.remove(entity);
        monsterList.remove(entity);

        if (pets.isEmpty()) {
            stopTasks();
        }
    }

    public static void addMonster(Entity monsterEntity, Game game) {
        if (!monsterList.contains(monsterEntity)) {
            monsterList.add(monsterEntity);
        }
    }

    public static void startTasks() {
        if (teleportPetsTask == null) {
            teleportPetsTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Iterator<Map.Entry<Entity, PetData>> iterator = pets.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Entity, PetData> entry = iterator.next();
                        Entity pet = entry.getKey();
                        Player owner = entry.getValue().getSpawner();

                        if (isPetInvalid(pet, owner)) {
                            iterator.remove();
                            pet.remove();
                            continue;
                        }

                        if (BedwarsUtil.isRespawning(owner)) continue;
                        if (pet instanceof Wolf && ((Wolf) pet).isSitting()) continue;

                        if (pet.getLocation().distanceSquared(owner.getLocation()) > Config.max_distance_teleport * Config.max_distance_teleport) {
                            pet.teleport(owner);
                        }
                    }
                    if (pets.isEmpty()) stopTasks();
                }
            }.runTaskTimer(BedwarsItem.getInstance(), 0L, Config.teleport_owner_interval);
        }

        if (targetPlayerTask == null) {
            targetPlayerTask = new BukkitRunnable() {
                @Override
                public void run() {
                    pets.forEach((pet, data) -> {
                        Player owner = data.getSpawner();
                        if (isPetInvalid(pet, owner)) {
                            return;
                        }
                        if (pet instanceof Creature) {
                            Player target = EntityUtils.findNearestEnemy(pet, owner);
                            ((Creature) pet).setTarget(target);
                        }
                    });
                }
            }.runTaskTimer(BedwarsItem.getInstance(), 0L, Config.target_enemy_interval);
        }
    }

    private static boolean isPetInvalid(Entity pet, Player owner) {
        return owner == null || !owner.isOnline() || pet == null || pet.isDead() || !pet.isValid();
    }

    public static void stopTasks() {
        if (teleportPetsTask != null) {
            teleportPetsTask.cancel();
            teleportPetsTask = null;
        }
        if (targetPlayerTask != null) {
            targetPlayerTask.cancel();
            targetPlayerTask = null;
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        removeEntity(event.getEntity());
    }

    /**
     * 内部数据类，封装宠物信息。保持 private 以实现良好封装。
     */
    @Getter
    private static class PetData {
        private final Player spawner;
        private final Game game;

        PetData(Player spawner, Game game) {
            this.spawner = spawner;
            this.game = game;
        }
    }
}
