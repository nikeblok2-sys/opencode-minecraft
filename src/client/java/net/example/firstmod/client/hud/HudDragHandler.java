package net.example.firstmod.client.hud;

import net.example.firstmod.client.config.HudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class HudDragHandler {

    private boolean dragging;
    private int dragOffX, dragOffY;
    private boolean wasMouseDown;

    public void handle(Minecraft mc, int bw, int bh) {
        int x = HudConfig.x();
        int y = HudConfig.y();
        var window = mc.getWindow();
        double mxRaw = mc.mouseHandler.xpos();
        double myRaw = mc.mouseHandler.ypos();
        double mx = mxRaw * window.getGuiScaledWidth() / window.getScreenWidth();
        double my = myRaw * window.getGuiScaledHeight() / window.getScreenHeight();
        boolean mouseInPanel = mx >= x && mx < x + bw && my >= y && my < y + bh;
        long handle = GLFW.glfwGetCurrentContext();
        boolean mouseDown = handle != 0 && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        int sw = window.getGuiScaledWidth();
        int sh = window.getGuiScaledHeight();

        if (mouseDown && !wasMouseDown && mouseInPanel) {
            dragging = true;
            HudConfig.setAnchor(0);
            dragOffX = (int) (mx - x);
            dragOffY = (int) (my - y);
        }
        if (dragging && mouseDown) {
            x = Mth.clamp((int) (mx - dragOffX), 0, sw - Math.min(bw, sw));
            y = Mth.clamp((int) (my - dragOffY), 0, sh - Math.min(bh, sh));
            HudConfig.setX(x);
            HudConfig.setY(y);
        }
        if (dragging && !mouseDown) {
            dragging = false;
            HudConfig.save();
        }
        wasMouseDown = mouseDown;
    }
}
