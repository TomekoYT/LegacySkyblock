package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.config.*;
import tomeko.legacyskyblock.config.keybinds.*;
import tomeko.legacyskyblock.dungeons.*;
import tomeko.legacyskyblock.screen.*;
import tomeko.legacyskyblock.screen.hud.custom.*;
import tomeko.legacyskyblock.screen.hud.vanillahud.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LegacySkyblockCommand.register();
        ScreenshotCopyCommand.register();
        ScreenshotDeleteCommand.register();

        ToggleSprintKeybind.register();
        LegacySkyblockConfig.register();

        AutoRefill.register();
        HideDamageSplash.register();

        HealthVignette.register();
        ToggleSprint.register();

        ActionBar.register();
        Chat.register();

        NoDeathAnimation.register();

        HypixelPackets.register();
    }
}