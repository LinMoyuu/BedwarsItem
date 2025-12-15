package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.items.FireBall;
import cn.linmoyu.bedwarsitem.items.TNT;
import cn.linmoyu.bedwarsitem.monsters.pig_zombie.SpawnPigZombiesCommand;
import cn.linmoyu.bedwarsitem.monsters.zombie.SpawnZombiesCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedwarsItem extends JavaPlugin implements Listener {

    @Getter
    private static BedwarsItem instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireBall(), this);
        Bukkit.getPluginManager().registerEvents(new TNT(), this);

        getCommand("spawnzombies").setExecutor(new SpawnZombiesCommand());
        getCommand("spawnpigzombies").setExecutor(new SpawnPigZombiesCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
