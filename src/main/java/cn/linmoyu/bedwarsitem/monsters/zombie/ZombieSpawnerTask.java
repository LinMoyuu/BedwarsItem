package cn.linmoyu.bedwarsitem.monsters.zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.monsters.MonsterMeta;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSpawnerTask extends BukkitRunnable {

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
                .filter(entity -> entity.hasMetadata(MonsterMeta.BWZOMBIE.name()))
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

    // 为什么要NMS生成?
    // https://github.com/BedwarsRel/BedwarsRel/blob/master/common/src/main/java/io/github/bedwarsrel/listener/EntityListener.java#L138
    private void spawnZombie() {
        // 1. 获取 NMS WorldServer 对象
        WorldServer nmsWorld = ((CraftWorld) spawnerLocation.getWorld()).getHandle();

        // 2. 创建一个 NMS 僵尸实体
        EntityZombie nmsZombie = new EntityZombie(nmsWorld);

        // 3. 设置僵尸的位置
        nmsZombie.setPosition(spawnerLocation.getX(), spawnerLocation.getY(), spawnerLocation.getZ());

        // 4. 设置装备
        ItemStack bukkitEggs = new ItemStack(Material.EGG, 8);
        // 将 Bukkit ItemStack 转换为 NMS ItemStack
        net.minecraft.server.v1_8_R3.ItemStack nmsEggs = CraftItemStack.asNMSCopy(bukkitEggs);
        // 设置到主手
        nmsZombie.setEquipment(0, nmsEggs);

        // 5. 设置其他属性
        nmsWorld.addEntity(nmsZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);

        // 7. 获取 Bukkit 实体并设置其他属性 (这是更安全和推荐的方式)
        Zombie bukkitZombie = (Zombie) nmsZombie.getBukkitEntity();
        // 设置头盔（防止阳光烧伤）
        bukkitZombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        // 设置头盔不掉落
        bukkitZombie.getEquipment().setHelmetDropChance(0.0f);
        // 设置主手鸡蛋的掉落概率为100%
        bukkitZombie.getEquipment().setItemInHandDropChance(1.0f);
        // 名字显示
        bukkitZombie.setCustomNameVisible(false);
        // 远处移除
        bukkitZombie.setRemoveWhenFarAway(false);
        bukkitZombie.setMetadata(MonsterMeta.BWZOMBIE.name(), new FixedMetadataValue(BedwarsItem.getInstance(), true));
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
