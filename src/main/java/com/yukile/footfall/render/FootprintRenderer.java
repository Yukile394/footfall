package com.yukile.footfall.render;

import com.yukile.footfall.config.FootfallConfig;
import com.yukile.footfall.footprint.Footprint;
import com.yukile.footfall.footprint.FootprintManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.util.Deque;

/**
 * Draws every currently-alive footprint in the player's dimension as a
 * single batched pass. Footprints outside the configured render distance or
 * outside the view frustum are skipped entirely before any vertex work
 * happens, which is what keeps this cheap even with thousands stored.
 */
public final class FootprintRenderer {

    private FootprintRenderer() {
    }

    public static void render(WorldRenderContext context) {
        FootfallConfig config = FootfallConfig.get();
        if (!config.modEnabled || !config.footprintsEnabled) {
            return;
        }

        ClientWorld world = context.world();
        Deque<Footprint> footprints = FootprintManager.get().footprintsFor(world.getRegistryKey());
        if (footprints.isEmpty()) {
            return;
        }

        Vec3d cameraPos = context.camera().getPos();
        double maxDistance = Math.min(config.performanceMode.renderDistance(), 128.0);
        double maxDistanceSq = maxDistance * maxDistance;

        long currentTick = world.getTime();
        long lifetimeTicks = Math.max(1, config.lifetimeSeconds * 20L);

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumerProvider = context.consumers();
        if (matrices == null || !(consumerProvider instanceof VertexConsumerProvider.Immediate immediate)) {
            return;
        }

        RenderLayer layer = FootprintRenderLayer.get();
        VertexConsumer buffer = immediate.getBuffer(layer);

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        for (Footprint print : footprints) {
            double dx = print.x - cameraPos.x;
            double dy = print.y - cameraPos.y;
            double dz = print.z - cameraPos.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq > maxDistanceSq) {
                continue;
            }

            if (!context.frustum().isVisible(new net.minecraft.util.math.Box(
                    print.x - 0.4, print.y - 0.1, print.z - 0.4,
                    print.x + 0.4, print.y + 0.1, print.z + 0.4))) {
                continue;
            }

            float alpha = MathHelper.clamp(print.remainingLife(currentTick, lifetimeTicks), 0f, 1f);
            if (alpha <= 0f) {
                continue;
            }

            int light = LightmapTextureManager.pack(
                    world.getLightLevel(net.minecraft.world.LightType.BLOCK, print.groundPos),
                    world.getLightLevel(net.minecraft.world.LightType.SKY, print.groundPos)
            );

            drawFootprint(buffer, positionMatrix, print, alpha, light);
        }

        matrices.pop();
        immediate.draw(layer);
    }

    private static void drawFootprint(VertexConsumer buffer, Matrix4f matrix, Footprint print,
                                       float alpha, int light) {
        float half = 0.14f * print.size;
        float yawRad = (float) Math.toRadians(print.yaw);
        float cos = MathHelper.cos(yawRad);
        float sin = MathHelper.sin(yawRad);

        // Local-space quad corners (footprint points "forward" along +Z),
        // rotated by yaw and translated to world position.
        float[][] local = {
                {-half, 0, -half},
                {half, 0, -half},
                {half, 0, half},
                {-half, 0, half}
        };
        float[] u = {0f, 1f, 1f, 0f};
        float[] v = {0f, 0f, 1f, 1f};

        int tint = print.surface.tint();
        float r = ((tint >> 16) & 0xFF) / 255f;
        float g = ((tint >> 8) & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;

        for (int i = 0; i < 4; i++) {
            float lx = local[i][0];
            float lz = local[i][2];
            float wx = (float) print.x + (lx * cos - lz * sin);
            float wy = (float) print.y;
            float wz = (float) print.z + (lx * sin + lz * cos);

            buffer.vertex(matrix, wx, wy, wz)
                    .texture(u[i], v[i])
                    .color(r, g, b, alpha)
                    .light(light)
                    .next();
        }
    }
}
