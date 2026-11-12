package tomeko.legacyskyblock.dungeons

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.config.v1.annotations.MultiSelectDropdown
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.location.HypixelPackets
import tomeko.legacyskyblock.location.SkyblockIslands
import java.util.LinkedList
import java.util.Queue

object AutoRefill {
    private var lastServerName: String? = null
    private var shouldScanInventory = false
    private var GFSTickDelay = 0
    private val GFSQueue: Queue<String> = LinkedList()
    private var scanTickDelay = 0
    private var scanBlockTime = 0

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(::manageRefill)
        ClientTickEvents.END_CLIENT_TICK.register(::manageGFS)
        ClientTickEvents.END_CLIENT_TICK.register(::scanInventory)
    }

    private fun manageRefill(mc: Minecraft) {
        if ((HypixelPackets.currentIsland != SkyblockIslands.CATACOMBS && HypixelPackets.currentIsland != SkyblockIslands.KUUDRA) || lastServerName == HypixelPackets.currentHypixelServerName) {
            return
        }

        lastServerName = HypixelPackets.currentHypixelServerName
        shouldScanInventory = true
        GFSTickDelay = 80
        scanTickDelay = 20
    }

    private fun scanInventory(client: Minecraft) {
        if (!shouldScanInventory || client.player == null || (HypixelPackets.currentIsland != SkyblockIslands.CATACOMBS && HypixelPackets.currentIsland != SkyblockIslands.KUUDRA)) {
            return
        }

        if (scanTickDelay > 0) {
            scanTickDelay--
            return
        }

        val inventory = client.player!!.inventory

        scanBlockTime++
        if (scanBlockTime > 1000) {
            scanBlockTime = 0
            shouldScanInventory = false
            return
        }

        val refillItems =
            if (HypixelPackets.currentIsland == SkyblockIslands.CATACOMBS)
                LegacySkyblockConfig::class.java
                    .getDeclaredField("dungeonsAutoRefillEnabledItems")
                    .getAnnotation(MultiSelectDropdown::class.java)
                    .options
            else
                LegacySkyblockConfig::class.java
                    .getDeclaredField("kuudraAutoRefillEnabledItems")
                    .getAnnotation(MultiSelectDropdown::class.java)
                    .options

        val refillCounter = IntArray(refillItems.size)

        for (stack in inventory) {
            if (stack.isEmpty) continue

            shouldScanInventory = false

            for ((i, name) in refillItems.withIndex()) {
                if (stack.hoverName.string == name) {
                    refillCounter[i] += stack.count
                }
            }
        }

        if (shouldScanInventory) {
            return
        }

        if (scanTickDelay > 0) {
            shouldScanInventory = true
            scanTickDelay--
            return
        }

        for ((i, name) in refillItems.withIndex()) {
            val item = RefillItems.fromName(name) ?: continue

            val missing = item.itemStackSize - refillCounter[i]

            if (LegacySkyblockConfig.dungeonsAutoRefillEnabledItems[i] && missing > 0) {
                GFSQueue.add("gfs ${item.itemID} $missing")
            }
        }
    }

    private fun manageGFS(client: Minecraft) {
        if (GFSTickDelay > 0) {
            GFSTickDelay--
            return
        }

        if (GFSQueue.isEmpty() || client.player == null) {
            return
        }

        client.player!!.connection.sendCommand(GFSQueue.peek())
        GFSQueue.poll()
        GFSTickDelay = 45
    }
}