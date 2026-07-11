package net.example.firstmod.client.render;

import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class RenderHelper {

    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, Colors.SHADOW);
        g.fillGradient(x, y, x + w, y + h, Colors.PANEL_TOP, Colors.PANEL_BOT);
        g.fill(x, y, x + w, y + 1, Colors.PANEL_HIGHLIGHT);
        g.fill(x, y + h - 1, x + w, y + h, Colors.PANEL_SHADOW_LINE);
    }

    public static void panelAccent(GuiGraphicsExtractor g, int x, int y, int w, int h, int accent) {
        panel(g, x, y, w, h);
        g.fill(x, y, x + 3, y + h, accent);
    }

    public static void card(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, Colors.CARD_BG);
        g.fill(x, y, x + w, y + 1, Colors.CARD_HIGHLIGHT);
    }

    public static void gradientSlot(GuiGraphicsExtractor g, int x, int y, int size, int color) {
        g.fillGradient(x, y, x + size, y + size, color, Colors.darken(color, 0.6f));
    }

    public static void backdrop(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fillGradient(x, y, x + w, y + h, Colors.BACKDROP_TOP, Colors.BACKDROP_BOT);
    }

    public static void divider(GuiGraphicsExtractor g, int cx, int y, int halfWidth) {
        g.fillGradient(cx - halfWidth, y, cx + halfWidth, y + 1, Colors.DIVIDER_LEFT, Colors.DIVIDER_RIGHT);
    }

    public static void tooltip(GuiGraphicsExtractor g, Font font, int x, int y, String text) {
        int tw = font.width(text);
        int pad = 4;
        g.fillGradient(x, y, x + tw + pad * 2, y + font.lineHeight + pad * 2,
            Colors.TOOLTIP_TOP, Colors.TOOLTIP_BOT);
        g.text(font, text, x + pad, y + pad, Colors.TEXT_PRIMARY);
    }

    public static void textShadow(GuiGraphicsExtractor g, Font font, String text, int x, int y, int color) {
        g.textWithBackdrop(font, Component.literal(text), x, y, color, Colors.TEXT_SHADOW);
    }

    public static void textShadow(GuiGraphicsExtractor g, Font font, Component text, int x, int y, int color) {
        g.textWithBackdrop(font, text, x, y, color, Colors.TEXT_SHADOW);
    }

    public static int centerX(int screenWidth, int elementWidth) {
        return (screenWidth - elementWidth) / 2;
    }

    public static int centerY(int screenHeight, int elementHeight) {
        return (screenHeight - elementHeight) / 2;
    }

    public static int clampToScreen(int x, int w, int screenW) {
        return Math.max(4, Math.min(x, screenW - w - 4));
    }

    public static int clampToScreenY(int y, int h, int screenH) {
        return Math.max(4, Math.min(y, screenH - h - 4));
    }
}
