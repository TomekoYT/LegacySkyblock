package tomeko.legacyskyblock

import net.fabricmc.api.ClientModInitializer
import tomeko.legacyskyblock.commands.*
import tomeko.legacyskyblock.config.*
import tomeko.legacyskyblock.dungeons.*
import tomeko.legacyskyblock.hud.*
import tomeko.legacyskyblock.utils.*

class LegacySkyblock : ClientModInitializer {
    override fun onInitializeClient() {
        LegacySkyblockCommand.register()

        LegacySkyblockConfig.register()

        HideDamageSplash.register()

        ActionBar.register()
        PetDisplay.register()

        HypixelPackets.register()

        Debug.println("Initialized!")
    }
}