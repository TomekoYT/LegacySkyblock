package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LegacySkyblockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("legacyskyblock")
                        .executes(ctx -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            client.setScreen(LegacySkyblockConfig.configScreen(client.currentScreen));
                            return 1;
                        })
                )
        );
    }
}
