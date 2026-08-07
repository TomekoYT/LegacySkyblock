package tomeko.legacyskyblock.hud

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.HypixelPackets

object ActionBar {
    fun register() {
        ClientReceiveMessageEvents.MODIFY_GAME.register(ClientReceiveMessageEvents.ModifyGame { message: Component, fromActionBar: Boolean ->
            var message = message
            if (!fromActionBar || !HypixelPackets.inSkyblock) {
                return@ModifyGame message
            }

            //Hide True Defense
            if (LegacySkyblockConfig.actionBarHideTrueDefense) {
                message = replaceActionBar(message, "§f\\d+.")
            }

            //Hide Defense
            if (LegacySkyblockConfig.actionBarHideDefense) {
                message = replaceActionBar(message, "§a\\d+.")
            }

            return@ModifyGame message
        })
    }

    private fun replaceActionBar(message: Component, replace: String): Component {
        var message = message
        val blank = " {5}"
        message = Component.nullToEmpty(message.string.replace((replace + blank).toRegex(), ""))
        message = Component.nullToEmpty(message.string.replace((blank + replace).toRegex(), ""))
        message = Component.nullToEmpty(message.string.replace(replace.toRegex(), ""))
        return message
    }
}
