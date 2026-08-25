package tomeko.legacyskyblock.waypoints

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.animal.bee.Bee
import org.polyfrost.compose.render.PolyColor
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.SkyblockIslands

object BeeheemothWaypoint {
    private var waypoint: Waypoint? = null

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(::render)
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register { context ->
            WaypointRenderer.renderWaypoint(
                waypoint,
                context
            )
        }
    }

    private fun render(mc: Minecraft) {
        waypoint = null

        if (!LegacySkyblockConfig.showBeeheemothWaypoint || HypixelPackets.currentIsland != SkyblockIslands.TORRHUS_CANYON) return

        val beeheemoth = mc.level?.entitiesForRendering()?.firstOrNull { entity ->
            entity is Bee && entity.boundingBox.xsize > 1.0
        } ?: return

        waypoint = Waypoint(
            pos = beeheemoth.blockPosition(),
            boxColor = PolyColor(0xFFE9AB17.toInt()),
            beamColor = PolyColor(0xFFE9AB17.toInt()),
            owner = "Beeheemoth",
            renderOwner = true,
            ownerColor = PolyColor(0xFFFFFFFF.toInt()),
            text = "",
            renderText = false,
            textColor = PolyColor(0xFFFFFFFF.toInt()),
            renderDistance = true,
            distanceTextColor = PolyColor(0xFFFFFF00.toInt())
        )
    }
}