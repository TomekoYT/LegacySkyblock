package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LegacySkyblockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("legacyskyblock")
                        .executes(ctx -> {
                            manageCommand();
                            return 1;
                        })
                )
        );
    }

    public static void manageCommand() {
        LegacySkyblockConfig.shouldOpenConfig = true;
    }
}
