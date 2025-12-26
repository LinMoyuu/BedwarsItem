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
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnPigZombiesCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("bedwarsitem.command.spawnpigzombies")) return true;
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
                int spawnCount = Config.spawner_pigzombies_count;
                for (int i = 0; i < spawnCount; i++) {
                    spawnPigZombie(spawner.getLocation(), game);
                    if (Config.debug) {
                        sender.sendMessage("§e[DEBUG] 在游戏 " + gameName + " 的钻石刷新点 " + spawner.getLocation() + " 生成了一个猪人.");
                    }
                }
            }
        }
        sender.sendMessage("§a已在游戏 " + gameName + " 的所有钻石刷新点生成猪人.");
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
