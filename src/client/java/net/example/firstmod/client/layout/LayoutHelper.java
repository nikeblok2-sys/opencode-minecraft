package net.example.firstmod.client.layout;

public final class LayoutHelper {
    public static final int PAD = 6;
    public static final int PAD_SMALL = 4;
    public static final int PAD_LARGE = 10;
    public static final int TITLE_H = 20;
    public static final int HEADER_H = 24;

    public static int rowHeight(int lineHeight) {
        return lineHeight + 3;
    }

    public static int gridX(int index, int cols, int cellW, int gap, int originX) {
        return originX + (index % cols) * (cellW + gap);
    }

    public static int gridY(int index, int cols, int cellH, int gap, int originY) {
        return originY + (index / cols) * (cellH + gap);
    }

    public static int gridWidth(int cols, int cellW, int gap) {
        return cols * cellW + (cols - 1) * gap;
    }

    public static int gridHeight(int rows, int cellH, int gap) {
        return rows * cellH + (rows - 1) * gap;
    }

    public static int windowWidth(int contentW, int padding) {
        return contentW + padding * 2;
    }

    public static int windowHeight(int titleH, int headerH, int contentH, int footerH) {
        return titleH + headerH + contentH + footerH;
    }
}
