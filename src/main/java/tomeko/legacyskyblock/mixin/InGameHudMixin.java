package tomeko.legacyskyblock.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "method_1758(Lnet/minecraft/class_2561;Z)V", at = @At("HEAD"), cancellable = true)
    private void setOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
        if (message == null || !HypixelPackets.inSkyblock) return;

        if (message.getString().contains("True Defense")) {
            if (!LegacySkyblockConfig.actionbarHideTrueDefense) return;

            ci.cancel();
        } else if (message.getString().contains("Defense")) {
            if (!LegacySkyblockConfig.actionbarHideDefense) return;

            ci.cancel();
        }
    }
}