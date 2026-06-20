package tomeko.legacyskyblock.hud

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.Debug
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.StringFormatting
import kotlin.math.max

class PetDisplay : LegacyHud("pet-display", "Pet Display", Category.PLAYER) {
    companion object {
        var petNameLine: Component? = null
        var petItemLine: Component? = null
        var petXPLine: Component? = null

        var petName: String? = null
        var petRarity: String? = null
        var petItem: String? = null

        private var tickCooldown = 0

        fun register() {
            HudManager.register(PetDisplay(), Constants.MOD_ID)
            ClientTickEvents.END_CLIENT_TICK.register(PetDisplay::searchTab)
            ClientReceiveMessageEvents.GAME.register(PetDisplay::onAutoPetMessage)
        }

        private fun searchTab(mc: Minecraft) {
            if (!HypixelPackets.inSkyblock) {
                resetAll()
                return
            }

            if (tickCooldown > 0) {
                tickCooldown--
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
                var component = player.tabListDisplayName ?: Component.literal(fallbackName)

                val plainText = StringFormatting.removeFormatting(component.string).trim()

                if (!foundHeader) {
                    if (plainText == "Pet:") {
                        foundHeader = true
                    }
                    continue
                }

                if (!parsedName) {
                    if (plainText.endsWith(" ✦")) {
                        removeSkinStar(plainText)
                        if (!component.siblings.isEmpty()) {
                            val copy: MutableComponent = component.copy()
                            copy.siblings.removeLast()
                            component = copy
                        }
                    }

                    val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(plainText)
                    if (match != null) {
                        component = removeBlankSpaceAtBeginning(component)
                        petNameLine = component
                        petName = match.groupValues[2]
                        petRarity = getRarityFromColor(component.siblings[1].style.color!!.value)
                        parsedName = true
                        continue
                    } else {
                        if (plainText == "No pet selected") resetAll()
                        break
                    }
                }

                if (!parsedItemOrXp) {
                    if (isXpLine(plainText)) {
                        component = removeBlankSpaceAtBeginning(component)
                        petXPLine = component
                        resetItem()
                        break
                    } else {
                        if (plainText.isEmpty() || plainText == "MAX LEVEL") {
                            resetItem()
                            resetXP()
                            break
                        }

                        component = removeBlankSpaceAtBeginning(component)
                        petItemLine = component
                        petItem = plainText
                        parsedItemOrXp = true
                        continue
                    }
                }

                if (isXpLine(plainText)) {
                    component = removeBlankSpaceAtBeginning(component)
                    petXPLine = component
                    break
                }

                resetXP()
                break
            }
        }

        private fun onAutoPetMessage(message: Component, fromActionBar: Boolean) {
            if (fromActionBar) return

            val match = Regex(
                "^§cAutopet §eequipped your (§7\\[Lvl (\\d+)\\] (§.)((?:[^§]|§.)+?))(?:§d ✦)?§e! §a§lVIEW RULE\$"
            ).find(
                message.string
            ) ?: return

            setTickCooldown()
            petNameLine = Component.literal(match.groupValues[1])
            petRarity = getRarityFromColor(match.groupValues[3])
            petName = match.groupValues[4]

            resetItem()
            resetXP()
        }

        fun getRarityFromColor(color: Int): String? = when (color) {
            16777215 -> "COMMON"
            5635925 -> "UNCOMMON"
            5592575 -> "RARE"
            11141290 -> "EPIC"
            16755200 -> "LEGENDARY"
            16733695 -> "MYTHIC"
            else -> null
        }

        fun getRarityFromColor(color: String): String? = when (color) {
            "§f" -> "COMMON"
            "§a" -> "UNCOMMON"
            "§9" -> "RARE"
            "§5" -> "EPIC"
            "§6" -> "LEGENDARY"
            "§d" -> "MYTHIC"
            else -> null
        }

        private fun shouldShowPetItem(): Boolean {
            return LegacySkyblockConfig.petDisplayShowItem && petItemLine != null
        }

        private fun shouldShowPetXP(): Boolean {
            return LegacySkyblockConfig.petDisplayShowXP && petXPLine != null
        }

        fun resetAll() {
            petNameLine = null
            petName = null
            petRarity = null
            resetItem()
            resetXP()
        }

        fun resetItem() {
            petItemLine = null
            petItem = null
        }

        fun resetXP() {
            petXPLine = null
        }

        private fun isXpLine(text: String): Boolean {
            return text.firstOrNull()?.isDigit() == true || text.firstOrNull() == '+'
        }

        private fun removeSkinStar(name: String): String {
            if (name.endsWith(" ✦")) return name.dropLast(2)
            return name
        }

        private fun removeBlankSpaceAtBeginning(component: Component): Component {
            val copy: MutableComponent = component.copy()
            copy.siblings.removeFirst()
            return copy
        }

        fun setTickCooldown() {
            tickCooldown = 67
        }
    }

