package net.example.firstmod.client.hud.component;

import net.example.firstmod.client.animation.Transition;
import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.hud.HudComponent;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.animation.FloatingTextRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class SpHudComponent implements HudComponent {

    private final Transition displayedPp = new Transition(0);
    private int prevRealPp;
    private float pulsePhase;
    private Font font;
    private int renderX = Integer.MIN_VALUE;
    private int renderY;

    public SpHudComponent() {
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public int getWidth() {
        if (!HudConfig.showSp()) return 0;
        int pp = Math.round(displayedPp.get());
        String text = "\u26A1 " + pp + " PP";
        return font.width(text);
    }

    @Override
    public int getHeight() {
        return HudConfig.showSp() ? font.lineHeight : 0;
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, float delta) {
        this.renderX = x;
        this.renderY = y;
        if (!HudConfig.showSp()) return;
        int realPp = ClientCache.getAvailablePp();
        if (realPp <= 0 && displayedPp.isComplete() && displayedPp.get() < 0.5f) return;

        int pp = Math.round(displayedPp.get());
        String text = "\u26A1 " + pp + " PP";

        float pulse = pulsePhase > 0 ? 0.7f + 0.3f * (float) Math.sin(pulsePhase * Math.PI * 2) : 1f;
        int alpha = Math.min(255, Math.max(60, (int)(0xBB * pulse)));
        int color = (alpha << 24) | (Colors.ACCENT_GOLD & 0x00FFFFFF);

        g.text(font, text, x, y, color);
    }

    @Override
    public void tick(float dt) {
        if (font == null) font = Minecraft.getInstance().font;
        int realPp = ClientCache.getAvailablePp();

        if (realPp != prevRealPp) {
            if (realPp > prevRealPp && renderX != Integer.MIN_VALUE) {
                int gained = realPp - prevRealPp;
                FloatingTextRenderer.add("+" + gained + " PP", renderX, renderY - 4, Colors.ACCENT_GOLD);
            }
            prevRealPp = realPp;
        }

        displayedPp.target(realPp, 0.25f);
        displayedPp.tick(dt);

        if (realPp > 0) {
            pulsePhase = (pulsePhase + dt * 0.05f) % 1;
        } else {
            pulsePhase = 0;
        }
    }
}
