package net.example.firstmod.client.screen;

import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.theme.StatTheme;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.component.StatRegistry;
import net.example.firstmod.client.widget.Button;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ProfileScreen extends BaseScreen {

    private static final int LINE_H = 12;

    public ProfileScreen() {
        super(Component.translatable("firstmod.screen.profile"));
    }

    @Override
    protected void init() {
        int themeCount = StatRegistry.themes().length;
        int linesPerTheme = 6;
        int contentH = themeCount * linesPerTheme * LINE_H;

        windowWidth = 340;
        windowHeight = LayoutHelper.TITLE_H + 32 + contentH + 40;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        addRenderableWidget(new Button(
            contentCenterX() - 30, windowY + windowHeight - 30, 60,
            Component.translatable("gui.back").getString(),
            Colors.ACCENT_RED, ScreenHistory::pop));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        int[] levels = ClientCache.getStatLevels();
        int pp = ClientCache.getAvailablePp();
        int totalEarned = ClientCache.getTotalEarned();
        int totalSpent = ClientCache.getTotalSpent();

        String ppStr = "\u26A1 " + pp + " PP";
        RenderHelper.textShadow(g, font, ppStr, contentCenterX() - font.width(ppStr) / 2, windowY + LayoutHelper.TITLE_H + 2, Colors.ACCENT_GOLD);

        String totalStr = "Earned: " + totalEarned + " | Spent: " + totalSpent;
        g.text(font, totalStr, contentCenterX() - font.width(totalStr) / 2, windowY + LayoutHelper.TITLE_H + 14, Colors.TEXT_DIM);

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H + 26, windowWidth / 2 - 10);

        int y = windowY + LayoutHelper.TITLE_H + 34;
        int leftX = windowX + 12;
        int rightX = windowX + windowWidth / 2 + 6;

        for (StatRegistry.Theme theme : StatRegistry.themes()) {
            String themeName = Component.translatable("firstmod.theme." + theme.name().toLowerCase()).getString();
            g.text(font, "\u25B8 " + themeName, leftX, y, Colors.ACCENT_GOLD);
            y += LINE_H;

            StatRegistry.StatDef[] stats = StatRegistry.byTheme(theme);
            int col = 0;
            for (StatRegistry.StatDef def : stats) {
                int lv = levels[def.index()];
                double effect = StatFormulas.getEffect(def.index(), lv);
                String val;
                if (def.isPercent()) {
                    val = "+" + String.format("%.1f", effect) + "%";
                } else {
                    val = "+" + String.format("%.1f", effect);
                }
                int x = (col % 2 == 0) ? leftX + 8 : rightX;
                String text = def.icon() + " " + Component.translatable(def.labelKey()).getString()
                    + " Lv." + lv + " (" + val + ")";
                g.text(font, text, x, y, def.color());

                if (col % 2 == 0) {
                    col++;
                } else {
                    y += LINE_H;
                    col = 0;
                }
            }
            if (col > 0) y += LINE_H;
            y += 4;
        }
    }
}
