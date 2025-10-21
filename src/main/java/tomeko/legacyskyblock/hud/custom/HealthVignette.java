package tomeko.legacyskyblock.hud.custom;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class HealthVignette {
    public static void register() {
        HudRenderCallback.EVENT.register(HealthVignette::renderHealthVignette);
    }

    private static void renderHealthVignette(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!LegacySkyblockConfig.healthVignetteEnabled
                || client.player == null
                || client.player.getMaxHealth() <= 0
                || client.player.getHealth() / client.player.getMaxHealth() > LegacySkyblockConfig.healthVignetteHealthPercentage / 100
        ) {
            return;
        }

        int alpha = (int) ((LegacySkyblockConfig.healthVignetteOpacityPercentage / 100) * 255.0f);
        context.fill(0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), ((alpha << 24) | 0xFF0000));
    }

}
