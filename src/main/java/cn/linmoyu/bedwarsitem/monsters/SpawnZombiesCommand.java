package cn.linmoyu.bedwarsitem.monsters;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnZombiesCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("bedwarsitem.command.spawnzombies")) return true;
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
                int spawnCount = Config.spawner_zombies_count;
                for (int i = 0; i < spawnCount; i++) {
                    spawnZombie(spawner.getLocation(), game);
                    if (Config.debug) {
                        sender.sendMessage("§e[DEBUG] 在游戏 " + gameName + " 的金锭刷新点 " + spawner.getLocation() + " 生成了一个僵尸.");
                    }
                }
            }
        }
        sender.sendMessage("§a已在游戏 " + gameName + " 的所有金锭刷新点生成僵尸.");
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = getSuggest(sender, args);
        String input = args.length > 0 ? args[args.length - 1].toUpperCase() : "";

        return suggestions.stream()
                .filter(s -> s.startsWith(input))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> getSuggest(CommandSender sender, String[] args) {
        List<String> games = new ArrayList<>();
        BedwarsRel.getInstance().getGameManager().getGames().forEach(game -> {
            games.add(game.getName());
        });
        if (args.length == 1) {
            return games;
        }
        return Collections.emptyList();
    }
}
