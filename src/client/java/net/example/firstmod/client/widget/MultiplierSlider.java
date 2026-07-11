package net.example.firstmod.client.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import java.util.function.Consumer;

public class MultiplierSlider extends AbstractSliderButton {
    private final String labelKey;
    private final float minVal;
    private final float maxVal;
    private final Consumer<Float> setter;

    public MultiplierSlider(int x, int y, int w, int h,
                            String labelKey, float min, float max, float initial,
                            Consumer<Float> setter) {
        super(x, y, w, h, Component.empty(), toSliderValue(initial, min, max));
        this.labelKey = labelKey;
        this.minVal = min;
        this.maxVal = max;
        this.setter = setter;
        updateMessage();
    }

    private static double toSliderValue(float val, float min, float max) {
        return Mth.clamp((val - min) / (max - min), 0.0, 1.0);
    }

    private float toFieldValue() {
        return minVal + (float) this.value * (maxVal - minVal);
    }

    public void setValueFromField(float fieldValue) {
        this.value = toSliderValue(fieldValue, minVal, maxVal);
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        float v = toFieldValue();
        MutableComponent label = Component.translatable(labelKey);
        String valueStr;
        if (maxVal <= 1.0f && minVal >= 0.0f) {
            valueStr = ": " + Math.round(v * 100) + "%";
        } else {
            valueStr = ": " + String.format("%.1f", v) + "x";
        }
        setMessage(label.append(Component.literal(valueStr)));
    }

    @Override
    protected void applyValue() {
        setter.accept(toFieldValue());
    }
}
