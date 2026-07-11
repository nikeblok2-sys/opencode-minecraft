package net.example.firstmod.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenHistory {
    private static final Deque<Screen> stack = new ArrayDeque<>();

    public static void push(Screen screen) {
        if (Minecraft.getInstance().gui != null) {
            stack.push(Minecraft.getInstance().gui.screen());
        }
        Minecraft.getInstance().gui.setScreen(screen);
    }

    public static void pop() {
        Screen prev = stack.isEmpty() ? null : stack.pop();
        Minecraft.getInstance().gui.setScreen(prev);
    }

    public static void clear() { stack.clear(); }
    public static boolean isEmpty() { return stack.isEmpty(); }
}
