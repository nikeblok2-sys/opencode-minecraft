package net.example.firstmod.client.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface HudComponent {
    int getWidth();
    int getHeight();
    void render(GuiGraphicsExtractor g, int x, int y, float delta);
    void tick(float dt);
}
