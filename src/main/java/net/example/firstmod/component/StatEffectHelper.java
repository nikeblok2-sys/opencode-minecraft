package net.example.firstmod.component;

import net.example.firstmod.ExampleMod;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StatEffectHelper {

    private static final Identifier STR_ID = ExampleMod.id("str_damage");
    private static final Identifier AGI_ID = ExampleMod.id("agi_speed");
    private static final Identifier VIT_ID = ExampleMod.id("vit_health");
    private static final Identifier LUK_ID = ExampleMod.id("luk_luck");

    public static void applyAttributes(ServerPlayer player) {
        var server = player.level().getServer();
        if (server == null) return;
        ProgressionStore store = ProgressionStore.getOrCreate(server);
        ProgressionData data = store.get(player.getUUID());
        applyStr(player, data.statLevels[StatFormulas.STR]);
        applyAgi(player, data.statLevels[StatFormulas.AGI]);
        applyVit(player, data.statLevels[StatFormulas.VIT]);
        applyLuk(player, data.statLevels[StatFormulas.LUK]);
    }

    private static void applyStr(ServerPlayer player, int level) {
        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attr == null) return;
        attr.removeModifier(STR_ID);
        if (level > 0) {
            double boost = StatFormulas.getEffect(StatFormulas.STR, level) - 1.0;
            attr.addPermanentModifier(new AttributeModifier(
                STR_ID, boost, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void applyAgi(ServerPlayer player, int level) {
        AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        attr.removeModifier(AGI_ID);
        if (level > 0) {
            double boost = StatFormulas.getEffect(StatFormulas.AGI, level) - 1.0;
            attr.addPermanentModifier(new AttributeModifier(
                AGI_ID, boost, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    private static void applyVit(ServerPlayer player, int level) {
        AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;
        attr.removeModifier(VIT_ID);
        if (level > 0) {
            double boost = StatFormulas.getEffect(StatFormulas.VIT, level) - 1.0;
            attr.addPermanentModifier(new AttributeModifier(
                VIT_ID, boost, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    private static void applyLuk(ServerPlayer player, int level) {
        AttributeInstance attr = player.getAttribute(Attributes.LUCK);
        if (attr == null) return;
        attr.removeModifier(LUK_ID);
        if (level > 0) {
            double boost = StatFormulas.getEffect(StatFormulas.LUK, level) - 1.0;
            attr.addPermanentModifier(new AttributeModifier(
                LUK_ID, boost * 10.0, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
