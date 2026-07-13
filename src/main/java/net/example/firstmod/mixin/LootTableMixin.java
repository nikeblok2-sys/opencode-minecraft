package net.example.firstmod.mixin;

import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.component.StatRegistry;
import net.example.firstmod.scaling.LootHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

    private static final int DOUBLE_DROP_INDEX = StatRegistry.indexOf("double_drop");

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

        Player player = ctx.getOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER);
        if (player == null) {
            Entity attacking = ctx.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
            if (attacking instanceof Player p) player = p;
        }
        if (player instanceof ServerPlayer sp) {
            var server = level.getServer();
            if (server != null) {
                ProgressionStore store = ProgressionStore.getOrCreate(server);
                ProgressionData data = store.get(sp.getUUID());
                double doubleDrop = StatFormulas.getEffect(DOUBLE_DROP_INDEX, data.statLevels[DOUBLE_DROP_INDEX]);
                double chance = doubleDrop / 100.0;
                if (chance > 0 && sp.getRandom().nextDouble() < chance) {
                    ObjectArrayList<ItemStack> bonus = new ObjectArrayList<>();
                    for (ItemStack stack : items) {
                        if (!stack.isEmpty()) {
                            bonus.add(stack.copy());
                        }
                    }
                    items.addAll(bonus);
                }
            }
        }

        Entity thisEntity = ctx.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (thisEntity instanceof ServerPlayer chestOpener && ctx.getOptionalParameter(LootContextParams.TOOL) == null) {
            var server = level.getServer();
            if (server != null) {
                ProgressionStore store = ProgressionStore.getOrCreate(server);
                ProgressionData data = store.get(chestOpener.getUUID());
                int ppReward = 2 + (int)(distFromSpawn / 1000);
                data.addPp(ppReward);
                store.save();
            }
        }
    }
}
