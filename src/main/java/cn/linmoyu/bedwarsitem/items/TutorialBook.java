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

    @EventHandler
    public void onPlayerJoin(BedwarsPlayerJoinedEvent event) {
        if (!Config.tutorial_book_enabled) return;
        // 你怎么知道花雨庭也没做游戏状态判断？
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().getInventory().setItem(7, createCustomBook());
            }
        }.runTaskLater(BedwarsItem.getInstance(), 5L);

    }

    private ItemStack createCustomBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        // 从配置文件读取
        String title = "§a§l起床战争指南§7（右键阅读）";
        String author = ("一般路过羽末末");

        meta.setTitle(title);
        meta.setAuthor(author);

        // 第一页
        String page1 = "§8§l      ● 起床战争指南\n\n" +
                "§8§l[♦] 基本玩法\n\n" +
                "§8- 收集铁锭、金锭与钻石，然\n后在村民商店购买多种多样的道具！\n\n" +
                "§8- 保护好你的床，被破坏后将\n永远无法复活，直至失败。\n\n" +
                "§8§l[♦] 进阶攻略\n\n" +
                "§8- 注意时间，每隔一段时间都";

        // 第二页
        String page2 = "§8会发生神奇的事件！\n\n" +
                "§8- 利用神奇事件争取反转胜负\n的机会！\n\n" +
                "§8§l[♦] 赢得胜利\n\n" +
                "§8- 破坏敌方的床并击杀敌人，\n即可获胜。";

        // 第三页
        String page3 = "§8§l     ● 更新日志\n\n" +
                "§8§l❒ 版本： §r§81.0.0\n" +
                "§8§l❑ 日期： §r§8待定\n\n" +
                " §8§l1-正式上线\n" +
                " §8§l■ §r§8传统起床战争升级打造\n" +
                " §8§l■ §r§8包含多种新玩法与体验";

        // 第四页
        String page4 = "§8§l     ● 关于我们\n" +
                "§8《花雨庭》是由一群热爱我的世界的小伙伴们创建的国风小游戏服务器；上线伊始，我们与网易CC直播达成合作，相信在大家的支持下，我们会越来越好! 《花雨庭》只为你的\n快乐而生。\n\n" +
                "§8§l ➜ R.I.P 2017-2025\n" +
                "§r§8本端或原形已在哔哩哔哩免费发布.\n" +
                "“只是一场烟火散落的尘埃.”";

        // 添加页面到书
        meta.addPage(page1, page2, page3, page4);

        book.setItemMeta(meta);
        return book;
    }

}
