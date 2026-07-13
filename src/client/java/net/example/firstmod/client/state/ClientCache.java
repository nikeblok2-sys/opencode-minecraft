package net.example.firstmod.client.state;

import net.example.firstmod.client.event.StatsUpdateEvent;
import net.example.firstmod.component.StatRegistry;

public class ClientCache {
    private static int[] statLevels = new int[StatRegistry.count()];
    private static int availablePp;
    private static int totalEarned;
    private static int totalSpent;

    public static int[] getStatLevels() { return statLevels; }
    public static int getAvailablePp() { return availablePp; }
    public static int getTotalEarned() { return totalEarned; }
    public static int getTotalSpent() { return totalSpent; }

    public static void update(int[] levels, int pp, int earned, int spent) {
        statLevels = levels.clone();
        availablePp = pp;
        totalEarned = earned;
        totalSpent = spent;
        StatsUpdateEvent.EVENT.invoker().onUpdate(statLevels, availablePp);
    }
}
