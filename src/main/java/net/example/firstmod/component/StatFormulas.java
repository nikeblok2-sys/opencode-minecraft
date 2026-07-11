package net.example.firstmod.component;

public class StatFormulas {

    public static final int STAT_COUNT = 6;
    public static final int STR = 0;
    public static final int AGI = 1;
    public static final int VIT = 2;
    public static final int INT = 3;
    public static final int LUK = 4;
    public static final int WIS = 5;

    public static final String[] STAT_NAMES = {"str", "agi", "vit", "int", "luk", "wis"};
    public static final String[] STAT_LABEL_KEYS = {
        "firstmod.stat.str.label",
        "firstmod.stat.agi.label",
        "firstmod.stat.vit.label",
        "firstmod.stat.int.label",
        "firstmod.stat.luk.label",
        "firstmod.stat.wis.label"
    };
    public static final String[] STAT_DESC_KEYS = {
        "firstmod.stat.str.desc",
        "firstmod.stat.agi.desc",
        "firstmod.stat.vit.desc",
        "firstmod.stat.int.desc",
        "firstmod.stat.luk.desc",
        "firstmod.stat.wis.desc"
    };

    public static int indexOf(String name) {
        return switch (name) {
            case "str" -> STR;
            case "agi" -> AGI;
            case "vit" -> VIT;
            case "int" -> INT;
            case "luk" -> LUK;
            case "wis" -> WIS;
            default -> -1;
        };
    }

    public static int cost(int currentLevel) {
        return (int) (5.0 * Math.pow(currentLevel + 1, 1.5) * 2.5);
    }

    public static double perLevelBoost(int level) {
        return 1.0 * (1.0 + level / 20.0) / 100.0;
    }

    public static double totalBoost(int level) {
        double sum = 0;
        for (int i = 0; i < level; i++) {
            sum += perLevelBoost(i);
        }
        return sum;
    }

    public static double getEffect(int statIndex, int level) {
        double boost = totalBoost(level);
        return switch (statIndex) {
            case STR -> 1.0 + boost * 4.0;
            case AGI -> 1.0 + boost * 2.0;
            case VIT -> 1.0 + boost * 2.0;
            case INT -> 1.0 + boost * 3.0;
            case LUK -> 1.0 + boost * 3.0;
            case WIS -> 1.0 + boost * 2.0;
            default -> 1.0;
        };
    }
}
