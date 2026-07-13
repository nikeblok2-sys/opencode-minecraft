package net.example.firstmod.client.widget;

import net.example.firstmod.client.animation.Transition;
import net.example.firstmod.client.theme.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class Button extends AbstractButton {

    private int accent;
    private Runnable action;
    private final Transition hoverT = new Transition(0);

    public Button(int x, int y, int w, String text, int accent, Runnable action) {
        super(x, y, w, 20, Component.literal(text));
        this.accent = accent;
        this.action = action;
    }

    public void setColor(int accent) {
        this.accent = accent;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        action.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mx, int my, float delta) {
        Font font = Minecraft.getInstance().font;
        boolean hovered = isHovered();

        hoverT.target(hovered ? 1f : 0f, 0.1f);
        hoverT.tick(delta);

        float h = hoverT.get();
        int px = getX();
        int py = getY();

        int bg = lerpColor(Colors.BTN_BG, Colors.BTN_BG_HOVER, h);
        int leftAccent = lerpColor(Colors.darken(accent, 0.4f), accent, h);
        int textColor = lerpColor(Colors.BTN_TEXT, Colors.BTN_TEXT_HOVER, h);

        g.fill(px, py, px + width, py + height, bg);
        g.fill(px, py, px + 2, py + height, leftAccent);

        String text = getMessage().getString();
        int tw = font.width(text);
        g.text(font, text, px + (width - tw) / 2, py + (height - font.lineHeight) / 2, textColor);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput o) {
        defaultButtonNarrationText(o);
    }

    private static int lerpColor(int a, int b, float t) {
        int ar = a >> 16 & 0xFF, ag = a >> 8 & 0xFF, ab = a & 0xFF, aa = a >> 24 & 0xFF;
        int br = b >> 16 & 0xFF, bg = b >> 8 & 0xFF, bb = b & 0xFF, ba = b >> 24 & 0xFF;
        return ((int)(aa + (ba - aa) * t) << 24) |
               ((int)(ar + (br - ar) * t) << 16) |
               ((int)(ag + (bg - ag) * t) << 8) |
               (int)(ab + (bb - ab) * t);
    }
}
