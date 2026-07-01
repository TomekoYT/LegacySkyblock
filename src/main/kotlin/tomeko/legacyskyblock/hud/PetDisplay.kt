package tomeko.legacyskyblock.hud

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.polyfrost.oneconfig.api.config.v1.annotations.Include
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.parseNumber
import tomeko.legacyskyblock.utils.removeFormatting
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.round

class PetDisplay : LegacyHud("pet-display", "Pet Display", Category.PLAYER) {
    companion object {
        @Include
        var petName: String? = null

        @Include
        var petLevel: String? = null

        @Include
        var petRarity: String? = null

        @Include
        var petItem: String? = null

        @Include
        var petItemRarity: String? = null

        @Include
        var petXPLeft: String? = null

        @Include
        var petXPRight: String? = null

        private var tickCooldown = 0

        fun register() {
            HudManager.register(PetDisplay(), Constants.MOD_ID)
            ClientTickEvents.END_CLIENT_TICK.register(PetDisplay::searchTab)
            ClientReceiveMessageEvents.GAME.register(PetDisplay::onAutoPetMessage)
            ClientTickEvents.END_CLIENT_TICK.register(PetDisplay::scanLoadoutsMenu)
        }

        private fun searchTab(mc: Minecraft) {
            if (tickCooldown > 0) {
                tickCooldown--
                return
            }

            if (!HypixelPackets.inSkyblock) return

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

                var plainText = component.string.removeFormatting().trim()

                if (!foundHeader) {
                    if (plainText == "Pet:") {
                        foundHeader = true
                    }
                    continue
                }

                if (!parsedName) {
                    if (plainText.endsWith(" ✦")) {
                        plainText = removeSkinStar(plainText)
                        if (!component.siblings.isEmpty()) {
                            val copy: MutableComponent = component.copy()
                            copy.siblings.removeLast()
                            component = copy
                        }
                    }

                    val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(plainText)
                    if (match != null) {
                        petLevel = match.groupValues[1]
                        petName = match.groupValues[2]
                        petRarity = getRarityFromComponentColor(component.siblings[2].style.color!!.value)
                        parsedName = true
                        continue
                    } else {
                        if (plainText == "No pet selected") resetAll()
                        break
                    }
                }

                if (!parsedItemOrXp) {
                    if (isXpLine(plainText)) {
                        setPetXPFromTab(component)
                        resetItem()
                        break
                    } else {
                        if (plainText.isEmpty() || plainText == "MAX LEVEL" || plainText.firstOrNull() == '+') {
                            resetItem()
                            resetXP()
                            break
                        }

                        petItem = plainText
                        petItemRarity = getRarityFromComponentColor(component.siblings[1].style.color!!.value)
                        parsedItemOrXp = true
                        continue
                    }
                }

                if (isXpLine(plainText)) {
                    setPetXPFromTab(component)
                    break
                }

                resetXP()
                break
            }
        }

        private fun onAutoPetMessage(message: Component, fromActionBar: Boolean) {
            if (fromActionBar) return

            val match = Regex(
                "^§cAutopet §eequipped your §7\\[Lvl (\\d+)] (§.)((?:[^§]|§.)+?)(?:§d ✦)?§e! §a§lVIEW RULE$"
            ).find(
                message.string
            ) ?: return

            setTickCooldown()
            petLevel = match.groupValues[1]
            petRarity = getRarityFromChatColor(match.groupValues[2])
            petName = match.groupValues[3]

            resetItem()
            resetXP()
        }

        private fun scanLoadoutsMenu(mc: Minecraft) {
            if (!HypixelPackets.inSkyblock) return

            val screen =
            //? if >= 26.2 {
                    /*mc.gui.screen()
                    *///?} else {
                mc.screen
            //?}
            if (screen !is ContainerScreen || !screen.title.string.endsWith("Loadouts")) return

            val item = screen.menu.container.getItem(21)
            val component = removeFavoriteAndSkinStar(item.hoverName)
            val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(component.string)

            if (match == null) {
                resetAll()
                return
            }

            val level = match.groupValues[1]
            val name = match.groupValues[2]

            setTickCooldown()
            petName = name
            petLevel = level
            petRarity = getRarityFromComponentColor(component.siblings[1].style.color!!.value)

            val tooltip: MutableList<Component> =
                item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL)

