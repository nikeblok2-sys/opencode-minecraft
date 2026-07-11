package net.example.firstmod.mixin;

import net.example.firstmod.scaling.LootHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @Inject(
        method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
        at = @At("RETURN")
    )
    private void onGetRandomItems(LootContext ctx, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        Vec3 origin = ctx.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin == null) return;

        ServerLevel level = ctx.getLevel();
        ObjectArrayList<ItemStack> items = cir.getReturnValue();
        if (items == null || items.isEmpty()) return;

        double distFromSpawn = Math.sqrt(origin.x * origin.x + origin.z * origin.z);

        LootHelper.modifyLoot(items, distFromSpawn, level);
    }
}
