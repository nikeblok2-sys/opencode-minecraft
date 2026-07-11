package net.example.firstmod.client.screen;

import net.example.firstmod.client.config.ThemeConfig;
import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.ColorPicker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ThemeConfigScreen extends BaseScreen {

    private static final int COLOR_ROWS = 6;
    private static final int PICKER_W = 170;

    private final Screen parent;
    private ColorPicker[] pickers = new ColorPicker[COLOR_ROWS];
    private int[] currentColors = new int[COLOR_ROWS];
    private String[] labels = new String[COLOR_ROWS];

    public ThemeConfigScreen(Screen parent) {
        super(Component.translatable("firstmod.theme.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        windowWidth = 280;
        int rowH = 34;
        windowHeight = LayoutHelper.TITLE_H + 16 + COLOR_ROWS * rowH + 64;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        currentColors[0] = ThemeConfig.getBackground();
        currentColors[1] = ThemeConfig.getCard();
        currentColors[2] = ThemeConfig.getAccentPrimary();
        currentColors[3] = ThemeConfig.getAccentSecondary();
        currentColors[4] = ThemeConfig.getTextPrimary();
        currentColors[5] = ThemeConfig.getTextSecondary();
        labels[0] = Component.translatable("firstmod.theme.background").getString();
        labels[1] = Component.translatable("firstmod.theme.card").getString();
        labels[2] = Component.translatable("firstmod.theme.accent1").getString();
        labels[3] = Component.translatable("firstmod.theme.accent2").getString();
        labels[4] = Component.translatable("firstmod.theme.text1").getString();
        labels[5] = Component.translatable("firstmod.theme.text2").getString();

        int contentX = windowX + 10;
        int pickerX = windowX + windowWidth - 10 - PICKER_W;
        int startY = windowY + LayoutHelper.TITLE_H + 8;

        for (int i = 0; i < COLOR_ROWS; i++) {
            final int idx = i;
            ColorPicker picker = new ColorPicker(pickerX, startY + i * rowH, PICKER_W,
                currentColors[i], c -> applyColor(idx, c));
            pickers[i] = picker;
            addRenderableWidget(picker);
        }

        int btnY = startY + COLOR_ROWS * rowH + 4;
        addPresetButtons(btnY);
    }

    private void addPresetButtons(int y) {
        String[] keys = {"firstmod.theme.dark_green", "firstmod.theme.blue_steel",
                         "firstmod.theme.red_dark", "firstmod.theme.neon_purple"};
        int btnW = 50;
        int btnGap = 4;
        int totalW = keys.length * btnW + (keys.length - 1) * btnGap;
        int btnX = contentCenterX() - totalW / 2;

        for (int i = 0; i < keys.length; i++) {
            final int idx = i;
            addRenderableWidget(new Button(btnX + i * (btnW + btnGap), y, btnW,
                Component.translatable(keys[idx]).getString(),
                Colors.ACCENT_BLUE, () -> applyPreset(idx)));
        }

        int doneY = y + 24;
        addRenderableWidget(new Button(contentCenterX() - 60, doneY, 55,
            "firstmod.theme.reset", Colors.ACCENT_RED, this::resetDefaults));
        addRenderableWidget(new Button(contentCenterX() + 5, doneY, 55,
            "gui.done", Colors.ACCENT_GREEN, () -> {
                ThemeConfig.save();
                if (minecraft != null) minecraft.gui.setScreen(parent);
            }));
    }

    private void applyColor(int idx, int color) {
        currentColors[idx] = color;
        switch (idx) {
            case 0 -> ThemeConfig.setBackground(color);
            case 1 -> ThemeConfig.setCard(color);
            case 2 -> ThemeConfig.setAccentPrimary(color);
            case 3 -> ThemeConfig.setAccentSecondary(color);
            case 4 -> ThemeConfig.setTextPrimary(color);
            case 5 -> ThemeConfig.setTextSecondary(color);
        }
        Colors.refresh();
    }

    private void applyPreset(int index) {
        switch (index) {
            case 0 -> {
                currentColors[0] = 0xFF2D4A3E; currentColors[1] = 0xFF3A5A4E;
                currentColors[2] = 0xFF88CCAA; currentColors[3] = 0xFFFFD700;
                currentColors[4] = 0xFFFFFFFF; currentColors[5] = 0xFFCCCCCC;
            }
            case 1 -> {
                currentColors[0] = 0xFF2C3E50; currentColors[1] = 0xFF3A4E62;
                currentColors[2] = 0xFF66AADD; currentColors[3] = 0xFFCCDDEE;
                currentColors[4] = 0xFFFFFFFF; currentColors[5] = 0xFFCCCCCC;
            }
            case 2 -> {
                currentColors[0] = 0xFF3A1A1A; currentColors[1] = 0xFF4A2A2A;
                currentColors[2] = 0xFFDD6666; currentColors[3] = 0xFFFFAA44;
                currentColors[4] = 0xFFFFFFFF; currentColors[5] = 0xFFCCCCCC;
            }
            case 3 -> {
                currentColors[0] = 0xFF1A1A3A; currentColors[1] = 0xFF2A2A5E;
                currentColors[2] = 0xFFAA66DD; currentColors[3] = 0xFF44FFFF;
                currentColors[4] = 0xFFFFFFFF; currentColors[5] = 0xFFCCCCCC;
            }
        }
        for (int i = 0; i < COLOR_ROWS; i++) {
            applyColor(i, currentColors[i]);
            if (pickers[i] != null) pickers[i].setSelectedColor(currentColors[i]);
        }
        ThemeConfig.save();
        Colors.refresh();
    }

    private void resetDefaults() {
        applyPreset(0);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        int startY = windowY + LayoutHelper.TITLE_H + 8;
        int rowH = 34;

        for (int i = 0; i < COLOR_ROWS; i++) {
            g.text(font, labels[i], windowX + 12, startY + i * rowH + 8, Colors.TEXT_SECONDARY);
        }

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H, windowWidth / 2 - 16);
        RenderHelper.divider(g, contentCenterX(), startY + COLOR_ROWS * rowH, windowWidth / 2 - 16);
    }
}
