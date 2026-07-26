package com.yukile.footfall.footprint;

import com.yukile.footfall.config.FootfallConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Owns every live footprint, keyed by dimension so switching worlds (or the
 * end/nether) never mixes footprints between them. A deque per dimension
 * gives O(1) eviction of the oldest entry once the global cap is hit.
 */
public final class FootprintManager {

    private static final FootprintManager INSTANCE = new FootprintManager();

    private final Map<RegistryKey<World>, Deque<Footprint>> byDimension = new HashMap<>();
    private int totalCount = 0;

    private FootprintManager() {
    }

    public static FootprintManager get() {
        return INSTANCE;
    }

    public void spawn(RegistryKey<World> dimension, Footprint footprint) {
        FootfallConfig config = FootfallConfig.get();
        Deque<Footprint> deque = byDimension.computeIfAbsent(dimension, k -> new ArrayDeque<>());
        deque.addLast(footprint);
        totalCount++;

        int cap = Math.min(config.maxFootprints, config.performanceMode.footprintCap());
        while (totalCount > cap && !deque.isEmpty()) {
            deque.pollFirst();
            totalCount--;
        }
    }

    public Deque<Footprint> footprintsFor(RegistryKey<World> dimension) {
        return byDimension.getOrDefault(dimension, new ArrayDeque<>());
    }

    /**
     * Advances decay, prunes expired prints, clears prints that ended up
     * underwater, and applies faster fading during rain/snow. Called once
     * per client tick for the current world only (cheap: bounded by the
     * per-dimension footprint cap).
     */
    public void tick(ClientWorld world) {
        FootfallConfig config = FootfallConfig.get();
        Deque<Footprint> deque = byDimension.get(world.getRegistryKey());
        if (deque == null || deque.isEmpty()) {
            return;
        }

        long currentTick = world.getTime();
        long lifetimeTicks = Math.max(1, config.lifetimeSeconds * 20L);

        Iterator<Footprint> iterator = deque.iterator();
        while (iterator.hasNext()) {
            Footprint print = iterator.next();

            // Water washes footprints away immediately.
            if (world.getFluidState(print.groundPos).isIn(net.minecraft.registry.tag.FluidTags.WATER)) {
                iterator.remove();
                totalCount--;
                continue;
            }

            applyWeatherDecay(world, print, config);

            if (print.remainingLife(currentTick, lifetimeTicks) <= 0f) {
                iterator.remove();
                totalCount--;
            }
        }
    }

    private void applyWeatherDecay(ClientWorld world, Footprint print, FootfallConfig config) {
        if (!world.hasRain(print.groundPos)) {
            return;
        }

        Biome.Precipitation precipitation = world.getBiome(print.groundPos).value()
                .getPrecipitation(print.groundPos);

        if (precipitation == Biome.Precipitation.RAIN) {
            print.bonusDecayTicks += Math.max(0f, config.rainFadeMultiplier - 1.0f);
        } else if (precipitation == Biome.Precipitation.SNOW && config.snowCoversFootprints) {
            // Snow doesn't just fade prints, it buries them under a new
            // layer, so it decays a little faster than rain by default.
            print.bonusDecayTicks += Math.max(0f, config.rainFadeMultiplier - 0.5f);
        }
    }

    public void clearAll() {
        byDimension.clear();
        totalCount = 0;
    }

    public int totalCount() {
        return totalCount;
    }
}