            searchForPetItemInTooltip(tooltip)
            searchForPetXPInTooltip(tooltip)
        }

        fun getRarityFromComponentColor(color: Int): String? = when (color) {
            16777215 -> "COMMON"
            5635925 -> "UNCOMMON"
            5592575 -> "RARE"
            11141290 -> "EPIC"
            16755200 -> "LEGENDARY"
            16733695 -> "MYTHIC"
            else -> null
        }

        fun getRarityFromChatColor(color: String): String? = when (color) {
            "§f" -> "COMMON"
            "§a" -> "UNCOMMON"
            "§9" -> "RARE"
            "§5" -> "EPIC"
            "§6" -> "LEGENDARY"
            "§d" -> "MYTHIC"
            else -> null
        }

        fun getChatColorFromRarity(rarity: String): String? = when (rarity) {
            "COMMON" -> "§f"
            "UNCOMMON" -> "§a"
            "RARE" -> "§9"
            "EPIC" -> "§5"
            "LEGENDARY" -> "§6"
            "MYTHIC" -> "§d"
            else -> null
        }

        private fun shouldShowPetItem(): Boolean {
            return LegacySkyblockConfig.petDisplayShowItem && petItem != null && petItemRarity != null
        }

        private fun shouldShowPetItemIcon(): Boolean {
            return LegacySkyblockConfig.petDisplayShowIcon && shouldShowPetItem() && LegacySkyblockConfig.petDisplayShowItemIcon
        }

        private fun shouldShowPetXP(): Boolean {
            return LegacySkyblockConfig.petDisplayShowXP && petXPLeft != null && petXPRight != null
        }

        fun resetAll() {
            petName = null
            petLevel = null
            petRarity = null
            resetItem()
            resetXP()
        }

        fun resetItem() {
            petItem = null
            petItemRarity = null
        }

        fun resetXP() {
            petXPLeft = null
            petXPRight = null
        }

        private fun setPetXPFromTab(component: Component) {
            petXPLeft = component.siblings[1].string
            petXPRight = component.siblings[3].string
            if (petXPRight!!.endsWith(" XP ")) {
                petXPRight = petXPRight!!.dropLast(4)
            }
        }

        private fun isXpLine(text: String): Boolean {
            return text.firstOrNull()?.isDigit() == true
        }

        private fun removeSkinStar(name: String): String {
            if (name.endsWith(" ✦")) return name.dropLast(2)
            return name
        }

        fun setTickCooldown() {
            tickCooldown = 60
        }

        fun searchForPetItemInTooltip(tooltip: MutableList<Component>) {
            for (line in tooltip) {
                val pattern = Pattern.compile("^Held Item: (.*)$")
                val matcher = pattern.matcher(line.string)
                if (matcher.find()) {
                    petItem = matcher.group(1)
                    petItemRarity = getRarityFromComponentColor(line.siblings[1].style.color!!.value)
                    return
                }
            }

            resetItem()
        }

        fun searchForPetXPInTooltip(tooltip: MutableList<Component>) {
            for (line in tooltip) {
                val pattern = Pattern.compile("^ {26}(.*)$")
                val matcher = pattern.matcher(line.string)
                if (matcher.find()) {
                    val copy = line.copy()
                    for (i in 0..3) {
                        if (copy.siblings.first().string.trim().parseNumber() != null) break

                        copy.siblings.removeFirst()
                    }
                    petXPLeft = copy.siblings.first().string
                    petXPRight = copy.siblings[2].string
                    return
                }
            }

            resetXP()
        }

        fun removeFavoriteAndSkinStar(name: Component): Component {
            if (name.siblings.isEmpty()) return name

            val copy = name.copy()

            val first: Component = copy.siblings.first()
            if (first.string.startsWith("⭐ ")) {
                copy.siblings.removeFirst()
            }

            if (copy.siblings.isEmpty()) return copy

            val last: Component = copy.siblings.last()
            if (last.string.endsWith(" ✦")) {
                copy.siblings.removeLast()
            }

            return copy
        }
    }

    @Slider(
        title = "Icon Size",
        min = 1f,
        max = 64f,
        step = 1f
    )
    var iconSize = 32f

    @Slider(
        title = "Item Icon Size",
        min = 1f,
        max = 32f,
        step = 1f
    )
    var itemIconSize = 16f

    @Slider(
        title = "Icon Padding",
        min = 0f,
        max = 10f,
        step = 0.1f
    )
    var iconPadding = 3f

    @Slider(
        title = "Item Icon Padding",
        min = 0f,
        max = 5f,
        step = 0.1f
    )
    var itemIconPadding = 2f

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
            || petName == null
            || petLevel == null
            || petRarity == null
        ) return

        val mc = Minecraft.getInstance()

        val icon: ItemStack? = SimpleItemAPI.getPetByIdOrNull(
            SkyBlockId.pet(
                petName!!.uppercase().replace(" ", "_"),
                petRarity!!
            )
        )

        var petNameLine = ""
        if (LegacySkyblockConfig.petDisplayShowLevel) petNameLine += "§7[Lvl $petLevel] "
        petNameLine += "${getChatColorFromRarity(petRarity!!)}${petName}"

        var maxTextWidth = mc.font.width(petNameLine).toFloat()
        var textLines = 1

        var petItemLine = ""
        if (shouldShowPetItem()) {
            petItemLine = "${getChatColorFromRarity(petItemRarity!!)}${petItem}"
            maxTextWidth = max(maxTextWidth, mc.font.width(petItemLine).toFloat())
            textLines++
        }

        var petXPLine = ""
        if (shouldShowPetXP()) {
            petXPLine = "§e${petXPLeft!!}§6/§e${petXPRight} XP"
            if (LegacySkyblockConfig.petDisplayShowXPPercentage) {
                val leftNum = petXPLeft!!.parseNumber() ?: 0.0
                val rightNum = petXPRight!!.parseNumber() ?: 1.0
                val percentage = if (rightNum > 0) (leftNum / rightNum) * 100 else 0.0

                petXPLine += " §6(${round(percentage * 10) / 10}%)"
            }
            maxTextWidth = max(maxTextWidth, mc.font.width(petXPLine).toFloat())
            textLines++
        }

        val textHeight = textLines * mc.font.lineHeight + max(0, textLines - 1) * linesPadding

        val hasIcon = (LegacySkyblockConfig.petDisplayShowIcon && icon != null)
        val hasItemIcon = hasIcon && shouldShowPetItemIcon() && petItem != null

        val actualIconSize = if (hasIcon) iconSize else 0f
        val actualIconPadding = if (hasIcon) iconPadding else 0f

        val effectiveItemIconPadding = itemIconPadding - 3f
        val actualItemIconPadding = if (hasItemIcon) max(0f, (itemIconSize / 2f) + effectiveItemIconPadding) else 0f

        val coreHeight = max(actualIconSize, textHeight)
        val itemY = (coreHeight - actualIconSize) / 2f
        var textY = (coreHeight - textHeight) / 2f

        val textStartX = actualIconSize + actualItemIconPadding + actualIconPadding
        actualWidth = textStartX + maxTextWidth

        val badgeBottomY = itemY + actualIconSize + actualItemIconPadding
        actualHeight = max(coreHeight, badgeBottomY)

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
            val scale = actualIconSize / 16f

            mcCtx.pose().pushMatrix()
            mcCtx.pose().translate(0f, itemY)
            mcCtx.pose().scale(scale, scale)
            mcCtx.item(icon, 0, 0)
            mcCtx.pose().popMatrix()

            if (hasItemIcon) {
                val itemIconID =
                    if (petItem!!.endsWith("Boost"))
                        "PET_ITEM_${petItem!!.substringBefore(" ").uppercase()}_SKILL_BOOST_${petItemRarity}"
                    else
                        petItem!!.uppercase().replace(" ", "_")
                var itemIcon: ItemStack? = SimpleItemAPI.getItemByIdOrNull(SkyBlockId.item(itemIconID))
                if (itemIcon == null) itemIcon = SimpleItemAPI.getPetByIdOrNull(SkyBlockId.item("PET_ITEM_$itemIconID"))

                if (itemIcon != null) {
                    val badgeX = actualIconSize - (itemIconSize / 2f) + effectiveItemIconPadding
                    val badgeY = (itemY + actualIconSize) - (itemIconSize / 2f) + effectiveItemIconPadding

                    val badgeScale = itemIconSize / 16f

                    mcCtx.pose().pushMatrix()
                    mcCtx.pose().translate(badgeX, badgeY)
                    mcCtx.pose().scale(badgeScale, badgeScale)

                    mcCtx.item(itemIcon, 0, 0)
                    mcCtx.pose().popMatrix()
                }
            }
        }

        renderComponent(mcCtx, Component.literal(petNameLine), textStartX, textY)
        textY += (mc.font.lineHeight + linesPadding)

        if (shouldShowPetItem()) {
            renderComponent(mcCtx, Component.literal(petItemLine), textStartX, textY)
            textY += (mc.font.lineHeight + linesPadding)
        }

        if (shouldShowPetXP()) {
            renderComponent(mcCtx, Component.literal(petXPLine), textStartX, textY)
        }

        mcCtx.pose().popMatrix()
    }

    private fun renderComponent(mcCtx: GuiGraphicsExtractor, component: Component, textX: Float, textY: Float) {
        val mc = Minecraft.getInstance()

        if (showShadow) {
            mcCtx.text(
                mc.font,
                component.string.removeFormatting(),
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
    override fun minimumSize() = 1f to 1f
}