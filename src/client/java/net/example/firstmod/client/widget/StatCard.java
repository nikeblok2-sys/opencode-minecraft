package net.example.firstmod.client.widget;

import net.example.firstmod.client.animation.Transition;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.theme.StatTheme;
import net.example.firstmod.component.StatFormulas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class StatCard extends AbstractWidget {

    public static final int CARD_W = 160;
    public static final int CARD_H = 54;
    private static final int EXPAND_H = 12;
    private static final int ACCENT_STRIPE = 3;

    private final int statIndex;
    private final Runnable onUpgrade;
    private int level;
    private int availableSp;
    private final Transition hoverT = new Transition(0);

    public StatCard(int x, int y, int statIndex, int level, int availableSp, Runnable onUpgrade) {
        super(x, y, CARD_W, CARD_H + EXPAND_H, Component.empty());
        this.statIndex = statIndex;
        this.level = level;
        this.availableSp = availableSp;
        this.onUpgrade = onUpgrade;
    }

    public void setData(int level, int availableSp) {
        this.level = level;
        this.availableSp = availableSp;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean triggerImmediately) {
        if (isMouseOver(event.x(), event.y()) && canAfford()) {
            onUpgrade.run();
            return true;
        }
        return false;
    }

    private int cost() { return StatFormulas.cost(level); }
    private boolean canAfford() { return availableSp >= cost(); }
    private double effect() { return StatFormulas.getEffect(statIndex, level); }
    private double boostPct() { return (effect() - 1.0) * 100; }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        Font font = Minecraft.getInstance().font;
        boolean hovered = isMouseOver(mx, my);

        hoverT.target(hovered ? 1f : 0f, 0.12f);
        hoverT.tick(delta);

        float h = hoverT.get();
        int x = getX();
        int y = getY();
        int ch = CARD_H + (int)(EXPAND_H * h);
        int descAlpha = (int)(Math.max(0, (h - 0.4f) / 0.6f) * 255);

        StatTheme st = StatTheme.get(statIndex);
        String name = Component.translatable(st.labelKey()).getString();
        String boost = "+" + String.format("%.0f", boostPct()) + "%";
        String costStr = cost() + " SP";
        String desc = Component.translatable(StatFormulas.STAT_DESC_KEYS[statIndex]).getString();

        g.fill(x, y, x + CARD_W, y + ch, canAfford() ? 0xCC18182E : 0xCC0E0E20);
        g.fill(x, y, x + CARD_W, y + 1, Colors.CARD_HIGHLIGHT);
        g.fill(x, y, x + ACCENT_STRIPE, y + ch, st.color() & 0xAAFFFFFF);

        g.text(font, st.icon() + " " + name, x + 10, y + 6, st.color());
        String lv = "Lv." + level;
        g.text(font, lv, x + CARD_W - 10 - font.width(lv), y + 6, Colors.TEXT_LEVEL);

        g.text(font, boost, x + 10, y + 26, Colors.TEXT_BOOST);
        int costColor = canAfford() ? Colors.TEXT_COST_OK : Colors.TEXT_COST_BAD;
        g.text(font, costStr, x + CARD_W - 10 - font.width(costStr), y + 26, costColor);

        if (descAlpha > 0) {
            int dc = (descAlpha << 24) | (Colors.TEXT_SECONDARY & 0x00FFFFFF);
            g.text(font, desc, x + 10, y + CARD_H + 2, dc);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput o) {
        defaultButtonNarrationText(o);
    }
}
