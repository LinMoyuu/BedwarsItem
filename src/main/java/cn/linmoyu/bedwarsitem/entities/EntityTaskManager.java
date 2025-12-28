package cn.linmoyu.bedwarsitem.entities;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.utils.EntityUtils;
import me.ram.bedwarsscoreboardaddon.utils.BedwarsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class EntityTaskManager implements Listener {

    public static final List<Entity> monsterList = new ArrayList<>();
    public static final List<Entity> petsList = new ArrayList<>();
    private static final long TELEPORT_TASK_INTERVAL = 200L;
    private static final long TARGET_TASK_INTERVAL = 20L;
    private static final double MAX_DISTANCE_BEFORE_TELEPORT = 20.0;
    private static BukkitTask teleportPetsTask;
    private static BukkitTask targetPlayerTask;

    public static void addPet(Entity petEntity) {
        if (petsList.contains(petEntity)) return;
        petsList.add(petEntity);
        startTasks();

        // 骷髅不着火 还是独立拉出来...
        if (petEntity instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) petEntity;
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player spawner = EntityUtils.getSpawner(skeleton);
                    if (skeleton.isDead() || !skeleton.isValid() || spawner == null || !spawner.isOnline()) {
                        this.cancel();
                        return;
                    }

                    skeleton.setFireTicks(0);
                }
            }.runTaskTimer(BedwarsItem.getInstance(), 0L, 0L);
        }
    }

    public static void addMonster(Entity monsterEntity) {
        if (!monsterList.contains(monsterEntity)) {
            monsterList.add(monsterEntity);
        }
    }

    /**
     * 从列表中移除实体。
     * 当实体死亡或失效时调用。
     */
    public static void removeEntity(Entity entity) {
        petsList.remove(entity);
        monsterList.remove(entity);

        if (petsList.isEmpty()) {
            stopTasks();
        }
    }

    /**
     * 启动宠物管理任务（如果尚未运行）。
     */
    public static void startTasks() {
        // 传送任务
        if (teleportPetsTask == null) {
            teleportPetsTask = Bukkit.getScheduler().runTaskTimer(BedwarsItem.getInstance(), () -> {
                Iterator<Entity> iterator = petsList.iterator();
                while (iterator.hasNext()) {
                    Entity pet = iterator.next();
                    Player owner = EntityUtils.getSpawner(pet);
                    if (isPetInvalid(pet, owner)) {
                        iterator.remove();
                        pet.remove();
                        continue;
                    }
                    if (BedwarsUtil.isRespawning(owner)) {
                        continue;
                    }
                    // 如果是狼且处于坐下状态，则不进行传送
                    if (pet instanceof Wolf) {
                        Wolf wolf = (Wolf) pet;
                        if (wolf.isSitting()) {
                            continue;
                        }
                    }
                    if (pet.getLocation().distanceSquared(owner.getLocation()) > MAX_DISTANCE_BEFORE_TELEPORT * MAX_DISTANCE_BEFORE_TELEPORT) {
                        pet.teleport(owner);
                    }
                }
                if (petsList.isEmpty()) {
                    stopTasks();
                }
            }, 0L, TELEPORT_TASK_INTERVAL);
        }
        // 设置Target任务
        if (targetPlayerTask == null) {
            targetPlayerTask = Bukkit.getScheduler().runTaskTimer(BedwarsItem.getInstance(), () -> {
                Iterator<Entity> iterator = petsList.iterator();
                while (iterator.hasNext()) {
                    Entity pet = iterator.next();
                    Player owner = EntityUtils.getSpawner(pet);

                    if (isPetInvalid(pet, owner)) {
                        iterator.remove();
                        pet.remove();
                        continue;
                    }

                    if (pet instanceof Creature) {
                        Player target = EntityUtils.findNearestEnemy(pet, owner);
                        ((Creature) pet).setTarget(target);
                    }
                }
                if (petsList.isEmpty()) {
                    stopTasks();
                }
            }, 0L, TARGET_TASK_INTERVAL);
        }
    }

    /**
     * 停止所有正在运行的任务。
     */
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

    /**
     * 检查宠物及其主人是否仍然有效。
     *
     * @param pet   宠物实体
     * @param owner 宠物的主人
     * @return 如果宠物或主人无效，则返回 true
     */
    private static boolean isPetInvalid(Entity pet, Player owner) {
        return pet == null || owner == null || pet.isDead() || !pet.isValid() || !owner.isOnline();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        removeEntity(event.getEntity());
    }
}
