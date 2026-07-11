package net.example.firstmod.client.widget;

import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.render.RenderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class SlotGrid {

    public record Rect(int x, int y, int w, int h) {
        public boolean contains(int mx, int my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private int gridX;
    private int gridY;
    private final int cols;
    private final int rows;
    private final int slotSize;
    private final int gap;
    private final int step;

    public SlotGrid(int gridX, int gridY, int cols, int rows, int slotSize, int gap) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.cols = cols;
        this.rows = rows;
        this.slotSize = slotSize;
        this.gap = gap;
        this.step = slotSize + gap;
    }

    public int hitTest(int mx, int my) {
        int col = (mx - gridX) / step;
        int row = (my - gridY) / step;
        if (col < 0 || col >= cols || row < 0 || row >= rows) return -1;
        Rect r = rect(row * cols + col);
        return r.contains(mx, my) ? row * cols + col : -1;
    }

    public Rect rect(int idx) {
        int row = idx / cols;
        int col = idx % cols;
        return new Rect(gridX + col * step, gridY + row * step, slotSize, slotSize);
    }

    public int gridW() { return cols * step - gap; }
    public int gridH() { return rows * step - gap; }

    public int gridX() { return gridX; }
    public int gridY() { return gridY; }
    public void setPos(int x, int y) { this.gridX = x; this.gridY = y; }

    public void forEach(BiConsumer<Integer, Rect> consumer) {
        for (int i = 0; i < cols * rows; i++) {
            consumer.accept(i, rect(i));
        }
    }

    public void renderSlot(GuiGraphicsExtractor g, Rect r, boolean hovered, boolean selected) {
        int bg = selected ? Colors.SLOT_SELECTED : (hovered ? Colors.SLOT_HOVER : Colors.SLOT_BG);
        RenderHelper.gradientSlot(g, r.x(), r.y(), r.w(), bg);
        if (selected) {
            g.fill(r.x(), r.y(), r.x() + r.w(), r.y() + 1, Colors.SLOT_SELECT_BORDER);
            g.fill(r.x(), r.y(), r.x() + 1, r.y() + r.h(), Colors.SLOT_SELECT_BORDER);
        }
    }

    public void renderEmpty(GuiGraphicsExtractor g, Rect r, boolean hovered) {
        int bg = hovered ? Colors.SLOT_EMPTY_HOVER : Colors.SLOT_EMPTY;
        RenderHelper.gradientSlot(g, r.x(), r.y(), r.w(), bg);
    }

    public void renderItem(GuiGraphicsExtractor g, Font font, Rect r, boolean selected, boolean hovered, ItemStack stack, double value) {
        renderSlot(g, r, hovered, selected);
        g.item(stack, r.x() + 1, r.y() + 1);
        g.itemDecorations(font, stack, r.x() + 1, r.y() + 1);
        if (value > 0) {
            String vStr = String.format("%.0f", value);
            int vc = value > 10 ? Colors.ACCENT_GOLD : Colors.ACCENT_GRAY;
            int vw = font.width(vStr);
            g.text(font, vStr, r.x() + r.w() - vw - 2, r.y() + r.h() - 8, vc);
        }
    }
}
