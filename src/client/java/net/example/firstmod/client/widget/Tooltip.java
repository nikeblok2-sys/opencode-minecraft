package net.example.firstmod.client.widget;

import net.example.firstmod.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Tooltip {

    public static void render(GuiGraphicsExtractor g, int mx, int my, String text) {
        render(g, mx, my, text, Minecraft.getInstance().font);
    }

    public static void render(GuiGraphicsExtractor g, int mx, int my, String text, Font font) {
        int tw = font.width(text);
        int screenW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int tx = mx + 8;
        int ty = my + 8;
        if (tx + tw + 8 > screenW) tx = mx - tw - 8;
        if (tx < 4) tx = 4;
        if (ty + 16 > screenH) ty = my - 16;
        if (ty < 4) ty = 4;

        RenderHelper.tooltip(g, font, tx, ty, text);
    }

    private Tooltip() {}
}
