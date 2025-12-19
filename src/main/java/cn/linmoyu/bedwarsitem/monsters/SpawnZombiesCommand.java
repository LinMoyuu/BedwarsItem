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
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class SpawnZombiesCommand implements CommandExecutor {

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
            // 检查这个刷新点是否产出金锭(第二岛屿)
            boolean isGoldSpawner = spawner.getResources().stream()
                    .anyMatch(itemStack -> itemStack.getType() == Material.GOLD_INGOT);

            if (isGoldSpawner) {
                // 生成数量
                int spawnCount = 2;
                for (int i = 0; i < spawnCount; i++) {
                    spawnZombie(spawner.getLocation(), game);
                }
            }
        }
        return true;
    }

    private void spawnZombie(Location spawnerLocation, Game game) {
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
        zombie.setMetadata(Monsters.GOLD_ZOMBIE.getMeta(), new FixedMetadataValue(BedwarsItem.getInstance(), game.getName() + ":" + spawnerLocation));
    }
}
