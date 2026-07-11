package net.example.firstmod.client.widget;

import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class Label {

    private String text;
    private int color;
    private boolean shadow;

    public Label(String text) {
        this(text, Colors.TEXT_PRIMARY);
    }

    public Label(String text, int color) {
        this.text = text;
        this.color = color;
        this.shadow = true;
    }

    public Label shadow(boolean v) { this.shadow = v; return this; }
    public Label color(int v) { this.color = v; return this; }
    public Label text(String v) { this.text = v; return this; }

    public int width(Font font) {
        return font.width(text);
    }

    public void render(GuiGraphicsExtractor g, int x, int y) {
        render(g, x, y, Minecraft.getInstance().font);
    }

    public void render(GuiGraphicsExtractor g, int x, int y, Font font) {
        if (shadow) {
            RenderHelper.textShadow(g, font, text, x, y, color);
        } else {
            g.text(font, text, x, y, color);
        }
    }

    public void renderCenter(GuiGraphicsExtractor g, int cx, int y) {
        Font font = Minecraft.getInstance().font;
        int tw = font.width(text);
        render(g, cx - tw / 2, y, font);
    }

    public void renderCenter(GuiGraphicsExtractor g, int cx, int y, Font font) {
        int tw = font.width(text);
        render(g, cx - tw / 2, y, font);
    }
}
