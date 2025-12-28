package cn.linmoyu.bedwarsitem;

import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(BedwarsItem.aboutMessage);

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("bedwarsitem.reload")) {
                sender.sendMessage(ColorUtil.color("§c你没有权限执行此命令!"));
                return true;
            }
            Config.setupConfig(BedwarsItem.getInstance());
            sender.sendMessage(ColorUtil.color("§a配置文件已重新加载!"));
        }

        return true;
    }

}
