package net.example.firstmod.client.animation;

import net.minecraft.util.Mth;

public class Transition {
    private float from;
    private float to;
    private float duration;
    private float elapsed;
    private boolean active;

    public Transition(float initial) {
        this.from = initial;
        this.to = initial;
        this.duration = 0.15f;
        this.elapsed = 0f;
    }

    public void target(float value) {
        target(value, 0.15f);
    }

    public void target(float value, float duration) {
        if (Math.abs(value - to) < 0.001f) return;
        this.from = get();
        this.to = value;
        this.duration = Math.max(duration, 0.01f);
        this.elapsed = 0f;
        this.active = true;
    }

    public void set(float value) {
        this.from = value;
        this.to = value;
        this.elapsed = 0f;
        this.active = false;
    }

    public float get() {
        if (!active) return to;
        return Mth.lerp(Easing.cubicOut(progress()), from, to);
    }

    public void tick(float dt) {
        if (!active) return;
        elapsed += dt;
        if (elapsed >= duration) {
            elapsed = duration;
            active = false;
        }
    }

    public boolean isComplete() { return !active; }
    private float progress() { return Mth.clamp(elapsed / duration, 0f, 1f); }
}
