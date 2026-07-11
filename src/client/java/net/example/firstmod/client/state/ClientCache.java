package net.example.firstmod.client.state;

import net.example.firstmod.client.event.StatsUpdateEvent;
import net.example.firstmod.component.StatFormulas;

public class ClientCache {
    private static int[] statLevels = new int[StatFormulas.STAT_COUNT];
    private static int availableSp;
    private static int totalEarned;
    private static int totalSpent;

    public static int[] getStatLevels() { return statLevels; }
    public static int getAvailableSp() { return availableSp; }
    public static int getTotalEarned() { return totalEarned; }
    public static int getTotalSpent() { return totalSpent; }

    public static void update(int[] levels, int sp, int earned, int spent) {
        statLevels = levels.clone();
        availableSp = sp;
        totalEarned = earned;
        totalSpent = spent;
        StatsUpdateEvent.EVENT.invoker().onUpdate(statLevels, availableSp);
    }
}
