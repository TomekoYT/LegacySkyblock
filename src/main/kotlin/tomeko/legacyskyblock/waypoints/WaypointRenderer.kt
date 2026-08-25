package tomeko.legacyskyblock.waypoints

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
//? if >= 26.2 {
/*import net.minecraft.client.renderer.OrderedSubmitNodeCollector
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource
//?}
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
//? if = 26.1 {
import org.joml.Matrix4f
//?}
import org.polyfrost.compose.render.PolyColor

import kotlin.math.*

class Waypoint(
    var pos: BlockPos,
    var boxColor: PolyColor,
    var beamColor: PolyColor,
    var owner: String,
    var renderOwner: Boolean,
    var ownerColor: PolyColor,
    var text: String,
    var renderText: Boolean,
    var textColor: PolyColor,
    var renderDistance: Boolean,
    var distanceTextColor: PolyColor
)

object WaypointRenderer {
    private const val BEACON_PNG = "textures/entity/beacon/beacon_beam.png"

    private const val TEXT_SCALE_START_DISTANCE = 12.0
    private const val TEXT_SCALE_EXPONENT = 1.3

    private val BEAM_TEXTURE = Identifier.parse(BEACON_PNG)

    fun renderWaypoint(
        waypoint: Waypoint?,
        context: LevelRenderContext
    ) {
        if (waypoint == null || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) return

        val camera =
        //? if >= 26.2 {
        //Minecraft.getInstance().gameRenderer.mainCamera()
            //?} else {
            Minecraft.getInstance().gameRenderer.mainCamera
        //?}

        val viewerX = camera.position().x
        val viewerY = camera.position().y
        val viewerZ = camera.position().z

        val renderX = waypoint.pos.x - viewerX
        val renderY = waypoint.pos.y - viewerY
        val renderZ = waypoint.pos.z - viewerZ

        drawBox(
            context.poseStack(),
            //? if >= 26.2 {
            /*context.submitNodeCollector().order(1),
            *///?} else {
            context.bufferSource(),
            //?}
            renderX, renderY, renderZ,
            waypoint.boxColor.red / 255f,
            waypoint.boxColor.green / 255f,
            waypoint.boxColor.blue / 255f,
            waypoint.boxColor.alpha / 255f
        )

        renderBeaconBeam(
            context.poseStack(),
            //? if >= 26.2 {
            /*context.submitNodeCollector().order(1),
            *///?} else {
            context.bufferSource(),
            //?}
            renderX, renderY + 1, renderZ,
            waypoint.beamColor.red / 255f,
            waypoint.beamColor.green / 255f,
            waypoint.beamColor.blue / 255f,
            waypoint.beamColor.alpha / 255f
        )

        val textArgb =
            (waypoint.textColor.alpha shl 24) or (waypoint.textColor.red shl 16) or (waypoint.textColor.green shl 8) or waypoint.textColor.blue
        val ownerArgb =
            (waypoint.ownerColor.alpha shl 24) or (waypoint.ownerColor.red shl 16) or (waypoint.ownerColor.green shl 8) or waypoint.ownerColor.blue
        val distArgb =
            (waypoint.distanceTextColor.alpha shl 24) or (waypoint.distanceTextColor.red shl 16) or (waypoint.distanceTextColor.green shl 8) or waypoint.distanceTextColor.blue

        renderWaypointText(
            context.poseStack(),
            //? if >= 26.2 {
            /*context.submitNodeCollector().order(1),
            *///?} else {
            context.bufferSource(),
            //?}
            waypoint.text, waypoint.owner, waypoint.pos,
            waypoint.renderText, waypoint.renderOwner, waypoint.renderDistance,
            textArgb, ownerArgb, distArgb,
            viewerX, viewerY, viewerZ
        )
    }

    private fun drawBox(
        matrices: PoseStack,
        //? if >= 26.2 {
        /*collector: OrderedSubmitNodeCollector,
        *///?} else {
        consumers: MultiBufferSource,
        //?}
        x: Double, y: Double, z: Double,
        r: Float, g: Float, b: Float, a: Float
    ) {
        matrices.pushPose()
        matrices.translate(x, y, z)

        //? if >= 26.2 {
        /*collector.submitCustomGeometry(matrices, RenderTypes.debugFilledBox()) { pose, buffer ->
            *///?} else {
        val buffer = consumers.getBuffer(RenderTypes.debugFilledBox())
        val pose = matrices.last().pose()
        //?}

        addDoubleSidedQuad(
            buffer, pose, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 1f, 0f, 0f, 1f, r, g, b, a
        )
        addDoubleSidedQuad(
            buffer, pose, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f, 1f, 0f, r, g, b, a
        )
        addDoubleSidedQuad(
            buffer, pose, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 0f, r, g, b, a
        )
        addDoubleSidedQuad(
            buffer, pose, 0f, 0f, 1f, 1f, 0f, 1f, 1f, 1f, 1f, 0f, 1f, 1f, r, g, b, a
        )
        addDoubleSidedQuad(
            buffer, pose, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f, 0f, r, g, b, a
        )
        addDoubleSidedQuad(
            buffer, pose, 1f, 0f, 0f, 1f, 0f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, r, g, b, a
        )

        //? if >= 26.2 {
        /*}
        *///?}
        matrices.popPose()
    }

    private fun addDoubleSidedQuad(
        buffer: VertexConsumer,
        //? if >= 26.2 {
        /*pose: PoseStack.Pose,
        *///?} else {
        pose: Matrix4f,
        //?}
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float,
        r: Float, g: Float, b: Float, a: Float
    ) {
        //? if >= 26.2 {
        /*val p = pose.pose()
        *///?} else {
        val p = pose
        //?}

        buffer.addVertex(p, x1, y1, z1).setColor(r, g, b, a)
        buffer.addVertex(p, x2, y2, z2).setColor(r, g, b, a)
        buffer.addVertex(p, x3, y3, z3).setColor(r, g, b, a)
        buffer.addVertex(p, x4, y4, z4).setColor(r, g, b, a)

        buffer.addVertex(p, x4, y4, z4).setColor(r, g, b, a)
        buffer.addVertex(p, x3, y3, z3).setColor(r, g, b, a)
        buffer.addVertex(p, x2, y2, z2).setColor(r, g, b, a)
        buffer.addVertex(p, x1, y1, z1).setColor(r, g, b, a)
    }

    private fun renderBeaconBeam(
        matrices: PoseStack,
        //? if >= 26.2 {
        /*collector: OrderedSubmitNodeCollector,
        *///?} else {
        consumers: MultiBufferSource,
        //?}
        x: Double, y: Double, z: Double,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val time =
            Minecraft.getInstance().level!!.gameTime + Minecraft.getInstance().deltaTracker.gameTimeDeltaTicks.toDouble()

        val t1 = -time * 0.2
        val d1 = t1 - floor(t1)
        val d14 = (-1.0 + d1).toFloat()
        val d15 = (300.0 * 2.5 + d14).toFloat()
        val d12 = (-1.0 + d1).toFloat()
        val d13 = 300.0f + d12

        val d2 = time * 0.025 * -1.5
        val d4 = (0.5 + cos(d2 + 2.356194490192345) * 0.2).toFloat()
        val d5 = (0.5 + sin(d2 + 2.356194490192345) * 0.2).toFloat()
        val d6 = (0.5 + cos(d2 + (Math.PI / 4)) * 0.2).toFloat()
        val d7 = (0.5 + sin(d2 + (Math.PI / 4)) * 0.2).toFloat()
        val d8 = (0.5 + cos(d2 + 3.9269908169872414) * 0.2).toFloat()
        val d9 = (0.5 + sin(d2 + 3.9269908169872414) * 0.2).toFloat()
        val d10 = (0.5 + cos(d2 + 5.497787143782138) * 0.2).toFloat()
        val d11 = (0.5 + sin(d2 + 5.497787143782138) * 0.2).toFloat()

        matrices.pushPose()
        matrices.translate(x, y, z)
        //? if >= 26.2 {
        /*collector.submitCustomGeometry(matrices, RenderTypes.beaconBeam(BEAM_TEXTURE, true)) { pose, buffer ->
            *///?} else {
        val pose = matrices.last()
        val buffer = consumers.getBuffer(RenderTypes.beaconBeam(BEAM_TEXTURE, true))
        //?}

        val yMin = 0.0f
        val yMax = 300.0f

        renderBeamSide(
            pose, buffer, r, g, b, a, 1.0f, yMin, yMax, d4, d5, d6, d7, 1.0f, 0.0f, d14, d15
        )
        renderBeamSide(
            pose, buffer, r, g, b, a, 1.0f, yMin, yMax, d10, d11, d8, d9, 1.0f, 0.0f, d14, d15
        )
        renderBeamSide(
            pose, buffer, r, g, b, a, 1.0f, yMin, yMax, d6, d7, d10, d11, 1.0f, 0.0f, d14, d15
        )
        renderBeamSide(
            pose, buffer, r, g, b, a, 1.0f, yMin, yMax, d8, d9, d4, d5, 1.0f, 0.0f, d14, d15
        )

        val innerTopA = 0.25f * a
        val innerBotA = 0.25f

        renderBeamSide(
            pose, buffer, r, g, b, innerTopA, innerBotA, yMin, yMax, 0.2f, 0.2f, 0.8f, 0.2f, 1.0f, 0.0f, d12, d13
        )
        renderBeamSide(
            pose, buffer, r, g, b, innerTopA, innerBotA, yMin, yMax, 0.8f, 0.8f, 0.2f, 0.8f, 1.0f, 0.0f, d12, d13
        )
        renderBeamSide(
            pose, buffer, r, g, b, innerTopA, innerBotA, yMin, yMax, 0.8f, 0.2f, 0.8f, 0.8f, 1.0f, 0.0f, d12, d13
        )
        renderBeamSide(
            pose, buffer, r, g, b, innerTopA, innerBotA, yMin, yMax, 0.2f, 0.8f, 0.2f, 0.2f, 1.0f, 0.0f, d12, d13
        )

        //? if >= 26.2 {
        /*}
        *///?}
        matrices.popPose()
    }

    private fun renderBeamSide(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        r: Float, g: Float, b: Float, topA: Float, botA: Float,
        yMin: Float, yMax: Float,
        x1: Float, z1: Float,
        x2: Float, z2: Float,
        u1: Float, u2: Float,
        v1: Float, v2: Float
    ) {
        //? if >= 26.2 {
        /*val p = pose.pose()
        *///?} else {
        val p = pose
        //?}
        buffer.addVertex(p, x1, yMax, z1).setColor(r, g, b, topA).setUv(u1, v2).setUv2(15, 15)
            //? if >= 26.2 {
            /*.setNormal(pose, 0.0f, 1.0f, 0.0f)*///?} else {
            .setNormal(0.0f, 1.0f, 0.0f)//?}
        buffer.addVertex(p, x1, yMin, z1).setColor(r, g, b, botA).setUv(u1, v1).setUv2(15, 15)
            //? if >= 26.2 {
            /*.setNormal(pose, 0.0f, 1.0f, 0.0f)*///?} else {
            .setNormal(0.0f, 1.0f, 0.0f)//?}
        buffer.addVertex(p, x2, yMin, z2).setColor(r, g, b, botA).setUv(u2, v1).setUv2(15, 15)
            //? if >= 26.2 {
            /*.setNormal(pose, 0.0f, 1.0f, 0.0f)*///?} else {
            .setNormal(0.0f, 1.0f, 0.0f)//?}
        buffer.addVertex(p, x2, yMax, z2).setColor(r, g, b, topA).setUv(u2, v2).setUv2(15, 15)
            //? if >= 26.2 {
            /*.setNormal(pose, 0.0f, 1.0f, 0.0f)*///?} else {
            .setNormal(0.0f, 1.0f, 0.0f)//?}
    }

    private fun renderWaypointText(
        matrices: PoseStack,
        //? if >= 26.2 {
        /*collector: OrderedSubmitNodeCollector,
        *///?} else {
        consumers: MultiBufferSource,
        //?}
        str: String, owner: String, loc: BlockPos,
        renderText: Boolean, renderOwner: Boolean, renderDistance: Boolean,
        textArgb: Int, ownerArgb: Int, distArgb: Int,
        viewerX: Double, viewerY: Double, viewerZ: Double
    ) {
        if (!renderText && !renderDistance && !renderOwner) return

        val dx = loc.x + 0.5 - viewerX
        val dy = loc.y + 2.0 - viewerY
        val dz = loc.z + 0.5 - viewerZ

        val distSq = dx * dx + dy * dy + dz * dz
        val dist = sqrt(distSq)
        val distText = "${dist.roundToInt()}m"

        val scaleMultiplier =
            if (dist > TEXT_SCALE_START_DISTANCE)
                (dist / TEXT_SCALE_START_DISTANCE).pow(TEXT_SCALE_EXPONENT).toFloat()
            else
                1f

        val camera =
        //? if >= 26.2 {
                /*Minecraft.getInstance().gameRenderer.mainCamera()
            *///?} else {
            Minecraft.getInstance().gameRenderer.mainCamera
        //?}

        matrices.pushPose()
        matrices.translate(dx, dy, dz)
        matrices.mulPose(Axis.YP.rotationDegrees(-camera.yRot()))
        matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()))

        val scale = 0.025f * scaleMultiplier
        matrices.scale(-scale, -scale, scale)

        var lineOffset = 0

        if (renderOwner && owner.isNotEmpty()) {
            drawNametag(
                owner, ownerArgb, lineOffset, scaleMultiplier,
                matrices,
                //? if >= 26.2 {
                /*collector
                *///?} else {
                consumers
                //?}
            )

            lineOffset += 10
        }

        if (renderText && str.isNotEmpty()) {
            drawNametag(
                str, textArgb, lineOffset, scaleMultiplier,
                matrices,
                //? if >= 26.2 {
                /*collector
                *///?} else {
                consumers
                //?}
            )

            lineOffset += 10
        }

        if (renderDistance) {
            drawNametag(
                distText, distArgb, lineOffset, scaleMultiplier,
                matrices,
                //? if >= 26.2 {
                /*collector
                *///?} else {
                consumers
                //?}
            )
        }

        matrices.popPose()
    }

    private fun drawNametag(
        text: String,
        colorArgb: Int,
        line: Int,
        scaleMultiplier: Float,
        matrices: PoseStack,
        //? if >= 26.2 {
        /*collector: OrderedSubmitNodeCollector
        *///?} else {
        consumers: MultiBufferSource
        //?}
    ) {
        val width = -Minecraft.getInstance().font.width(text) / 2f
        val background = (Minecraft.getInstance().options.textBackgroundOpacity().get() * 255.0).toInt() shl 24

        //? if >= 26.2 {
        /*collector.submitText(
            matrices,
            width,
            line.toFloat(),
            Component.literal(text).visualOrderText,
            true,
            Font.DisplayMode.SEE_THROUGH,
            15728880,
            colorArgb,
            background,
            0
        )
        *///?} else {
        Minecraft.getInstance().font.drawInBatch(
            Component.literal(text),
            width,
            line.toFloat(),
            colorArgb,
            true,
            matrices.last().pose(),
            consumers,
            Font.DisplayMode.SEE_THROUGH,
            background,
            15728880
        )
        //?}
    }
}