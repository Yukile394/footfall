package com.yukile.footfall.footprint;

import net.minecraft.util.math.BlockPos;

/**
 * A single placed footprint. Mutable only in {@code decayTicks}, which the
 * manager advances faster than real time under rain/snow to make prints
 * disappear sooner.
 */
public final class Footprint {

    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final BlockPos groundPos;
    public final FootprintSurface surface;
    public final float size;
    public final boolean leftFoot;
    public final long spawnTick;

    /** Extra decay accumulated from rain/snow exposure, in ticks. */
    public float bonusDecayTicks = 0f;

    public Footprint(double x, double y, double z, float yaw, BlockPos groundPos,
                      FootprintSurface surface, float size, boolean leftFoot, long spawnTick) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.groundPos = groundPos;
        this.surface = surface;
        this.size = size;
        this.leftFoot = leftFoot;
        this.spawnTick = spawnTick;
    }

    /**
     * Returns remaining life in the 0..1 range given the current tick and
     * the configured lifetime. 0 or below means the footprint should be
     * removed.
     */
    public float remainingLife(long currentTick, long lifetimeTicks) {
        float age = (currentTick - spawnTick) + bonusDecayTicks;
        return 1.0f - (age / (float) lifetimeTicks);
    }
}
