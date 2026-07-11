package net.example.firstmod.client;

import net.example.firstmod.client.config.HudConfig;
import net.example.firstmod.client.config.ThemeConfig;
import net.example.firstmod.client.hud.HudManager;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudConfig.load();
        ThemeConfig.load();
        Colors.refresh();
        HudElementRegistry.addFirst(
            Identifier.fromNamespaceAndPath("firstmod", "hud"),
            new HudManager()
        );

        ClientPlayNetworking.registerGlobalReceiver(ProgressionPayloads.SyncPayload.TYPE, (payload, context) -> {
            ClientCache.update(payload.statLevels(), payload.availableSp(), payload.totalEarned(), payload.spent());
        });
    }
}
