package net.example.firstmod.client.screen;

import net.example.firstmod.client.render.RenderHelper;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.widget.IntSlider;
import net.example.firstmod.client.widget.MultiplierSlider;
import net.example.firstmod.config.ProgressionConfig;
import net.example.firstmod.config.ProgressionSettings;
import net.example.firstmod.profile.ProgressionProfileManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ProgressionConfigScreen extends BaseScreen {
    private final Screen parent;

    private CycleButton<Integer> presetButton;
    private CycleButton<Boolean> extraLootButton;
    private CycleButton<Boolean> overlimitButton;
    private MultiplierSlider dungeonScaleSlider;
    private MultiplierSlider progressionScaleSlider;
    private MultiplierSlider armorTierSlider;
    private MultiplierSlider lootMultSlider;
    private IntSlider enchantBonusSlider;
    private MultiplierSlider mobSpawnSlider;
    private MultiplierSlider difficultySlider;
    private MultiplierSlider mobGearSlider;
    private MultiplierSlider bossHpSlider;
    private MultiplierSlider bossDmgSlider;
    private MultiplierSlider compoundBaseSlider;
    private MultiplierSlider compoundRateSlider;

    public ProgressionConfigScreen(Screen parent) {
        super(Component.translatable("firstmod.config.title"));
        this.parent = parent;
        this.useWindow = false;
    }

    public static ProgressionConfigScreen create(Screen parent) {
        return new ProgressionConfigScreen(parent);
    }

    @Override
    protected void init() {
        int w = 220;

        LinearLayout layout = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
        layout.spacing(4);

        layout.addChild(new StringWidget(w, 14,
            Component.literal("\u25B8 " + Component.translatable("firstmod.config.profile").getString() + ": " + getActiveProfileName())
                .withColor(Colors.ACCENT_GOLD), this.font));

        layout.addChild(new Button(0, 0, w, Component.translatable("firstmod.config.selectProfile").getString(),
            Colors.ACCENT_BLUE, () -> {
                ScreenHistory.push(new ProfileScreen());
            }));
        layout.addChild(spacer(w, 2));

        this.presetButton = CycleButton.<Integer>builder(this::getPresetName, ProgressionSettings.currentPreset)
            .withValues(0, 1, 2, 3, 4)
            .create(0, 0, w, 20, Component.translatable("firstmod.config.preset"), (button, value) -> {
                ProgressionSettings.applyPreset(value);
                refreshSliders();
                ProgressionConfig.save();
            });
        layout.addChild(this.presetButton);

        layout.addChild(header(w, "firstmod.config.header.generation"));
        this.dungeonScaleSlider = addSlider(layout, w, "firstmod.config.structureFrequency", 0.5f, 20.0f,
            ProgressionSettings.dungeonScalableMultiplier, v -> ProgressionSettings.dungeonScalableMultiplier = v);
        this.mobSpawnSlider = addSlider(layout, w, "firstmod.config.mobMultiplier", 1.0f, 10.0f,
            ProgressionSettings.mobSpawnMultiplier, v -> ProgressionSettings.mobSpawnMultiplier = v);

        layout.addChild(header(w, "firstmod.config.header.progression"));
        this.progressionScaleSlider = addSlider(layout, w, "firstmod.config.progressionScale", 0.0f, 10.0f,
            ProgressionSettings.progressionScale, v -> ProgressionSettings.progressionScale = v);
        this.armorTierSlider = addSlider(layout, w, "firstmod.config.armorTier", 0.0f, 5.0f,
            ProgressionSettings.armorTierScale, v -> ProgressionSettings.armorTierScale = v);
        this.difficultySlider = addSlider(layout, w, "firstmod.config.difficulty", 0.0f, 10.0f,
            ProgressionSettings.difficultyBonus, v -> ProgressionSettings.difficultyBonus = v);
        this.mobGearSlider = addSlider(layout, w, "firstmod.config.mobGear", 0.0f, 1.0f,
            ProgressionSettings.mobGearChance, v -> ProgressionSettings.mobGearChance = v);

        layout.addChild(header(w, "firstmod.config.header.costs"));
        this.compoundBaseSlider = addSlider(layout, w, "firstmod.config.compoundBase", 1.0f, 20.0f,
            (float)ProgressionSettings.compoundBase, v -> ProgressionSettings.compoundBase = v);
        this.compoundRateSlider = addSlider(layout, w, "firstmod.config.compoundRate", 0.01f, 0.5f,
            (float)ProgressionSettings.compoundRate, v -> ProgressionSettings.compoundRate = v);

        layout.addChild(header(w, "firstmod.config.header.bosses"));
        this.bossHpSlider = addSlider(layout, w, "firstmod.config.bossHp", 1.0f, 10.0f,
            ProgressionSettings.bossHpMultiplier, v -> ProgressionSettings.bossHpMultiplier = v);
        this.bossDmgSlider = addSlider(layout, w, "firstmod.config.bossDamage", 1.0f, 10.0f,
            ProgressionSettings.bossDamageMultiplier, v -> ProgressionSettings.bossDamageMultiplier = v);

        layout.addChild(header(w, "firstmod.config.header.loot"));
        this.extraLootButton = CycleButton.onOffBuilder(ProgressionSettings.extraLoot)
            .create(0, 0, w, 20, Component.translatable("firstmod.config.extraLoot"), (button, value) -> {
                ProgressionSettings.extraLoot = value;
            });
        layout.addChild(this.extraLootButton);
        this.lootMultSlider = addSlider(layout, w, "firstmod.config.lootMultiplier", 1.0f, 10.0f,
            ProgressionSettings.lootMultiplier, v -> ProgressionSettings.lootMultiplier = v);
        this.overlimitButton = CycleButton.onOffBuilder(ProgressionSettings.overlimitEnchants)
            .create(0, 0, w, 20, Component.translatable("firstmod.config.overlimitEnchants"), (button, value) -> {
                ProgressionSettings.overlimitEnchants = value;
            });
        layout.addChild(this.overlimitButton);
        this.enchantBonusSlider = addIntSlider(layout, w, "firstmod.config.enchantBonus", 0, 50,
            ProgressionSettings.enchantBonusLevels, v -> ProgressionSettings.enchantBonusLevels = v);

        int maxH = this.height - 76;
        ScrollableLayout scroll = new ScrollableLayout(this.minecraft, layout, maxH);
        scroll.setMinWidth(w + 24);
        scroll.arrangeElements();
        scroll.setX((this.width - scroll.getWidth()) / 2);
        scroll.setY(36);
        scroll.visitChildren(child -> this.addRenderableWidget((AbstractWidget) child));

        this.addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
            Component.translatable("gui.done"), button -> {
                ProgressionConfig.save();
                this.minecraft.gui.setScreen(this.parent);
            }).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    private MultiplierSlider addSlider(LinearLayout layout, int w, String key, float min, float max, float initial, java.util.function.Consumer<Float> setter) {
        MultiplierSlider slider = new MultiplierSlider(0, 0, w, 20, key, min, max, initial, setter);
        layout.addChild(slider);
        return slider;
    }

    private IntSlider addIntSlider(LinearLayout layout, int w, String key, int min, int max, int initial, java.util.function.IntConsumer setter) {
        IntSlider slider = new IntSlider(0, 0, w, 20, key, min, max, initial, setter);
        layout.addChild(slider);
        return slider;
    }

    private void refreshSliders() {
        this.dungeonScaleSlider.setValueFromField(ProgressionSettings.dungeonScalableMultiplier);
        this.mobSpawnSlider.setValueFromField(ProgressionSettings.mobSpawnMultiplier);
        this.progressionScaleSlider.setValueFromField(ProgressionSettings.progressionScale);
        this.armorTierSlider.setValueFromField(ProgressionSettings.armorTierScale);
        this.difficultySlider.setValueFromField(ProgressionSettings.difficultyBonus);
        this.mobGearSlider.setValueFromField(ProgressionSettings.mobGearChance);
        this.bossHpSlider.setValueFromField(ProgressionSettings.bossHpMultiplier);
        this.bossDmgSlider.setValueFromField(ProgressionSettings.bossDamageMultiplier);
        this.lootMultSlider.setValueFromField(ProgressionSettings.lootMultiplier);
        this.enchantBonusSlider.setValueFromField(ProgressionSettings.enchantBonusLevels);
        this.compoundBaseSlider.setValueFromField((float)ProgressionSettings.compoundBase);
        this.compoundRateSlider.setValueFromField((float)ProgressionSettings.compoundRate);
        if (this.extraLootButton != null) this.extraLootButton.setValue(ProgressionSettings.extraLoot);
        if (this.overlimitButton != null) this.overlimitButton.setValue(ProgressionSettings.overlimitEnchants);
    }

    private Component getPresetName(Integer value) {
        return Component.translatable("firstmod.preset." + value);
    }

    private String getActiveProfileName() {
        try {
            String name = ProgressionProfileManager.getProfile().profileName();
            return name != null && !name.isEmpty() ? name : "default";
        } catch (Exception e) {
            return "default";
        }
    }

    private StringWidget header(int w, String key) {
        MutableComponent text = Component.empty()
            .append(Component.literal("\u25B8 ").withColor(Colors.ACCENT_GOLD))
            .append(Component.translatable(key).withColor(Colors.ACCENT_GOLD));
        return new StringWidget(w, 14, text, this.font);
    }

    private static AbstractWidget spacer(int w, int h) {
        return new AbstractWidget(0, 0, w, h, Component.empty()) {
            @Override
            protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {}
            @Override
            public void updateWidgetNarration(NarrationElementOutput o) {}
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        RenderHelper.textShadow(g, font, title, centerX(font.width(title)), 8, Colors.TEXT_PRIMARY);
        RenderHelper.divider(g, width / 2, 20, 120);
    }
}
