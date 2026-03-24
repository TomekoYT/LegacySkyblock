package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.config.*;
import tomeko.legacyskyblock.dungeons.*;
import tomeko.legacyskyblock.hud.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LegacySkyblockCommand.register();
        LSBCommand.register();

        LegacySkyblockConfig.register();

        HideDamageSplash.register();

        ActionBar.register();
        Chat.register();

        HypixelPackets.register();
    }
}