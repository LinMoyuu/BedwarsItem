package cn.linmoyu.bedwarsitem.monsters.zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSpawnerTask extends BukkitRunnable {

    public static String meta = "BwZombie";

    // 期望僵尸数量
    private static final int DESIRED_ZOMBIE_COUNT = 2;
    // 半径检测范围
    private static final double CHECK_RADIUS = 10;
    // 此任务持续时长
    private static final int TASK_DURATION_SECONDS = 180;
    // 任务执行间隔 (秒)
    private static final int TASK_INTERVAL_SECONDS = 30; // 30秒是瞎给的 没有测过具体数值
    private final Location spawnerLocation;
    // 已执行秒数
    private int elapsedSeconds = 0;

    public ZombieSpawnerTask(Location spawnerLocation) {
        this.spawnerLocation = spawnerLocation;
    }

    @Override
    public void run() {
        if (elapsedSeconds >= TASK_DURATION_SECONDS) {
            this.cancel();
//            removeRemainingZombies();
            return;
        }

        long currentZombieCount = spawnerLocation.getWorld().getNearbyEntities(spawnerLocation, CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS)
                .stream()
                .filter(entity -> entity instanceof Zombie)
                .filter(entity -> entity.hasMetadata(meta))
                .filter(entity -> !entity.isDead())
                .count();

        long zombiesToSpawn = DESIRED_ZOMBIE_COUNT - currentZombieCount;
        if (zombiesToSpawn > 0) {
            for (int i = 0; i < zombiesToSpawn; i++) {
                spawnZombie();
            }
        }

        elapsedSeconds += TASK_INTERVAL_SECONDS;
    }

    private void spawnZombie() {
        Zombie zombie = (Zombie) spawnerLocation.getWorld().spawnEntity(
                spawnerLocation,
                EntityType.ZOMBIE
        );

        zombie.getEquipment().setItemInHand(new ItemStack(Material.EGG, 8));
        zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        zombie.getEquipment().setHelmetDropChance(0.0f); // 头盔不掉落
        zombie.getEquipment().setItemInHandDropChance(1.0f); // 主手物品100%掉落
        zombie.setCustomNameVisible(false);
        zombie.setRemoveWhenFarAway(false);
        zombie.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), true));
    }



//    private void removeRemainingZombies() {
//        spawnerLocation.getWorld().getNearbyEntities(spawnerLocation, CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS)
//                .forEach(entity -> {
//                    if (entity instanceof Zombie) {
//                        entity.remove(); // 移除僵尸
//                    }
//                });
//    }
}
