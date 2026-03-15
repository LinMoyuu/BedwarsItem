package cn.linmoyu.bedwarsitem.utils;

import io.github.bedwarsrel.BedwarsRel;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TakeItemUtil {
    public static void TakeItem(Player player, ItemStack stack) {
        if (BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_8")) {
            if (player.getInventory().getItemInHand() != null) {
                ItemStack itemInHand = player.getInventory().getItemInHand();
                if (itemInHand.getType() == stack.getType()) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    player.getInventory().setItemInHand(itemInHand);
                    return;
                }
            }
        }
    }
}