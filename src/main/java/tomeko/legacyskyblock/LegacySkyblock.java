package tomeko.legacyskyblock;

import net.fabricmc.api.ClientModInitializer;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import tomeko.legacyskyblock.chat.*;
import tomeko.legacyskyblock.commands.*;
import tomeko.legacyskyblock.config.*;
import tomeko.legacyskyblock.utils.*;

public class LegacySkyblock implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MVPEmotes.register();
        LegacySkyblockCommand.register();
        LegacySkyblockConfig.CONFIG.load();
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, HypixelPackets::onLocationPacket);
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
    }
}