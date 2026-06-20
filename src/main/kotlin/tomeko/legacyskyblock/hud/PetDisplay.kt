package tomeko.legacyskyblock.hud

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.Debug
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.StringFormatting
import kotlin.math.max

class PetDisplay : LegacyHud("pet-display", "Pet Display", Category.PLAYER) {
    companion object {
        var petName: String? = null
        var petLevel: Int? = null
        var petItem: String? = null
        var petXP: String? = null

        var formattedPetNameLine: Component? = null
        var formattedPetItemLine: Component? = null
        var formattedPetXPLine: Component? = null

        fun register() {
            HudManager.register(PetDisplay(), Constants.MOD_ID)
            ClientTickEvents.END_CLIENT_TICK.register(PetDisplay::searchTab)
        }

        private fun searchTab(mc: Minecraft) {
            if (!HypixelPackets.inSkyblock) {
                resetAll()
                return
            }

            val connection = mc.connection ?: return

            val sortedPlayers = connection.listedOnlinePlayers.sortedWith(
                compareBy<PlayerInfo> { it.team?.name ?: "" }
                    .thenBy { it.profile.name ?: "" }
            )

            var foundHeader = false
            var parsedName = false
            var parsedItemOrXp = false

            for (player in sortedPlayers) {
                val fallbackName = player.profile.name ?: ""
                val component = player.tabListDisplayName ?: Component.literal(fallbackName)

                val plainText = StringFormatting.removeFormatting(component.string).trim()

                if (plainText.isEmpty()) continue

                if (!foundHeader) {
                    if (plainText == "Pet:") {
                        foundHeader = true
                    }
                    continue
                }

                if (!parsedName) {
                    val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(plainText)
                    if (match != null) {
                        petLevel = match.groupValues[1].toIntOrNull()
                        petName = match.groupValues[2]
                        formattedPetNameLine = component
                        parsedName = true
                        continue
                    } else {
                        resetAll()
                        break
                    }
                }

                if (!parsedItemOrXp) {
                    if (isXpLine(plainText)) {
                        resetItem()
                        petXP = plainText
                        formattedPetXPLine = component
                        break
                    } else {
                        if (plainText == "MAX LEVEL") {
                            resetItem()
                            resetXP()
                            break
                        }

                        petItem = plainText
                        formattedPetItemLine = component
                        parsedItemOrXp = true
                        continue
                    }
                }

                if (isXpLine(plainText)) {
                    petXP = plainText
                    formattedPetXPLine = component
                    break
                }

                resetXP()
                break
            }

            Debug.println("Pet Name in Tab: " + (formattedPetNameLine?.string ?: "None"))
            Debug.println("Pet Item in Tab: " + (formattedPetItemLine?.string ?: "None"))
            Debug.println("Pet XP in Tab: " + (formattedPetXPLine?.string ?: "None"))
        }

        private fun shouldShowPetItem(): Boolean {
            return LegacySkyblockConfig.petDisplayShowItem && formattedPetItemLine != null
        }

        private fun shouldShowPetXP(): Boolean {
            return LegacySkyblockConfig.petDisplayShowXP && formattedPetXPLine != null
        }

        private fun isXpLine(text: String): Boolean {
            return text.firstOrNull()?.isDigit() == true || text.firstOrNull() == '+'
        }

        private fun resetAll() {
            petName = null
            petLevel = null
            formattedPetNameLine = null
            resetItem()
            resetXP()
        }

        private fun resetItem() {
            petItem = null
            formattedPetItemLine = null
        }

        private fun resetXP() {
            petXP = null
            formattedPetXPLine = null
        }
    }

    @Slider(
        title = "Text Padding",
        min = 0f,
        max = 10f,
        step = 0.1f
    )
    var textPadding = 5f

    private var actualWidth = 0f
    private var actualHeight = 0f

    override fun render(mcCtx: GuiGraphicsExtractor) {
        if (!HypixelPackets.inSkyblock || !LegacySkyblockConfig.petDisplayEnabled) return

        if (formattedPetNameLine == null) return

        val mc = Minecraft.getInstance()

        actualWidth = mc.font.width(formattedPetNameLine!!.string).toFloat()

        var size = 1
        if (shouldShowPetItem()) {
            actualWidth = max(actualWidth, mc.font.width(formattedPetItemLine!!.string).toFloat())
            size++
        }
        if (shouldShowPetXP()) {
            actualWidth = max(actualWidth, mc.font.width(formattedPetXPLine!!.string).toFloat())
            size++
        }

        actualHeight = size * (mc.font.lineHeight + textPadding) - textPadding

        mcCtx.pose().pushMatrix()

        if (showBackground) {
            mcCtx.fill(
                -bgRadius.toInt(),
                -bgRadius.toInt(),
                (actualWidth + bgRadius).toInt(),
                (actualHeight + bgRadius).toInt(),
                bgColor
            )
        }

        var textY = 0f
        renderComponent(mcCtx, formattedPetNameLine!!, textY)
        textY += (mc.font.lineHeight + textPadding)

        if (shouldShowPetItem()) {
            renderComponent(mcCtx, formattedPetItemLine!!, textY)
            textY += (mc.font.lineHeight + textPadding)
        }

        if (shouldShowPetXP()) {
            renderComponent(mcCtx, formattedPetXPLine!!, textY)
        }

        mcCtx.pose().popMatrix()
    }

    override val height: Float = actualHeight
    override val width: Float = actualWidth
    override fun update(): Boolean = true
    override fun multipleInstancesAllowed(): Boolean = false

    private fun renderComponent(mcCtx: GuiGraphicsExtractor, component: Component, textY: Float) {
        val mc = Minecraft.getInstance()

        if (showShadow) {
            mcCtx.text(
                mc.font,
                component.string,
                1,
                textY.toInt() + 1,
                shadowColor,
                false
            )
        }

        mcCtx.text(
            mc.font,
            component,
            0,
            textY.toInt(),
            0xFFFFFFFF.toInt(),
            false
        )
    }
}