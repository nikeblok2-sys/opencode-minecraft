package net.example.firstmod.client.theme;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.example.firstmod.ExampleMod;
import net.fabricmc.loader.api.FabricLoader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private static final Map<String, Integer> colorCache = new HashMap<>();
    private static boolean loaded;
    private static String currentTheme = "default";

    static {
        load("default");
    }

    public static void load(String themeName) {
        colorCache.clear();
        loaded = false;
        currentTheme = themeName;

        try {
            InputStream is = ThemeManager.class.getClassLoader()
                .getResourceAsStream("assets/firstmod/theme/" + themeName + ".json");
            if (is == null) return;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject colors = obj.getAsJsonObject("colors");
                for (String key : colors.keySet()) {
                    String hex = colors.get(key).getAsString();
                    colorCache.put(key, (int) Long.parseLong(hex.substring(2), 16));
                }
            }
            loaded = true;
        } catch (Exception e) {
            loaded = false;
            ExampleMod.LOGGER.warn("Failed to load theme: {}", e.getMessage());
        }
    }

    public static int color(String name, int fallback) {
        return loaded ? colorCache.getOrDefault(name, fallback) : fallback;
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}