package net.example.firstmod.profile;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.example.firstmod.ExampleMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class ProgressionProfileCommand {

    private static final SuggestionProvider<CommandSourceStack> PROFILE_SUGGESTIONS =
        (ctx, builder) -> SharedSuggestionProvider.suggest(ProgressionProfileManager.getAvailableProfiles(), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("progression-profile")
                .then(Commands.literal("set")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(PROFILE_SUGGESTIONS)
                        .executes(ctx -> executeSet(ctx))))
                .then(Commands.literal("status")
                    .executes(ctx -> executeStatus(ctx)))
                .then(Commands.literal("list")
                    .executes(ctx -> executeList(ctx)))
        );
    }

    private static int executeSet(CommandContext<CommandSourceStack> ctx) {
        String name = ctx.getArgument("name", String.class);
        if (ProgressionProfileManager.setProfileByName(name)) {
            ctx.getSource().sendSuccess(() ->
                Component.literal("Progression profile set to: " + name), true);
            return 1;
        }
        ctx.getSource().sendFailure(Component.literal("Unknown profile: " + name + ". Available: " + String.join(", ", ProgressionProfileManager.getAvailableProfiles())));
        return 0;
    }

    private static int executeStatus(CommandContext<CommandSourceStack> ctx) {
        ProgressionProfile profile = ProgressionProfileManager.getProfile();
        var sb = new StringBuilder();
        sb.append("§6=== Progression Profile ===§r\n");
        sb.append("§7Name: §f").append(profile.profileName()).append("§r\n");
        sb.append("§7Scaling: §f").append(profile.scalingFormula()).append("§r\n");
        sb.append("§7Enchant: §f").append(profile.enchantBonus()).append("§r\n");
        sb.append("§7Loot chance: §f").append(profile.lootChanceBase()).append("§r\n");
        if (profile.equipThresholds() != null) {
            sb.append("§7Equip tiers: §f").append(profile.equipThresholds().toString()).append("§r\n");
        }
        if (profile.lootItemTiers() != null) {
            sb.append("§7Loot items: §f").append(profile.lootItemTiers().size()).append(" entries§r\n");
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) {
        String[] names = ProgressionProfileManager.getAvailableProfiles();
        var sb = new StringBuilder();
        sb.append("§6Available profiles:§r\n");
        for (String name : names) {
            sb.append(" §7- §f").append(name).append("§r\n");
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }
}
