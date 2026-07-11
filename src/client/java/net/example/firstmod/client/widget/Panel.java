package net.example.firstmod.client.widget;

import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Panel {

    public static void background(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        RenderHelper.panel(g, x, y, w, h);
    }

    public static void backgroundAccent(GuiGraphicsExtractor g, int x, int y, int w, int h, int accent) {
        RenderHelper.panelAccent(g, x, y, w, h, accent);
    }

    public static void window(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, Colors.SHADOW);
        g.fillGradient(x, y, x + w, y + h, Colors.PANEL_TOP, Colors.PANEL_BOT);
        g.fill(x, y, x + w, y + 1, Colors.PANEL_HIGHLIGHT);
        g.fill(x, y + h - 1, x + w, y + h, Colors.PANEL_SHADOW_LINE);
    }

    public static void screenBackground(GuiGraphicsExtractor g, int w, int h) {
        g.fillGradient(0, 0, w, h, Colors.BACKDROP_TOP, Colors.BACKDROP_BOT);
    }

    private Panel() {}
}
