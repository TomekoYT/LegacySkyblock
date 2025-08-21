package tomeko.legacyskyblock.commands;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class LegacySkyblockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(literal("legacyskyblock")
                        .executes(ctx -> {
                            MinecraftClient client = MinecraftClient.getInstance();
                            Screen parentScreen = client.currentScreen;

                            ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
                                private boolean opened = false;

                                @Override
                                public void onEndTick(MinecraftClient tickClient) {
                                    if (!opened) {
                                        opened = true;
                                        tickClient.setScreen(LegacySkyblockConfig.configScreen(parentScreen));
                                    }
                                }
                            });
                            return 1;
                        })
                )
        );
    }
}
