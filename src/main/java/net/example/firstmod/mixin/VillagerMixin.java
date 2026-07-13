package net.example.firstmod.mixin;

import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.config.ProgressionSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public class VillagerMixin {

    @Inject(method = "notifyTrade", at = @At("TAIL"))
    private void onNotifyTrade(MerchantOffer offer, CallbackInfo ci) {
        AbstractVillager villager = (AbstractVillager)(Object)this;
        Player tradingPlayer = villager.getTradingPlayer();
        if (tradingPlayer == null) return;
        if (!(villager.level() instanceof ServerLevel level)) return;

        double dist = Math.sqrt(villager.getX() * villager.getX() + villager.getZ() * villager.getZ());

        if (ProgressionSettings.isEnabled()) {
            float mult = ProgressionSettings.getEffectiveMobSpawnMultiplier(dist);
            int bonusXp = Math.round(offer.getXp() * (mult - 1.0f) * 0.5f);
            if (bonusXp > 0) {
                tradingPlayer.giveExperiencePoints(bonusXp);
            }
        }

        if (tradingPlayer instanceof ServerPlayer player) {
            var server = level.getServer();
            if (server != null) {
                ProgressionStore store = ProgressionStore.getOrCreate(server);
                ProgressionData data = store.get(player.getUUID());
                int ppReward = 1 + (int)(dist / 5000);
                data.addPp(ppReward);
                store.save();

                net.example.firstmod.network.ProgressionPayloads.SyncPayload sync = new net.example.firstmod.network.ProgressionPayloads.SyncPayload(
                    data.statLevels, data.availablePp(), data.totalEarnedPp, data.spentPp);
                net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, sync);
            }
        }
    }
}
