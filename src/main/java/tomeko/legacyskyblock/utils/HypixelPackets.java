package tomeko.legacyskyblock.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class HypixelPackets {
    public static boolean inSkyblock = false;
    public static boolean inDungeons = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(HypixelPackets::checkHypixel);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, HypixelPackets::onLocationPacket);
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
    }

    private static void checkHypixel(Minecraft mc) {
        ServerData server = mc.getCurrentServer();
        if (server == null || !server.ip.contains("hypixel")) {
            disableSkyblock();
        }
    }

    private static void onLocationPacket(ClientboundLocationPacket packet) {
        if (packet.getServerType().isEmpty() || !packet.getServerType().get().getName().equalsIgnoreCase("skyblock")) {
            disableSkyblock();
            return;
        }

        inSkyblock = true;
        inDungeons = packet.getMode().orElse(null).equalsIgnoreCase("dungeon");
    }

    private static void disableSkyblock() {
        inSkyblock = false;
        inDungeons = false;
    }
}