    @Slider(
        title = "Icon Size",
        min = 0f,
        max = 64f,
        step = 1f
    )
    var iconSize = 16f

    @Slider(
        title = "Icon Padding",
        min = 0f,
        max = 10f,
        step = 0.1f
    )
    var iconPadding = 3f

    @Slider(
        title = "Lines Padding",
        min = 0f,
        max = 10f,
        step = 0.1f
    )
    var linesPadding = 3f

    private var actualWidth = 0f
    private var actualHeight = 0f

    override fun render(mcCtx: GuiGraphicsExtractor) {
        if (!HypixelPackets.inSkyblock
            || !LegacySkyblockConfig.petDisplayEnabled
            || petNameLine == null
            || petName == null
            || petRarity == null
        ) return

        val mc = Minecraft.getInstance()

        val icon: ItemStack? = SimpleItemAPI.getPetByIdOrNull(
            SkyBlockId.pet(
                petName!!.uppercase().replace(" ", "_"),
                petRarity!!
            )
        )

        var maxTextWidth = mc.font.width(petNameLine!!.string).toFloat()
        var textLines = 1

        if (shouldShowPetItem()) {
            maxTextWidth = max(maxTextWidth, mc.font.width(petItemLine!!.string).toFloat())
            textLines++
        }
        if (shouldShowPetXP()) {
            maxTextWidth = max(maxTextWidth, mc.font.width(petXPLine!!.string).toFloat())
            textLines++
        }

        val textHeight = textLines * mc.font.lineHeight + max(0, textLines - 1) * linesPadding

        val hasIcon = (LegacySkyblockConfig.petDisplayShowIcon && icon != null)
        val actualIconSize = if (hasIcon) iconSize else 0f
        val actualIconPadding = if (hasIcon) iconPadding else 0f

        actualWidth = actualIconSize + actualIconPadding + maxTextWidth
        actualHeight = max(actualIconSize, textHeight)

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

        if (hasIcon) {
            val itemY = (actualHeight - actualIconSize) / 2f
            val scale = actualIconSize / 16f

            mcCtx.pose().pushMatrix()
            mcCtx.pose().translate(0f, itemY)
            mcCtx.pose().scale(scale, scale)

            mcCtx.item(icon, 0, 0)

            mcCtx.pose().popMatrix()
        }

        val textStartX = actualIconSize + actualIconPadding
        var textY = (actualHeight - textHeight) / 2f

        renderComponent(mcCtx, petNameLine!!, textStartX, textY)
        textY += (mc.font.lineHeight + linesPadding)

        if (shouldShowPetItem()) {
            renderComponent(mcCtx, petItemLine!!, textStartX, textY)
            textY += (mc.font.lineHeight + linesPadding)
        }

        if (shouldShowPetXP()) {
            renderComponent(mcCtx, petXPLine!!, textStartX, textY)
        }

        mcCtx.pose().popMatrix()
    }

    private fun renderComponent(mcCtx: GuiGraphicsExtractor, component: Component, textX: Float, textY: Float) {
        val mc = Minecraft.getInstance()

        if (showShadow) {
            mcCtx.text(
                mc.font,
                component.string,
                textX.toInt() + 1,
                textY.toInt() + 1,
                shadowColor,
                false
            )
        }

        mcCtx.text(
            mc.font,
            component,
            textX.toInt(),
            textY.toInt(),
            0xFFFFFFFF.toInt(),
            false
        )
    }

    override val height: Float = actualHeight
    override val width: Float = actualWidth
    override fun update(): Boolean = true
    override fun multipleInstancesAllowed(): Boolean = false
}