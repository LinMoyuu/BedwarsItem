package cn.linmoyu.bedwarsitem.monsters.pig_zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PigZombieSpawnerTask extends BukkitRunnable {

    // 期望僵尸数量
    private static final int DESIRED_PIG_ZOMBIE_COUNT = 2;
    // 半径检测范围
    private static final double CHECK_RADIUS = 10;
    // 此任务持续时长
    private static final int TASK_DURATION_SECONDS = 180;
    // 任务执行间隔 (秒)
    private static final int TASK_INTERVAL_SECONDS = 30; // 30秒是瞎给的 没有测过具体数值
    public static String meta = "BwPigZombie";
    private final Location spawnerLocation;
    // 已执行秒数
    private int elapsedSeconds = 0;

    public PigZombieSpawnerTask(Location spawnerLocation) {
        this.spawnerLocation = spawnerLocation;
    }

    @Override
    public void run() {
        if (elapsedSeconds >= TASK_DURATION_SECONDS) {
            this.cancel();
//            removeRemainingZombies();
            return;
        }

        long currentPigZombieCount = spawnerLocation.getWorld().getNearbyEntities(spawnerLocation, CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS)
                .stream()
                .filter(entity -> entity instanceof PigZombie)
                .filter(entity -> entity.hasMetadata(meta))
                .filter(entity -> !entity.isDead())
                .count();

        long pigZombiesToSpawn = DESIRED_PIG_ZOMBIE_COUNT - currentPigZombieCount;
        if (pigZombiesToSpawn > 0) {
            for (int i = 0; i < pigZombiesToSpawn; i++) {
                spawnPigZombie();
            }
        }

        elapsedSeconds += TASK_INTERVAL_SECONDS;
    }

    private void spawnPigZombie() {
        PigZombie pigZombie = (PigZombie) spawnerLocation.getWorld().spawnEntity(
                spawnerLocation,
                EntityType.PIG_ZOMBIE
        );

        pigZombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        pigZombie.getEquipment().setItemInHand(new ItemStack(Material.EGG, 8));
        pigZombie.getEquipment().setHelmetDropChance(0.0f); // 头盔不掉落
        pigZombie.getEquipment().setItemInHandDropChance(1.0f); // 主手物品100%掉落
        pigZombie.setCustomNameVisible(false);
        pigZombie.setRemoveWhenFarAway(false);
        pigZombie.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), true));
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
