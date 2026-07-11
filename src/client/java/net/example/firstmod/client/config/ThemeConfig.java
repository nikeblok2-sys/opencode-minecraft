package net.example.firstmod.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.example.firstmod.ExampleMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ThemeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("firstmod-theme.json");

    private static int background = 0x2D4A3E;
    private static int card = 0x3A5A4E;
    private static int accentPrimary = 0x88CCAA;
    private static int accentSecondary = 0xFFD700;
    private static int textPrimary = 0xFFFFFFFF;
    private static int textSecondary = 0xFFCCCCCC;

    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try {
            ThemeData d = GSON.fromJson(Files.readString(PATH), ThemeData.class);
            if (d != null) {
                background = d.background;
                card = d.card;
                accentPrimary = d.accentPrimary;
                accentSecondary = d.accentSecondary;
                textPrimary = d.textPrimary;
                textSecondary = d.textSecondary;
            }
        } catch (Exception e) {
            ExampleMod.LOGGER.warn("Failed to load theme config: {}", e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(new ThemeData()));
        } catch (IOException e) {
            ExampleMod.LOGGER.warn("Failed to save theme config: {}", e.getMessage());
        }
    }

    public static int getBackground() { return background; }
    public static int getCard() { return card; }
    public static int getAccentPrimary() { return accentPrimary; }
    public static int getAccentSecondary() { return accentSecondary; }
    public static int getTextPrimary() { return textPrimary; }
    public static int getTextSecondary() { return textSecondary; }

    public static void setBackground(int v) { background = v; }
    public static void setCard(int v) { card = v; }
    public static void setAccentPrimary(int v) { accentPrimary = v; }
    public static void setAccentSecondary(int v) { accentSecondary = v; }
    public static void setTextPrimary(int v) { textPrimary = v; }
    public static void setTextSecondary(int v) { textSecondary = v; }

    private static class ThemeData {
        public int background;
        public int card;
        public int accentPrimary;
        public int accentSecondary;
        public int textPrimary;
        public int textSecondary;
        public ThemeData() {
            this.background = ThemeConfig.getBackground();
            this.card = ThemeConfig.getCard();
            this.accentPrimary = ThemeConfig.getAccentPrimary();
            this.accentSecondary = ThemeConfig.getAccentSecondary();
            this.textPrimary = ThemeConfig.getTextPrimary();
            this.textSecondary = ThemeConfig.getTextSecondary();
        }
    }
}