package net.example.firstmod.client.animation;

public final class Easing {
    public static float linear(float t) { return t; }
    public static float quadIn(float t) { return t * t; }
    public static float quadOut(float t) { return t * (2f - t); }
    public static float cubicOut(float t) { float f = 1f - t; return 1f - f * f * f; }
    public static float expOut(float t) { return t == 1f ? 1f : 1f - (float) Math.pow(2f, -10f * t); }
    public static float bounceOut(float t) {
        if (t < 0.3636f) return 7.5625f * t * t;
        if (t < 0.7273f) { float f = t - 0.5455f; return 7.5625f * f * f + 0.75f; }
        if (t < 0.9091f) { float f = t - 0.8182f; return 7.5625f * f * f + 0.9375f; }
        float f = t - 0.9545f; return 7.5625f * f * f + 0.984375f;
    }
}
