package net.example.firstmod.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HudConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("firstmod-hud.json");

    private static int hudX = 4;
    private static int hudY = 4;
    private static int hudAnchor = 0;
    private static boolean showSp = true;
    private static boolean showStats = true;
    private static boolean showDistance = true;
    private static float hudScale = 1.0f;

    public static int x() { return hudX; }
    public static int y() { return hudY; }
    public static int anchor() { return hudAnchor; }
    public static boolean showSp() { return showSp; }
    public static boolean showStats() { return showStats; }
    public static boolean showDistance() { return showDistance; }
    public static float scale() { return hudScale; }

    public static void setX(int v) { hudX = v; }
    public static void setY(int v) { hudY = v; }
    public static void setAnchor(int v) { hudAnchor = v; }
    public static void setShowSp(boolean v) { showSp = v; }
    public static void setShowStats(boolean v) { showStats = v; }
    public static void setShowDistance(boolean v) { showDistance = v; }
    public static void setScale(float v) { hudScale = Math.max(0.5f, Math.min(v, 2.0f)); }

    private record Data(int x, int y, int anc, Boolean sp, Boolean stats, Boolean dist, Float scale) {
        static Data current() {
            return new Data(hudX, hudY, hudAnchor, showSp, showStats, showDistance, hudScale);
        }
        void apply() {
            hudX = x; hudY = y; hudAnchor = anc;
            if (sp != null) showSp = sp;
            if (stats != null) showStats = stats;
            if (dist != null) showDistance = dist;
            if (scale != null) hudScale = scale;
        }
    }

    public static void load() {
        if (!Files.exists(PATH)) { save(); return; }
        try {
            Data d = GSON.fromJson(Files.readString(PATH), Data.class);
            if (d != null) d.apply();
        } catch (Exception e) {
            ExampleMod.LOGGER.warn("Failed to load HUD config: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(Data.current()));
        } catch (IOException e) {
            ExampleMod.LOGGER.warn("Failed to save HUD config: {}", e.getMessage());
        }
    }
}
