package net.example.firstmod.mixin;

import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public class FishingHookMixin {

    @Shadow
    private int nibble;

    @Shadow
    private net.minecraft.world.entity.Entity hookedIn;

    @Inject(method = "retrieve(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"))
    private void onRetrieve(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() <= 0) return;
        FishingHook hook = (FishingHook)(Object)this;
        if (!(hook.level() instanceof ServerLevel level)) return;
        if (nibble <= 0 && hookedIn == null) return;

        var owner = hook.getPlayerOwner();
        if (!(owner instanceof ServerPlayer player)) return;

        double dist = Math.sqrt(hook.getX() * hook.getX() + hook.getZ() * hook.getZ());
        var server = level.getServer();
        if (server == null) return;
        ProgressionStore store = ProgressionStore.getOrCreate(server);
        ProgressionData data = store.get(player.getUUID());
        int ppReward = 1 + (int)(dist / 5000);
        data.addPp(ppReward);
        store.save();

        ServerPlayNetworking.send(player, new ProgressionPayloads.SyncPayload(
            data.statLevels, data.availablePp(), data.totalEarnedPp, data.spentPp));
    }
}
