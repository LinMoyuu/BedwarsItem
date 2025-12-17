package cn.linmoyu.bedwarsitem.monsters.zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ZombieSpawnerTask extends BukkitRunnable {

    public static String meta = "BwZombie";
    private final Location spawnerLocation;
    private final Game game;
    // 期望僵尸数量
    private static final int DESIRED_ZOMBIE_COUNT = 2;
    // 半径检测范围
    private static final double CHECK_RADIUS = 10;

    public ZombieSpawnerTask(Game game, Location spawnerLocation) {
        this.game = game;
        this.spawnerLocation = spawnerLocation;
    }

    @Override
    public void run() {
        if (game.isOverSet()) {
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
        zombie.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawnerLocation));
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
