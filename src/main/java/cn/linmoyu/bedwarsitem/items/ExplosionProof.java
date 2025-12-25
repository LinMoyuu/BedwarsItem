package cn.linmoyu.bedwarsitem.items;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 reward:
 - type: GLASS
 meta:
 ==: ItemMeta
 meta-type: UNSPECIFIC
 display-name: "§c防爆玻璃"
 lore:
 - "§7TNT无法炸破"
 - "§7但是又很容易被拆掉"
 - "§7ps:他并不能保护别的方块被炸"
 */

public class ExplosionProof implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        Location location = e.getEntity().getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
        Game game = BedwarsRel.getInstance().getGameManager().getGameByLocation(location);
        if (game == null) {
            return;
        }
        List<Block> block_list = new ArrayList<>();
        for (Block block : e.blockList()) {
            if (block.getType().equals(Material.GLASS) || block.getType().equals(Material.STAINED_GLASS) || !game.getRegion().isPlacedBlock(block)) {
                continue;
            }
            block_list.add(block);
        }
        e.blockList().clear();
        e.blockList().addAll(block_list);
    }
}