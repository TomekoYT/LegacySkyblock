package tomeko.legacyskyblock.dungeons

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.HypixelPackets

object HideDamageSplash {
    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { mc: Minecraft -> hideSplash(mc) })
    }

    private fun hideSplash(mc: Minecraft) {
        if (
            !HypixelPackets.inSkyblock
            || mc.isPaused
            || mc.level == null
            || HypixelPackets.currentIsland == null
            || !LegacySkyblockConfig.hideDamageSplashEnabledIslands[HypixelPackets.currentIsland!!.ordinal]
        ) return


        val entities: Iterable<Entity?> = mc.level!!.entitiesForRendering()
        for (entity in entities) {
            if (entity == null) continue

            if (entity is ArmorStand) {
                var name = entity.name.string.replace(",", "").replace(".", "")

                if (name.endsWith("k") || name.endsWith("M")) {
                    name = name.substring(0, name.length - 1)
                }

                try {
                    name.toLong()
                    entity.discard()
                    continue
                } catch (_: NumberFormatException) {
                }

                if (name.length >= 2) {
                    name = name.substring(1, name.length - 1)
                }

                if (name.endsWith("k") || name.endsWith("M")) {
                    name = name.substring(0, name.length - 1)
                }

                try {
                    name.toLong()
                    entity.discard()
                } catch (_: NumberFormatException) {
                }
            }
        }
    }
}
