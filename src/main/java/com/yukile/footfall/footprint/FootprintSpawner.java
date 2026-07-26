package com.yukile.footfall.footprint;

import com.yukile.footfall.config.FootfallConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Entry point called from {@code LivingEntityStepMixin} every time vanilla
 * decides an entity has taken an audible step. Reusing vanilla's own step
 * timing keeps footprints perfectly in sync with the walk animation without
 * any extra per-tick position tracking.
 */
public final class FootprintSpawner {

    private static final Map<Entity, Boolean> LAST_FOOT_LEFT = new WeakHashMap<>();
    private static final Map<Entity, Integer> STEP_COUNTER = new WeakHashMap<>();

    private FootprintSpawner() {
    }

    public static void handleStep(LivingEntity entity, BlockPos pos, BlockState state) {
        FootfallConfig config = FootfallConfig.get();
        if (!config.modEnabled || !config.footprintsEnabled) {
            return;
        }

        World world = entity.getWorld();
        if (!world.isClient) {
            return;
        }

        boolean isPlayer = entity instanceof PlayerEntity;
        if (!isPlayer && !config.mobFootprintsEnabled) {
            return;
        }
        if (!isPlayer && !isSupportedMob(entity)) {
            return;
        }

        // Never print in/under liquid.
        if (!world.getFluidState(pos).isEmpty()) {
            return;
        }

        Optional<FootprintSurface> surface = FootprintSurface.from(state);
        if (surface.isEmpty()) {
            return;
        }

        int skip = config.performanceMode.spawnSkip();
        if (skip > 1) {
            int count = STEP_COUNTER.merge(entity, 1, Integer::sum);
            if (count % skip != 0) {
                return;
            }
        }

        FootSize footSize = isPlayer ? FootSize.PLAYER : FootSize.forEntity(entity);
        boolean leftFoot = !LAST_FOOT_LEFT.getOrDefault(entity, false);
        LAST_FOOT_LEFT.put(entity, leftFoot);

        float yawRad = (float) Math.toRadians(entity.getYaw());
        double sideX = MathHelper.cos(yawRad);
        double sideZ = MathHelper.sin(yawRad);
        double offset = footSize.footSpacing() * (leftFoot ? -1 : 1);

        double footX = entity.getX() + sideX * offset;
        double footZ = entity.getZ() + sideZ * offset;
        double footY = pos.getY() + 1.0 + 0.01; // sit just above the block surface

        float size = footSize.scale() * config.footprintSize;

        Footprint footprint = new Footprint(
                footX, footY, footZ,
                entity.getYaw(),
                pos.toImmutable(),
                surface.get(),
                size,
                leftFoot,
                world.getTime()
        );

        FootprintManager.get().spawn(world.getRegistryKey(), footprint);
    }

    private static boolean isSupportedMob(Entity entity) {
        return entity instanceof net.minecraft.entity.passive.WolfEntity
                || entity instanceof net.minecraft.entity.passive.FoxEntity
                || entity instanceof net.minecraft.entity.passive.CatEntity
                || entity instanceof net.minecraft.entity.passive.TameableEntity
                || entity instanceof net.minecraft.entity.passive.AbstractHorseEntity
                || entity instanceof net.minecraft.entity.passive.CowEntity
                || entity instanceof net.minecraft.entity.passive.SheepEntity
                || entity instanceof net.minecraft.entity.passive.PigEntity
                || entity instanceof net.minecraft.entity.passive.ChickenEntity
                || entity instanceof net.minecraft.entity.passive.CamelEntity
                || entity instanceof net.minecraft.entity.passive.LlamaEntity
                || entity instanceof net.minecraft.entity.mob.IronGolemEntity
                || entity instanceof net.minecraft.entity.mob.WardenEntity;
    }
}
