package tomeko.legacyskyblock.misc

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.mixin.client.keymapping.KeyMappingAccessor
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.location.HypixelPackets

object PreventDroppingSkyblockMenu {
    private var wasInventoryKeyDown = false

    fun register() {
        ClientTickEvents.START_CLIENT_TICK.register(::onTick)
    }

    private fun onTick(mc: Minecraft) {
        val isDown = mc.options.keyInventory.isDown
        val justPressed = isDown && !wasInventoryKeyDown
        wasInventoryKeyDown = isDown
        if (!LegacySkyblockConfig.preventDroppingSkyblockMenuEnabled || !HypixelPackets.inSkyblock || !justPressed) return

        val screen =
            //? if >= 26.2
            mc.gui.screen()
        //? else
        //mc.screen
        if (screen != null) return

        val player = mc.player ?: return
        if (player.inventory.selectedSlot != 8) return

        val boundKey = (mc.options.keyHotbarSlots[0] as KeyMappingAccessor).fabric_getBoundKey()

        if (boundKey != InputConstants.UNKNOWN) {
            KeyMapping.click(boundKey)
        }
    }
}