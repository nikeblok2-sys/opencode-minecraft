package net.example.firstmod.profile;

import java.util.List;

public class ProgressionProfilePresets {

    public static final ProgressionProfile LINEAR = new ProgressionProfile(
        "linear",
        "1 + dist / 10000",
        List.of(500.0, 2000.0, 8000.0, 20000.0),
        "dist / 2000",
        "dist / 20000",
        List.of(
            new ProgressionProfile.LootItemTier("minecraft:iron_ingot", 500, 1, 3),
            new ProgressionProfile.LootItemTier("minecraft:diamond", 2000, 1, 2),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_book", 2000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:experience_bottle", 1000, 3, 8),
            new ProgressionProfile.LootItemTier("minecraft:netherite_scrap", 5000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_golden_apple", 5000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:ancient_debris", 10000, 1, 2)
        )
    );

    public static final ProgressionProfile SQRT = new ProgressionProfile(
        "sqrt",
        "1 + sqrt(dist / 5000)",
        List.of(300.0, 1000.0, 4000.0, 10000.0),
        "1 + sqrt(dist / 5000) * 2",
        "sqrt(dist / 5000)",
        List.of(
            new ProgressionProfile.LootItemTier("minecraft:iron_ingot", 300, 1, 3),
            new ProgressionProfile.LootItemTier("minecraft:diamond", 1000, 1, 2),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_book", 1000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:experience_bottle", 500, 3, 8),
            new ProgressionProfile.LootItemTier("minecraft:netherite_scrap", 3000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_golden_apple", 3000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:ancient_debris", 6000, 1, 2)
        )
    );

    public static final ProgressionProfile POWER = new ProgressionProfile(
        "power",
        "1 + (dist / 3000) ^ 1.5",
        List.of(800.0, 3000.0, 12000.0, 30000.0),
        "1 + (dist / 3000) ^ 1.5",
        "(dist / 10000) ^ 1.5",
        List.of(
            new ProgressionProfile.LootItemTier("minecraft:iron_ingot", 800, 1, 3),
            new ProgressionProfile.LootItemTier("minecraft:diamond", 3000, 1, 2),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_book", 3000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:experience_bottle", 1500, 3, 8),
            new ProgressionProfile.LootItemTier("minecraft:netherite_scrap", 8000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_golden_apple", 8000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:ancient_debris", 15000, 1, 2)
        )
    );

    public static final ProgressionProfile SIGMOID = new ProgressionProfile(
        "sigmoid",
        "1 + 3 / (1 + exp(-(dist - 8000) / 1500))",
        List.of(500.0, 1500.0, 6000.0, 15000.0),
        "1 + 2 / (1 + exp(-(dist - 6000) / 2000))",
        "max(0, (dist - 2000) / 18000)",
        List.of(
            new ProgressionProfile.LootItemTier("minecraft:iron_ingot", 500, 1, 3),
            new ProgressionProfile.LootItemTier("minecraft:diamond", 1500, 1, 2),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_book", 1500, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:experience_bottle", 1000, 3, 8),
            new ProgressionProfile.LootItemTier("minecraft:netherite_scrap", 4000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:enchanted_golden_apple", 4000, 1, 1),
            new ProgressionProfile.LootItemTier("minecraft:ancient_debris", 8000, 1, 2)
        )
    );

    public static ProgressionProfile getByName(String name) {
        return switch (name.toLowerCase()) {
            case "linear" -> LINEAR;
            case "sqrt" -> SQRT;
            case "power" -> POWER;
            case "sigmoid" -> SIGMOID;
            default -> null;
        };
    }

    public static String[] getNames() {
        return new String[]{"linear", "sqrt", "power", "sigmoid"};
    }
}
