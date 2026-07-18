package tomeko.legacyskyblock

import net.fabricmc.api.ClientModInitializer
import tomeko.legacyskyblock.commands.*
import tomeko.legacyskyblock.config.*
import tomeko.legacyskyblock.hud.*
import tomeko.legacyskyblock.tooltip.*
import tomeko.legacyskyblock.utils.*

class LegacySkyblock : ClientModInitializer {
    override fun onInitializeClient() {
        LegacySkyblockCommand.register()

        LegacySkyblockConfig.register()

        ActionBar.register()
        PetDisplay.register()

        MissingEnchants.register()

        ShowNBTData.register()

        HypixelPackets.register()

        Debug.forcePrint("Initialized!")
    }
}