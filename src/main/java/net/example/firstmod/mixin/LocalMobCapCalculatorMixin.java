package net.example.firstmod.mixin;

import net.example.firstmod.ExampleMod;
import net.example.firstmod.config.ProgressionSettings;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(LocalMobCapCalculator.class)
public abstract class LocalMobCapCalculatorMixin {

    @Inject(method = "canSpawn", at = @At("RETURN"), cancellable = true)
    private void onCanSpawn(MobCategory mobCategory, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!ProgressionSettings.isEnabled()) return;
        if (cir.getReturnValue()) return;

        double distFromSpawn = Math.sqrt(
            (double)pos.getMiddleBlockX() * pos.getMiddleBlockX() +
            (double)pos.getMiddleBlockZ() * pos.getMiddleBlockZ()
        );
        float multiplier = ProgressionSettings.getEffectiveMobSpawnMultiplier(distFromSpawn);
        if (multiplier <= 1.0f) return;

        float extraChance = Math.min(multiplier - 1.0f, 0.8f);
        if (ThreadLocalRandom.current().nextFloat() < extraChance) {
            ExampleMod.LOGGER.debug("MOB_SPAWN bypass cap at {} (mult={})", pos, multiplier);
            cir.setReturnValue(true);
        }
    }
}
