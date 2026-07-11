package net.example.firstmod.mixin;

import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.config.ProgressionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockExpMixin {

    @Inject(method = "popExperience", at = @At("TAIL"))
    private void onPopExperience(ServerLevel level, BlockPos pos, int amount, CallbackInfo ci) {
        if (!ProgressionSettings.isEnabled() || amount <= 0) return;
        Player nearest = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10.0, false);
        if (nearest instanceof ServerPlayer player) {
            var server = level.getServer();
            if (server != null) {
                ProgressionStore store = ProgressionStore.getOrCreate(server);
                ProgressionData data = store.get(player.getUUID());
                double wisMult = StatFormulas.getEffect(StatFormulas.WIS, data.statLevels[StatFormulas.WIS]);
                int extra = (int) Math.round(amount * (wisMult - 1.0));
                if (extra > 0) {
                    ExperienceOrb.award(level, Vec3.atCenterOf(pos), extra);
                }
            }
        }
    }
}
