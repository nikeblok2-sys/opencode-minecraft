package net.example.firstmod.client.screen;

import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.StatCard;
import net.example.firstmod.component.StatFormulas;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class StatsScreen extends BaseScreen {

    private static final int CARD_GAP = 16;
    private static final int COLS = 2;

    private int[] statLevels;
    private int availableSp;
    private final StatCard[] cards = new StatCard[StatFormulas.STAT_COUNT];

    private int upgradeMode = 1;
    private static final int[] MODES = {1, 10, Integer.MAX_VALUE};
    private static final String[] MODE_LABELS = {"1x", "10x", "MAX"};

    public StatsScreen(int[] statLevels, int availableSp) {
        super(Component.translatable("firstmod.screen.stats"));
        this.statLevels = statLevels;
        this.availableSp = availableSp;
    }

    @Override
    protected void init() {
        syncFromCache();

        int totalW = COLS * StatCard.CARD_W + (COLS - 1) * CARD_GAP;
        int rows = (int) Math.ceil((double) StatFormulas.STAT_COUNT / COLS);
        int gridH = rows * StatCard.CARD_H + (rows - 1) * CARD_GAP;

        windowWidth = totalW + 32;
        windowHeight = LayoutHelper.TITLE_H + 24 + gridH + 36;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int contentX = windowX + (windowWidth - totalW) / 2;
        int contentY = windowY + LayoutHelper.TITLE_H + 24;

        for (int i = 0; i < StatFormulas.STAT_COUNT; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int cx = contentX + col * (StatCard.CARD_W + CARD_GAP);
            int cy = contentY + row * (StatCard.CARD_H + CARD_GAP);
            int idx = i;

            StatCard card = new StatCard(cx, cy, idx, statLevels[i], availableSp, () -> {
                int count = upgradeMode == Integer.MAX_VALUE ? 999 : upgradeMode;
                ClientPlayNetworking.send(new ProgressionPayloads.StatUpgradePayload(idx, count));
            });
            cards[i] = card;
            addRenderableWidget(card);
        }

        int btnW = 50;
        int btnGap = 6;
        int totalBtnW = 3 * btnW + 2 * btnGap;
        int btnStartX = windowX + (windowWidth - totalBtnW) / 2;
        int btnY = contentBottom() - 30;

        for (int i = 0; i < 3; i++) {
            final int mode = MODES[i];
            int bx = btnStartX + i * (btnW + btnGap);
            addRenderableWidget(new Button(bx, btnY, btnW, MODE_LABELS[i], Colors.ACCENT_BLUE, () -> upgradeMode = mode));
        }

        addRenderableWidget(new Button(
            contentCenterX() - 30, contentBottom(), 60, "gui.back",
            Colors.ACCENT_RED, ScreenHistory::pop));
    }

    @Override
    public void onClose() {
        ScreenHistory.pop();
    }

    private void syncFromCache() {
        int[] levels = ClientCache.getStatLevels();
        int sp = ClientCache.getAvailableSp();
        this.statLevels = levels;
        this.availableSp = sp;
        if (cards[0] == null) return;
        for (int i = 0; i < StatFormulas.STAT_COUNT; i++) {
            if (cards[i] != null) cards[i].setData(statLevels[i], availableSp);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        syncFromCache();
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        String spStr = "\u26A1 " + availableSp + " SP";
        RenderHelper.textShadow(g, font, spStr, contentCenterX() - font.width(spStr) / 2, windowY + LayoutHelper.TITLE_H + 4, Colors.ACCENT_GOLD);

        String modeLabel = "\u00D7" + (upgradeMode == Integer.MAX_VALUE ? "MAX" : upgradeMode + "x");
        RenderHelper.textShadow(g, font, modeLabel, contentCenterX() - font.width(modeLabel) / 2, windowY + LayoutHelper.TITLE_H + 16, Colors.TEXT_DIM);

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H + 28, windowWidth / 2 - 16);
    }
}
