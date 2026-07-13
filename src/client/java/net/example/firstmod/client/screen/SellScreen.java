package net.example.firstmod.client.screen;

import net.example.firstmod.client.animation.Transition;
import net.example.firstmod.client.layout.LayoutHelper;
import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.Tooltip;
import net.example.firstmod.client.widget.SlotGrid;
import net.example.firstmod.network.ProgressionPayloads;
import net.example.firstmod.shop.ItemValuation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SellScreen extends BaseScreen {

    private static final int COLS = 9;
    private static final int ROWS = 4;
    private static final int SLOT_SIZE = Colors.SLOT_SIZE;
    private static final int GAP = 2;

    private final int[] selectedCounts = new int[36];
    private double totalValue;
    private int selectedCount;
    private int hoveredSlot = -1;
    private SlotGrid grid;
    private final Transition valueAnim = new Transition(0);

    public SellScreen() {
        super(Component.translatable("firstmod.screen.sell"));
    }

    @Override
    protected void init() {
        int panelPad = LayoutHelper.PAD;
        int step = SLOT_SIZE + GAP;
        int gridW = COLS * step - GAP;
        int gridH = ROWS * step - GAP;
        int panelW = gridW + panelPad * 2;
        int panelH = gridH + panelPad * 2;

        windowWidth = panelW + 36;
        int headerH = 60;
        int footerH = 32;
        windowHeight = LayoutHelper.TITLE_H + headerH + panelH + footerH;
        windowX = (width - windowWidth) / 2;
        windowY = (height - windowHeight) / 2;

        int gridX = windowX + (windowWidth - panelW) / 2;
        int gridY = windowY + LayoutHelper.TITLE_H + headerH + panelPad;
        grid = new SlotGrid(gridX, gridY, COLS, ROWS, SLOT_SIZE, GAP);

        int sellY = windowY + windowHeight - 28;
        int bw = 110;
        int btnGroupW = bw + 10 + 60;
        int btnX = contentCenterX() - btnGroupW / 2;
        addRenderableWidget(new Button(btnX, sellY, bw,
            Component.translatable("firstmod.shop.sell_all").getString(),
            Colors.ACCENT_ORANGE, this::sell));
        addRenderableWidget(new Button(btnX + bw + 10, sellY, 60,
            Component.translatable("gui.back").getString(),
            Colors.ACCENT_RED, ScreenHistory::pop));

        valueAnim.set(0);
        recalcTotal();
        valueAnim.set((float) totalValue);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean triggerImmediately) {
        if (event.buttonInfo().button() != 0) return super.mouseClicked(event, triggerImmediately);
        int mx = (int) event.x();
        int my = (int) event.y();
        int idx = grid.hitTest(mx, my);
        if (idx >= 0) {
            toggle(idx);
            recalcTotal();
            return true;
        }
        return super.mouseClicked(event, triggerImmediately);
    }

    private void toggle(int idx) {
        if (minecraft.player == null) return;
        ItemStack stack = minecraft.player.getInventory().getItem(idx);
        if (stack.isEmpty()) {
            selectedCounts[idx] = 0;
        } else {
            selectedCounts[idx] = selectedCounts[idx] > 0 ? 0 : stack.getCount();
        }
    }

    private void sell() {
        if (minecraft.player == null) return;
        List<Integer> slots = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        var inv = minecraft.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (selectedCounts[i] <= 0) continue;
            if (!inv.getItem(i).isEmpty()) {
                slots.add(i);
                counts.add(selectedCounts[i]);
            }
        }
        if (slots.isEmpty()) return;
        ClientPlayNetworking.send(new ProgressionPayloads.SellItemsPayload(
            slots.stream().mapToInt(Integer::intValue).toArray(),
            counts.stream().mapToInt(Integer::intValue).toArray()));
        for (int idx : slots) selectedCounts[idx] = 0;
        ScreenHistory.pop();
    }

    private void recalcTotal() {
        totalValue = 0;
        selectedCount = 0;
        if (minecraft.player == null) return;
        var inv = minecraft.player.getInventory();
        for (int i = 0; i < 36; i++) {
            if (selectedCounts[i] <= 0) continue;
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                double perItem = ItemValuation.getValue(stack) / Math.max(1, stack.getCount());
                totalValue += perItem * selectedCounts[i];
                selectedCount += selectedCounts[i];
            }
        }
        valueAnim.target((float) totalValue, 0.15f);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        if (!useWindow || minecraft.player == null) return;

        valueAnim.tick(delta);

        String valueStr = "\u26A1 " + String.format("%.0f", valueAnim.get()) + " PP";
        if (selectedCount > 0) {
            valueStr += "   (" + selectedCount + " " + Component.translatable("firstmod.shop.items").getString() + ")";
        }
        RenderHelper.textShadow(g, font, valueStr, contentCenterX() - font.width(valueStr) / 2,
            windowY + LayoutHelper.TITLE_H + 4, Colors.ACCENT_GOLD);

        RenderHelper.divider(g, contentCenterX(), windowY + LayoutHelper.TITLE_H + 28, windowWidth / 2 - 16);

        hoveredSlot = -1;
        var inv = minecraft.player.getInventory();

        grid.forEach((i, r) -> {
            boolean slotHovered = r.contains(mx, my);
            if (slotHovered) hoveredSlot = i;
            boolean selected = selectedCounts[i] > 0;
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty()) {
                grid.renderEmpty(g, r, slotHovered);
            } else {
                double value = ItemValuation.getValue(stack);
                grid.renderItem(g, font, r, selected, slotHovered, stack, value);
            }
        });

        if (hoveredSlot >= 0 && hoveredSlot < inv.getContainerSize()) {
            ItemStack stack = inv.getItem(hoveredSlot);
            if (!stack.isEmpty()) {
                Tooltip.render(g, mx, my, stack.getHoverName().getString());
            }
        }
    }

    @Override
    protected void onDrag(int dx, int dy) {
        grid.setPos(grid.gridX() + dx, grid.gridY() + dy);
    }
}
