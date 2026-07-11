package net.example.firstmod.client.theme;

import net.example.firstmod.component.StatFormulas;

public record StatTheme(String icon, int color, String labelKey) {

    public static final StatTheme[] STATS = {
        new StatTheme("\u25B2", 0xFFFF4444, StatFormulas.STAT_LABEL_KEYS[0]),
        new StatTheme("\u25C6", 0xFF44FF44, StatFormulas.STAT_LABEL_KEYS[1]),
        new StatTheme("\u25CF", 0xFFFFAA00, StatFormulas.STAT_LABEL_KEYS[2]),
        new StatTheme("\u2605", 0xFF4488FF, StatFormulas.STAT_LABEL_KEYS[3]),
        new StatTheme("\u263E", 0xFFCC44FF, StatFormulas.STAT_LABEL_KEYS[4]),
        new StatTheme("\u2B21", 0xFF44FFFF, StatFormulas.STAT_LABEL_KEYS[5]),
    };

    public static StatTheme get(int index) {
        if (index < 0 || index >= STATS.length) return STATS[0];
        return STATS[index];
    }
}
