package net.example.firstmod.client.widget;

import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Panel {

    public static void background(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        RoundedPanel.card(g, x, y, w, h);
    }

    public static void backgroundAccent(GuiGraphicsExtractor g, int x, int y, int w, int h, int accent) {
        RoundedPanel.accentCard(g, x, y, w, h, accent);
    }

    public static void window(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        RoundedPanel.window(g, x, y, w, h);
    }

    public static void screenBackground(GuiGraphicsExtractor g, int w, int h) {
        g.fillGradient(0, 0, w, h, Colors.BACKDROP_TOP, Colors.BACKDROP_BOT);
    }

    private Panel() {}
}
