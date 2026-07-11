package net.example.firstmod.client.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.util.ArrayList;
import java.util.List;

public class FloatingTextRenderer {

    private static final List<FloatingText> texts = new ArrayList<>();

    public static void add(String text, int x, int y, int color) {
        synchronized (texts) {
            texts.add(new FloatingText(text, x, y, color));
        }
    }

    public static void tickAll(float dt) {
        synchronized (texts) {
            texts.removeIf(ft -> { ft.age += dt; return ft.age > ft.maxAge; });
        }
    }

    public static void renderAll(GuiGraphicsExtractor ctx, int screenWidth, int screenHeight) {
        var font = Minecraft.getInstance().font;
        synchronized (texts) {
            for (FloatingText ft : texts) {
                float progress = ft.age / ft.maxAge;
                int alpha = (int) (255 * (1f - progress));
                if (alpha < 4) continue;

                int yOff = (int) (progress * -30);
                int color = (ft.color & 0x00FFFFFF) | (alpha << 24);
                ctx.text(font, ft.text, ft.x, ft.y + yOff, color);

                double pulse = 0.5 + 0.5 * Math.sin(ft.age * 0.3);
                int glow = (ft.color & 0x00FFFFFF) | ((int)(alpha * pulse * 0.3) << 24);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        ctx.text(font, ft.text, ft.x + dx, ft.y + yOff + dy, glow);
                    }
                }
            }
        }
    }

    public static void clear() {
        synchronized (texts) {
            texts.clear();
        }
    }

    private static class FloatingText {
        final String text;
        final float maxAge = 40;
        final int color;
        float age;
        int x, y;

        FloatingText(String text, int x, int y, int color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}
