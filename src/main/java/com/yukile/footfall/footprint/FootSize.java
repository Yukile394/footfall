package com.yukile.footfall.footprint;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;

/**
 * Relative footprint scale for a given entity type. Multiplied against the
 * user's global {@code footprintSize} config value.
 */
public enum FootSize {
    PLAYER(1.0f, 0.18f),
    TINY(0.55f, 0.10f),      // cat, fox, chicken, rabbit
    SMALL(0.75f, 0.13f),     // wolf, dog, sheep, pig
    MEDIUM(1.1f, 0.20f),     // cow, llama, camel
    LARGE(1.4f, 0.28f),      // horse, donkey, mule
    GIANT(2.2f, 0.42f);      // iron golem, warden, bear-like

    private final float scale;
    private final float footSpacing;

    FootSize(float scale, float footSpacing) {
        this.scale = scale;
        this.footSpacing = footSpacing;
    }

    public float scale() {
        return scale;
    }

    /** Half-distance between left/right foot placements, in blocks. */
    public float footSpacing() {
        return footSpacing;
    }

    public static FootSize forEntity(Entity entity) {
        if (entity instanceof CatEntity || entity instanceof FoxEntity
                || entity instanceof ChickenEntity || entity instanceof RabbitEntity) {
            return TINY;
        }
        if (entity instanceof WolfEntity || entity instanceof SheepEntity
                || entity instanceof PigEntity) {
            return SMALL;
        }
        if (entity instanceof CowEntity || entity instanceof LlamaEntity
                || entity instanceof net.minecraft.entity.passive.CamelEntity) {
            return MEDIUM;
        }
        if (entity instanceof AbstractHorseEntity) {
            return LARGE;
        }
        if (entity instanceof net.minecraft.entity.passive.IronGolemEntity
                || entity instanceof net.minecraft.entity.mob.WardenEntity) {
            return GIANT;
        }
        return SMALL;
    }
}
