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
    private var pendingReturn = false
    private var screenSeen = false
    private var waitTicks = 0

    fun register() {
        ClientTickEvents.START_CLIENT_TICK.register(::onTick)
    }

    private fun clickHotbar(mc: Minecraft, index: Int) {
        val boundKey = (mc.options.keyHotbarSlots[index] as KeyMappingAccessor).fabric_getBoundKey()
        if (boundKey != InputConstants.UNKNOWN) {
            KeyMapping.click(boundKey)
        }
    }

    private fun resetReturn() {
        pendingReturn = false
        screenSeen = false
        waitTicks = 0
    }

    private fun onTick(mc: Minecraft) {
        val screen =
            //? if >= 26.2
            mc.gui.screen()
        //? else
        //mc.screen

        val player = mc.player
        if (player == null) {
            resetReturn()
            return
        }

        if (pendingReturn) {
            if (screen != null) {
                screenSeen = true
            } else if (screenSeen) {
                if (player.inventory.selectedSlot == 0) {
                    clickHotbar(mc, 8)
                }
                resetReturn()
            } else if (++waitTicks > 10) {
                resetReturn()
            }
        }

        val isDown = mc.options.keyInventory.isDown
        val justPressed = isDown && !wasInventoryKeyDown
        wasInventoryKeyDown = isDown
        if (!LegacySkyblockConfig.preventDroppingSkyblockMenuEnabled
            || !HypixelPackets.inSkyblock
            || !justPressed
            || screen != null
            || pendingReturn
            || player.inventory.selectedSlot != 8
        ) return

        val boundKey = (mc.options.keyHotbarSlots[0] as KeyMappingAccessor).fabric_getBoundKey()
        if (boundKey != InputConstants.UNKNOWN) {
            KeyMapping.click(boundKey)
            pendingReturn = true
            screenSeen = false
            waitTicks = 0
        }
    }
}