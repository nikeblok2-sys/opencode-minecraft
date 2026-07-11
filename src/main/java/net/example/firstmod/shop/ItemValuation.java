package net.example.firstmod.shop;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.HashMap;
import java.util.Map;

public class ItemValuation {

    private static final Map<Item, Double> BASE_VALUES = new HashMap<>();

    static {
        put(Items.DIRT, 1.0);
        put(Items.COBBLESTONE, 1.0);
        put(Items.STONE, 1.0);
        put(Items.GRAVEL, 1.0);
        put(Items.SAND, 1.0);
        put(Items.NETHERRACK, 1.0);
        put(Items.OAK_LOG, 2.0);
        put(Items.SPRUCE_LOG, 2.0);
        put(Items.BIRCH_LOG, 2.0);
        put(Items.JUNGLE_LOG, 2.0);
        put(Items.ACACIA_LOG, 2.0);
        put(Items.DARK_OAK_LOG, 2.0);
        put(Items.MANGROVE_LOG, 2.0);
        put(Items.CHERRY_LOG, 2.0);
        put(Items.OAK_PLANKS, 2.0);
        put(Items.COAL, 5.0);
        put(Items.COPPER_INGOT, 8.0);
        put(Items.IRON_INGOT, 15.0);
        put(Items.GOLD_INGOT, 25.0);
        put(Items.LAPIS_LAZULI, 8.0);
        put(Items.REDSTONE, 6.0);
        put(Items.EMERALD, 20.0);
        put(Items.DIAMOND, 50.0);
        put(Items.NETHERITE_SCRAP, 100.0);
        put(Items.NETHERITE_INGOT, 300.0);
        put(Items.COAL_BLOCK, 45.0);
        put(Items.RAW_COPPER_BLOCK, 72.0);
        put(Items.IRON_BLOCK, 135.0);
        put(Items.GOLD_BLOCK, 225.0);
        put(Items.DIAMOND_BLOCK, 450.0);
        put(Items.NETHERITE_BLOCK, 2700.0);
        put(Items.AMETHYST_SHARD, 10.0);
        put(Items.QUARTZ, 5.0);
        put(Items.GLOWSTONE_DUST, 8.0);
        put(Items.OBSIDIAN, 10.0);
        put(Items.CRYING_OBSIDIAN, 20.0);
        put(Items.SHULKER_SHELL, 50.0);
        put(Items.ENDER_PEARL, 15.0);
        put(Items.FEATHER, 2.0);
        put(Items.BONE, 2.0);
        put(Items.STRING, 2.0);
        put(Items.GUNPOWDER, 5.0);
        put(Items.BLAZE_ROD, 30.0);
        put(Items.BLAZE_POWDER, 10.0);
        put(Items.GHAST_TEAR, 40.0);
        put(Items.MAGMA_CREAM, 15.0);
        put(Items.SLIME_BALL, 8.0);
        put(Items.SPIDER_EYE, 4.0);
        put(Items.ROTTEN_FLESH, 1.0);
        put(Items.PHANTOM_MEMBRANE, 25.0);
        put(Items.NAUTILUS_SHELL, 30.0);
        put(Items.HEART_OF_THE_SEA, 100.0);
        put(Items.TURTLE_SCUTE, 30.0);
        put(Items.ARMADILLO_SCUTE, 20.0);
        put(Items.TOTEM_OF_UNDYING, 500.0);
        put(Items.TRIDENT, 200.0);
        put(Items.ELYTRA, 500.0);
        put(Items.DRAGON_EGG, 2000.0);
        put(Items.DRAGON_BREATH, 100.0);
        put(Items.SHULKER_BOX, 100.0);
        put(Items.WITHER_SKELETON_SKULL, 150.0);
        put(Items.NETHER_STAR, 400.0);
        put(Items.ECHO_SHARD, 50.0);
        put(Items.DISC_FRAGMENT_5, 30.0);
        put(Items.SPONGE, 15.0);
        put(Items.EXPERIENCE_BOTTLE, 30.0);
        put(Items.SADDLE, 100.0);
        put(Items.NAME_TAG, 50.0);
        put(Items.MUSIC_DISC_13, 20.0);
        put(Items.MUSIC_DISC_CAT, 20.0);
        put(Items.MUSIC_DISC_OTHERSIDE, 200.0);
        put(Items.ANCIENT_DEBRIS, 150.0);
        put(Items.TURTLE_HELMET, 30.0);
        put(Items.GOLDEN_APPLE, 50.0);
        put(Items.ENCHANTED_GOLDEN_APPLE, 300.0);
        put(Items.HEAVY_CORE, 300.0);
    }

    private static void put(Item item, double value) {
        BASE_VALUES.put(item, value);
    }

    public static double getValue(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Item item = stack.getItem();
        double base = BASE_VALUES.getOrDefault(item, 0.5);
        double total = base * stack.getCount();
        boolean hasEnchants = EnchantmentHelper.hasAnyEnchantments(stack);
        if (hasEnchants) {
            total *= 1.5;
        }
        var rarity = stack.get(DataComponents.RARITY);
        if (rarity != null) {
            total *= switch (rarity) {
                case UNCOMMON -> 1.2;
                case RARE -> 1.5;
                case EPIC -> 2.0;
                default -> 1.0;
            };
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
