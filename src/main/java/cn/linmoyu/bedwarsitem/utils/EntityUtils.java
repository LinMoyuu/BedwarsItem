package cn.linmoyu.bedwarsitem.utils;

import cn.linmoyu.bedwarsitem.entities.EntityManager;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.projectiles.ProjectileSource;

public class EntityUtils {

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

    public static Player findNearestEnemy(Entity entity, Player spawner) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        Game game = getPetGame(entity);
        if (game == null) return null;
        Team spawnerTeam = game.getPlayerTeam(spawner);
        if (spawnerTeam == null) return null;
        for (Player player : entity.getWorld().getPlayers()) {
            if (spawner.equals(player)) continue;
            if (game.isSpectator(player) || player.getGameMode() == GameMode.SPECTATOR) continue;
            if (spawnerTeam == game.getPlayerTeam(player)) continue;

            // 检查距离
            double distance = player.getLocation().distance(entity.getLocation());
            if (distance < 20 && distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    /**
     * 检查实体是否为游戏内由插件生成的实体（宠物或怪物）。
     */
    public static boolean isGameEntity(Entity entity) {
        // 调用 EntityManager 的新公共方法
        return EntityManager.isPet(entity) || EntityManager.monsterList.contains(entity);
    }

    /**
     * 获取宠物所在的游戏。
     */
    public static Game getPetGame(Entity entity) {
        return EntityManager.getGame(entity);
    }

    /**
     * 获取宠物的生成者（主人）。
     */
    public static Player getSpawner(Entity pet) {
        return EntityManager.getSpawner(pet);
    }


    public static Player getPlayer(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }

        if (entity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Entity) {
                return getPlayer((Entity) shooter);
            }
        }

        if (entity instanceof TNTPrimed) {
            Entity source = ((TNTPrimed) entity).getSource();
            if (source != null) {
                return getPlayer(source);
            }
        }

        return null;
    }
}
