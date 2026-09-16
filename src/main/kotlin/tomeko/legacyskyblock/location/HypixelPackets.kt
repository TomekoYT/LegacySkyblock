package tomeko.legacyskyblock.location

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import tomeko.legacyskyblock.utils.Debug

object HypixelPackets {
    var inSkyblock: Boolean = false
        private set
    var currentIsland: SkyblockIslands? = null
        private set
    var currentHypixelServerName: String? = null
        private set

    fun register() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> disableAll() }
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket::class.java, ::onLocationPacket)
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
    }

    private fun onLocationPacket(packet: ClientboundLocationPacket) {
        if (packet.serverType.isEmpty) {
            disableAll()
            return
        }

        currentHypixelServerName = packet.serverName

        val serverTypeName = packet.serverType.get().name

        Debug.log("Server Type: $serverTypeName")

        inSkyblock = (serverTypeName == "SkyBlock")

        if (packet.mode.isEmpty) {
            disableModes()
            return
        }

        val modeName = packet.mode.get()

        Debug.log("Mode: $modeName")

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
        currentHypixelServerName = null
    }

    private fun disableModes() {
        currentIsland = null
    }
}
