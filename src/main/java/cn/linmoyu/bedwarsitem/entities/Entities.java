package cn.linmoyu.bedwarsitem.entities;

import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public enum Entities {
    GOLD_ZOMBIE(EntityType.ZOMBIE, "BwZombie"),
    DIAMOND_PIG_ZOMBIE(EntityType.PIG_ZOMBIE, "BwPigZombie"),

    PETS_SILVERFISH(EntityType.SILVERFISH, "BwSilverFish"),
    PETS_SKELETON(EntityType.SKELETON, "BwSkeleton"),
    PETS_SPIDER(EntityType.SPIDER, "BwSpider"),
    PETS_WOLF(EntityType.WOLF, "BwWolf");

    private final EntityType entityType;
    private final String meta;

    Entities(EntityType entityType, String meta) {
        this.entityType = entityType;
        this.meta = meta;
    }
}
