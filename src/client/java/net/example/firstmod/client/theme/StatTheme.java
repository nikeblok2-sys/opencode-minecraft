package net.example.firstmod.client.theme;

import net.example.firstmod.component.StatRegistry;

public record StatTheme(String icon, int color, String labelKey) {

    public static StatTheme get(int index) {
        StatRegistry.StatDef def = StatRegistry.get(index);
        return new StatTheme(def.icon(), def.color(), def.labelKey());
    }
}
