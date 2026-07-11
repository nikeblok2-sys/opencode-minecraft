package net.example.firstmod.scaling;

import net.example.firstmod.ExampleMod;
import net.example.firstmod.profile.ExpressionEvaluator;
import net.example.firstmod.profile.ProgressionProfile;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LootHelper {

    private record UpgradeEntry(int minDist, Item result) {}

    private static final Map<Item, UpgradeEntry> UPGRADES = new HashMap<>();

    static {
        addUpgrade(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, 500);
        addUpgrade(Items.CHAINMAIL_HELMET, Items.IRON_HELMET, 1000);
        addUpgrade(Items.IRON_HELMET, Items.DIAMOND_HELMET, 3000);
        addUpgrade(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, 8000);

        addUpgrade(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, 500);
        addUpgrade(Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, 1000);
        addUpgrade(Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, 3000);
        addUpgrade(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE, 8000);

        addUpgrade(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, 500);
        addUpgrade(Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, 1000);
        addUpgrade(Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, 3000);
        addUpgrade(Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS, 8000);

        addUpgrade(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, 500);
        addUpgrade(Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, 1000);
        addUpgrade(Items.IRON_BOOTS, Items.DIAMOND_BOOTS, 3000);
        addUpgrade(Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS, 8000);

        addUpgrade(Items.WOODEN_SWORD, Items.STONE_SWORD, 300);
        addUpgrade(Items.STONE_SWORD, Items.IRON_SWORD, 1000);
        addUpgrade(Items.IRON_SWORD, Items.DIAMOND_SWORD, 3000);
        addUpgrade(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD, 8000);

        addUpgrade(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, 300);
        addUpgrade(Items.STONE_PICKAXE, Items.IRON_PICKAXE, 1000);
        addUpgrade(Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, 3000);
        addUpgrade(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE, 8000);

        addUpgrade(Items.WOODEN_AXE, Items.STONE_AXE, 300);
        addUpgrade(Items.STONE_AXE, Items.IRON_AXE, 1000);
        addUpgrade(Items.IRON_AXE, Items.DIAMOND_AXE, 3000);
        addUpgrade(Items.DIAMOND_AXE, Items.NETHERITE_AXE, 8000);

        addUpgrade(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, 300);
        addUpgrade(Items.STONE_SHOVEL, Items.IRON_SHOVEL, 1000);
        addUpgrade(Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, 3000);
        addUpgrade(Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL, 8000);

        addUpgrade(Items.WOODEN_HOE, Items.STONE_HOE, 300);
        addUpgrade(Items.STONE_HOE, Items.IRON_HOE, 1000);
        addUpgrade(Items.IRON_HOE, Items.DIAMOND_HOE, 3000);
        addUpgrade(Items.DIAMOND_HOE, Items.NETHERITE_HOE, 8000);

        addUpgrade(Items.IRON_INGOT, Items.DIAMOND, 2000);
        addUpgrade(Items.GOLD_INGOT, Items.DIAMOND, 4000);
        addUpgrade(Items.DIAMOND, Items.NETHERITE_INGOT, 8000);
    }

    private static void addUpgrade(Item from, Item to, int minDist) {
        UPGRADES.put(from, new UpgradeEntry(minDist, to));
    }

    public static void modifyLoot(ObjectArrayList<ItemStack> items, double dist, ServerLevel level) {
        ProgressionProfile profile = ProgressionProfileManager.getProfile();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            Item upgraded = tryUpgrade(stack.getItem(), (int) dist);
            if (upgraded != null) {
                items.set(i, stack.transmuteCopy(upgraded));
                stack = items.get(i);
            }
        }

        addBonusLoot(items, dist, profile, level, rng);
    }

    private static void addBonusLoot(ObjectArrayList<ItemStack> items, double dist, ProgressionProfile profile, ServerLevel level, ThreadLocalRandom rng) {
        if (profile.lootItemTiers() == null) return;

        double chanceMult = evaluateChance(profile.lootChanceBase(), dist);

        for (var tier : profile.lootItemTiers()) {
            if (dist < tier.baseDist()) continue;

            double chance = chanceMult;
            if (rng.nextDouble() < chance) {
                int count = tier.minAmount() + (tier.maxAmount() > tier.minAmount() ? rng.nextInt(tier.maxAmount() - tier.minAmount() + 1) : 0);
                Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(tier.itemId()));
                if (item != null && item != Items.AIR) {
                    ItemStack bonus = new ItemStack(item, count);
                    if (tier.itemId().contains("enchanted_book")) {
                        int levels = 10 + (int)(dist / 5000.0);
                        EnchantmentHelper.enchantItem(level.getRandom(), bonus, levels, level.registryAccess(), Optional.empty());
                    }
                    items.add(bonus);
                }
            }
        }
    }

    private static Item tryUpgrade(Item item, int dist) {
        UpgradeEntry entry = UPGRADES.get(item);
        if (entry != null && dist >= entry.minDist()) {
            return entry.result();
        }
        return null;
    }

    private static double evaluateChance(String formula, double dist) {
        try {
            return Math.min(1.0, Math.max(0, ExpressionEvaluator.eval(formula, dist)));
        } catch (Exception e) {
            return 0;
        }
    }
}
