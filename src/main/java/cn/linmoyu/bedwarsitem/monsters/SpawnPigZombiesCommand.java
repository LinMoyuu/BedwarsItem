package cn.linmoyu.bedwarsitem.monsters;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class SpawnPigZombiesCommand implements CommandExecutor {

    public static String meta = "BwPigZombie";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) return true;
        if (args.length == 0) return true;
        String gameName = args[0];
        Game game = BedwarsRel.getInstance().getGameManager().getGame(gameName);
        if (game == null) {
            return true;
        }
        for (ResourceSpawner spawner : game.getResourceSpawners()) {
            // 检查这个刷新点是否产出钻石(钻石岛屿)
            boolean isDiamondSpawner = spawner.getResources().stream()
                    .anyMatch(itemStack -> itemStack.getType() == Material.DIAMOND);

            if (isDiamondSpawner) {
                for (int i = 0; i < 1; i++) {
                    spawnPigZombie(spawner.getLocation(), game);
                }
                // 为每个金锭刷新点启动一个生成任务
//                new PigZombieSpawnerTask(game, spawner.getLocation()).runTaskTimer(BedwarsItem.getInstance(), 0L, 300L);
            }
        }
        return true;
    }

    private void spawnPigZombie(Location spawnerLocation, Game game) {
        PigZombie pigZombie = (PigZombie) spawnerLocation.getWorld().spawnEntity(
                spawnerLocation,
                EntityType.PIG_ZOMBIE
        );

        pigZombie.getEquipment().setItemInHand(new ItemStack(Material.OBSIDIAN, 2));
        pigZombie.getEquipment().setItemInHandDropChance(1.0f); // 主手物品100%掉落
        pigZombie.setCustomNameVisible(false);
        pigZombie.setRemoveWhenFarAway(false);
        pigZombie.setMetadata(meta, new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawnerLocation));
    }
}
