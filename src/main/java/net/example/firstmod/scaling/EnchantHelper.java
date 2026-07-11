package net.example.firstmod.scaling;

import net.example.firstmod.ExampleMod;
import net.example.firstmod.profile.ExpressionEvaluator;
import net.example.firstmod.profile.ProgressionProfileManager;

public class EnchantHelper {

    public static int applyEnchantBonus(int vanillaMax, double distance) {
        String formula = ProgressionProfileManager.getProfile().enchantBonus();
        try {
            double bonus = ExpressionEvaluator.eval(formula, distance);
            return vanillaMax + Math.max(0, (int) Math.round(bonus));
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Failed to evaluate enchant formula '{}' at dist={}: {}", formula, distance, e.getMessage());
            return vanillaMax;
        }
    }
}
