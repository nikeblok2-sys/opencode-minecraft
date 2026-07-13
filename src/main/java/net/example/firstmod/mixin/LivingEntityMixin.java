package net.example.firstmod.mixin;

import net.example.firstmod.component.BossRewards;
import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.component.StatRegistry;
import net.example.firstmod.config.ProgressionSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private static final int XP_BOOST_INDEX = StatRegistry.indexOf("xp_boost");

    @Inject(method = "getBaseExperienceReward", at = @At("RETURN"), cancellable = true)
    private void onGetBaseExperienceReward(ServerLevel level, CallbackInfoReturnable<Integer> cir) {
        if (!ProgressionSettings.isEnabled()) return;
        int original = cir.getReturnValue();
        if (original <= 0) return;

        LivingEntity entity = (LivingEntity)(Object)this;
        double dist = Math.sqrt(entity.getX() * entity.getX() + entity.getZ() * entity.getZ());
        float mult = ProgressionSettings.getEffectiveMobSpawnMultiplier(dist);
        int bonus = Math.round(original * (mult - 1.0f) * 0.5f);

        var lastAttacker = entity.getLastAttacker();
        if (lastAttacker instanceof ServerPlayer player) {
            var server = player.level().getServer();
            if (server != null) {
                ProgressionStore store = ProgressionStore.getOrCreate(server);
                ProgressionData data = store.get(player.getUUID());
                double xpBoost = StatFormulas.getEffect(XP_BOOST_INDEX, data.statLevels[XP_BOOST_INDEX]);
                bonus += Math.round(original * xpBoost / 100.0);
            }
        }

        if (bonus > 0) {
            cir.setReturnValue(original + bonus);
        }
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void onDie(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity.level() instanceof ServerLevel) {
            Entity attacker = damageSource.getEntity();
            if (attacker instanceof ServerPlayer player) {
                BossRewards.onEntityDeath(entity, player);
            }
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        if (!ProgressionSettings.isEnabled()) return;
        LivingEntity entity = (LivingEntity)(Object)this;
        if (!(entity instanceof ServerPlayer player)) return;
        Entity attacker = source.getEntity();
        if (attacker == null) return;

        int critIndex = StatRegistry.indexOf("crit_chance");
        var server = player.level().getServer();
        if (server == null) return;
        ProgressionStore store = ProgressionStore.getOrCreate(server);
        ProgressionData data = store.get(player.getUUID());
        double critChance = StatFormulas.getEffect(critIndex, data.statLevels[critIndex]) / 100.0;

        if (critChance > 0 && player.getRandom().nextDouble() < critChance) {
            LivingEntity target = (LivingEntity) attacker;
            target.hurt(source, amount * 0.5f);
        }
    }
}
