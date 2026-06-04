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
            disableAll();
        }
    }

    private static void onLocationPacket(ClientboundLocationPacket packet) {
        if (packet.getServerType().isEmpty()) {
            disableAll();
            return;
        }

        String serverTypeName = packet.getServerType().get().getName();

        inSkyblock = serverTypeName.equals("SkyBlock");

        if (packet.getMode().isEmpty()) {
            disableModes();
            return;
        }

        String modeName = packet.getMode().get();

        inDungeons = inSkyblock && modeName.equals("dungeon");
    }

    private static void disableAll() {
        disableServerTypes();
        disableModes();
    }

    private static void disableServerTypes() {
        inSkyblock = false;
    }

    private static void disableModes() {
        inDungeons = false;
    }
}
