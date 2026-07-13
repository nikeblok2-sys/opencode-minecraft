package net.example.firstmod.client.screen;

import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.StatCard;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.component.StatRegistry;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class StatsScreen extends BaseScreen {

    private static final int CARD_GAP = 16;
    private static final int COLS = 2;
    private static final int TAB_H = 20;

    private int[] statLevels;
    private int availablePp;
    private final StatCard[] allCards = new StatCard[StatRegistry.count()];
    private StatRegistry.Theme selectedTheme = StatRegistry.Theme.COMBAT;
    private final StatRegistry.Theme[] themes = StatRegistry.themes();

    private int upgradeMode = 1;
    private static final int[] MODES = {1, 10, Integer.MAX_VALUE};
    private static final String[] MODE_LABELS = {"1x", "10x", "MAX"};

    public StatsScreen(int[] statLevels, int availablePp) {
        super(Component.translatable("firstmod.screen.stats"));
        this.statLevels = statLevels;
        this.availablePp = availablePp;
    }

    @Override
    protected void init() {
        syncFromCache();

        int themeCount = themes.length;
        int maxStatsInTheme = 0;
        for (var theme : themes) {
            int n = StatRegistry.byTheme(theme).length;
            if (n > maxStatsInTheme) maxStatsInTheme = n;
        }
        int rows = (int) Math.ceil((double) maxStatsInTheme / COLS);
        int cardH = StatCard.CARD_H + StatCard.EXPAND_H;
        int gridH = rows * cardH + (rows - 1) * CARD_GAP;

        int totalW = COLS * StatCard.CARD_W + (COLS - 1) * CARD_GAP;
        int tabAreaH = TAB_H + 6;
        windowWidth = totalW + 32;
        windowHeight = LayoutHelper.TITLE_H + tabAreaH + 36 + gridH + 36;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int tabStartX = windowX + 16;
        int tabY = windowY + LayoutHelper.TITLE_H + 8;
        int tabW = (windowWidth - 32) / themeCount;
        for (int i = 0; i < themeCount; i++) {
            final StatRegistry.Theme t = themes[i];
            int tx = tabStartX + i * tabW;
            String themeName = Component.translatable("firstmod.theme." + t.name().toLowerCase()).getString();
            addRenderableWidget(new Button(tx, tabY, tabW - 2, themeName, Colors.ACCENT_BLUE, () -> {
                selectedTheme = t;
                rebuildCards();
            }));
        }

        int contentY = tabY + TAB_H + 16;
        rebuildCardsAt(contentY);

        int btnW = 50;
        int btnGap = 6;
        int totalBtnW = 3 * btnW + 2 * btnGap;
        int btnStartX = windowX + (windowWidth - totalBtnW) / 2;
        int modeBtnY = contentBottom() - 30;

        for (int i = 0; i < 3; i++) {
            final int mode = MODES[i];
            int bx = btnStartX + i * (btnW + btnGap);
            addRenderableWidget(new Button(bx, modeBtnY, btnW, MODE_LABELS[i], Colors.ACCENT_BLUE, () -> upgradeMode = mode));
        }

        addRenderableWidget(new Button(
            contentCenterX() - 70, contentBottom(), 60,
            Component.translatable("firstmod.screen.profile").getString(),
            Colors.ACCENT_GREEN, () -> ScreenHistory.push(new ProfileScreen())));

        addRenderableWidget(new Button(
            contentCenterX() + 10, contentBottom(), 60,
            Component.translatable("gui.back").getString(),
            Colors.ACCENT_RED, ScreenHistory::pop));
    }

    private void rebuildCards() {
        int themeCount = themes.length;
        int tabStartX = windowX + 16;
        int tabY = windowY + LayoutHelper.TITLE_H + 8;
        int contentY = tabY + TAB_H + 16;
        rebuildCardsAt(contentY);
    }

    private void rebuildCardsAt(int contentY) {
        for (int i = 0; i < allCards.length; i++) {
            if (allCards[i] != null) {
                removeWidget(allCards[i]);
                allCards[i] = null;
            }
        }
        StatRegistry.StatDef[] themeStats = StatRegistry.byTheme(selectedTheme);
        int totalW = COLS * StatCard.CARD_W + (COLS - 1) * CARD_GAP;
        int contentX = windowX + (windowWidth - totalW) / 2;

        for (int i = 0; i < themeStats.length; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int cx = contentX + col * (StatCard.CARD_W + CARD_GAP);
            int cy = contentY + row * (StatCard.CARD_H + CARD_GAP);
            int idx = themeStats[i].index();

            StatCard card = new StatCard(cx, cy, idx, statLevels[idx], availablePp, () -> {
                int count = upgradeMode == Integer.MAX_VALUE ? 999 : upgradeMode;
                ClientPlayNetworking.send(new ProgressionPayloads.StatUpgradePayload(idx, count));
            });
            allCards[idx] = card;
            addRenderableWidget(card);
        }
    }

    @Override
    public void onClose() {
        ScreenHistory.pop();
    }

    private void syncFromCache() {
        int[] levels = ClientCache.getStatLevels();
        int pp = ClientCache.getAvailablePp();
        this.statLevels = levels;
        this.availablePp = pp;
        for (int i = 0; i < StatRegistry.count(); i++) {
            if (allCards[i] != null) allCards[i].setData(statLevels[i], availablePp);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        syncFromCache();
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        String ppStr = "\u26A1 " + availablePp + " PP";
        RenderHelper.textShadow(g, font, ppStr, contentCenterX() - font.width(ppStr) / 2, windowY + LayoutHelper.TITLE_H + 4, Colors.ACCENT_GOLD);

        String modeLabel = "\u00D7" + (upgradeMode == Integer.MAX_VALUE ? "MAX" : upgradeMode + "x");
        RenderHelper.textShadow(g, font, modeLabel, contentCenterX() - font.width(modeLabel) / 2, windowY + LayoutHelper.TITLE_H + 30, Colors.TEXT_DIM);

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H + 26, windowWidth / 2 - 16);
    }
}
