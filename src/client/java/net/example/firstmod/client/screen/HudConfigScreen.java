package net.example.firstmod.client.screen;

import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.MultiplierSlider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class HudConfigScreen extends BaseScreen {

    private static final String[] ANCHOR_NAMES = {"\u2196", "\u2197", "\u2199", "\u2198"};
    private static final int[] ANCHOR_VALUES = {0, 1, 2, 3};

    private boolean showSp = HudConfig.showSp();
    private boolean showStats = HudConfig.showStats();
    private boolean showDistance = HudConfig.showDistance();
    private int anchor = HudConfig.anchor();
    private float scale = HudConfig.scale();

    private Button toggleSp;
    private Button toggleStats;
    private Button toggleDist;
    private Button[] anchorBtns;
    private MultiplierSlider scaleSlider;

    public HudConfigScreen() {
        super(Component.translatable("firstmod.hud.title"));
    }

    @Override
    protected void init() {
        int w = 220;
        int rowH = 22;
        int gap = 6;

        int toggleRows = 3;
        int toggleH = toggleRows * (rowH + gap) - gap;

        windowWidth = w + 40;
        windowHeight = LayoutHelper.TITLE_H + 16 + toggleH + 8 + rowH + 8 + 40 + 36;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int contentX = windowX + (windowWidth - w) / 2;
        int contentY = windowY + LayoutHelper.TITLE_H + 12;

        toggleSp = toggleButton(contentX, contentY, w, "firstmod.hud.show_sp", showSp, v -> showSp = v);
        toggleStats = toggleButton(contentX, contentY + (rowH + gap), w, "firstmod.hud.show_stats", showStats, v -> showStats = v);
        toggleDist = toggleButton(contentX, contentY + 2 * (rowH + gap), w, "firstmod.hud.show_distance", showDistance, v -> showDistance = v);

        int sliderY = contentY + toggleH + 8;
        scaleSlider = new MultiplierSlider(contentX, sliderY, w, 20, "firstmod.hud.scale", 0.5f, 2.0f, scale, v -> scale = v);
        addRenderableWidget(scaleSlider);

        int anchorY = sliderY + rowH + 8;
        int btnW = 40;
        int anchorGap = 6;
        int totalAnchorW = 4 * btnW + 3 * anchorGap;
        int anchorX = contentCenterX() - totalAnchorW / 2;
        anchorBtns = new Button[4];
        for (int i = 0; i < 4; i++) {
            final int a = ANCHOR_VALUES[i];
            int bx = anchorX + i * (btnW + anchorGap);
            int accent = (anchor == a) ? Colors.ACCENT_GOLD : Colors.ACCENT_GRAY;
            anchorBtns[i] = new Button(bx, anchorY, btnW, ANCHOR_NAMES[i], accent, () -> {
                anchor = a;
                updateAnchorButtons();
            });
            addRenderableWidget(anchorBtns[i]);
        }

        int bottomY = windowY + windowHeight - 28;
        addRenderableWidget(new Button(contentCenterX() - 70, bottomY, 65,
            Component.translatable("firstmod.hud.reset").getString(),
            Colors.ACCENT_RED, this::resetPosition));
        addRenderableWidget(new Button(contentCenterX() + 5, bottomY, 65,
            Component.translatable("firstmod.hud.done").getString(),
            Colors.ACCENT_GREEN, this::saveAndClose));
    }

    private Button toggleButton(int x, int y, int w, String labelKey, boolean initial, java.util.function.Consumer<Boolean> setter) {
        boolean[] state = {initial};
        Button btn = new Button(x, y, w, labelText(labelKey, state[0]),
            state[0] ? Colors.ACCENT_GREEN : Colors.ACCENT_GRAY, () -> {});
        btn.setAction(() -> {
            state[0] = !state[0];
            setter.accept(state[0]);
            btn.setMessage(Component.literal(labelText(labelKey, state[0])));
            btn.setColor(state[0] ? Colors.ACCENT_GREEN : Colors.ACCENT_GRAY);
        });
        addRenderableWidget(btn);
        return btn;
    }

    private void updateAnchorButtons() {
        for (int i = 0; i < 4; i++) {
            boolean active = anchor == ANCHOR_VALUES[i];
            anchorBtns[i].setMessage(Component.literal(ANCHOR_NAMES[i]));
            anchorBtns[i].setColor(active ? Colors.ACCENT_GOLD : Colors.ACCENT_GRAY);
        }
    }

    private static String labelText(String key, boolean on) {
        return Component.translatable(key).getString() + ": " + (on ? "\u2713" : "\u2717");
    }

    private static boolean labelValue(String text) {
        return text.contains("\u2713");
    }

    private void resetPosition() {
        HudConfig.setX(4);
        HudConfig.setY(4);
        HudConfig.setAnchor(0);
        anchor = 0;
        updateAnchorButtons();
        HudConfig.save();
    }

    private void saveAndClose() {
        HudConfig.setShowSp(showSp);
        HudConfig.setShowStats(showStats);
        HudConfig.setShowDistance(showDistance);
        HudConfig.setAnchor(anchor);
        HudConfig.setScale(scale);
        HudConfig.save();
        ScreenHistory.pop();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        String anchorLabel = Component.translatable("firstmod.hud.anchor").getString();
        g.text(font, anchorLabel, contentCenterX() - font.width(anchorLabel) / 2,
            windowY + windowHeight - 76, Colors.TEXT_DIM);
    }
}
