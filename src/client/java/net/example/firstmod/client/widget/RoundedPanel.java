package net.example.firstmod.client.widget;

import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class RoundedPanel {

    public static final int RADIUS = 8;

    public static void window(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, Colors.SHADOW);
        RenderHelper.roundedRect(g, x, y, w, h, RADIUS, Colors.PANEL_TOP);
        g.fill(x + 2, y + 1, x + w - 2, y + 2, Colors.PANEL_HIGHLIGHT & 0x30FFFFFF);
        g.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, Colors.PANEL_SHADOW_LINE);
    }

    public static void card(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        RenderHelper.roundedRect(g, x, y, w, h, RADIUS, Colors.CARD_BG);
        g.fill(x + 2, y, x + w - 2, y + 1, Colors.CARD_HIGHLIGHT);
    }

    public static void accentCard(GuiGraphicsExtractor g, int x, int y, int w, int h, int accent) {
        card(g, x, y, w, h);
        if (accent != 0) {
            RenderHelper.roundedRect(g, x + 2, y + 2, 2, h - 4, 2, accent & 0xAAFFFFFF);
        }
    }

    public static void screenBackground(GuiGraphicsExtractor g, int w, int h) {
        g.fillGradient(0, 0, w, h, Colors.BACKDROP_TOP, Colors.BACKDROP_BOT);
    }

    private RoundedPanel() {}
}
