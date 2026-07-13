package net.example.firstmod.client.hud;

import net.example.firstmod.client.animation.FloatingTextRenderer;
import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.hud.component.DistanceHudComponent;
import net.example.firstmod.client.hud.component.SpHudComponent;
import net.example.firstmod.client.hud.component.StatsHudComponent;
import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.theme.Colors;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public class HudManager implements HudElement {

    private final List<HudComponent> components = new ArrayList<>();
    private final HudDragHandler dragHandler = new HudDragHandler();

    public HudManager() {
        components.add(new SpHudComponent());
        components.add(new StatsHudComponent());
        components.add(new DistanceHudComponent());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        float dt = delta.getGameTimeDeltaTicks();

        float scale = HudConfig.scale();

        for (HudComponent c : components) {
            c.tick(dt);
        }

        int pad = (int)(LayoutHelper.PAD * scale);
        int gap = (int)(3 * scale);

        int bw = 0;
        int bh = 0;
        for (HudComponent c : components) {
            bw = Math.max(bw, c.getWidth());
            bh += c.getHeight();
        }
        if (bh <= 0) return;
        bh += gap * Math.max(0, (int) components.stream().filter(c -> c.getHeight() > 0).count() - 1);

        bw += pad * 2 + 8;
        bh += pad * 2;

        applyAnchor(mc, bw, bh);
        int x = HudConfig.x();
        int y = HudConfig.y();

        dragHandler.handle(mc, bw, bh);

        g.fillGradient(x, y, x + bw, y + bh, Colors.HUD_BG, Colors.darken(Colors.HUD_BG, 0.6f));

        int drawY = y + pad + 1;
        for (HudComponent c : components) {
            if (c.getHeight() <= 0) continue;
            c.render(g, x + pad + 2, drawY, dt);
            drawY += c.getHeight() + gap;
        }

        FloatingTextRenderer.tickAll(dt);
        FloatingTextRenderer.renderAll(g, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    private void applyAnchor(Minecraft mc, int bw, int bh) {
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        switch (HudConfig.anchor()) {
            case 1 -> { HudConfig.setX(sw - bw - 4); }
            case 2 -> { HudConfig.setY(sh - bh - 4); }
            case 3 -> { HudConfig.setX(sw - bw - 4); HudConfig.setY(sh - bh - 4); }
        }
    }
}
