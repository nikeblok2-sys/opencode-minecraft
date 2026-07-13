package net.example.firstmod.client.hud.component;

import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.hud.HudComponent;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.theme.StatTheme;
import net.example.firstmod.component.StatRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class StatsHudComponent implements HudComponent {

    private Font font;

    public StatsHudComponent() {
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public int getWidth() {
        if (!HudConfig.showStats() || ClientCache.getStatLevels() == null) return 0;
        int[] levels = ClientCache.getStatLevels();
        int w = 0;
        for (int i = 0; i < Math.min(6, StatRegistry.count()); i++) {
            StatRegistry.StatDef def = StatRegistry.get(i);
            w += font.width(def.icon() + levels[i]) + 6;
        }
        if (StatRegistry.count() > 0) w -= 6;
        return w;
    }

    @Override
    public int getHeight() {
        return HudConfig.showStats() ? font.lineHeight : 0;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, float delta) {
        if (!HudConfig.showStats()) return;
        int[] levels = ClientCache.getStatLevels();
        if (levels == null) return;

        int sx = x;
        int max = Math.min(6, StatRegistry.count());
        for (int i = 0; i < max; i++) {
            StatRegistry.StatDef def = StatRegistry.get(i);
            String s = def.icon() + levels[i];
            int c = def.color() & 0xBBFFFFFF;
            g.text(font, s, sx, y, c);
            sx += font.width(s) + 6;
        }
    }

    @Override
    public void tick(float dt) {
        if (font == null) font = Minecraft.getInstance().font;
    }
}
