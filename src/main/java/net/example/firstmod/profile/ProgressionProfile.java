package net.example.firstmod.profile;

import java.util.List;

public record ProgressionProfile(
    String profileName,
    String scalingFormula,
    List<Double> equipThresholds,
    String enchantBonus,
    String lootChanceBase,
    List<LootItemTier> lootItemTiers
) {
    public record LootItemTier(String itemId, double baseDist, int minAmount, int maxAmount) {}
}
