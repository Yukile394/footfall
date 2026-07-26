package com.yukile.footfall.client;

import com.yukile.footfall.config.FootfallConfig;
import com.yukile.footfall.footprint.FootprintManager;
import com.yukile.footfall.render.FootprintRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public final class FootfallClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FootfallConfig.get(); // eager load/create config file

        WorldRenderEvents.AFTER_TRANSLUCENT.register(FootprintRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && !client.isPaused()) {
                FootprintManager.get().tick(client.world);
            }
        });
    }

    public static MinecraftClient client() {
        return MinecraftClient.getInstance();
    }
}
