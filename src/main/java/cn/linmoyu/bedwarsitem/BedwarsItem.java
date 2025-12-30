package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.entities.*;
import cn.linmoyu.bedwarsitem.items.*;
import lombok.Getter;
import me.ram.bedwarsscoreboardaddon.Main;
import me.ram.bedwarsscoreboardaddon.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedwarsItem extends JavaPlugin implements Listener {

    @Getter
    private static BedwarsItem instance;
    public static String aboutMessage;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Config.setupConfig(this);
        aboutMessage = ColorUtil.color("§f* This server is running §bBedwarsItem Plugin§f. \n§f* By §b@YukiEnd §f| §bLinMoyu_ §7v" + Main.getInstance().getDescription().getVersion());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new BridgeEgg(), this);
        Bukkit.getPluginManager().registerEvents(new ExplosionProof(), this);
        Bukkit.getPluginManager().registerEvents(new FireBall(), this);
        Bukkit.getPluginManager().registerEvents(new TNT(), this);
        Bukkit.getPluginManager().registerEvents(new TutorialBook(), this);

        Bukkit.getPluginManager().registerEvents(new SilverFishSpawner(), this);
        Bukkit.getPluginManager().registerEvents(new SkeletonSpawner(), this);
        Bukkit.getPluginManager().registerEvents(new SpiderSpawner(), this);
        Bukkit.getPluginManager().registerEvents(new WolfSpawner(), this);

        getCommand("bedwarsitem").setExecutor(new MainCommand());
        getCommand("bwitem").setExecutor(new MainCommand());
        getCommand("spawnzombies").setExecutor(new SpawnZombiesCommand());
        getCommand("spawnpigzombies").setExecutor(new SpawnPigZombiesCommand());

        Bukkit.getConsoleSender().sendMessage(aboutMessage);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
