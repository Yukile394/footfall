package com.yukile.footfall.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * We deliberately do NOT hand-roll a custom RenderLayer here: the exact
 * protected field/constructor shapes of RenderLayer shift between mapping
 * versions and are the single riskiest thing to guess correctly without a
 * local compile. Reusing Minecraft's own public
 * {@code RenderLayer.getEntityTranslucent(Identifier)} factory gives us a
 * translucent, lightmap-aware, texture-bound layer for free, and it's a
 * stable public entry point across recent versions.
 */
public final class FootprintRenderLayer {

    public static final Identifier TEXTURE = Identifier.of("footfall", "textures/footprint/footprint.png");

    private FootprintRenderLayer() {
    }

    public static RenderLayer get() {
        return RenderLayer.getEntityTranslucent(TEXTURE);
    }
}
