package com.yukile.footfall.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

/**
 * A single shared texture ("footprint.png") is tinted per-surface via vertex
 * color, so one render layer / one draw call batch covers every surface type
 * and every mob size at once. Depth test is LEQUAL (not "always") so
 * footprints correctly hide behind terrain, but no polygon offset trickery
 * is needed since prints are drawn a hair above the block surface.
 */
public final class FootprintRenderLayer extends RenderLayer {

    public static final Identifier TEXTURE = Identifier.of("footfall", "textures/footprint/footprint.png");

    private static final RenderLayer FOOTPRINT = of(
            "footfall_footprint",
            VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(POSITION_TEXTURE_COLOR_LIGHTMAP_PROGRAM)
                    .texture(new RenderPhase.Texture(TEXTURE, false, false))
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .cull(DISABLE_CULLING)
                    .depthTest(LEQUAL_DEPTH_TEST)
                    .writeMaskState(COLOR_MASK)
                    .build(false)
    );

    private FootprintRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode,
                                  int expectedBufferSize, boolean hasCrumbling, boolean translucent,
                                  MultiPhaseParameters phases) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }

    public static RenderLayer get() {
        return FOOTPRINT;
    }
}
