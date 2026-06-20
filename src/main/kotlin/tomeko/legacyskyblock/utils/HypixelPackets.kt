package tomeko.legacyskyblock.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.client.Minecraft

object HypixelPackets {
    @JvmField
    var inSkyblock: Boolean = false

    var currentIsland: SkyblockIslands? = null

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { mc: Minecraft -> checkHypixel(mc) })
        HypixelModAPI.getInstance()
            .createHandler(ClientboundLocationPacket::class.java) { packet: ClientboundLocationPacket ->
                onLocationPacket(packet)
            }
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
    }

    private fun checkHypixel(mc: Minecraft) {
        val server = mc.currentServer
        if (server == null || !server.ip.contains("hypixel")) {
            disableAll()
        }
    }

    private fun onLocationPacket(packet: ClientboundLocationPacket) {
        if (packet.serverType.isEmpty) {
            disableAll()
            return
        }

        val serverTypeName = packet.serverType.get().name

        Debug.println("Server Type: $serverTypeName")

        inSkyblock = (serverTypeName == "SkyBlock")

        if (packet.mode.isEmpty) {
            disableModes()
            return
        }

        val modeName = packet.mode.get()

        Debug.println("Mode: $modeName")

        if (!inSkyblock) {
            currentIsland = null
            return
        }

        currentIsland = SkyblockIslands.fromId(modeName)
    }

    private fun disableAll() {
        disableServerTypes()
        disableModes()
    }

    private fun disableServerTypes() {
        inSkyblock = false
    }

    private fun disableModes() {
        currentIsland = null
    }
}
