package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.Constants;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LegacySkyblockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal(Constants.MOD_ID)
                        .executes(ctx -> {
                            manageCommand();
                            return 1;
                        })
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal(Constants.MOD_ABBREVIATION)
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
