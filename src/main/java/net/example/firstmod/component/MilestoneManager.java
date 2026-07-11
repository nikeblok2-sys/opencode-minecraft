package net.example.firstmod.component;

import net.example.firstmod.config.ProgressionSettings;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class MilestoneManager {

    private static final int[][] MILESTONES = {
        {1000, 25},
        {2500, 50},
        {5000, 100},
        {10000, 200},
        {25000, 500},
        {50000, 1000},
        {100000, 2500},
        {250000, 5000},
        {500000, 10000},
        {1000000, 25000},
        {2500000, 50000},
        {5000000, 100000},
    };

    private static final int CHECK_INTERVAL = 100;
    private static int tickCounter = 0;

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;
        checkAllPlayers(server);
    }

    private static void checkAllPlayers(MinecraftServer server) {
        if (!ProgressionSettings.isEnabled()) return;
        ProgressionStore store = ProgressionStore.getOrCreate(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            double dist = Math.sqrt(player.getX() * player.getX() + player.getZ() * player.getZ());
            ProgressionData data = store.get(player.getUUID());
            boolean changed = false;
            for (int[] milestone : MILESTONES) {
                int threshold = milestone[0];
                int reward = milestone[1];
                if (dist >= threshold && data.distanceMilestones.add(threshold)) {
                    data.addSp(reward);
                    changed = true;
                    player.sendSystemMessage(Component.translatable(
                        "firstmod.milestone.reached", threshold, reward));
                }
            }
            if (changed) {
                store.save();
                ProgressionPayloads.SyncPayload sync = new ProgressionPayloads.SyncPayload(
                    data.statLevels, data.availableSp(), data.totalEarnedSp, data.spentSp);
                ServerPlayNetworking.send(player, sync);
            }
        }
    }
}
