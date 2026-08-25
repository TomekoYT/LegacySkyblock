package tomeko.legacyskyblock

import net.fabricmc.api.ClientModInitializer
import tomeko.legacyskyblock.commands.*
import tomeko.legacyskyblock.config.*
import tomeko.legacyskyblock.dungeons.*
import tomeko.legacyskyblock.hud.*
import tomeko.legacyskyblock.tooltip.*
import tomeko.legacyskyblock.utils.*
import tomeko.legacyskyblock.waypoints.BeeheemothWaypoint

class LegacySkyblock : ClientModInitializer {
    override fun onInitializeClient() {
        LegacySkyblockCommand.register()

        LegacySkyblockConfig.register()

        AutoRefill.register()

        ActionBar.register()
        PetDisplay.register()

        MissingEnchantments.register()
        ShowNBTData.register()

        HypixelPackets.register()

        BeeheemothWaypoint.register()

        Debug.forceLog("Initialized!")
    }
}