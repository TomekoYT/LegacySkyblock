package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import tomeko.legacyskyblock.utils.Debug;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LSBCommand {
    public static void register() {
        Debug.print("LSBCommand registered");
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("lsb")
                        .executes(ctx -> {
                            LegacySkyblockCommand.manageCommand();
                            return 1;
                        })
                )
        );
    }
}
