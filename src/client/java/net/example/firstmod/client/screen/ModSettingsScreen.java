package net.example.firstmod.client.screen;

import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.RoundedPanel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ModSettingsScreen extends BaseScreen {

    public ModSettingsScreen() {
        super(Component.translatable("firstmod.settings.title"));
    }

    @Override
    protected void init() {
        int cardW = 200;
        int cardH = 60;
        int gap = 12;
        int totalW = cardW;
        int totalH = 3 * cardH + 2 * gap;

        windowWidth = totalW + 40;
        windowHeight = totalH + LayoutHelper.TITLE_H + 56;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int contentX = windowX + (windowWidth - totalW) / 2;
        int contentY = windowY + LayoutHelper.TITLE_H + 16;

        addRenderableWidget(new SettingsCard(contentX, contentY, cardW, cardH,
            Component.translatable("firstmod.settings.theme").getString(),
            Component.translatable("firstmod.settings.theme.desc").getString(),
            Colors.ACCENT_GREEN, () -> {
                if (minecraft != null) ScreenHistory.push(new ThemeConfigScreen(this));
            }));

        addRenderableWidget(new SettingsCard(contentX, contentY + (cardH + gap), cardW, cardH,
            Component.translatable("firstmod.settings.hud").getString(),
            Component.translatable("firstmod.settings.hud.desc").getString(),
            Colors.ACCENT_BLUE, () -> {
                if (minecraft != null) ScreenHistory.push(new HudConfigScreen());
            }));

        addRenderableWidget(new SettingsCard(contentX, contentY + 2 * (cardH + gap), cardW, cardH,
            Component.translatable("firstmod.settings.progression").getString(),
            Component.translatable("firstmod.settings.progression.desc").getString(),
            Colors.ACCENT_GOLD, () -> {
                if (minecraft != null) ScreenHistory.push(new ProgressionConfigScreen(this));
            }));

        addRenderableWidget(new Button(
            contentCenterX() - 30, windowY + windowHeight - 28, 60,
            Component.translatable("gui.back").getString(),
            Colors.ACCENT_RED, ScreenHistory::pop));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow) return;
        String subtitle = Component.translatable("firstmod.settings.subtitle").getString();
        RenderHelper.textShadow(g, font, subtitle,
            contentCenterX() - font.width(subtitle) / 2,
            windowY + LayoutHelper.TITLE_H + 2, Colors.TEXT_DIM);
        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H + 14, windowWidth / 2 - 16);
    }

    private static class SettingsCard extends net.minecraft.client.gui.components.AbstractWidget {
        private final String title;
        private final String desc;
        private final int accent;
        private final Runnable onClick;

        SettingsCard(int x, int y, int w, int h, String title, String desc, int accent, Runnable onClick) {
            super(x, y, w, h, Component.literal(title));
            this.title = title;
            this.desc = desc;
            this.accent = accent;
            this.onClick = onClick;
        }

        @Override
        public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean triggerImmediately) {
            if (isMouseOver(event.x(), event.y())) { onClick.run(); return true; }
            return false;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
            boolean hovered = isMouseOver(mx, my);
            int bg = hovered ? 0x881E1E38 : 0x880E0E20;
            RoundedPanel.card(g, getX(), getY(), width, height);
            g.fill(getX(), getY(), getX() + width, getY() + height, bg);
            g.fill(getX() + 2, getY() + 2, getX() + 4, getY() + height - 2, accent & 0xAAFFFFFF);

            var font = net.minecraft.client.Minecraft.getInstance().font;
            g.text(font, title, getX() + 12, getY() + 8, hovered ? 0xFFFFFFFF : Colors.TEXT_SECONDARY);
            g.text(font, desc, getX() + 12, getY() + 24, Colors.TEXT_DIM);
            if (hovered) {
                g.text(font, "\u25B6", getX() + width - 14, getY() + 12, accent);
            }
        }

        @Override
        protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput o) {
            defaultButtonNarrationText(o);
        }
    }
}
