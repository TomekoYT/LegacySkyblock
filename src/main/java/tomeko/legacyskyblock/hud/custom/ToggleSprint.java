package tomeko.legacyskyblock.hud.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class ToggleSprint {
    public static void register() {
        HudRenderCallback.EVENT.register(ToggleSprint::render);
        ClientTickEvents.END_CLIENT_TICK.register(ToggleSprint::sprint);
    }

    private static void render(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!LegacySkyblockConfig.toggleSprintEnabled || client.player == null) {
            return;
        }

        context.drawText(client.textRenderer, LegacySkyblockConfig.toggleSprintText, LegacySkyblockConfig.toggleSprintTextPositionX, LegacySkyblockConfig.toggleSprintTextPositionY, LegacySkyblockConfig.toggleSprintTextColor.getRGB(), LegacySkyblockConfig.toggleSprintTextShadowEnabled);
    }

    private static void sprint(MinecraftClient client) {
        if (!LegacySkyblockConfig.toggleSprintEnabled || client.player == null) {
            return;
        }

        client.player.setSprinting(client.player.input.hasForwardMovement());
    }
}