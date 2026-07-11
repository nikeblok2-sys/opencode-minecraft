package net.example.firstmod.scaling;

import net.example.firstmod.ExampleMod;
import net.example.firstmod.profile.ExpressionEvaluator;
import net.example.firstmod.profile.ProgressionProfile;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MobScalingHelper {

    private record GearTier(Item mainhand, Item helmet, Item chestplate, Item leggings, Item boots) {}

    private static final GearTier[] GEAR_TIERS = {
        new GearTier(Items.WOODEN_SWORD,   Items.LEATHER_HELMET,      Items.LEATHER_CHESTPLATE,   Items.LEATHER_LEGGINGS,   Items.LEATHER_BOOTS),
        new GearTier(Items.STONE_SWORD,    Items.CHAINMAIL_HELMET,    Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS),
        new GearTier(Items.IRON_SWORD,     Items.IRON_HELMET,         Items.IRON_CHESTPLATE,      Items.IRON_LEGGINGS,      Items.IRON_BOOTS),
        new GearTier(Items.DIAMOND_SWORD,  Items.DIAMOND_HELMET,      Items.DIAMOND_CHESTPLATE,   Items.DIAMOND_LEGGINGS,   Items.DIAMOND_BOOTS),
        new GearTier(Items.NETHERITE_SWORD, Items.NETHERITE_HELMET,   Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS),
    };

    public static void applyScaling(Mob mob, ServerLevel level, DifficultyInstance difficulty) {
        double dist = Math.sqrt(mob.getX() * mob.getX() + mob.getZ() * mob.getZ());
        ProgressionProfile profile = ProgressionProfileManager.getProfile();

        double mult = evaluateFormula(profile.scalingFormula(), dist);
        if (mult > 1.01) {
            var hpAttr = mob.getAttribute(Attributes.MAX_HEALTH);
            if (hpAttr != null) {
                double newMax = hpAttr.getBaseValue() * mult;
                hpAttr.setBaseValue(newMax);
                mob.setHealth((float) newMax);
            }

            var dmgAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmgAttr != null) {
                dmgAttr.setBaseValue(dmgAttr.getBaseValue() * mult);
            }
        }

        tryEquipGear(mob, dist, difficulty, profile, level);
    }

    private static void tryEquipGear(Mob mob, double dist, DifficultyInstance difficulty, ProgressionProfile profile, ServerLevel level) {
        if (profile.equipThresholds() == null || profile.equipThresholds().isEmpty()) return;

        List<Double> thresholds = profile.equipThresholds();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR && slot != EquipmentSlot.MAINHAND) continue;
            if (!mob.getItemBySlot(slot).isEmpty()) continue;

            int bestTier = -1;
            for (int t = 0; t < thresholds.size() && t < GEAR_TIERS.length; t++) {
                double chance = Math.min(1.0, dist / thresholds.get(t));
                if (rng.nextDouble() < chance) {
                    bestTier = t;
                }
            }

            if (bestTier >= 0 && bestTier < GEAR_TIERS.length) {
                Item item = itemForSlot(slot, bestTier);
                if (item != null) {
                    ItemStack gear = new ItemStack(item);
                    EnchantmentHelper.enchantItemFromProvider(
                        gear, level.registryAccess(),
                        VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT,
                        difficulty, level.getRandom()
                    );
                    mob.setItemSlot(slot, gear);
                }
            }
        }
    }

    private static Item itemForSlot(EquipmentSlot slot, int tierIdx) {
        if (tierIdx < 0 || tierIdx >= GEAR_TIERS.length) return null;
        GearTier t = GEAR_TIERS[tierIdx];
        return switch (slot) {
            case MAINHAND -> t.mainhand();
            case HEAD -> t.helmet();
            case CHEST -> t.chestplate();
            case LEGS -> t.leggings();
            case FEET -> t.boots();
            default -> null;
        };
    }

    private static double evaluateFormula(String formula, double dist) {
        try {
            return ExpressionEvaluator.eval(formula, dist);
        } catch (Exception e) {
            ExampleMod.LOGGER.error("Failed to evaluate scaling formula '{}' at dist={}: {}", formula, dist, e.getMessage());
            return 1.0;
        }
    }
}
