package net.example.firstmod.client.mixin;

import net.example.firstmod.client.screen.ScreenHistory;
import net.example.firstmod.client.screen.SellScreen;
import net.example.firstmod.client.screen.StatsScreen;
import net.example.firstmod.client.state.ClientCache;
import net.example.firstmod.client.widget.Button;
import net.example.firstmod.client.theme.Colors;
import net.example.firstmod.network.ProgressionPayloads;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    private InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void firstmod$addButtons(CallbackInfo ci) {
        int l = leftPos;
        int t = topPos;

        addRenderableWidget(new Button(l - 56, t + 10, 54,
            "\u269C " + Component.translatable("firstmod.inventory.stats").getString(),
            Colors.ACCENT_BLUE, () -> {
                ClientPlayNetworking.send(new ProgressionPayloads.RequestStatsPayload());
                ScreenHistory.push(new StatsScreen(
                    ClientCache.getStatLevels(),
                    ClientCache.getAvailableSp()
                ));
            }));

        addRenderableWidget(new Button(l - 56, t + 34, 54,
            "\u26C1 " + Component.translatable("firstmod.inventory.sell").getString(),
            Colors.ACCENT_ORANGE, () -> {
                ScreenHistory.push(new SellScreen());
            }));
    }
}
