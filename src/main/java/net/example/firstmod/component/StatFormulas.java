package net.example.firstmod.component;

import net.example.firstmod.config.ProgressionSettings;

public class StatFormulas {

    public static int cost(int currentLevel) {
        double base = ProgressionSettings.compoundBase;
        double rate = ProgressionSettings.compoundRate;
        return (int) Math.round(base * Math.pow(1.0 + rate, currentLevel));
    }

    public static double getEffect(int statIndex, int level) {
        StatRegistry.StatDef def = StatRegistry.get(statIndex);
        return def.perLevelBonus() * level;
    }
}
