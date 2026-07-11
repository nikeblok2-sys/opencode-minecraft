package net.example.firstmod.client.widget;

import net.example.firstmod.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ColorPicker extends AbstractWidget {

    private static final int SWATCH_SIZE = 16;
    private static final int SWATCH_GAP = 2;
    private static final int COLS = 8;

    private static final int[] PRESETS = {
        0xFF88CCAA, 0xFF44AA66, 0xFF2D4A3E, 0xFF3A5A4E,
        0xFFFFD700, 0xFFFFAA00, 0xFFFF5555, 0xFFFF4444,
        0xFF4488FF, 0xFF44AAFF, 0xFFCC44FF, 0xFFFF44FF,
        0xFFFFFFFF, 0xFFCCCCCC, 0xFF888888, 0xFF444444,
        0xFF88FF88, 0xFF44FF44, 0xFF00AA00, 0xFF006600,
        0xFFFF8844, 0xFFFF6600, 0xFFCC2200, 0xFFAA0000,
        0xFFAABBCC, 0xFF6688AA, 0xFF445566, 0xFF223344
    };

    private int selectedColor;
    private final Consumer<Integer> onChange;
    private final Font font;

    public ColorPicker(int x, int y, int w, int initialColor, Consumer<Integer> onChange) {
        super(x, y, w, rows() * (SWATCH_SIZE + SWATCH_GAP) - SWATCH_GAP + 20, Component.empty());
        this.selectedColor = initialColor;
        this.onChange = onChange;
        this.font = Minecraft.getInstance().font;
    }

    private static int rows() {
        return (int) Math.ceil((double) PRESETS.length / COLS);
    }

    private int swatchX(int col) { return getX() + col * (SWATCH_SIZE + SWATCH_GAP); }
    private int swatchY(int row) { return getY() + 20 + row * (SWATCH_SIZE + SWATCH_GAP); }

    private int hitIndex(int mx, int my) {
        for (int i = 0; i < PRESETS.length; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int sx = swatchX(col);
            int sy = swatchY(row);
            if (mx >= sx && mx < sx + SWATCH_SIZE && my >= sy && my < sy + SWATCH_SIZE) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean triggerImmediately) {
        int mx = (int) event.x();
        int my = (int) event.y();
        int idx = hitIndex(mx, my);
        if (idx >= 0) {
            selectedColor = PRESETS[idx];
            onChange.accept(selectedColor);
            return true;
        }
        return super.mouseClicked(event, triggerImmediately);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        String label = String.format("#%06X", selectedColor & 0x00FFFFFF);
        RenderHelper.textShadow(g, font, label, getX(), getY(), 0xFFFFFFFF);
        int previewSize = 12;
        g.fill(getX() + font.width(label) + 6, getY(), getX() + font.width(label) + 6 + previewSize, getY() + previewSize, selectedColor);
        g.fill(getX() + font.width(label) + 6, getY(), getX() + font.width(label) + 6 + previewSize, getY() + 1, 0x44FFFFFF);

        int hovered = hitIndex(mx, my);
        for (int i = 0; i < PRESETS.length; i++) {
            int row = i / COLS;
            int col = i % COLS;
            int sx = swatchX(col);
            int sy = swatchY(row);
            boolean isSelected = PRESETS[i] == selectedColor;
            boolean isHovered = i == hovered;

            g.fill(sx, sy, sx + SWATCH_SIZE, sy + SWATCH_SIZE, PRESETS[i]);
            if (isSelected) {
                g.fill(sx, sy, sx + SWATCH_SIZE, sy + 1, 0xFFFFFFFF);
                g.fill(sx, sy, sx + 1, sy + SWATCH_SIZE, 0xFFFFFFFF);
                g.fill(sx + SWATCH_SIZE - 1, sy, sx + SWATCH_SIZE, sy + SWATCH_SIZE, 0xFFFFFFFF);
                g.fill(sx, sy + SWATCH_SIZE - 1, sx + SWATCH_SIZE, sy + SWATCH_SIZE, 0xFFFFFFFF);
            } else if (isHovered) {
                g.fill(sx - 1, sy - 1, sx + SWATCH_SIZE + 1, sy + SWATCH_SIZE + 1, 0x44FFFFFF);
            }
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput o) {
        defaultButtonNarrationText(o);
    }

    public int getSelectedColor() { return selectedColor; }
    public void setSelectedColor(int color) { this.selectedColor = color; }
}
