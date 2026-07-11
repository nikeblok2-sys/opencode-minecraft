package net.example.firstmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.example.firstmod.component.ProgressionData;
import net.example.firstmod.component.ProgressionStore;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.config.ProgressionConfig;
import net.example.firstmod.config.ProgressionSettings;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProgressionCommand {

    private static final Map<String, SettingDef> SETTINGS = new LinkedHashMap<>();

    private record SettingDef(String labelKey, String descKey) {}

    static {
        SETTINGS.put("progressionScale", new SettingDef("firstmod.setting.progressionScale.label", "firstmod.setting.progressionScale.desc"));
        SETTINGS.put("armorTierScale", new SettingDef("firstmod.setting.armorTierScale.label", "firstmod.setting.armorTierScale.desc"));
        SETTINGS.put("dungeonScalableMultiplier", new SettingDef("firstmod.setting.dungeonScalableMultiplier.label", "firstmod.setting.dungeonScalableMultiplier.desc"));
        SETTINGS.put("mobSpawnMultiplier", new SettingDef("firstmod.setting.mobSpawnMultiplier.label", "firstmod.setting.mobSpawnMultiplier.desc"));
        SETTINGS.put("difficultyBonus", new SettingDef("firstmod.setting.difficultyBonus.label", "firstmod.setting.difficultyBonus.desc"));
        SETTINGS.put("mobGearChance", new SettingDef("firstmod.setting.mobGearChance.label", "firstmod.setting.mobGearChance.desc"));
        SETTINGS.put("extraLoot", new SettingDef("firstmod.setting.extraLoot.label", "firstmod.setting.extraLoot.desc"));
        SETTINGS.put("lootMultiplier", new SettingDef("firstmod.setting.lootMultiplier.label", "firstmod.setting.lootMultiplier.desc"));
        SETTINGS.put("overlimitEnchants", new SettingDef("firstmod.setting.overlimitEnchants.label", "firstmod.setting.overlimitEnchants.desc"));
        SETTINGS.put("enchantBonusLevels", new SettingDef("firstmod.setting.enchantBonusLevels.label", "firstmod.setting.enchantBonusLevels.desc"));
        SETTINGS.put("bossHpMultiplier", new SettingDef("firstmod.setting.bossHpMultiplier.label", "firstmod.setting.bossHpMultiplier.desc"));
        SETTINGS.put("bossDamageMultiplier", new SettingDef("firstmod.setting.bossDamageMultiplier.label", "firstmod.setting.bossDamageMultiplier.desc"));
    }

    private static final SuggestionProvider<CommandSourceStack> SETTING_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(SETTINGS.keySet(), builder);

    private static final SuggestionProvider<CommandSourceStack> PRESET_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"base", "easy", "mid", "high", "custom"}, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("progression")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))

                .then(Commands.literal("addsp")
                    .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                            .executes(ctx -> executeAddSp(ctx)))))

                .then(Commands.literal("getsp")
                    .executes(ctx -> executeGetSp(ctx)))

                .then(Commands.literal("shop")
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(() ->
                            Component.translatable("firstmod.shop.open_instructions"), false);
                        return 1;
                    }))

                .then(Commands.literal("preset")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(PRESET_SUGGESTIONS)
                        .executes(ctx -> executePreset(ctx))))

                .then(Commands.literal("status")
                    .executes(ctx -> executeStatus(ctx)))

                .then(Commands.literal("get")
                    .then(Commands.argument("setting", StringArgumentType.word())
                        .suggests(SETTING_SUGGESTIONS)
                        .executes(ctx -> executeGet(ctx))))

                .then(Commands.literal("set")
                    .then(Commands.argument("setting", StringArgumentType.word())
                        .suggests(SETTING_SUGGESTIONS)
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 100f))
                            .executes(ctx -> executeSet(ctx)))))

                .then(Commands.literal("list")
                    .executes(ctx -> executeList(ctx)))
        );
    }

    private static int executeAddSp(CommandContext<CommandSourceStack> ctx) {
        try {
            var players = EntityArgument.getPlayers(ctx, "targets");
            int amount = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "amount");
            for (ServerPlayer p : players) {
                ProgressionStore store = ProgressionStore.getOrCreate(p.level().getServer());
                ProgressionData data = store.get(p.getUUID());
                data.addSp(amount);
                store.save();
                ServerPlayNetworking.send(p, new ProgressionPayloads.SyncPayload(
                    data.statLevels, data.availableSp(), data.totalEarnedSp, data.spentSp));
                p.sendSystemMessage(Component.translatable("firstmod.shop.sold", amount));
            }
            ctx.getSource().sendSuccess(() ->
                Component.literal("Added " + amount + " SP to " + players.size() + " player(s)"), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int executeGetSp(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        if (src.getEntity() instanceof ServerPlayer player) {
            ProgressionStore store = ProgressionStore.getOrCreate(player.level().getServer());
            ProgressionData data = store.get(player.getUUID());
            int sp = data.availableSp();
            int total = data.totalEarnedSp;
            var sb = new StringBuilder();
            sb.append("§6=== Progression Stats ===§r\n");
            sb.append("§7Available SP: §f").append(sp).append("§r\n");
            sb.append("§7Total earned: §f").append(total).append("§r\n");
            sb.append("§7Spent: §f").append(data.spentSp).append("§r\n");
            for (int i = 0; i < StatFormulas.STAT_COUNT; i++) {
                String label = Component.translatable(StatFormulas.STAT_LABEL_KEYS[i]).getString();
                double effect = StatFormulas.getEffect(i, data.statLevels[i]);
                sb.append("§7").append(label).append(": §fLv.").append(data.statLevels[i])
                    .append(" (").append(String.format("%.0f", (effect - 1.0) * 100)).append("%)§r\n");
            }
            ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
            return 1;
        }
        ctx.getSource().sendFailure(Component.literal("Player only command"));
        return 0;
    }

    private static int executePreset(CommandContext<CommandSourceStack> ctx) {
        String name = ctx.getArgument("name", String.class);
        int idx = switch (name.toLowerCase()) {
            case "base", "off", "0" -> 0;
            case "easy", "1" -> 1;
            case "mid", "2" -> 2;
            case "high", "3" -> 3;
            case "custom", "4" -> 4;
            default -> -1;
        };
        if (idx < 0) {
            ctx.getSource().sendFailure(Component.translatable("firstmod.cmd.preset.unknown"));
            return 0;
        }
        ProgressionSettings.applyPreset(idx);
        ProgressionConfig.save();
        ctx.getSource().sendSuccess(() ->
            Component.translatable("firstmod.cmd.preset.set", Component.translatable("firstmod.preset." + idx)), true);
        return 1;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> ctx) {
        MutableComponent result = Component.translatable("firstmod.cmd.status.header")
            .append(Component.literal("\n"))
            .append(Component.translatable("firstmod.cmd.status.preset", Component.translatable("firstmod.preset." + ProgressionSettings.currentPreset)))
            .append(Component.literal("\n"))
            .append(statusLine("progressionScale", ProgressionSettings.progressionScale))
            .append(statusLine("armorTierScale", ProgressionSettings.armorTierScale))
            .append(statusLine("dungeonScalableMultiplier", ProgressionSettings.dungeonScalableMultiplier))
            .append(statusLine("mobSpawnMultiplier", ProgressionSettings.mobSpawnMultiplier))
            .append(statusLine("difficultyBonus", ProgressionSettings.difficultyBonus))
            .append(statusLine("mobGearChance", ProgressionSettings.mobGearChance))
            .append(statusLine("lootMultiplier", ProgressionSettings.lootMultiplier))
            .append(statusBool("extraLoot", ProgressionSettings.extraLoot))
            .append(statusBool("overlimitEnchants", ProgressionSettings.overlimitEnchants))
            .append(statusLine("enchantBonusLevels", ProgressionSettings.enchantBonusLevels))
            .append(statusLine("bossHpMultiplier", ProgressionSettings.bossHpMultiplier))
            .append(statusLine("bossDamageMultiplier", ProgressionSettings.bossDamageMultiplier));
        ctx.getSource().sendSuccess(() -> result, false);
        return 1;
    }

    private static int executeGet(CommandContext<CommandSourceStack> ctx) {
        String name = ctx.getArgument("setting", String.class);
        SettingDef def = SETTINGS.get(name);
        if (def == null) {
            ctx.getSource().sendFailure(Component.translatable("firstmod.cmd.get.unknown", name));
            return 0;
        }
        float val = getFieldValue(name);
        Component display;
        if (name.equals("extraLoot") || name.equals("overlimitEnchants")) {
            display = val > 0.5f
                ? Component.translatable("firstmod.cmd.status.enabled")
                : Component.translatable("firstmod.cmd.status.disabled");
        } else {
            display = Component.literal(String.format("%.2f", val));
        }
        ctx.getSource().sendSuccess(() ->
            Component.translatable("firstmod.cmd.get.format",
                Component.translatable(def.labelKey), display, Component.translatable(def.descKey)), false);
        return 1;
    }

    private static int executeSet(CommandContext<CommandSourceStack> ctx) {
        String name = ctx.getArgument("setting", String.class);
        float value = ctx.getArgument("value", Float.class);
        SettingDef def = SETTINGS.get(name);
        if (def == null) {
            ctx.getSource().sendFailure(Component.translatable("firstmod.cmd.get.unknown", name));
            return 0;
        }
        setFieldValue(name, value);
        ProgressionConfig.save();
        Component display;
        if (name.equals("extraLoot") || name.equals("overlimitEnchants")) {
            display = value > 0.5f
                ? Component.translatable("firstmod.cmd.status.enabled")
                : Component.translatable("firstmod.cmd.status.disabled");
        } else {
            display = Component.literal(String.format("%.2f", value));
        }
        ctx.getSource().sendSuccess(() ->
            Component.translatable("firstmod.cmd.set.format", Component.translatable(def.labelKey), display), true);
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) {
        MutableComponent result = Component.translatable("firstmod.cmd.list.header");
        for (Map.Entry<String, SettingDef> e : SETTINGS.entrySet()) {
            String key = e.getKey();
            SettingDef def = e.getValue();
            float val = getFieldValue(key);
            Component valStr;
            if (key.equals("extraLoot") || key.equals("overlimitEnchants")) {
                valStr = Component.translatable(val > 0.5f ? "firstmod.cmd.status.on" : "firstmod.cmd.status.off");
            } else {
                valStr = Component.literal(String.format("%.2f", val));
            }
            result.append(Component.literal("\n"))
                .append(Component.translatable("firstmod.cmd.list.entry",
                    Component.literal(key),
                    Component.translatable(def.labelKey),
                    valStr,
                    Component.translatable(def.descKey)));
        }
        ctx.getSource().sendSuccess(() -> result, false);
        return 1;
    }

    private static MutableComponent statusLine(String key, float val) {
        SettingDef def = SETTINGS.get(key);
        MutableComponent label = def != null ? Component.translatable(def.labelKey) : Component.literal(key);
        return Component.literal("§7").append(label).append(Component.literal(": §f" + String.format("%.2f", val) + "§r\n"));
    }

    private static MutableComponent statusLine(String key, int val) {
        SettingDef def = SETTINGS.get(key);
        MutableComponent label = def != null ? Component.translatable(def.labelKey) : Component.literal(key);
        return Component.literal("§7").append(label).append(Component.literal(": §f" + val + "§r\n"));
    }

    private static MutableComponent statusBool(String key, boolean val) {
        SettingDef def = SETTINGS.get(key);
        MutableComponent label = def != null ? Component.translatable(def.labelKey) : Component.literal(key);
        MutableComponent value = val
            ? Component.translatable("firstmod.cmd.status.on")
            : Component.translatable("firstmod.cmd.status.off");
        return Component.literal("§7").append(label).append(Component.literal(": ")).append(value).append(Component.literal("§r\n"));
    }

    private static float getFieldValue(String name) {
        return switch (name) {
            case "progressionScale" -> ProgressionSettings.progressionScale;
            case "armorTierScale" -> ProgressionSettings.armorTierScale;
            case "dungeonScalableMultiplier" -> ProgressionSettings.dungeonScalableMultiplier;
            case "mobSpawnMultiplier" -> ProgressionSettings.mobSpawnMultiplier;
            case "difficultyBonus" -> ProgressionSettings.difficultyBonus;
            case "mobGearChance" -> ProgressionSettings.mobGearChance;
            case "extraLoot" -> ProgressionSettings.extraLoot ? 1 : 0;
            case "lootMultiplier" -> ProgressionSettings.lootMultiplier;
            case "overlimitEnchants" -> ProgressionSettings.overlimitEnchants ? 1 : 0;
            case "enchantBonusLevels" -> ProgressionSettings.enchantBonusLevels;
            case "bossHpMultiplier" -> ProgressionSettings.bossHpMultiplier;
            case "bossDamageMultiplier" -> ProgressionSettings.bossDamageMultiplier;
            default -> 0;
        };
    }

    private static void setFieldValue(String name, float value) {
        switch (name) {
            case "progressionScale" -> ProgressionSettings.progressionScale = value;
            case "armorTierScale" -> ProgressionSettings.armorTierScale = value;
            case "dungeonScalableMultiplier" -> ProgressionSettings.dungeonScalableMultiplier = value;
            case "mobSpawnMultiplier" -> ProgressionSettings.mobSpawnMultiplier = value;
            case "difficultyBonus" -> ProgressionSettings.difficultyBonus = value;
            case "mobGearChance" -> ProgressionSettings.mobGearChance = value;
            case "extraLoot" -> ProgressionSettings.extraLoot = value > 0.5f;
            case "lootMultiplier" -> ProgressionSettings.lootMultiplier = value;
            case "overlimitEnchants" -> ProgressionSettings.overlimitEnchants = value > 0.5f;
            case "enchantBonusLevels" -> ProgressionSettings.enchantBonusLevels = Math.round(value);
            case "bossHpMultiplier" -> ProgressionSettings.bossHpMultiplier = value;
            case "bossDamageMultiplier" -> ProgressionSettings.bossDamageMultiplier = value;
        }
    }
}
