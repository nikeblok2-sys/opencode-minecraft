package net.example.firstmod.component;

import net.example.firstmod.ExampleMod;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class StatEffectHelper {

    public static void applyAttributes(ServerPlayer player) {
        var server = player.level().getServer();
        if (server == null) return;
        ProgressionStore store = ProgressionStore.getOrCreate(server);
        ProgressionData data = store.get(player.getUUID());

        for (StatRegistry.StatDef def : StatRegistry.getAll()) {
            if (def.attribute() == null) continue;
            int level = data.statLevels[def.index()];
            apply(player, def, level);
        }
    }

    private static void apply(ServerPlayer player, StatRegistry.StatDef def, int level) {
        Holder<Attribute> holder = def.attribute();
        AttributeInstance attr = player.getAttribute(holder);
        if (attr == null) return;
        Identifier id = ExampleMod.id(def.key());
        attr.removeModifier(id);
        if (level > 0) {
            double value = def.perLevelBonus() * level;
            attr.addPermanentModifier(new AttributeModifier(id, value, def.operation()));
            if (def.key().equals("max_health") && player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }
}
