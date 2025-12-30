package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.config.*;
import tomeko.legacyskyblock.dungeons.*;
import tomeko.legacyskyblock.keybinds.*;
import tomeko.legacyskyblock.misc.*;
import tomeko.legacyskyblock.hud.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LegacySkyblockCommand.register();
        ScreenshotCopyCommand.register();
        ScreenshotDeleteCommand.register();

        LegacySkyblockConfig.register();

        AutoRefill.register();
        HideDamageSplash.register();

        ActionBar.register();
        Chat.register();
        HealthVignette.register();

        ToggleSprintKeybind.register();

        AutoTip.register();
        ToggleSprint.register();

        HypixelPackets.register();
    }
}