package cn.linmoyu.bedwarsitem.items.bridgeegg.type;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.items.bridgeegg.BridgeEgg;
import cn.linmoyu.bedwarsitem.items.bridgeegg.SetBlock;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class HuaYuTing implements SetBlock {

    // AI糊的 还是有点崩(比如斜扔的最后几个方块)
    public void start(Game game, Egg egg, Player player) {
        final Set<String> placedPositions = new HashSet<>();
        final Location[] lastLoc = {null};
        final int[] placedBlockCount = {0}; // 跟踪已放置的方块数量

        new BukkitRunnable() {
            @Override
            public void run() {
                if (egg.isDead()) {
                    this.cancel();
                    return;
                }

                Location loc = egg.getLocation();
                Location groundLoc = loc.clone().subtract(0.0, 2.0, 0.0);

                if (player.getLocation().distance(loc) > 1.0D) {
                    // 检查是否已达到方块上限
                    if (placedBlockCount[0] >= Config.bridge_egg_max_blocks) {
                        this.cancel();
                        return;
                    }

                    // 获取玩家的朝向
                    Vector playerDirection = player.getLocation().getDirection().normalize();
                    boolean isStraight = Math.abs(playerDirection.getX()) > 0.5 || Math.abs(playerDirection.getZ()) > 0.5;

                    // 每隔一定距离放置一次图案
                    if (lastLoc[0] == null || lastLoc[0].distance(groundLoc) >= 2.0) {
                        String posKey = (int) groundLoc.getX() + "," + (int) groundLoc.getZ();

                        if (!placedPositions.contains(posKey)) {
                            placedPositions.add(posKey);
                            lastLoc[0] = groundLoc.clone();

                            if (isStraight) {
                                int blocksPlaced = createCrossPattern(groundLoc, game, player, BridgeEgg.sound);
                                placedBlockCount[0] += blocksPlaced;
                            } else {
                                int blocksPlaced = createXPattern(groundLoc, game, player, BridgeEgg.sound);
                                placedBlockCount[0] += blocksPlaced;
                            }

                            // 检查是否达到上限
                            if (placedBlockCount[0] >= Config.bridge_egg_max_blocks) {
                                this.cancel();
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(BedwarsItem.getInstance(), 0L, 1L);
    }

    private int createCrossPattern(Location center, Game game, Player player, Sound sound) {
        int[][] offsets = {
                {-1, 0, 0}, {0, 0, 0}, {1, 0, 0},  // 水平
                {0, 0, -1}, {0, 0, 1}   // 垂直
        };

        int placedCount = 0;

        for (int[] offset : offsets) {
            Location blockLoc = center.clone().add(offset[0], offset[1], offset[2]);
            Block block = blockLoc.getBlock();

            if (BridgeEgg.canPlace(block, player, game, blockLoc)) {
                block.setType(Material.SANDSTONE);
                game.getRegion().addPlacedBlock(block, null);
                placedCount++;
                if (sound != null) {
                    player.playSound(blockLoc, sound, 1.0f, 1.0f);
                }
            }
        }

        return placedCount;
    }

    private int createXPattern(Location center, Game game, Player player, Sound sound) {
        int[][] offsets = {
                {-1, 0, -1}, {1, 0, 1},  // 对角线1
                {1, 0, -1}, {-1, 0, 1}   // 对角线2
        };

        int placedCount = 0;

        for (int[] offset : offsets) {
            Location blockLoc = center.clone().add(offset[0], offset[1], offset[2]);
            Block block = blockLoc.getBlock();

            if (BridgeEgg.canPlace(block, player, game, blockLoc)) {
                block.setType(Material.SANDSTONE);
                game.getRegion().addPlacedBlock(block, null);
                placedCount++;
                if (sound != null) {
                    player.playSound(blockLoc, sound, 1.0f, 1.0f);
                }
            }
        }

        return placedCount;
    }
}
