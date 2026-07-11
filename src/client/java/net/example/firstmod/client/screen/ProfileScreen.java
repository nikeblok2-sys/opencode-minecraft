package net.example.firstmod.client.screen;

import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.profile.ExpressionEvaluator;
import net.example.firstmod.profile.ProgressionProfile;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ProfileScreen extends BaseScreen {

    private static final int CARD_W = 180;
    private static final int CARD_H = 70;
    private static final int COLS = 2;

    private static final String[] PRESET_NAMES = {"linear", "sqrt", "power", "sigmoid"};
    private static final String[] PRESET_DESC = {
        "1 + dist / 10000",
        "1 + sqrt(dist / 5000)",
        "1 + (dist / 3000)^1.5",
        "S-curve around 8000"
    };

    private final Runnable onClose;
    private EditBox customField;
    private String selectedFormula;
    private String selectedName;
    private String errorMsg;

    public ProfileScreen(Runnable onClose) {
        super(Component.translatable("firstmod.screen.profile"));
        this.onClose = onClose;
    }

    @Override
    protected void init() {
        int totalW = COLS * CARD_W + (COLS - 1) * 12;
        int gridRows = 2;
        int gridH = gridRows * CARD_H + (gridRows - 1) * 8;

        windowWidth = totalW + 40;
        windowHeight = LayoutHelper.TITLE_H + 16 + gridH + 44 + 40;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int contentX = windowX + (windowWidth - totalW) / 2;
        int contentY = windowY + LayoutHelper.TITLE_H + 12;

        for (int i = 0; i < PRESET_NAMES.length; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int cx = contentX + col * (CARD_W + 12);
            int cy = contentY + row * (CARD_H + 8);
            String name = PRESET_NAMES[i];
            String desc = PRESET_DESC[i];
            addRenderableWidget(new PresetCard(cx, cy, CARD_W, CARD_H, name, desc, () -> selectPreset(name)));
        }

        int fieldY = contentY + gridH + 8;
        customField = new EditBox(font, windowX + (windowWidth - 200) / 2, fieldY, 200, 18,
            Component.literal("Custom formula"));
        customField.setMaxLength(80);
        customField.setHint(Component.literal("e.g. 1 + dist / 5000"));
        addRenderableWidget(customField);

        addRenderableWidget(new Button(contentCenterX() - 30, customField.getY() + 24, 60,
            "firstmod.profile.apply", Colors.ACCENT_GREEN, this::applyCustom));

        addRenderableWidget(new Button(contentCenterX() - 30, windowY + windowHeight - 28, 60,
            "gui.back", Colors.ACCENT_RED, () -> {
                if (onClose != null) onClose.run();
                ScreenHistory.pop();
            }));
    }

    private void selectPreset(String name) {
        ProgressionProfileManager.setProfileByName(name);
        selectedName = name;
        selectedFormula = null;
        errorMsg = null;
    }

    private void applyCustom() {
        String input = customField.getValue().trim();
        if (input.isEmpty()) return;
        try {
            ExpressionEvaluator.eval(input, 1000);
            selectedFormula = input;
            selectedName = "custom";
            errorMsg = null;
            ProgressionProfileManager.setCustomProfile(input, "dist/2000", "dist/20000",
                new double[]{500, 2000, 8000, 20000}, new String[0]);
        } catch (Exception e) {
            errorMsg = "Invalid: " + e.getMessage();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H, windowWidth / 2 - 16);

        String status;
        int statusColor;
        if (selectedName != null) {
            status = "\u2713 " + Component.translatable("firstmod.profile.active", selectedName).getString();
            if (selectedFormula != null) status += " (" + selectedFormula + ")";
            statusColor = Colors.ACCENT_GREEN;
        } else {
            ProgressionProfile current = ProgressionProfileManager.getProfile();
            status = Component.translatable("firstmod.profile.current", current.profileName()).getString();
            statusColor = Colors.TEXT_SECONDARY;
        }
        RenderHelper.textShadow(g, font, status, contentCenterX() - font.width(status) / 2,
            windowY + windowHeight - 55, statusColor);

        if (errorMsg != null) {
            RenderHelper.textShadow(g, font, "\u2717 " + errorMsg, contentCenterX() - font.width(errorMsg) / 2,
                customField.getY() + 48, Colors.ACCENT_RED);
        }
    }

    private static class PresetCard extends AbstractWidget {
        private final String name;
        private final String formula;
        private final Runnable onClick;

        PresetCard(int x, int y, int w, int h, String name, String formula, Runnable onClick) {
            super(x, y, w, h, Component.literal(name));
            this.name = name;
            this.formula = formula;
            this.onClick = onClick;
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean triggerImmediately) {
            if (isMouseOver(event.x(), event.y())) { onClick.run(); return true; }
            return false;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            boolean hovered = isMouseOver(mx, my);
            boolean active = name.equals(ProgressionProfileManager.getProfile().profileName());

            int bg = active ? 0x881A3A1A : (hovered ? 0x881E1E38 : 0x880E0E20);
            g.fill(getX(), getY(), getX() + width, getY() + height, bg);
            if (active) {
                g.fill(getX(), getY(), getX() + 3, getY() + height, Colors.ACCENT_GREEN & 0xAAFFFFFF);
            }

            var font = Minecraft.getInstance().font;
            String displayName = name.substring(0, 1).toUpperCase() + name.substring(1);
            g.text(font, displayName, getX() + 8, getY() + 8,
                active ? Colors.ACCENT_GREEN : Colors.TEXT_SECONDARY);
            g.text(font, formula, getX() + 8, getY() + 28,
                active ? Colors.TEXT_BOOST : Colors.TEXT_DIM);
            if (active) {
                g.text(font, "\u2713", getX() + width - 14, getY() + 8, Colors.ACCENT_GREEN);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput o) {
            defaultButtonNarrationText(o);
        }
    }
}
