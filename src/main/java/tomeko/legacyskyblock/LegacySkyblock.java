package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.config.*;
import tomeko.legacyskyblock.config.keybinds.*;
import tomeko.legacyskyblock.dungeons.*;
import tomeko.legacyskyblock.hud.custom.*;
import tomeko.legacyskyblock.hud.vanillahud.*;
import tomeko.legacyskyblock.misc.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LegacySkyblockCommand.register();

        ToggleSprintKeybind.register();
        LegacySkyblockConfig.register();

        AutoRefill.register();

        HealthVignette.register();
        ToggleSprint.register();

        ActionBar.register();
        Chat.register();

        NoDeathAnimation.register();

        HypixelPackets.register();
    }
}