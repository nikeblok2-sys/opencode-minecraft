package net.example.firstmod.client.theme;

import net.example.firstmod.client.config.ThemeConfig;

public final class Colors {

    public static int
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

    public static int
        BTN_BG = 0xCC0E0E20,
        BTN_BG_HOVER = 0xCC2A2A5E,
        BTN_BG_PRESSED = 0xCC1A1A3A,
        BTN_TEXT = 0xFFCCCCCC,
        BTN_TEXT_HOVER = 0xFFFFFFFF,
        BTN_TEXT_PRESSED = 0xFF888888;

    public static int
        SLOT_BG = 0x881A1A2E,
        SLOT_HOVER = 0x881E1E38,
        SLOT_SELECTED = 0x88224422,
        SLOT_EMPTY = 0x44111122,
        SLOT_EMPTY_HOVER = 0x66111122,
        SLOT_SELECT_BORDER = 0x55FFFF44;

    public static int
        TOOLTIP_TOP = 0xDD1A1A2E,
        TOOLTIP_BOT = 0xDD0A0A1A;

    public static int
        TEXT_SHADOW = 0x66000000,
        TEXT_PRIMARY = 0xFFFFFFFF,
        TEXT_SECONDARY = 0xFFCCCCCC,
        TEXT_BOOST = 0xFF88FF88,
        TEXT_COST_OK = 0xFFFFDD44,
        TEXT_COST_BAD = 0xFFFF6666,
        TEXT_LEVEL = 0xFFCCCCCC,
        TEXT_DIM = 0xFF888888;

    public static int
        ACCENT_GOLD = 0xFFFFDD44,
        ACCENT_GREEN = 0xFF44FF44,
        ACCENT_RED = 0xFFFF5555,
        ACCENT_BLUE = 0xFF4488FF,
        ACCENT_ORANGE = 0xFFFFAA00,
        ACCENT_GRAY = 0xFF888888;

    public static int
        DIVIDER_LEFT = 0xFF444466,
        DIVIDER_RIGHT = 0x00222244,
        BORDER_ACCENT = 0x60FFFFFF,
        BORDER_GLOW = 0x30FFFFFF;

    public static int HUD_BG = 0xAA0A0A1A;

    public static final int PAD = 6;
    public static final int PAD_SMALL = 4;
    public static final int PAD_LARGE = 10;
    public static final int SLOT_SIZE = 20;
    public static final int BTN_H = 20;

    public static void refresh() {
        int bgAccent = (ThemeConfig.getBackground() & 0x00FFFFFF) | 0xCC000000;
        int cardColor = ThemeConfig.getCard() | 0x22000000;
        PANEL_TOP = bgAccent;
        PANEL_BOT = darken(PANEL_TOP, 0.7f);
        PANEL_HIGHLIGHT = 0x18FFFFFF;
        PANEL_SHADOW_LINE = 0x08000000;
        CARD_BG = cardColor;
        CARD_HIGHLIGHT = 0x18FFFFFF;
        CARD_HOVER_TOP = lighten(CARD_BG, 0.2f);
        CARD_HOVER_BOT = darken(CARD_BG, 0.6f);
        SHADOW = 0x44000000;
        BACKDROP_TOP = 0xFF000000 | (darken(bgAccent, 0.3f) & 0x00FFFFFF);
        BACKDROP_BOT = 0xFF000000 | (darken(bgAccent, 0.1f) & 0x00FFFFFF);

        TEXT_PRIMARY = ThemeConfig.getTextPrimary();
        TEXT_SECONDARY = ThemeConfig.getTextSecondary();
        TEXT_BOOST = 0xFF88FF88;
        TEXT_COST_OK = 0xFFFFDD44;
        TEXT_COST_BAD = 0xFFFF6666;
        TEXT_LEVEL = 0xFFCCCCCC;
        TEXT_DIM = 0xFF888888;

        ACCENT_GOLD = ThemeConfig.getAccentSecondary();
        ACCENT_GREEN = 0xFF44FF44;
        ACCENT_RED = 0xFFFF5555;
        ACCENT_BLUE = 0xFF4488FF;
        ACCENT_ORANGE = 0xFFFFAA00;
        ACCENT_GRAY = 0xFF888888;

        int accent1 = (ThemeConfig.getAccentPrimary() & 0x00FFFFFF) | 0xFF000000;
        BTN_BG = bgAccent;
        BTN_BG_HOVER = lighten(bgAccent, 0.3f);
        BTN_BG_PRESSED = darken(bgAccent, 0.5f);
        BTN_TEXT = TEXT_PRIMARY;
        BTN_TEXT_HOVER = 0xFFFFFFFF;
        BTN_TEXT_PRESSED = 0xFF888888;

        SLOT_BG = (cardColor & 0x00FFFFFF) | 0x88000000;
        SLOT_HOVER = (lighten(cardColor, 0.1f) & 0x00FFFFFF) | 0x88000000;
        SLOT_SELECTED = 0x88224422;
        SLOT_EMPTY = 0x44111122;
        SLOT_EMPTY_HOVER = 0x66111122;
        SLOT_SELECT_BORDER = 0x55FFFF44;

        BORDER_ACCENT = 0x60FFFFFF;
        BORDER_GLOW = 0x30FFFFFF;
        HUD_BG = (ThemeConfig.getBackground() & 0x00FFFFFF) | 0xAA000000;

        int accent = ThemeConfig.getAccentPrimary();
        DIVIDER_LEFT = (accent & 0x00FFFFFF) | 0xFF000000;
        DIVIDER_RIGHT = 0x00222244;
        TOOLTIP_TOP = (darken(cardColor, 0.5f) & 0x00FFFFFF) | 0xDD000000;
        TOOLTIP_BOT = (darken(cardColor, 0.8f) & 0x00FFFFFF) | 0xDD000000;
    }

    public static int darken(int color, float factor) {
        int a = color & 0xFF000000;
        int r = (int)(((color >> 16) & 0xFF) * factor);
        int g = (int)(((color >> 8) & 0xFF) * factor);
        int b = (int)((color & 0xFF) * factor);
        return a | (r << 16) | (g << 8) | b;
    }

    private static int lighten(int color, float factor) {
        int a = color & 0xFF000000;
        int r = Math.min(255, (int)(((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int)(((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int)((color & 0xFF) * (1 + factor)));
        return a | (r << 16) | (g << 8) | b;
    }
}
