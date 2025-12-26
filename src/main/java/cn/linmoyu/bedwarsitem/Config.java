package cn.linmoyu.bedwarsitem;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

    public static boolean debug;
    public static int spawner_zombies_count;
    public static int spawner_pigzombies_count;
    public static boolean explosion_proof_enabled;
    public static boolean bridge_egg_enabled;
    public static String bridge_egg_name;
    public static int bridge_egg_max_blocks;
    public static boolean fireball_enabled;
    public static float fireball_yield;
    public static double fireball_velocity;
    public static double fireball_damage;
    public static boolean tnt_enable;
    public static float tnt_yield;
    public static int tnt_fuse_ticks;
    public static int tnt_damage;
    public static boolean silverfish_spawner_enabled;
    public static boolean skeleton_spawner_enabled;
    public static boolean spider_spawner_enabled;
    public static boolean wolf_spawner_enabled;

    public static void setupConfig(Plugin plugin) {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        debug = config.getBoolean("debug");
        spawner_zombies_count = config.getInt("res_spawner.zombies");
        spawner_pigzombies_count = config.getInt("res_spawner.pigzombies");
        explosion_proof_enabled = config.getBoolean("explosion_proof.enabled");
        bridge_egg_enabled = config.getBoolean("bridge_egg.enabled");
        bridge_egg_name = config.getString("bridge_egg.name");
        bridge_egg_max_blocks = config.getInt("bridge_egg.max_blocks");
        fireball_enabled = config.getBoolean("fireball.enabled");
        fireball_yield = (float) config.getDouble("fireball.yield");
        fireball_velocity = config.getDouble("fireball.velocity");
        fireball_damage = config.getDouble("fireball.damage");
        tnt_enable = config.getBoolean("tnt.enabled");
        tnt_yield = (float) config.getDouble("tnt.yield");
        tnt_fuse_ticks = config.getInt("tnt.fuse_ticks");
        tnt_damage = config.getInt("tnt.damage");
        silverfish_spawner_enabled = config.getBoolean("spawner.silverfish");
        skeleton_spawner_enabled = config.getBoolean("spawner.skeleton");
        spider_spawner_enabled = config.getBoolean("spawner.spider");
        wolf_spawner_enabled = config.getBoolean("spawner.wolf");
    }
}
