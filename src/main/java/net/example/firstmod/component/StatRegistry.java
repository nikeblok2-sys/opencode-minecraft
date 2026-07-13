package net.example.firstmod.component;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class StatRegistry {

    public enum Theme {
        COMBAT, DEFENSE, GATHERING, INTERACTION, ADVENTURE
    }

    public record StatDef(
        int index,
        String key,
        Theme theme,
        Holder<Attribute> attribute,
        AttributeModifier.Operation operation,
        double perLevelBonus,
        boolean isPercent,
        String icon,
        int color,
        String labelKey,
        String descKey
    ) {}

    private static final StatDef[] STATS = buildStats();

    private static StatDef[] buildStats() {
        return new StatDef[] {
            // COMBAT
            new StatDef(0,  "attack_damage",     Theme.COMBAT,     Attributes.ATTACK_DAMAGE,         AttributeModifier.Operation.ADD_VALUE,          0.5,  false, "\u2694", 0xFFFF4444, "firstmod.stat.attack_damage.label",     "firstmod.stat.attack_damage.desc"),
            new StatDef(1,  "attack_speed",       Theme.COMBAT,     Attributes.ATTACK_SPEED,         AttributeModifier.Operation.ADD_VALUE,          0.05, false, "\u26A1", 0xFFFF8844, "firstmod.stat.attack_speed.label",       "firstmod.stat.attack_speed.desc"),
            new StatDef(2,  "attack_knockback",   Theme.COMBAT,     Attributes.ATTACK_KNOCKBACK,     AttributeModifier.Operation.ADD_VALUE,          0.1,  false, "\u2B50", 0xFFFFCC44, "firstmod.stat.attack_knockback.label",   "firstmod.stat.attack_knockback.desc"),
            new StatDef(3,  "crit_chance",        Theme.COMBAT,     null,                            AttributeModifier.Operation.ADD_VALUE,          0.5,  true,  "\u2728", 0xFFFFAA00, "firstmod.stat.crit_chance.label",        "firstmod.stat.crit_chance.desc"),

            // DEFENSE
            new StatDef(4,  "max_health",         Theme.DEFENSE,    Attributes.MAX_HEALTH,           AttributeModifier.Operation.ADD_VALUE,          1.0,  false, "\u2764", 0xFFFF6688, "firstmod.stat.max_health.label",         "firstmod.stat.max_health.desc"),
            new StatDef(5,  "armor",              Theme.DEFENSE,    Attributes.ARMOR,                AttributeModifier.Operation.ADD_VALUE,          0.5,  false, "\u25C8", 0xAAAAAA,    "firstmod.stat.armor.label",              "firstmod.stat.armor.desc"),
            new StatDef(6,  "armor_toughness",    Theme.DEFENSE,    Attributes.ARMOR_TOUGHNESS,      AttributeModifier.Operation.ADD_VALUE,          0.3,  false, "\u25C6", 0x8888CC,    "firstmod.stat.armor_toughness.label",    "firstmod.stat.armor_toughness.desc"),
            new StatDef(7,  "knockback_resist",   Theme.DEFENSE,    Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE,          0.05, false, "\u26E8", 0x7788AA,    "firstmod.stat.knockback_resist.label",   "firstmod.stat.knockback_resist.desc"),

            // GATHERING
            new StatDef(8,  "mining_speed",       Theme.GATHERING,  Attributes.BLOCK_BREAK_SPEED,    AttributeModifier.Operation.ADD_VALUE,          0.1,  false, "\u26CF", 0x44AA44,    "firstmod.stat.mining_speed.label",       "firstmod.stat.mining_speed.desc"),
            new StatDef(9,  "luck",               Theme.GATHERING,  Attributes.LUCK,                 AttributeModifier.Operation.ADD_VALUE,          0.5,  false, "\u2600", 0x44CC44,    "firstmod.stat.luck.label",               "firstmod.stat.luck.desc"),
            new StatDef(10, "double_drop",        Theme.GATHERING,  null,                            AttributeModifier.Operation.ADD_VALUE,          0.5,  true,  "\u2726", 0x66DD66,    "firstmod.stat.double_drop.label",        "firstmod.stat.double_drop.desc"),
            new StatDef(11, "block_reach",        Theme.GATHERING,  Attributes.BLOCK_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE,        0.3,  false, "\u2194", 0x88EE88,    "firstmod.stat.block_reach.label",        "firstmod.stat.block_reach.desc"),

            // INTERACTION
            new StatDef(12, "xp_boost",           Theme.INTERACTION, null,                           AttributeModifier.Operation.ADD_VALUE,          2.0,  true,  "\u2B50", 0x4488FF,    "firstmod.stat.xp_boost.label",           "firstmod.stat.xp_boost.desc"),
            new StatDef(13, "trading_discount",   Theme.INTERACTION, null,                           AttributeModifier.Operation.ADD_VALUE,          1.0,  true,  "\u2696", 0x6688DD,    "firstmod.stat.trading_discount.label",   "firstmod.stat.trading_discount.desc"),
            new StatDef(14, "entity_reach",       Theme.INTERACTION, Attributes.ENTITY_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE,      0.3,  false, "\u2194", 0x5588CC,    "firstmod.stat.entity_reach.label",       "firstmod.stat.entity_reach.desc"),
            new StatDef(15, "fishing_luck",       Theme.INTERACTION, Attributes.LUCK,                AttributeModifier.Operation.ADD_VALUE,          0.5,  false, "\u2693", 0x44AACC,    "firstmod.stat.fishing_luck.label",       "firstmod.stat.fishing_luck.desc"),

            // ADVENTURE
            new StatDef(16, "movement_speed",     Theme.ADVENTURE,  Attributes.MOVEMENT_SPEED,       AttributeModifier.Operation.ADD_VALUE,          0.005, false, "\u2714", 0x44FF44,    "firstmod.stat.movement_speed.label",     "firstmod.stat.movement_speed.desc"),
            new StatDef(17, "step_height",        Theme.ADVENTURE,  Attributes.STEP_HEIGHT,          AttributeModifier.Operation.ADD_VALUE,          0.1,  false, "\u2B06", 0x66FF66,    "firstmod.stat.step_height.label",        "firstmod.stat.step_height.desc"),
            new StatDef(18, "jump_strength",      Theme.ADVENTURE,  Attributes.JUMP_STRENGTH,        AttributeModifier.Operation.ADD_VALUE,          0.1,  false, "\u2191", 0x88FF88,    "firstmod.stat.jump_strength.label",      "firstmod.stat.jump_strength.desc"),
            new StatDef(19, "safe_fall",          Theme.ADVENTURE,  Attributes.SAFE_FALL_DISTANCE,   AttributeModifier.Operation.ADD_VALUE,          0.5,  false, "\u25EF", 0x44DDDD,    "firstmod.stat.safe_fall.label",          "firstmod.stat.safe_fall.desc"),
        };
    }

    public static StatDef[] getAll() { return STATS; }
    public static int count() { return STATS.length; }

    public static StatDef get(int index) {
        if (index < 0 || index >= STATS.length) return STATS[0];
        return STATS[index];
    }

    public static StatDef byKey(String key) {
        for (StatDef s : STATS) {
            if (s.key().equals(key)) return s;
        }
        return STATS[0];
    }

    public static int indexOf(String key) {
        for (StatDef s : STATS) {
            if (s.key().equals(key)) return s.index();
        }
        return -1;
    }

    public static StatDef[] byTheme(Theme theme) {
        java.util.ArrayList<StatDef> list = new java.util.ArrayList<>();
        for (StatDef s : STATS) {
            if (s.theme() == theme) list.add(s);
        }
        return list.toArray(new StatDef[0]);
    }

    public static Theme[] themes() { return Theme.values(); }
}
