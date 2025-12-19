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
                // 生成数量
                int spawnCount = 1;
                for (int i = 0; i < spawnCount; i++) {
                    spawnPigZombie(spawner.getLocation(), game);
                }
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
        pigZombie.setMetadata(Monsters.DIAMOND_PIG_ZOMBIE.getMeta(), new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawnerLocation));
    }
}
