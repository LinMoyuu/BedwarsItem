package cn.linmoyu.bedwarsitem.items;

import cn.linmoyu.bedwarsitem.BedwarsItem;
import cn.linmoyu.bedwarsitem.Config;
import io.github.bedwarsrel.events.BedwarsPlayerJoinedEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TutorialBook implements Listener {

    public ItemStack customBook;

    public TutorialBook() {
        customBook = createCustomBook();
    }

    @EventHandler
    public void onPlayerJoin(BedwarsPlayerJoinedEvent event) {
        if (!Config.tutorial_book_enabled) return;
        if (customBook == null) createCustomBook();
        // 你怎么知道花雨庭也没做游戏状态判断？
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().getInventory().setItem(7, customBook);
            }
        }.runTaskLater(BedwarsItem.getInstance(), 5L);
    }

    private ItemStack createCustomBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        // 从配置文件读取
        String title = "§a§l起床战争指南§7(右键阅读)";
        String author = ("一般路过羽末末");

        meta.setTitle(title);
        meta.setAuthor(author);

        // 第一页
        String page1 = "§r§r§8§l§8§l     ● 起床战争指南§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l[♦] 基本玩法§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8- 收集铁锭、金锭与钻石，然\n" +
                "§r§r§8§8后在村民商店购买多种多样§r§r\n" +
                "§r§r§8§8的道具！§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8- 保护好你的床，被破坏后将§r§r\n" +
                "§r§r§8§8永远无法复活，直至失败。§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l[♦] 进阶攻略§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8- 注意时间，每隔一段时间都§r";


        // 第二页
        String page2 = "§r§r§8§8会发生神奇的事件！§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8-§r\n" +
                "§r§8§8利用神奇事件争取反转胜负§r§r\n" +
                "§r§r§8§8的机会！§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l[♦] 赢得胜利§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8- 破坏敌方的床并击杀敌人，§r§r\n" +
                "§r§r§8§8即可获胜。§r\n";

        // 第三页
        String page3 = "§r§r§8§l§8§l      ● 更新日志§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l❒ 版本：§r§8§8 1.0.0§r§r\n" +
                "§r§r§8§l§8§l❑ 日期：§r§8§8 待定§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l 1-正式上线§r§r\n" +
                "§r§r§8§8 ■ 传统起床战争升级打造§r§r\n" +
                "§r§r§8§8 ■ 包含多种新玩法与体验§r\n";

        // 第四页
        String page4 = "§r§r§8§l§8§l      ● 关于我们§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§8《花雨庭》是由一群热爱我§r§r\n" +
                "§r§r§8§8的世界的小伙伴们创建的国§r§r\n" +
                "§r§r§8§8风小游戏服务器；上线伊始，§r\n" +
                "§r§8§8我§r§r\n" +
                "§r§r§8§8们与网易CC直播达成合作，相§r§r\n" +
                "§r§r§8§8信在大家的支持下，我们会越§r§r\n" +
                "§r§r§8§8来越好! 《花雨庭》只为你的§r§r\n" +
                "§r§r§8§8快乐而生。§r§r\n" +
                "§r§r§r\n" +
                "§r§r§8§l§8§l   ➜ §8本端或原形已在哔哩哔哩免费发布.\n" +
                "§r§r§8§l§8§l   ➜ R.I.P 2017-2025\n" +
                "§r§r§8“只是一场烟火散落的尘埃.”";

        // 添加页面到书
        meta.addPage(page1, page2, page3, page4);

        book.setItemMeta(meta);
        return book;
    }

}
