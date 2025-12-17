package cn.linmoyu.bedwarsitem.monsters.pig_zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class PigZombieSpawnerTask extends BukkitRunnable {

    public static String meta = "BwPigZombie";
    private final Location spawnerLocation;
    private final Game game;
    // 期望僵尸数量
    private static final int DESIRED_PIG_ZOMBIE_COUNT = 2;
    // 半径检测范围
    private static final double CHECK_RADIUS = 10;

    public PigZombieSpawnerTask(Game game, Location spawnerLocation) {
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
    }

    private void spawnPigZombie() {
        PigZombie pigZombie = (PigZombie) spawnerLocation.getWorld().spawnEntity(
                spawnerLocation,
                EntityType.PIG_ZOMBIE
        );

        pigZombie.getEquipment().setItemInHand(new ItemStack(Material.OBSIDIAN, 2));
        pigZombie.getEquipment().setItemInHandDropChance(1.0f); // 主手物品100%掉落
        pigZombie.setCustomNameVisible(false);
        pigZombie.setRemoveWhenFarAway(false);
        pigZombie.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName()));
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
