package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.Config;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * reward:
 * - type: GLASS
 * meta:
 * ==: ItemMeta
 * meta-type: UNSPECIFIC
 * display-name: "§c防爆玻璃"
 * lore:
 * - "§7TNT无法炸破"
 * - "§7但是又很容易被拆掉"
 * - "§7ps:他并不能保护别的方块被炸"
 */

public class ExplosionProof implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (!Config.explosion_proof_enabled) return;
        if (!(e.getEntity() instanceof TNT) && !(e.getEntity() instanceof TNTPrimed)) return;
        Location location = e.getEntity().getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5);
        Game game = BedwarsRel.getInstance().getGameManager().getGameByLocation(location);
        if (game == null) {
            return;
        }
        boolean isLightTNT = e.getEntity().hasMetadata("LightTNT");

        List<Block> block_list = new ArrayList<>();
        for (Block block : e.blockList()) {
            // 防爆玻璃略过
            if (block.getType().equals(Material.GLASS) ||
                    block.getType().equals(Material.STAINED_GLASS)) {
                continue;
            }
            // 如果是本插件放置的TNT 同时非玩家放置方块 略过
            // TNT羊会破坏地图方块 这是机制
            if (isLightTNT && !game.getRegion().isPlacedBlock(block)) {
                continue;
            }
            block_list.add(block);
        }
        e.blockList().clear();
        e.blockList().addAll(block_list);
    }
}
