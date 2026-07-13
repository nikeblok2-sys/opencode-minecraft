package net.example.firstmod.client.hud.component;

import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.hud.HudComponent;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.profile.ProgressionProfile;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

import java.util.List;

public class DistanceHudComponent implements HudComponent {

    private static final int BAR_H = 4;
    private Font font;

    public DistanceHudComponent() {
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public int getHeight() {
        if (!HudConfig.showDistance()) return 0;
        return font.lineHeight + BAR_H + 4;
    }

    @Override
    public int getWidth() {
        if (!HudConfig.showDistance()) return 0;
        String dist = distanceText();
        if (dist == null) return 0;
        return font.width(dist);
    }

    @Override
    public void render(GuiGraphicsExtractor g, int x, int y, float delta) {
        if (!HudConfig.showDistance()) return;
        Minecraft mc = Minecraft.getInstance();
        int dist = currentDistance(mc);
        if (dist < 0) return;

        String distText = distanceText();
        ProgressionProfile profile = ProgressionProfileManager.getProfile();
        List<Double> thresholds = profile.equipThresholds();
        String next = (thresholds != null) ? nextThreshold(dist, thresholds) : null;

        int distW = font.width(distText);
        int nextW = (next != null) ? font.width(next) : 0;
        int totalW = Math.max(distW, nextW);

        g.text(font, distText, x, y, Colors.TEXT_DIM);
        if (next != null) {
            g.text(font, next, x + totalW - nextW, y, Colors.TEXT_DIM);
        }

        if (thresholds == null || thresholds.isEmpty()) return;

        float progress = thresholdProgress(dist, thresholds);
        int barY = y + font.lineHeight + 2;

        g.fill(x, barY, x + totalW, barY + BAR_H, Colors.SHADOW);
        int fillW = (int) (totalW * Mth.clamp(progress, 0, 1));
        if (fillW > 0) {
            g.fill(x, barY, x + fillW, barY + BAR_H, Colors.ACCENT_GOLD);
        }
    }

    @Override
    public void tick(float dt) {
        if (font == null) font = Minecraft.getInstance().font;
    }

    private String distanceText() {
        int dist = currentDistance(Minecraft.getInstance());
        if (dist < 0) return null;
        return "\u2302 " + String.format("%,d", dist) + " blocks";
    }

    private static int currentDistance(Minecraft mc) {
        if (mc.level == null || mc.player == null) return -1;
        double dx = mc.player.getX();
        double dz = mc.player.getZ();
        return (int) Math.sqrt(dx * dx + dz * dz);
    }

    private static float thresholdProgress(int dist, List<Double> thresholds) {
        if (thresholds.isEmpty()) return 0;
        double prev = 0;
        for (double t : thresholds) {
            if (dist < t) return (float) ((dist - prev) / (t - prev));
            prev = t;
        }
        return 1f;
    }

    private static String nextThreshold(int dist, List<Double> thresholds) {
        for (double t : thresholds) {
            if (dist < t) {
                int rem = (int) Math.ceil(t - dist);
                return "\u2191 " + String.format("%,d", rem) + " blocks";
            }
        }
        return null;
    }
}
