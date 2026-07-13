package net.example.firstmod.client.mixin;

import net.example.firstmod.client.screen.ProgressionConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreenMixin {
    @Shadow private Button customizeTypeButton;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CreateWorldScreen outer, CallbackInfo ci) {
        if (this.customizeTypeButton != null) {
            this.customizeTypeButton.active = true;
            outer.getUiState().addListener(data -> {
                if (this.customizeTypeButton != null) this.customizeTypeButton.active = true;
            });
        }
    }

    @Inject(method = "openPresetEditor", at = @At("HEAD"), cancellable = true)
    private void onOpenPresetEditor(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.gui.screen() instanceof CreateWorldScreen createWorldScreen) {
            client.gui.setScreen(new ProgressionConfigScreen(createWorldScreen));
            ci.cancel();
        }
    }
}
