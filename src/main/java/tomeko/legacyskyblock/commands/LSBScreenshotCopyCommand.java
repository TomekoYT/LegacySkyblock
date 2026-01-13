package tomeko.legacyskyblock.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import tomeko.legacyskyblock.screenshots.ScreenshotManager;
import tomeko.legacyskyblock.utils.Constants;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LSBScreenshotCopyCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal(Constants.SCREENSHOT_COPY_COMMAND)
                        .then(argument("pos", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    int pos = IntegerArgumentType.getInteger(ctx, "pos");
                                    ScreenshotManager.copyScreenshot(pos);
                                    return 1;
                                })
                        )
                )
        );
    }
}