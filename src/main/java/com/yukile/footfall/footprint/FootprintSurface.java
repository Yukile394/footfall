package com.yukile.footfall.footprint;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Optional;

/**
 * Enumerates every block "family" footprints are allowed to appear on, along
 * with the tint color used to recolor the single shared footprint sprite for
 * that surface. Hard surfaces (stone, wood, glass, metal) are intentionally
 * absent so they never match.
 */
public enum FootprintSurface {
    SNOW(0xF2F6FF),
    SAND(0xD9C48A),
    RED_SAND(0xB35A2E),
    MUD(0x4A3826),
    DUST(0xC9C2B4),
    SOUL_SAND(0x4B3A2E),
    SOUL_SOIL(0x2E2620),
    MOSS(0x3F6E32);

    private final int tint;

    FootprintSurface(int tint) {
        this.tint = tint;
    }

    /** Packed 0xRRGGBB tint applied to the shared footprint sprite. */
    public int tint() {
        return tint;
    }

    /**
     * Resolves the footprint surface for a block state, or empty if the
     * block is not soft enough to hold a footprint.
     */
    public static Optional<FootprintSurface> from(BlockState state) {
        Block block = state.getBlock();

        if (block == Blocks.SNOW || block == Blocks.SNOW_BLOCK) {
            return Optional.of(SNOW);
        }
        if (block == Blocks.SAND || block == Blocks.SUSPICIOUS_SAND) {
            return Optional.of(SAND);
        }
        if (block == Blocks.RED_SAND) {
            return Optional.of(RED_SAND);
        }
        if (block == Blocks.MUD || block == Blocks.MUDDY_MANGROVE_ROOTS) {
            return Optional.of(MUD);
        }
        if (block == Blocks.SOUL_SAND) {
            return Optional.of(SOUL_SAND);
        }
        if (block == Blocks.SOUL_SOIL) {
            return Optional.of(SOUL_SOIL);
        }
        if (isMoss(block)) {
            return Optional.of(MOSS);
        }
        if (isDust(block)) {
            return Optional.of(DUST);
        }
        return Optional.empty();
    }

    private static boolean isMoss(Block block) {
        return block == Blocks.MOSS_BLOCK || block == Blocks.MOSS_CARPET;
    }

    private static boolean isDust(Block block) {
        // "Tozlu bloklar": powder/ash style blocks that should read as dusty.
        return block == Blocks.GRAVEL
                || block == Blocks.COARSE_DIRT
                || block == Blocks.DIRT_PATH;
    }
}
