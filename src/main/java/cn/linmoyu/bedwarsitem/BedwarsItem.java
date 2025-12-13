package cn.linmoyu.bedwarsitem;

import cn.linmoyu.bedwarsitem.items.FireBall;
import cn.linmoyu.bedwarsitem.items.TNT;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BedwarsItem extends JavaPlugin {

    @Getter
    private static BedwarsItem instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Bukkit.getPluginManager().registerEvents(new FireBall(), this);
        Bukkit.getPluginManager().registerEvents(new TNT(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
