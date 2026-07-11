package net.example.firstmod.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProgressionProfileManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ProgressionProfile currentProfile;
    private static Path profilePath;

    public static void init(MinecraftServer server) {
        Path worldDir = server.getServerDirectory().resolve("world");
        profilePath = worldDir.resolve("firstmod_progression.json");
        load();
    }

    public static ProgressionProfile getProfile() {
        if (currentProfile == null) {
            currentProfile = ProgressionProfilePresets.LINEAR;
        }
        return currentProfile;
    }

    public static void setProfile(ProgressionProfile profile) {
        currentProfile = profile;
        save();
    }

    public static boolean setProfileByName(String name) {
        ProgressionProfile preset = ProgressionProfilePresets.getByName(name);
        if (preset != null) {
            setProfile(preset);
            return true;
        }
        return false;
    }

    public static void load() {
        if (profilePath == null || !Files.exists(profilePath)) {
            currentProfile = ProgressionProfilePresets.LINEAR;
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(profilePath)) {
            ProgressionProfile data = GSON.fromJson(reader, ProgressionProfile.class);
            if (data != null) {
                currentProfile = data;
            } else {
                currentProfile = ProgressionProfilePresets.LINEAR;
            }
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Failed to load progression profile", e);
            currentProfile = ProgressionProfilePresets.LINEAR;
        }
    }

    public static void save() {
        if (profilePath == null) return;
        try {
            Files.createDirectories(profilePath.getParent());
            try (Writer writer = Files.newBufferedWriter(profilePath)) {
                GSON.toJson(currentProfile, writer);
            }
        } catch (IOException e) {
            ExampleMod.LOGGER.error("Failed to save progression profile", e);
        }
    }

    public static String[] getAvailableProfiles() {
        return ProgressionProfilePresets.getNames();
    }

    public static boolean setCustomProfile(String scalingFormula, String enchantBonus, String lootChanceBase,
                                            double[] equipThresholds,
                                            String[] lootItems) {
        try {
            ExpressionEvaluator.eval(scalingFormula, 1000);
        } catch (Exception e) {
            return false;
        }
        var thresholds = new java.util.ArrayList<Double>();
        for (double t : equipThresholds) thresholds.add(t);

        var tiers = new java.util.ArrayList<ProgressionProfile.LootItemTier>();
        currentProfile = new ProgressionProfile(
            "custom", scalingFormula, thresholds, enchantBonus, lootChanceBase, tiers
        );
        save();
        return true;
    }
}
