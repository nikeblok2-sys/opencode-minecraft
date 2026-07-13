package net.example.firstmod.mixin;

import net.example.firstmod.scaling.EnchantHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentsMixin {

    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
    private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        int vanillaMax = cir.getReturnValue();
        int newMax = EnchantHelper.applyEnchantBonus(vanillaMax, 5000);
        if (newMax != vanillaMax) {
            cir.setReturnValue(newMax);
        }
    }
}
