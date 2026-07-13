package net.example.firstmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProgressionConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("firstmod.json");

    private record SettingsData(
        int currentPreset,
        float dungeonScalableMultiplier,
        float dungeonFixedMultiplier,
        float peacefulMultiplier,
        float progressionScale,
        float armorTierScale,
        boolean extraLoot,
        float lootMultiplier,
        boolean overlimitEnchants,
        int enchantBonusLevels,
        float mobSpawnMultiplier,
        float difficultyBonus,
        float mobGearChance,
        float bossHpMultiplier,
        float bossDamageMultiplier,
        double compoundBase,
        double compoundRate
    ) {
        static SettingsData fromCurrent() {
            return new SettingsData(
                ProgressionSettings.currentPreset,
                ProgressionSettings.dungeonScalableMultiplier,
                ProgressionSettings.dungeonFixedMultiplier,
                ProgressionSettings.peacefulMultiplier,
                ProgressionSettings.progressionScale,
                ProgressionSettings.armorTierScale,
                ProgressionSettings.extraLoot,
                ProgressionSettings.lootMultiplier,
                ProgressionSettings.overlimitEnchants,
                ProgressionSettings.enchantBonusLevels,
                ProgressionSettings.mobSpawnMultiplier,
                ProgressionSettings.difficultyBonus,
                ProgressionSettings.mobGearChance,
                ProgressionSettings.bossHpMultiplier,
                ProgressionSettings.bossDamageMultiplier,
                ProgressionSettings.compoundBase,
                ProgressionSettings.compoundRate
            );
        }

        void apply() {
            ProgressionSettings.currentPreset = currentPreset;
            ProgressionSettings.dungeonScalableMultiplier = dungeonScalableMultiplier;
            ProgressionSettings.dungeonFixedMultiplier = dungeonFixedMultiplier;
            ProgressionSettings.peacefulMultiplier = peacefulMultiplier;
            ProgressionSettings.progressionScale = progressionScale;
            ProgressionSettings.armorTierScale = armorTierScale;
            ProgressionSettings.extraLoot = extraLoot;
            ProgressionSettings.lootMultiplier = lootMultiplier;
            ProgressionSettings.overlimitEnchants = overlimitEnchants;
            ProgressionSettings.enchantBonusLevels = enchantBonusLevels;
            ProgressionSettings.mobSpawnMultiplier = mobSpawnMultiplier;
            ProgressionSettings.difficultyBonus = difficultyBonus;
            ProgressionSettings.mobGearChance = mobGearChance;
            ProgressionSettings.bossHpMultiplier = bossHpMultiplier;
            ProgressionSettings.bossDamageMultiplier = bossDamageMultiplier;
            ProgressionSettings.compoundBase = compoundBase;
            ProgressionSettings.compoundRate = compoundRate;
        }
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            SettingsData data = GSON.fromJson(json, SettingsData.class);
            if (data != null) data.apply();
        } catch (Exception e) {
            ExampleMod.LOGGER.warn("Failed to load config, using defaults: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(SettingsData.fromCurrent()));
        } catch (IOException e) {
            ExampleMod.LOGGER.warn("Failed to save config: {}", e.getMessage());
        }
    }
}
