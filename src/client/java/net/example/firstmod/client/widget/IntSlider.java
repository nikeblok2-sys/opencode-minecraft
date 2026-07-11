package net.example.firstmod.client.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import java.util.function.IntConsumer;

public class IntSlider extends AbstractSliderButton {
    private final String labelKey;
    private final int minVal;
    private final int maxVal;
    private final IntConsumer setter;

    public IntSlider(int x, int y, int w, int h,
                     String labelKey, int min, int max, int initial,
                     IntConsumer setter) {
        super(x, y, w, h, Component.empty(), toSliderValue(initial, min, max));
        this.labelKey = labelKey;
        this.minVal = min;
        this.maxVal = max;
        this.setter = setter;
        updateMessage();
    }

    private static double toSliderValue(int val, int min, int max) {
        return max == min ? 0 : Mth.clamp((double)(val - min) / (max - min), 0.0, 1.0);
    }

    private int toFieldValue() {
        return minVal + Math.round((float) this.value * (maxVal - minVal));
    }

    public void setValueFromField(int fieldValue) {
        this.value = toSliderValue(fieldValue, minVal, maxVal);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        int v = toFieldValue();
        MutableComponent label = Component.translatable(labelKey);
        setMessage(label.append(Component.literal(": +" + v)));
    }

    @Override
    protected void applyValue() {
        setter.accept(toFieldValue());
    }
}
