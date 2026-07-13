package net.example.firstmod.client.screen;

import net.example.firstmod.client.animation.Easing;
import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Panel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class BaseScreen extends Screen {

    protected int windowX;
    protected int windowY;
    protected int windowWidth;
    protected int windowHeight;

    private float slideProgress = 1f;
    private float slideTargetY;
    private float slideStartY;
    private boolean slideInit;

    private boolean dragging;
    private int dragOffX, dragOffY;

    protected boolean useWindow = true;

    protected BaseScreen(Component title) {
        super(title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        if (!useWindow) {
            Panel.screenBackground(g, width, height);
            super.extractRenderState(g, mx, my, delta);
            return;
        }

        if (!slideInit) {
            slideInit = true;
            slideProgress = 0f;
            slideStartY = windowY;
            slideTargetY = windowY;
        }

        if (slideProgress < 1f) {
            slideProgress = Math.min(slideProgress + delta * 0.08f, 1f);
            float eased = Easing.cubicOut(slideProgress);
            int slideOffset = (int)((1f - eased) * height);
            windowY = (int)slideStartY + slideOffset;
        }

        Panel.screenBackground(g, width, height);
        Panel.window(g, windowX, windowY, windowWidth, windowHeight);

        renderTitle(g);
        renderCloseButton(g, mx, my);
        RenderHelper.divider(g, windowX + windowWidth / 2, windowY + LayoutHelper.TITLE_H, windowWidth / 2 - 10);

        if (slideProgress >= 1f) {
            super.extractRenderState(g, mx, my, delta);
        }
    }

    protected void renderTitle(GuiGraphicsExtractor g) {
        RenderHelper.textShadow(g, font, title, windowX + (windowWidth - font.width(title)) / 2, windowY + 3, Colors.TEXT_PRIMARY);
    }

    protected void renderCloseButton(GuiGraphicsExtractor g, int mx, int my) {
        int cx = windowX + windowWidth - 16;
        int cy = windowY + 2;
        boolean hover = mx >= cx && mx < cx + 14 && my >= cy && my < cy + 16;
        g.text(font, "\u2716", cx + 2, cy + 1, hover ? Colors.ACCENT_RED : Colors.TEXT_DIM);
    }

    protected int contentTop() { return windowY + LayoutHelper.TITLE_H + LayoutHelper.HEADER_H; }
    protected int contentBottom() { return windowY + windowHeight - 32; }
    protected int contentCenterX() { return windowX + windowWidth / 2; }
    protected int centerX(int elementW) { return (width - elementW) / 2; }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean triggerImmediately) {
        if (!useWindow) return super.mouseClicked(event, triggerImmediately);
        int mx = (int)event.x();
        int my = (int)event.y();

        if (closeButtonHit(mx, my)) {
            closeScreen();
            return true;
        }

        for (var w : children()) {
            if (w instanceof AbstractWidget aw && aw.isMouseOver(mx, my)) {
                setFocused(aw);
                return aw.mouseClicked(event, triggerImmediately);
            }
        }

        startDrag(mx, my);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            continueDrag((int)event.x(), (int)event.y());
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.buttonInfo().button() == 0) dragging = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256 && !ScreenHistory.isEmpty()) {
            ScreenHistory.pop();
            return true;
        }
        return super.keyPressed(event);
    }

    protected void closeScreen() {
        ScreenHistory.pop();
    }

    protected boolean closeButtonHit(int mx, int my) {
        int cx = windowX + windowWidth - 16;
        int cy = windowY + 2;
        return mx >= cx && mx < cx + 14 && my >= cy && my < cy + 16;
    }

    private void startDrag(int mx, int my) {
        if (slideProgress < 1f) return;
        if (my < windowY || my >= windowY + LayoutHelper.TITLE_H) return;
        if (mx < windowX || mx >= windowX + windowWidth) return;
        if (closeButtonHit(mx, my)) return;
        dragging = true;
        dragOffX = mx - windowX;
        dragOffY = my - windowY;
    }

    private void continueDrag(int mx, int my) {
        if (!dragging) return;
        int newX = Mth.clamp(mx - dragOffX, 0, width - windowWidth);
        int newY = Mth.clamp(my - dragOffY, 0, height - windowHeight);
        if (newX != windowX || newY != windowY) {
            int dx = newX - windowX;
            int dy = newY - windowY;
            windowX = newX;
            windowY = newY;
            for (var w : children()) {
                if (w instanceof AbstractWidget aw) {
                    aw.setX(aw.getX() + dx);
                    aw.setY(aw.getY() + dy);
                }
            }
            onDrag(dx, dy);
        }
    }

    protected void onDrag(int dx, int dy) {}
}
