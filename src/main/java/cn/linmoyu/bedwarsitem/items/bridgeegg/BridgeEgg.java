package cn.linmoyu.bedwarsitem.items.bridgeegg;

import cn.linmoyu.bedwarsitem.Config;
import cn.linmoyu.bedwarsitem.items.bridgeegg.type.HuaYuTing;
import cn.linmoyu.bedwarsitem.items.bridgeegg.type.Original;
import cn.linmoyu.bedwarsitem.utils.TakeItemUtil;
import cn.linmoyu.bedwarsitem.utils.Utils;
import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class BridgeEgg implements Listener {

    public static Sound sound;
    private final Map<Integer, SetBlock> egg_style_Type;

    public BridgeEgg() {
        egg_style_Type = new HashMap<>();
        egg_style_Type.put(1, new Original());
        egg_style_Type.put(2, new HuaYuTing());
        sound = Utils.getSound("BLOCK_STONE_BREAK", "DIG_STONE");
    }

    public static boolean canPlace(Block block, Player player, Game game, Location location) {
        return block.getType() == new ItemStack(Material.AIR).getType() &&
                !block.equals(player.getLocation().getBlock()) &&
                !block.equals(player.getLocation().clone().add(0, 1, 0).getBlock())
                && game.getRegion().isInRegion(location)
                && Utils.isCanPlace(game, location);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!Config.bridge_egg_enabled) return;
        if (!egg_style_Type.containsKey(Config.bridge_egg_mode)) {
            return;
        }
        SetBlock setBlock = egg_style_Type.get(Config.bridge_egg_mode);
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack handItem = e.getItem();
        if (handItem == null || handItem.getType() != Material.EGG) {
            return;
        }
        ItemMeta itemMeta = handItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String displayName = itemMeta.hasDisplayName() ?
                ColorUtil.removeColor(itemMeta.getDisplayName()) : "";
        if (!(Config.bridge_egg_name.equals(displayName) || displayName.isEmpty() && Config.bridge_egg_name.isEmpty())) {
            return;
        }
        Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
        if (game == null) {
            return;
        }
        e.setCancelled(true);

        Egg egg = player.launchProjectile(Egg.class);
        egg.setBounce(false);
        egg.setShooter(player);
        setBlock.start(game, egg, player);
        TakeItemUtil.TakeItem(player, handItem);
    }
}
