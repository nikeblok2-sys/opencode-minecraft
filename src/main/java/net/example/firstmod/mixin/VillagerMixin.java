package net.example.firstmod.mixin;

import net.example.firstmod.config.ProgressionSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public class VillagerMixin {

    @Inject(method = "notifyTrade", at = @At("TAIL"))
    private void onNotifyTrade(MerchantOffer offer, CallbackInfo ci) {
        if (!ProgressionSettings.isEnabled()) return;
        AbstractVillager villager = (AbstractVillager)(Object)this;

        if (villager.getTradingPlayer() == null) return;
        if (!(villager.level() instanceof ServerLevel level)) return;

        double dist = Math.sqrt(villager.getX() * villager.getX() + villager.getZ() * villager.getZ());
        float mult = ProgressionSettings.getEffectiveMobSpawnMultiplier(dist);
        int bonusXp = Math.round(offer.getXp() * (mult - 1.0f) * 0.5f);
        if (bonusXp > 0) {
            villager.getTradingPlayer().giveExperiencePoints(bonusXp);
        }
    }
}
