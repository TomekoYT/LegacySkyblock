package tomeko.legacyskyblock.hud

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.hud.v1.LegacyHud
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.removeFormatting
import java.util.regex.Pattern
import kotlin.math.*

object PetDisplay : LegacyHud("pet-display", "Pet Display", Category.PLAYER) {
    fun register() {
        HudManager.register(PetDisplay, Constants.MOD_ID, Constants.MOD_ICON)
        ClientTickEvents.END_CLIENT_TICK.register(::searchTab)
        ClientReceiveMessageEvents.GAME.register(::onChatMessage)
        ClientTickEvents.END_CLIENT_TICK.register(::scanLoadoutsMenu)
    }

    private const val CATEGORY_GENERAL = "General"

    private const val SUBCATEGORY_GENERAL = "General"

    @Switch(
        title = "Text Shadow",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_GENERAL
    )
    var textShadow = true

    @Color(
        title = "Text Shadow Color",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_GENERAL
    )
    var textShadowColor = PolyColor(0xFF000000.toInt())

    @Slider(
        title = "Lines Padding",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_GENERAL
    )
    var linesPadding = 3f


    private const val SUBCATEGORY_PET = "Pet"

    @Switch(
        title = "Show Pet Name",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showName = true

    @Switch(
        title = "Show Pet Level",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showLevel = true

    @Switch(
        title = "Show Pet Item Name",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showItemName = true

    @Switch(
        title = "Show Pet XP",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showXP = true


    private const val CATEGORY_ICONS = "Icons"

    private const val SUBCATEGORY_PET_ICON = "Pet Icon"

    @Switch(
        title = "Show Pet Icon",
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var showIcon = true

    @Slider(
        title = "Icon Size",
        min = 1f,
        max = 64f,
        step = 1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var iconSize = 32f

    @Slider(
        title = "Icon Padding",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var iconPadding = 3f


    private const val SUBCATEGORY_PET_ITEM_ICON = "Pet Item Icon"

    @Switch(
        title = "Show Pet Item Icon",
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var showItemIcon = true

    @Slider(
        title = "Item Icon Size",
        min = 1f,
        max = 32f,
        step = 1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var itemIconSize = 16f

    @Slider(
        title = "Item Icon Padding",
        min = 0f,
        max = 5f,
        step = 0.1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var itemIconPadding = 2f


    private const val CATEGORY_BACKGROUND = "Background"

    @Switch(
        title = "Show Background",
        category = CATEGORY_BACKGROUND
    )
    var background = false

    @Color(
        title = "Background Color",
        category = CATEGORY_BACKGROUND
    )
    var backgroundColor = PolyColor(0x80000000.toInt())

    @Slider(
        title = "Background Radius",
        category = CATEGORY_BACKGROUND,
        min = 0f,
        max = 10f,
        step = 0.1f
    )
    var backgroundRadius = 5f


    @JvmStatic
    @Include
    var petNameCache: String? = null

    @JvmStatic
    @Include
    var petLevelCache: Int? = null

    @JvmStatic
    @Include
    var petRarityCache: String? = null

    @Include
    var petItemCache: String? = null

    @Include
    var petItemRarityCache: String? = null

    @JvmStatic
    var petXPLineCache: Component? = null

    private var tickCooldown = 0

    private var actualWidth = 0f
    private var actualHeight = 0f

    override val width: Float = actualWidth
    override val height: Float = actualHeight
    override fun minimumSize(): Pair<Float, Float> = actualWidth to actualHeight
    override fun update(): Boolean = true
    override fun multipleInstancesAllowed(): Boolean = false
    override fun deletable(): Boolean = false

    override fun render(mcCtx: GuiGraphicsExtractor) {
        if (!HudManager.isEditing && !HypixelPackets.inSkyblock) return

        val petName: String?
        val petLevel: Int?
        val petRarity: String?
        val petItem: String?
        val petItemRarity: String?
        val petXPLine: Component?

        if (HudManager.isEditing
            && (petNameCache == null
                    || petLevelCache == null
                    || petRarityCache == null
                    )
        ) {
            petName = "Golden Dragon"
            petLevel = 124
            petRarity = "LEGENDARY"
            petItem = "Antique Remedies"
            petItemRarity = "EPIC"
            petXPLine = Component.empty()
                .append(Component.literal("475,180").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("1.9M XP ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("(25.2%)").withStyle(ChatFormatting.GOLD))
        } else {
            petName = petNameCache
            petLevel = petLevelCache
            petRarity = petRarityCache
            petItem = petItemCache
            petItemRarity = petItemRarityCache
            petXPLine = petXPLineCache
        }

        if (petName == null || petLevel == null || petRarity == null) return

        val shouldShowPetItem = showItemName && petItem != null && petItemRarity != null
        val shouldShowPetItemIcon = showItemIcon && petItem != null && petItemRarity != null
        val shouldShowPetXP = showXP && petXPLine != null

        val mc = Minecraft.getInstance()

        var petNameLine = ""
        if (showLevel) petNameLine += "§7[Lvl $petLevel] "
        if (showName) petNameLine += "${getChatColorFromRarity(petRarity)}${petName}"

        var maxTextWidth = mc.font.width(petNameLine).toFloat()
        var textLines = 1

        var petItemLine = ""
        if (shouldShowPetItem) {
            petItemLine = "${getChatColorFromRarity(petItemRarity)}${petItem}"
            maxTextWidth = max(maxTextWidth, mc.font.width(petItemLine).toFloat())
            textLines++
        }

        if (shouldShowPetXP) {
            maxTextWidth = max(maxTextWidth, mc.font.width(petXPLine).toFloat())
            textLines++
        }

        fun getPetItemStack(): ItemStack? {
            val itemIconID =
                if (petItem!!.endsWith("Boost"))
                    "PET_ITEM_${petItem.substringBefore(" ").uppercase()}_SKILL_BOOST_${petItemRarity}"
                else
                    petItem.uppercase().replace(" ", "_")

            var itemIcon: ItemStack? = SimpleItemAPI.getItemByIdOrNull(SkyBlockId.item(itemIconID))
            if (itemIcon == null) itemIcon = SimpleItemAPI.getPetByIdOrNull(SkyBlockId.item("PET_ITEM_$itemIconID"))
            return itemIcon
        }

        val textHeight = textLines * mc.font.lineHeight + max(0, textLines - 1) * linesPadding

        val icon: ItemStack? =
            if (showIcon)
                SimpleItemAPI.getPetByIdOrNull(
                    SkyBlockId.pet(
                        petName.uppercase().replace(" ", "_"),
                        petRarity
                    )
                )
            else if (shouldShowPetItemIcon)
                getPetItemStack()
            else
                null

        val hasBothIcons = icon != null && showIcon && shouldShowPetItemIcon

        val actualIconSize = if (icon != null) iconSize else 0f
        val actualIconPadding = if (icon != null) iconPadding else 0f

        val effectiveItemIconPadding = itemIconPadding - 3f
        val actualItemIconPadding =
            if (hasBothIcons) max(0f, (itemIconSize / 2f) + effectiveItemIconPadding) else 0f

        val coreHeight = max(actualIconSize, textHeight)
        val itemY = (coreHeight - actualIconSize) / 2f
        var textY = (coreHeight - textHeight) / 2f

        val textStartX = actualIconSize + actualItemIconPadding + actualIconPadding
        actualWidth = textStartX + maxTextWidth

        val badgeBottomY = itemY + actualIconSize + actualItemIconPadding
        actualHeight = max(coreHeight, badgeBottomY)

        mcCtx.pose().pushMatrix()

        if (background) {
            mcCtx.fill(
                -backgroundRadius.toInt(),
                -backgroundRadius.toInt(),
                (actualWidth + backgroundRadius).toInt(),
                (actualHeight + backgroundRadius).toInt(),
                backgroundColor.argb
            )
        }

        if (icon != null) {
            val scale = actualIconSize / 16f

            mcCtx.pose().pushMatrix()
            mcCtx.pose().translate(0f, itemY)
            mcCtx.pose().scale(scale, scale)
            mcCtx.item(icon, 0, 0)
            mcCtx.pose().popMatrix()

            if (hasBothIcons) {
                val itemIcon = getPetItemStack()

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

        if (showName || showLevel) {
            renderComponent(mcCtx, Component.literal(petNameLine), textStartX, textY)
            textY += (mc.font.lineHeight + linesPadding)
        }

        if (shouldShowPetItem) {
            renderComponent(mcCtx, Component.literal(petItemLine), textStartX, textY)
            textY += (mc.font.lineHeight + linesPadding)
        }

        if (shouldShowPetXP) {
            renderComponent(mcCtx, petXPLine, textStartX, textY)
        }

        mcCtx.pose().popMatrix()
    }

    private fun renderComponent(mcCtx: GuiGraphicsExtractor, component: Component, textX: Float, textY: Float) {
        val mc = Minecraft.getInstance()

        if (textShadow) {
            mcCtx.text(
                mc.font,
                component.string.removeFormatting(),
                textX.toInt() + 1,
                textY.toInt() + 1,
                textShadowColor.argb,
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
            var component = player.tabListDisplayName ?: Component.literal("")
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
                    petLevelCache = match.groupValues[1].toInt()
                    petNameCache = match.groupValues[2]
                    petRarityCache = getRarityFromComponentColor(component.siblings[2].style.color!!.value)
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
                        petXPLineCache = null
                        break
                    }

                    petItemCache = plainText
                    petItemRarityCache = getRarityFromComponentColor(component.siblings[1].style.color!!.value)
                    parsedItemOrXp = true
                    continue
                }
            }

            if (isXpLine(plainText)) {
                setPetXPFromTab(component)
                break
            }

            petXPLineCache = null
            break
        }
    }

    private fun onChatMessage(message: Component, fromActionBar: Boolean) {
        if (!HypixelPackets.inSkyblock || fromActionBar) return

        val autoPetMatch = Regex(
            "^§cAutopet §eequipped your §7\\[Lvl (\\d+)] (§.)((?:[^§]|§.)+?)(?:§d ✦)?§e! §a§lVIEW RULE$"
        ).find(message.string)

        if (autoPetMatch != null) {
            setTickCooldown()
            petLevelCache = autoPetMatch.groupValues[1].toInt()
            petRarityCache = getRarityFromChatColor(autoPetMatch.groupValues[2])
            petNameCache = autoPetMatch.groupValues[3]

            resetItem()
            petXPLineCache = null
            return
        }


        val levelUpMatch = Regex(
            "^Your\\s+(.+?)\\s+leveled up to level\\s+(\\d+)!$"
        ).find(message.string)

        if (levelUpMatch != null) {
            val name = levelUpMatch.groupValues[1]
            val level = levelUpMatch.groupValues[2].toInt()

            if (name != petNameCache
                || getRarityFromComponentColor(message.siblings[1].style.color!!.value) != petRarityCache
                || (petLevelCache != null && level <= petLevelCache!!)
            ) return

            setTickCooldown()
            petLevelCache = level
            petXPLineCache = null
            return
        }

        val holdingPetItemMatch = Regex(
            "^Your pet is now holding (.+)\\.$"
        ).find(message.string)

        if (holdingPetItemMatch != null) {
            val sibling = message.siblings[1]

            setTickCooldown()
            petItemCache = sibling.string
            petItemRarityCache = getRarityFromComponentColor(sibling.style.color!!.value)
            return
        }

        val removedPetItemMatch = Regex(
            "^You removed (.+) from your pet!$"
        ).find(message.string)

        if (removedPetItemMatch != null) {
            setTickCooldown()
            resetItem()
            return
        }
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

        val level = match.groupValues[1].toInt()
        val name = match.groupValues[2]

        setTickCooldown()
        petNameCache = name
        petLevelCache = level
        petRarityCache = getRarityFromComponentColor(component.siblings[1].style.color!!.value)

        val tooltip: MutableList<Component> =
            item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL)

        searchForPetItemInTooltip(tooltip)
        petXPLineCache = null
    }

    @JvmStatic
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

    @JvmStatic
    fun resetAll() {
        petNameCache = null
        petLevelCache = null
        petRarityCache = null
        resetItem()
        petXPLineCache = null
    }

    fun resetItem() {
        petItemCache = null
        petItemRarityCache = null
    }

    private fun setPetXPFromTab(component: Component) {
        val copy: MutableComponent = component.copy()
        copy.siblings.removeFirst()
        petXPLineCache = copy
    }

    private fun isXpLine(text: String): Boolean {
        return text.firstOrNull()?.isDigit() == true
    }

    private fun removeSkinStar(name: String): String {
        if (name.endsWith(" ✦")) return name.dropLast(2)
        return name
    }

    @JvmStatic
    fun setTickCooldown() {
        tickCooldown = 60
    }

    @JvmStatic
    fun searchForPetItemInTooltip(tooltip: MutableList<Component>) {
        for (line in tooltip) {
            val pattern = Pattern.compile("^Held Item: (.*)$")
            val matcher = pattern.matcher(line.string)
            if (matcher.find()) {
                petItemCache = matcher.group(1)
                petItemRarityCache = getRarityFromComponentColor(line.siblings[1].style.color!!.value)
                return
            }
        }

        resetItem()
    }

    @JvmStatic
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