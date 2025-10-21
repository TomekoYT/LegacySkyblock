package tomeko.legacyskyblock.utils;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class HypixelPackets {
    public static boolean onHypixel = false;
    public static boolean inSkyblock = false;
    public static boolean inDungeons = false;

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        if (!packet.getServerType().isPresent()) {
            disableHypixel();
            return;
        }
        onHypixel = true;

        if (!packet.getServerType().get().getName().equalsIgnoreCase("skyblock")) {
            disableSkyblock();
            return;
        }
        inSkyblock = true;

        if (!packet.getMode().orElse(null).equalsIgnoreCase("dungeon")) {
            inDungeons = false;
            return;
        }
        inDungeons = true;
    }

    private static void disableSkyblock() {
        inSkyblock = false;
        inDungeons = false;
    }

    private static void disableHypixel() {
        onHypixel = false;
        disableSkyblock();
    }
}
