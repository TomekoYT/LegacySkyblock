package tomeko.legacyskyblock.utils;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class HypixelPackets {
    public static boolean onHypixel = false;
    public static boolean inSkyblock = false;

    public static void onLocationPacket(ClientboundLocationPacket packet) {
        if (!packet.getServerType().isPresent()) {
            onHypixel = false;
            inSkyblock = false;
            return;
        }
        onHypixel = true;

        if (!packet.getServerType().get().getName().equalsIgnoreCase("skyblock")) {
            inSkyblock = false;
            return;
        }
        inSkyblock = true;
    }
}
