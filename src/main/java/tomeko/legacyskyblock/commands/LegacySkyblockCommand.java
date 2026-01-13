package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LegacySkyblockCommand {
    private static boolean configOpened = false;

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var mainNode = literal("legacyskyblock")
                    .executes(ctx -> {
                        configOpened = false;
                        return 1;
                    });

            dispatcher.register(mainNode);
        });
        ClientTickEvents.END_CLIENT_TICK.register(LegacySkyblockCommand::onTick);
    }

    private static void onTick(MinecraftClient client) {
        if (!configOpened) {
            configOpened = true;
            client.setScreen(LegacySkyblockConfig.configScreen(client.currentScreen));
        }
    }
}
