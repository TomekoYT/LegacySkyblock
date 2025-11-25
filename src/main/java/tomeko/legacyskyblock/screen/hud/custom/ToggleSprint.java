package tomeko.legacyskyblock.screen.hud.custom;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class ToggleSprint {
    public static int lastWindowWidth = 0;
    public static int lastWindowHeight = 0;

    public static void register() {
        HudRenderCallback.EVENT.register(ToggleSprint::render);
        ClientTickEvents.START_CLIENT_TICK.register(ToggleSprint::sprint);
        ClientTickEvents.END_CLIENT_TICK.register(ToggleSprint::sprint);
    }

    private static void render(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!LegacySkyblockConfig.toggleSprintEnabled || client.player == null) {
            return;
        }

        if (lastWindowWidth == 0 || lastWindowHeight == 0) {
            lastWindowWidth = client.getWindow().getScaledWidth();
            lastWindowHeight = client.getWindow().getScaledHeight();
            return;
        }

        boolean changed = false;

        if (lastWindowWidth != client.getWindow().getScaledWidth()) {
            LegacySkyblockConfig.toggleSprintTextWidth = client.getWindow().getScaledWidth() - (lastWindowWidth - LegacySkyblockConfig.toggleSprintTextWidth);
            lastWindowWidth = client.getWindow().getScaledWidth();
            changed = true;
        }

        if (lastWindowHeight != client.getWindow().getScaledHeight()) {
            LegacySkyblockConfig.toggleSprintTextHeight = client.getWindow().getScaledHeight() - (lastWindowHeight - LegacySkyblockConfig.toggleSprintTextHeight);
            lastWindowHeight = client.getWindow().getScaledHeight();
            changed = true;
        }

        if (changed) {
            LegacySkyblockConfig.CONFIG.save();
        }

        context.drawText(client.textRenderer, LegacySkyblockConfig.toggleSprintText, LegacySkyblockConfig.toggleSprintTextWidth, LegacySkyblockConfig.toggleSprintTextHeight, LegacySkyblockConfig.toggleSprintTextColor.getRGB(), LegacySkyblockConfig.toggleSprintTextShadowEnabled);
    }

    private static void sprint(MinecraftClient client) {
        if (!LegacySkyblockConfig.toggleSprintEnabled || client.player == null) {
            return;
        }

        client.player.setSprinting(client.player.input.hasForwardMovement());
    }
}