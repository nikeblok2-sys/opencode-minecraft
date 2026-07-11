package net.example.firstmod.config;

import net.example.firstmod.StructureGroup;

public class ProgressionSettings {

    public static int currentPreset = 0;

    public static float dungeonScalableMultiplier = 1.0f;
    public static float dungeonFixedMultiplier    = 1.0f;
    public static float peacefulMultiplier        = 1.0f;

    public static float progressionScale = 0.0f;
    public static float armorTierScale   = 0.0f;

    public static boolean extraLoot      = false;
    public static float   lootMultiplier = 1.0f;

    public static boolean overlimitEnchants  = false;
    public static int     enchantBonusLevels = 0;

    public static float mobSpawnMultiplier  = 1.0f;
    public static float difficultyBonus     = 0.0f;
    public static float mobGearChance       = 0.0f;
    public static float bossHpMultiplier    = 1.0f;
    public static float bossDamageMultiplier = 1.0f;

    public static void applyPreset(int preset) {
        currentPreset = preset;
        switch (preset) {
            case 0 -> {
                dungeonScalableMultiplier = 1.0f;
                dungeonFixedMultiplier    = 1.0f;
                peacefulMultiplier        = 1.0f;
                progressionScale          = 0.0f;
                armorTierScale            = 0.0f;
                extraLoot                 = false;
                overlimitEnchants         = false;
                lootMultiplier            = 1.0f;
                enchantBonusLevels        = 0;
                mobSpawnMultiplier       = 1.0f;
                difficultyBonus           = 0.0f;
                mobGearChance             = 0.0f;
                bossHpMultiplier          = 1.0f;
                bossDamageMultiplier      = 1.0f;
            }
            case 1 -> {
                dungeonScalableMultiplier = 1.2f;
                dungeonFixedMultiplier    = 1.0f;
                peacefulMultiplier        = 1.0f;
                progressionScale          = 0.025f;
                armorTierScale            = 0.3f;
                extraLoot                 = true;
                overlimitEnchants         = false;
                lootMultiplier            = 1.2f;
                enchantBonusLevels        = 0;
                mobSpawnMultiplier       = 1.2f;
                difficultyBonus           = 0.5f;
                mobGearChance             = 0.2f;
                bossHpMultiplier          = 1.3f;
                bossDamageMultiplier      = 1.3f;
            }
            case 2 -> {
                dungeonScalableMultiplier = 1.5f;
                dungeonFixedMultiplier    = 1.0f;
                peacefulMultiplier        = 1.0f;
                progressionScale          = 0.225f;
                armorTierScale            = 1.0f;
                extraLoot                 = true;
                overlimitEnchants         = false;
                lootMultiplier            = 1.5f;
                enchantBonusLevels        = 5;
                mobSpawnMultiplier       = 1.5f;
                difficultyBonus           = 1.0f;
                mobGearChance             = 0.4f;
                bossHpMultiplier          = 1.7f;
                bossDamageMultiplier      = 1.7f;
            }
            case 3 -> {
                dungeonScalableMultiplier = 2.0f;
                dungeonFixedMultiplier    = 1.0f;
                peacefulMultiplier        = 1.0f;
                progressionScale          = 0.9f;
                armorTierScale            = 2.0f;
                extraLoot                 = true;
                overlimitEnchants         = true;
                lootMultiplier            = 2.0f;
                enchantBonusLevels        = 10;
                mobSpawnMultiplier       = 2.0f;
                difficultyBonus           = 2.0f;
                mobGearChance             = 0.6f;
                bossHpMultiplier          = 2.5f;
                bossDamageMultiplier      = 2.5f;
            }
            case 4 -> {
            }
        }
    }

    public static float getEffectiveMultiplier(StructureGroup group, double distFromSpawn) {
        if (currentPreset == 0) return 1.0f;
        if (group == StructureGroup.PEACEFUL || group == StructureGroup.DUNGEON_FIXED) return 1.0f;

        return dungeonScalableMultiplier * (1.0f + (float)(distFromSpawn / 5000.0) * progressionScale);
    }

    public static float getEffectiveLootMultiplier(double distFromSpawn) {
        if (!extraLoot || currentPreset == 0) return 1.0f;
        return lootMultiplier * (1.0f + (float)Math.sqrt(distFromSpawn / 1000.0 * progressionScale));
    }

    public static int getEffectiveEnchantBonus(double distFromSpawn) {
        if (!overlimitEnchants || currentPreset == 0) return 0;
        return enchantBonusLevels + (int)(Math.sqrt(distFromSpawn / 1000.0 * progressionScale) * 10);
    }

    public static float getEffectiveMobSpawnMultiplier(double distFromSpawn) {
        if (currentPreset == 0) return 1.0f;
        return mobSpawnMultiplier * (1.0f + (float)Math.sqrt(distFromSpawn / 1000.0 * progressionScale));
    }

    public static float getEffectiveDifficultyBonus(double distFromSpawn) {
        if (currentPreset == 0) return 0.0f;
        return difficultyBonus + (float)(distFromSpawn / 1000.0) * progressionScale * 2.0f;
    }

    public static float getEffectiveMobGearChance(double distFromSpawn) {
        if (currentPreset == 0) return 0.0f;
        float distFactor = (float)Math.sqrt(distFromSpawn / 1000.0 * progressionScale) * 0.3f;
        return Math.min(1.0f, mobGearChance + distFactor);
    }

    public static int getEquipmentTier(double distFromSpawn) {
        if (currentPreset == 0 || armorTierScale <= 0.01f) return 0;
        double raw = Math.sqrt(distFromSpawn / 1000.0 * armorTierScale);
        return Math.min((int)raw, 5);
    }

    public static boolean isEnabled() {
        return currentPreset != 0;
    }
}
