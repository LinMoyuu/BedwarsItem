package cn.linmoyu.bedwarsitem.utils;

import cn.linmoyu.bedwarsitem.monsters.Monsters;
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

    public static boolean isGameMonsters(Entity entity) {
        EntityType entityType = entity.getType();
        for (Monsters monster : Monsters.values()) {
            if (monster.getEntityType() == entityType && entity.hasMetadata(monster.getMeta())) {
                return true;
            }
        }
        return false;
    }

    public static String getMonsterMeta(EntityType entityType) {
        for (Monsters monster : Monsters.values()) {
            if (monster.getEntityType() == entityType) {
                return monster.getMeta();
            }
        }
        return "";
    }

    public static String getGameName(Entity entity, String meta) {
        String gameName = meta;
        if (entity == null || meta == null || meta.isEmpty()) return gameName;
        String[] metas = entity.getMetadata(meta).get(0).asString().split(":");
        if (metas.length < 1) return gameName;
        gameName = metas[0];
        return gameName;
    }

    public static Player getThrower(Entity entity, String meta) {
        String throwerName;
        if (entity == null || meta == null || meta.isEmpty()) return null;
        String[] metas = entity.getMetadata(meta).get(0).asString().split(":");
        if (metas.length < 2) return null;
        throwerName = metas[1];
        return Bukkit.getPlayerExact(throwerName);
    }
}
