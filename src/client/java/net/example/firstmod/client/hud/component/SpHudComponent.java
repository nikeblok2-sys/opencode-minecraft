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

    private final Transition displayedSp = new Transition(0);
    private int prevRealSp;
    private float pulsePhase;
    private Font font;
    private int renderX;
    private int renderY;

    public SpHudComponent() {
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public int getWidth() {
        if (!HudConfig.showSp()) return 0;
        int sp = Math.round(displayedSp.get());
        String text = "\u26A1 " + sp + " SP";
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
        int realSp = ClientCache.getAvailableSp();
        if (realSp <= 0 && displayedSp.isComplete() && displayedSp.get() < 0.5f) return;

        int sp = Math.round(displayedSp.get());
        String text = "\u26A1 " + sp + " SP";

        float pulse = pulsePhase > 0 ? 0.7f + 0.3f * (float) Math.sin(pulsePhase * Math.PI * 2) : 1f;
        int alpha = Math.min(255, Math.max(60, (int)(0xBB * pulse)));
        int color = (alpha << 24) | (Colors.ACCENT_GOLD & 0x00FFFFFF);

        g.text(font, text, x, y, color);
    }

    @Override
    public void tick(float dt) {
        if (font == null) font = Minecraft.getInstance().font;
        int realSp = ClientCache.getAvailableSp();

        if (realSp != prevRealSp) {
            if (realSp > prevRealSp) {
                int gained = realSp - prevRealSp;
                FloatingTextRenderer.add("+" + gained + " SP", renderX, renderY - 4, Colors.ACCENT_GOLD);
            }
            prevRealSp = realSp;
        }

        displayedSp.target(realSp, 0.25f);
        displayedSp.tick(dt);

        if (realSp > 0) {
            pulsePhase = (pulsePhase + dt * 0.05f) % 1;
        } else {
            pulsePhase = 0;
        }
    }
}
