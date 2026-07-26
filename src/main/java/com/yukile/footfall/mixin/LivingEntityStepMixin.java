package com.yukile.footfall.mixin;

import com.yukile.footfall.footprint.FootprintSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the exact moment vanilla plays a footstep sound. This is the same
 * signal the game itself uses to know "a step happened here", so footprints
 * stay perfectly synced with the walk cycle for both players and mobs,
 * without any extra per-tick position-delta tracking.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityStepMixin {

    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void footfall$onStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        FootprintSpawner.handleStep((LivingEntity) (Object) this, pos, state);
    }
}
