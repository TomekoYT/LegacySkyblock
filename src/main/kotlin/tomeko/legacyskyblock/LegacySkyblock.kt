package tomeko.legacyskyblock

import net.fabricmc.api.ClientModInitializer
import tomeko.legacyskyblock.commands.*
import tomeko.legacyskyblock.config.*
import tomeko.legacyskyblock.dungeons.*
import tomeko.legacyskyblock.hud.*
import tomeko.legacyskyblock.location.*
import tomeko.legacyskyblock.misc.*
import tomeko.legacyskyblock.tooltip.*
import tomeko.legacyskyblock.utils.*
import tomeko.legacyskyblock.waypoints.*

class LegacySkyblock : ClientModInitializer {
    override fun onInitializeClient() {
        LegacySkyblockCommand.register()

        LegacySkyblockConfig.register()

        AutoRefill.register()

        ActionBar.register()
        PetDisplay.register()

        HypixelPackets.register()

        PreventDroppingSkyblockMenu.register()

        MissingEnchantments.register()
        ShowNBTData.register()

        BeeheemothWaypoint.register()

        Debug.forceLog("${Constants.MOD_VERSION} Initialized!")
    }
}