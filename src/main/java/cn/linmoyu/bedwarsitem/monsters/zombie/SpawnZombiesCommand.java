package cn.linmoyu.bedwarsitem.monsters.zombie;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.ResourceSpawner;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                // 为每个金锭刷新点启动一个生成任务
                new ZombieSpawnerTask(spawner.getLocation()).runTaskTimer(BedwarsItem.getInstance(), 0L, 300L);
            }
        }
        return true;
    }
}
