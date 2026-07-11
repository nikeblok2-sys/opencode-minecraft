package net.example.firstmod.data;

public class PlayerData {

    private double totalDistanceTraveled;
    private long lastLoginTime;
    private long playTimeTicks;

    public PlayerData() {
        this.totalDistanceTraveled = 0;
        this.lastLoginTime = System.currentTimeMillis();
        this.playTimeTicks = 0;
    }

    public double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    public void addDistance(double meters) {
        this.totalDistanceTraveled += meters;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public long getPlayTimeTicks() {
        return playTimeTicks;
    }

    public void tickPlayTime() {
        this.playTimeTicks++;
    }
}
