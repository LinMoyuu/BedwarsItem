package cn.linmoyu.bedwarsitem.utils;

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
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MonsterUtils {

    public static Location getSpawnLocation(Location location, BlockFace blockFace) {
        Location spawnLocation = location.clone();
        // 根据点击的面调整生成位置
        switch (blockFace) {
            case UP:
                spawnLocation.add(0, 1, 0); // 在方块正上方生成
                break;
            case DOWN:
                spawnLocation.add(0, -1, 0); // 在方块正下方生成
                break;
            case NORTH:
                spawnLocation.add(0, 0, -1);
                break;
            case SOUTH:
                spawnLocation.add(0, 0, 1);
                break;
            case EAST:
                spawnLocation.add(1, 0, 0);
                break;
            case WEST:
                spawnLocation.add(-1, 0, 0);
                break;
            default:
                spawnLocation.add(0, 1, 0); // 默认在方块上方生成
        }
        // 确保生成位置是安全的
        while (!spawnLocation.getBlock().isEmpty()) {
            spawnLocation.add(0, 1, 0); // 如果位置被占用 向上移动一格
        }
        return spawnLocation;
    }

    public static Player findNearestEnemy(LivingEntity entity, Player thrower) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(thrower);
        if (game == null) return null;
        Team throwerTeam = game.getPlayerTeam(thrower);
        if (throwerTeam == null) return null;
        for (Player player : entity.getWorld().getPlayers()) {
            if (thrower.equals(player)) continue;
            if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR) continue;
            if (throwerTeam == game.getPlayerTeam(player)) continue;

            // 检查距离
            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < 20 && distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public static boolean isGameMonsters(Entity entity, EntityType entityType) {
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

    public static String getMonsterMeta(Entity entity) {
        String meta = "";
        switch (entity.getType()) {
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
        return meta;
    }

    public static String getThrowerName(Entity entity, String meta) {
        String throwerName = meta;
        if (entity == null || meta == null || meta.isEmpty()) return throwerName;
        throwerName = entity.getMetadata(meta).get(0).asString();
        Player thrower = Bukkit.getPlayerExact(throwerName);
        if (thrower != null) thrower.getDisplayName();
        return throwerName;
    }
}
