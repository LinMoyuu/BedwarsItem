package cn.linmoyu.bedwarsitem.utils;

import cn.linmoyu.bedwarsitem.entities.Entities;
import cn.linmoyu.bedwarsitem.entities.EntityTaskManager;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
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

    public static Player findNearestEnemy(Entity entity, Player thrower) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        Game game = getMonsterGame(entity);
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

    public static boolean isGameEntity(Entity entity) {
        return EntityTaskManager.petsList.contains(entity) || EntityTaskManager.monsterList.contains(entity);
    }

//    public static boolean isGameMonsters(Entity entity) {
//        EntityType entityType = entity.getType();
//        for (Entities monster : Entities.values()) {
//            if (monster.getEntityType() == entityType && entity.hasMetadata(monster.getMeta())) {
//                return true;
//            }
//        }
//        return false;
//    }

    public static String getMonsterMeta(EntityType entityType) {
        for (Entities monster : Entities.values()) {
            if (monster.getEntityType() == entityType) {
                return monster.getMeta();
            }
        }
        return "";
    }

    public static Game getMonsterGame(Entity entity) {
        String meta = getMonsterMeta(entity.getType());
        if (meta == null || meta.isEmpty()) return null;
        String[] metas = entity.getMetadata(meta).get(0).asString().split(":");
        if (metas.length < 1) return null;
        return BedwarsRel.getInstance().getGameManager().getGame(metas[0]);
    }

    public static Player getThrower(Entity entity) {
        String meta = getMonsterMeta(entity.getType());
        if (meta == null || meta.isEmpty()) return null;
        String[] metas = entity.getMetadata(meta).get(0).asString().split(":");
        if (metas.length < 2) return null;
        return Bukkit.getPlayerExact(metas[1]);
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

        if (EntityUtils.isGameEntity(entity)) {
            return EntityUtils.getThrower(entity);
        }

        return null;
    }
}
