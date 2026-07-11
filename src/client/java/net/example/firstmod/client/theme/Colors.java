package net.example.firstmod.client.theme;

public final class Colors {

    public static final int
        PANEL_TOP = 0xCC0E0E20,
        PANEL_BOT = 0xCC060612,
        PANEL_HIGHLIGHT = 0x18FFFFFF,
        PANEL_SHADOW_LINE = 0x08000000,
        CARD_BG = 0xCC0E0E20,
        CARD_HIGHLIGHT = 0x18FFFFFF,
        CARD_HOVER_TOP = 0xDD1A1A3A,
        CARD_HOVER_BOT = 0xDD0E0E1E,
        SHADOW = 0x44000000,
        BACKDROP_TOP = 0x88000000,
        BACKDROP_BOT = 0x88000000;

    public static final int
        BTN_BG = 0xCC0E0E20,
        BTN_BG_HOVER = 0xCC2A2A5E,
        BTN_BG_PRESSED = 0xCC1A1A3A,
        BTN_TEXT = 0xFFCCCCCC,
        BTN_TEXT_HOVER = 0xFFFFFFFF,
        BTN_TEXT_PRESSED = 0xFF888888;

    public static final int
        SLOT_BG = 0x881A1A2E,
        SLOT_HOVER = 0x881E1E38,
        SLOT_SELECTED = 0x88224422,
        SLOT_EMPTY = 0x44111122,
        SLOT_EMPTY_HOVER = 0x66111122,
        SLOT_SELECT_BORDER = 0x55FFFF44;

    public static final int
        TOOLTIP_TOP = 0xDD1A1A2E,
        TOOLTIP_BOT = 0xDD0A0A1A;

    public static final int
        TEXT_SHADOW = 0x66000000,
        TEXT_PRIMARY = 0xFFFFFFFF,
        TEXT_SECONDARY = 0xFFCCCCCC,
        TEXT_BOOST = 0xFF88FF88,
        TEXT_COST_OK = 0xFFFFDD44,
        TEXT_COST_BAD = 0xFFFF6666,
        TEXT_LEVEL = 0xFFCCCCCC,
        TEXT_DIM = 0xFF888888;

    public static final int
        ACCENT_GOLD = 0xFFFFDD44,
        ACCENT_GREEN = 0xFF44FF44,
        ACCENT_RED = 0xFFFF5555,
        ACCENT_BLUE = 0xFF4488FF,
        ACCENT_ORANGE = 0xFFFFAA00,
        ACCENT_GRAY = 0xFF888888;

    public static final int
        DIVIDER_LEFT = 0xFF444466,
        DIVIDER_RIGHT = 0x00222244,
        BORDER_ACCENT = 0x60FFFFFF,
        BORDER_GLOW = 0x30FFFFFF;

    public static final int HUD_BG = 0xAA0A0A1A;

    public static final int PAD = 6;
    public static final int PAD_SMALL = 4;
    public static final int PAD_LARGE = 10;
    public static final int SLOT_SIZE = 20;
    public static final int BTN_H = 20;

    private static final ThemeDelegate DELEGATE = new ThemeDelegate();

    public static int accentGold() { return ThemeManager.color("ACCENT_GOLD", DELEGATE.accentGold()); }
    public static int accentGreen() { return ThemeManager.color("ACCENT_GREEN", DELEGATE.accentGreen()); }
    public static int accentRed() { return ThemeManager.color("ACCENT_RED", DELEGATE.accentRed()); }
    public static int accentBlue() { return ThemeManager.color("ACCENT_BLUE", DELEGATE.accentBlue()); }
    public static int accentGray() { return ThemeManager.color("ACCENT_GRAY", DELEGATE.accentGray()); }
    public static int textPrimary() { return ThemeManager.color("TEXT_PRIMARY", DELEGATE.textPrimary()); }
    public static int textSecondary() { return ThemeManager.color("TEXT_SECONDARY", DELEGATE.textSecondary()); }

    private static class ThemeDelegate {
        public int accentGold() { return ACCENT_GOLD; }
        public int accentGreen() { return ACCENT_GREEN; }
        public int accentRed() { return ACCENT_RED; }
        public int accentBlue() { return ACCENT_BLUE; }
        public int accentGray() { return ACCENT_GRAY; }
        public int textPrimary() { return TEXT_PRIMARY; }
        public int textSecondary() { return TEXT_SECONDARY; }
    }

    public static int darken(int color, float factor) {
        int a = color & 0xFF000000;
        int r = (int)(((color >> 16) & 0xFF) * factor);
        int g = (int)(((color >> 8) & 0xFF) * factor);
        int b = (int)((color & 0xFF) * factor);
        return a | (r << 16) | (g << 8) | b;
    }
}
