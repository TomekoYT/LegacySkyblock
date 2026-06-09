package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LegacySkyblockCommand.register();

        HypixelPackets.register();
    }
}